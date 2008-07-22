package cbit.vcell.microscopy;

import java.awt.Rectangle;
import java.io.File;

import cbit.sql.KeyValue;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.server.VCDataManager;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.opt.Parameter;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class FRAPOptData {
	public static String[] PARAMETER_NAMES = new String[]{ "diffRate",
														  "mobileFrac",
														  "bleachWhileMonitoringRate"};
	public static final int DIFFUSION_RATE_INDEX = 0;
	public static final int MOBILE_FRACTION_INDEX = 1;
	public static final int BLEACH_WHILE_MONITOR_INDEX = 2;
			
	public static final Parameter REF_DIFFUSION_RATE_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX], 0, 20, 1.0, 1.0);
	public static final Parameter REF_MOBILE_FRACTION_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1.0);
	public static final Parameter REF_BLEACH_WHILE_MONITOR_PARAM =
		new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX], 0, 1, 1.0,  0);

	
	private static int maxRefSavePoints = 500;
	private static int startingTime = 0;
	
	private FRAPStudy expFrapStudy = null;
	private LocalWorkspace localWorkspace = null;
	private TimeBounds refTimeBounds = null;
	private TimeStep refTimeStep = null;
	private DefaultOutputTimeSpec  refTimeSpec = null;
	private double[][] dimensionReducedRefData = null;
	private double[] refDataTimePoints = null;
	private double[][] dimensionReducedExpData = null;
	private double[] reducedExpTimePoints = null;
	
	public FRAPOptData(FRAPStudy argExpFrapStudy, LocalWorkspace argLocalWorkSpace,
			DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		expFrapStudy = argExpFrapStudy;
//		REF_DIFFUSION_RATE = Double.parseDouble(expFrapStudy.getFrapModelParameters().diffusionRate);
//		REF_MOBILE_FRACTION =
//			(expFrapStudy.getFrapModelParameters().mobileFraction != null
//				?Double.parseDouble(expFrapStudy.getFrapModelParameters().mobileFraction)
//				:1.0);
//
//		REF_BLEACH_WHILE_MONITOR = 
//			(expFrapStudy.getFrapModelParameters().monitorBleachRate != null
//				?Double.parseDouble(expFrapStudy.getFrapModelParameters().monitorBleachRate)
//				:0);
		
		localWorkspace = argLocalWorkSpace;
		if(progressListener != null){
			progressListener.updateMessage("Getting experimental data ROI averages...");
		}
		dimensionReducedExpData = getDimensionReducedExpData();
		dimensionReducedRefData = getDimensionReducedRefData(progressListener);
	}
	
	public TimeBounds getRefTimeBounds()
	{
		if(refTimeBounds == null)
		{
			//estimated t = ( bleach area max width /(4*D)) * ln(1/delta), use bleah area width as length.
			ROI bleachedROI = getExpFrapStudy().getFrapData().getRoi(RoiType.ROI_BLEACHED);
			
			Rectangle bleachRect = bleachedROI.getRoiImages()[0].getNonzeroBoundingBox();
			double width = ((double)bleachRect.width/getExpFrapStudy().getFrapData().getImageDataset().getISize().getX()) * 
			               getExpFrapStudy().getFrapData().getImageDataset().getExtent().getX();
			double height = ((double)bleachRect.height/getExpFrapStudy().getFrapData().getImageDataset().getISize().getY()) * 
            			   getExpFrapStudy().getFrapData().getImageDataset().getExtent().getY();
			
			double bleachWidth = Math.max(width, height);
			final double  unrecovery_threshold = .01;
			double refEndingTime = (bleachWidth * bleachWidth/(4*REF_DIFFUSION_RATE_PARAM.getInitialGuess())) * Math.log(1/unrecovery_threshold);
			
			refTimeBounds = new TimeBounds(FRAPOptData.startingTime, refEndingTime);
		}
		return refTimeBounds;
	}
	
	public TimeStep getRefTimeStep()
	{
		//time step is estimated as deltaX^2/(4*D)
		if(refTimeStep == null)
		{
			int numX = getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getNumX();
			double deltaX = getExpFrapStudy().getFrapData().getImageDataset().getAllImages()[0].getExtent().getX()/(numX-1);
			double timeStep = (deltaX*deltaX /REF_DIFFUSION_RATE_PARAM.getInitialGuess()) * 0.25;
			refTimeStep = new TimeStep(timeStep, timeStep, timeStep);
		}
		return refTimeStep;
	}
	
	public DefaultOutputTimeSpec getRefTimeSpec()
	{
		if(refTimeSpec == null)
		{
			int numSavePoints = (int)Math.ceil((getRefTimeBounds().getEndingTime() - getRefTimeBounds().getStartingTime())/getRefTimeStep().getDefaultTimeStep());
			if(numSavePoints <= FRAPOptData.maxRefSavePoints)
			{
				refTimeSpec = new DefaultOutputTimeSpec(1, FRAPOptData.maxRefSavePoints);
			}
			else
			{
				int keepEvery = (int)Math.ceil(numSavePoints/FRAPOptData.maxRefSavePoints);
				refTimeSpec = new DefaultOutputTimeSpec(keepEvery, FRAPOptData.maxRefSavePoints);
			}
			
		}
		return refTimeSpec;
	}
	
	public double[][] getDimensionReducedRefData(DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		if(dimensionReducedRefData == null){
			refreshDimensionReducedRefData(progressListener);
		}
		return dimensionReducedRefData;
	}
	
	public double[][] getDimensionReducedExpData() throws Exception
	{
		if(dimensionReducedExpData == null){
			//normalize the experimental data, because the reference data is normalized
			VCDataManager vcManager = getLocalWorkspace().getVCDataManager();
			double[] prebleachAvg = vcManager.getSimDataBlock(getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			dimensionReducedExpData = FRAPOptimization.dataReduction(getExpFrapStudy().getFrapData(),true,startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(), prebleachAvg);
		}
		return dimensionReducedExpData;
	}
	
	public double[] getReducedExpTimePoints() {
		if(reducedExpTimePoints == null)
		{
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			reducedExpTimePoints = FRAPOptimization.timeReduction(getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps(), startRecoveryIndex); 
		}
		return reducedExpTimePoints;
	}
	
	public void refreshDimensionReducedRefData(final DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		final double RUNSIM_PROGRESS_FRACTION = .5;
		DataSetControllerImpl.ProgressListener runSimProgressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress) {
					if(progressListener != null){
						progressListener.updateProgress(progress*RUNSIM_PROGRESS_FRACTION);
					}
				}
				public void updateMessage(String message){
					if(progressListener != null){
						progressListener.updateMessage(message);
					}
				}
		};
		System.out.println("run simulation...");
		KeyValue referenceSimKeyValue = runRefSimulation(runSimProgressListener);
		try{
			VCSimulationIdentifier vcSimID =
				new VCSimulationIdentifier(referenceSimKeyValue,LocalWorkspace.getDefaultOwner());
			VCSimulationDataIdentifier vcSimDataID =
				new VCSimulationDataIdentifier(vcSimID,FieldDataFileOperationSpec.JOBINDEX_DEFAULT);
			refDataTimePoints = getLocalWorkspace().getVCDataManager().getDataSetTimes(vcSimDataID);
			System.out.println("simulation done...");
			int startRecoveryIndex = Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery);
			
			DataSetControllerImpl.ProgressListener reducedRefDataProgressListener =
				new DataSetControllerImpl.ProgressListener(){
					public void updateProgress(double progress) {
						if(progressListener != null){
							progressListener.updateProgress(.5+progress*(1-RUNSIM_PROGRESS_FRACTION));
						}
					}
					public void updateMessage(String message){
						if(progressListener != null){
							progressListener.updateMessage(message);
						}
					}
			};
			dimensionReducedRefData =
				FRAPOptimization.dataReduction(getLocalWorkspace().getVCDataManager(),vcSimDataID,
						startRecoveryIndex, getExpFrapStudy().getFrapData().getRois(),reducedRefDataProgressListener);
			System.out.println("generating dimension reduced ref data, done ....");
		}finally{
			FRAPStudy.removeExternalDataAndSimulationFiles(referenceSimKeyValue, null, null, getLocalWorkspace());
		}
	}
	
	public KeyValue runRefSimulation(final DataSetControllerImpl.ProgressListener progressListener) throws Exception
	{
		BioModel bioModel = null;
		if(progressListener != null){
			progressListener.updateMessage("Running Reference Simulation...");
		}
		try{
			bioModel =
				FRAPStudy.createNewBioModel(
					expFrapStudy,
					new Double(REF_DIFFUSION_RATE_PARAM.getInitialGuess()),
					REF_BLEACH_WHILE_MONITOR_PARAM.getInitialGuess()+"",
					REF_MOBILE_FRACTION_PARAM.getInitialGuess()+"",
					LocalWorkspace.createNewKeyValue(),
					LocalWorkspace.getDefaultOwner(),
					new Integer(expFrapStudy.getFrapModelParameters().startIndexForRecovery));
			
			//change time bound and time step
			Simulation sim = bioModel.getSimulations()[0];
			sim.getSolverTaskDescription().setTimeBounds(getRefTimeBounds());
			sim.getSolverTaskDescription().setTimeStep(getRefTimeStep());
			sim.getSolverTaskDescription().setOutputTimeSpec(getRefTimeSpec());
			
	//		System.out.println("run FRAP Reference Simulation...");
			final double RUN_REFSIM_PROGRESS_FRACTION = .5;
			DataSetControllerImpl.ProgressListener runRefSimProgressListener =
				new DataSetControllerImpl.ProgressListener(){
					public void updateProgress(double progress) {
						if(progressListener != null){
							progressListener.updateProgress(progress*RUN_REFSIM_PROGRESS_FRACTION);
						}
					}
					public void updateMessage(String message){
						if(progressListener != null){
							progressListener.updateMessage(message);
						}
					}
			};
	
			//run simulation
			FRAPStudy.runFVSolverStandalone(
				new File(getLocalWorkspace().getDefaultSimDataDirectory()),
				new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
				bioModel.getSimulations(0),
				getExpFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
				getExpFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
				runRefSimProgressListener);
			
			return sim.getVersion().getVersionKey();
		}catch(Exception e){
			if(bioModel != null && bioModel.getSimulations() != null){
				FRAPStudy.removeExternalDataAndSimulationFiles(
					bioModel.getSimulations()[0].getVersion().getVersionKey(), null, null, getLocalWorkspace());
			}
			throw e;
		}
	}
	
	public double computeError(double newParamVals[], boolean[] eoi) throws Exception
	{
		double error = FRAPOptimization.getErrorByNewParameters(REF_DIFFUSION_RATE_PARAM.getInitialGuess(), 
				                                              newParamVals,
				                                              getDimensionReducedRefData(null),
				                                              getDimensionReducedExpData(),
				                                              refDataTimePoints,
				                                              getReducedExpTimePoints(),
				                                              getExpFrapStudy().getFrapData().getRois().length, 
				                                              (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery()),
				                                              eoi);
		
		for(int i=0; i<newParamVals.length; i++)
		{
			System.out.println("Parameter "+ FRAPOptData.PARAMETER_NAMES[i]+ " is: " + newParamVals[i]);
		}
		System.out.println("Variance:" + error);
		System.out.println("--------------------------------");
		return error;
	}

	public double[][] getFitData(Parameter[] newParams, boolean[] errorOfInterest) throws Exception
	{
		double[][] reducedExpData = getDimensionReducedExpData();
		double[] reducedExpTimePoints = getReducedExpTimePoints();
		int roiLen = getExpFrapStudy().getFrapData().getRois().length;
		double refTimeInterval = (getRefTimeStep().getDefaultTimeStep() * getRefTimeSpec().getKeepEvery());
		
		double[][] newData = new double[roiLen][reducedExpTimePoints.length];
		double diffRate = 0;
		double[][] diffData = null;
		double mobileFrac = 1;
		double bleachWhileMonitoringRate = 0;
		
		if(newParams != null && newParams.length > 0)
		{
			for(int i=0; i<newParams.length; i++)
			{
				if(newParams[i].getName().equals(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX]))
				{
					diffRate = newParams[FRAPOptData.DIFFUSION_RATE_INDEX].getInitialGuess();
				}
				else if(newParams[i].getName().equals(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX]))
				{
					mobileFrac = newParams[FRAPOptData.MOBILE_FRACTION_INDEX].getInitialGuess();
					mobileFrac = Math.min(mobileFrac, 1);
				}
				else if(newParams[i].getName().equals(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX]))
				{
					bleachWhileMonitoringRate = newParams[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX].getInitialGuess();
				}
			}
			
			diffData = FRAPOptimization.getValueByDiffRate(REF_DIFFUSION_RATE_PARAM.getInitialGuess(),
                    diffRate,
                    getDimensionReducedRefData(null),
                    reducedExpData,
                    refDataTimePoints,
                    reducedExpTimePoints,
                    roiLen,
                    refTimeInterval);
			
			// get diffusion initial condition for immobile part
			double[] firstPostBleach = new double[roiLen];
			for(int i = 0; i < roiLen; i++)
			{
				firstPostBleach[i] = diffData[i][0];
			}
			
			for(int i=0; i<roiLen; i++)
			{
				for(int j=0; j<getReducedExpTimePoints().length; j++)
				{
//					newData[i][j] = (mobileFrac * diffData[i][j] + imMobielFrac * firstPostBleach[i]) * Math.exp(-(bleachWhileMonitoringRate * reducedExpTimePoints[j]));
					newData[i][j] = FRAPOptimization.getValueFromParameters(diffData[i][j], mobileFrac, bleachWhileMonitoringRate, firstPostBleach[i], reducedExpTimePoints[j]);
				}
			}
			
			//REORder according to roiTypes
			double[][] fitDataInROITypeOrder = new double[newData.length][];
			for (int i = 0; i < getExpFrapStudy().getFrapData().getRois().length; i++) {
				for (int j = 0; j < ROI.RoiType.values().length; j++) {
					if(getExpFrapStudy().getFrapData().getRois()[i].getROIType().equals(ROI.RoiType.values()[j])){
						fitDataInROITypeOrder[j] = newData[i];
						break;
					}
				}
			}
			//print out error
//			double error = 0;
//			double[][] expData = getDimensionReducedExpData();
//			for(int i=0; i<getExpFrapStudy().getFrapData().getRois().length; i++)
//			{
//				if(errorOfInterest != null && errorOfInterest[i])
//				{				
//					for(int j=0; j<getReducedExpTimePoints().length; j++)
//					{
//						double difference = (expData[i][j] - newData[i][j]);
//						error = error + difference * difference;
//					}
//				}
//			}
//			System.out.println("##################################");
//			System.out.println("In getFitData() the variance computed is :" + error);
			
			return fitDataInROITypeOrder;
		}
		else
		{
			throw new Exception("Cannot get fit data because there is no required parameters.");
		}
	}
	
	public Parameter[] getBestParamters(Parameter[] inParams, boolean[] errorOfInterest) throws Exception
	{
		Parameter[] outParams = new Parameter[inParams.length];
		String[] outParaNames = new String[inParams.length];
		double[] outParaVals = new double[inParams.length];
		FRAPOptimization.estimate(this, inParams, outParaNames, outParaVals, errorOfInterest);
		for(int i = 0; i < inParams.length; i++)
		{
			outParams[i] = new Parameter(outParaNames[i], Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0, outParaVals[i]);
		}
		for(int i = 0; i < outParams.length; i++)
		{
			System.out.println("Estimate result for "+outParams[i].getName()+ " is: "+outParams[i].getInitialGuess());
		}
		return outParams;
	}
	
	private void checkValidityOfRefData() throws Exception 
	{
		double[] portion = new double[]{0.8, 0.9};
		double[][] refData = getDimensionReducedRefData(null);
		double[] refTimePoints = FRAPOptimization.timeReduction(refDataTimePoints, Integer.parseInt(getExpFrapStudy().getFrapModelParameters().startIndexForRecovery));
		for(int i = 0 ; i < getExpFrapStudy().getFrapData().getRois().length; i++)
		{
			for(int k = 0 ; k < portion.length; k++)
			{
				int startingTimeIndex = (int)Math.round(refTimePoints.length * portion[k]);
				double max = 0;
				double avg = 0;
				double std = 0;
				for(int j = startingTimeIndex; j < (refTimePoints.length); j++ )
				{
					if(refData[i][j] > max)
					{
						max = refData[i][j];
					}
					avg = avg + refData[i][j];
				}
				avg = avg / (refTimePoints.length - startingTimeIndex);
				for(int j = startingTimeIndex; j < (refTimePoints.length); j++ )
				{
					std = std + (refData[i][j] - avg)*(refData[i][j] - avg);
				}
				std = Math.sqrt(std);
				System.out.println("In ROI Type " + getExpFrapStudy().getFrapData().getRois()[i].getROIType().name() + ".   Max of last "+ (1-portion[k])*100+"% data is:" + max +".  Average is:" + avg +". Standard Deviation is:" + std + ".    Std is "+ ((std/max)*100) + "% of max.");
			}
		}
		
		System.out.println("End of check validity of reference data");
	}
	
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
	
	public FRAPStudy getExpFrapStudy() 
	{
		return expFrapStudy;
	}
	
	public static void main(String[] args) 
	{
		String workingDir = "C:\\";
				
		LocalWorkspace localWorkspace = new LocalWorkspace(new File(workingDir));
		String xmlString;
		try {
			//read original data from file
			System.out.println("start loading original data....");
			String expFileName = "C:/VirtualMicroscopy/forOptTest_cell_10_blc_2_d_1p0_t_10_saveEvery_10_another.vfrap";
			xmlString = XmlUtil.getXMLString(expFileName);
			MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
			FRAPStudy expFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null), null);
			expFrapStudy.setXmlFilename(expFileName);
			System.out.println("experimental data time points"+expFrapStudy.getFrapData().getImageDataset().getSizeT());
			System.out.println("finish loading original data....");
			
			//create rederence data
			System.out.println("creating rederence data....");
			
			DataSetControllerImpl.ProgressListener progressListener =
			new DataSetControllerImpl.ProgressListener(){
				public void updateProgress(double progress){
					System.out.println((int)Math.round(progress*100));
				}
				public void updateMessage(String message){
					//ignore
				}
			};

			FRAPOptData optData = new FRAPOptData(expFrapStudy, localWorkspace,progressListener);
			
			//trying 3 parameters
//			Parameter diff = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.DIFFUSION_RATE_INDEX], 0, 100, 1.0, Double.parseDouble(expFrapStudy.getFrapModelParameters().diffusionRate));
//			Parameter mobileFrac = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.MOBILE_FRACTION_INDEX], 0, 1, 1.0, 1/*Double.parseDouble(expFrapStudy.getFrapModelParameters().mobileFraction)*/);
//			Parameter bleachWhileMonitoringRate = new cbit.vcell.opt.Parameter(FRAPOptData.PARAMETER_NAMES[FRAPOptData.BLEACH_WHILE_MONITOR_INDEX], 0, 10, 1.0,  0/*Double.parseDouble(expFrapStudy.getFrapModelParameters().monitorBleachRate)*/);
			
			//trying 5 parameters
			Parameter diffFastOffset = new cbit.vcell.opt.Parameter("fast_diff_offset", 0, 50, 1.0, 10);
			Parameter mFracFast = new cbit.vcell.opt.Parameter("fast_mobile_fraction", 0, 1, 1.0, 0.5);
			Parameter diffSlow = new cbit.vcell.opt.Parameter("slow_diff_rate", 0, 10, 1.0, 0.1);
			Parameter mFracSlow = new cbit.vcell.opt.Parameter("slow_mobiel_fraction", 0, 1, 1.0, 0.1);
			Parameter bleachWhileMonitoringRate = new cbit.vcell.opt.Parameter("bleach_while_monitoring_rate", 0, 10, 1.0,  0);
			Parameter[] inParams = new Parameter[]{diffFastOffset, mFracFast, diffSlow, mFracSlow, bleachWhileMonitoringRate};
			
			ROI[] rois = expFrapStudy.getFrapData().getRois();
			boolean[] errorOfInterest = new boolean[rois.length];
			for(int i=0; i<rois.length; i++)
			{
				if(!rois[i].getROIType().equals(RoiType.ROI_BLEACHED)/*rois[i].getROIType().equals(RoiType.ROI_BACKGROUND) || rois[i].getROIType().equals(RoiType.ROI_CELL)*/)
				{
					errorOfInterest[i] = false;
				}
				else
				{
					errorOfInterest[i] = true;
				}
			}
			Parameter[] bestParams = optData.getBestParamters(inParams, errorOfInterest);
//			for(int i=0; i<bestParams.length; i++)
//			{
//				System.out.println("Best estimation for " + bestParams[i].getName()+" is: " + bestParams[i].getInitialGuess());
//			}
			
//			double[][] result = optData.getFitData(inParams, errorOfInterest); // double[roiLen][timePoints]
//			int indexROI = -1;
//			for(int j = 0; j < expFrapStudy.getFrapData().getRois().length; j++)
//			{
//				if(expFrapStudy.getFrapData().getRois()[j].getROIType().equals(RoiType.ROI_BLEACHED))
//				{
//					indexROI = j;
//					break;
//				}
//			}
//			int index = Integer.parseInt(expFrapStudy.getFrapModelParameters().startIndexForRecovery);
//			for(int i = 0; i < (expFrapStudy.getFrapData().getImageDataset().getImageTimeStamps().length - index); i++)
//			{
//				System.out.println(expFrapStudy.getFrapData().getImageDataset().getImageTimeStamps()[i+index]+"\t"+result[indexROI][i]);
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
}
