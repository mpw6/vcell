package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import cbit.gui.DialogUtils;
import cbit.gui.ZEnforcer;
import cbit.image.ImageException;
import org.vcell.util.document.KeyValue;
import cbit.util.AsynchProgressPopup;
import cbit.util.BeanUtils;
import org.vcell.util.Compare;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ImageDatasetReader;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.data.NewClientPDEDataContext;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationModelInfo;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.server.MergedDataManager;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.desktop.controls.DataManager;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.microscopy.ExternalDataInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.MicroscopyXmlproducer;
import cbit.vcell.microscopy.FRAPStudy.FRAPModelParameters;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.server.VCDataIdentifier;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ExternalDataIdentifier;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.simdata.gui.PDEPlotControlPanel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;

public class FRAPStudyPanel extends JPanel implements PropertyChangeListener{
	
	public static final String FRAPSTUDYPANEL_TABNAME_IMAGES = "Images";
	public static final String FRAPSTUDYPANEL_TABNAME_IniParameters = "Initial Parameters";
	public static final String FRAPSTUDYPANEL_TABNAME_AdjParameters = "Adjust Parameters";
	public static final String FRAPSTUDYPANEL_TABNAME_2DResults = "2D Results";
	public static final String MODEL_TYPE_PURE_DIFFUSION = "Pure_Diffusion";
	public static final String MODEL_TYPE_REACTION_DIFFUSION = "Reaction_Diffusion";
	public static final LineBorder TAB_LINE_BORDER = new LineBorder(new Color(153, 186,243), 3);
	
	private FRAPStudy frapStudy = null;
	private FRAPOptData frapOptData = null;
	private FRAPDataPanel frapDataPanel = null;
	private LocalWorkspace localWorkspace = null;
	private JTabbedPane jTabbedPane = null;
	private FRAPParametersPanel frapParametersPanel = null;
	//to make alternative view for comparing simulation results/frap data
	final static String SPLITEDVIEW = "Parallel View";
	final static String TABBEDVIEW = "Tabbed View";
	private JRadioButton tabRadio = null;
	private JRadioButton splitRadio = null;
	private JPanel simResultsViewPanel = null;
	private PDEDataViewer pdeDataViewer = null;
	private PDEDataViewer flourDataViewer = null;
	private JPanel fitSpatialModelPanel = null;
	private FRAPSimDataViewerPanel frapSimDataViewerPanel = null;
		
	private ResultsSummaryPanel resultsSummaryPanel;
	private boolean isSetTabIdxFromSpatialAnalysis = false;
	public interface NewFRAPFromParameters{
		void create(Parameter[] newParameters, String modelType);
	}
	
	enum DisplayChoice { PDE,EXTTIMEDATA};

	private static final int USER_CHANGES_FLAG_ALL = 0xFFFFFFFF;
	private static final int USER_CHANGES_FLAG_ROI = 0x01;
	private static final int USER_CHANGES_FLAG_INI_PARAMS = 0x02;
	private static final int USER_CHANGES_FLAG_ADJUST_PARAMS = 0x04;
	
	//Elements that change the Model
	public static class FrapChangeInfo{
		public final boolean bROIValuesChanged;
		public final boolean bROISizeChanged;
		public final boolean bFreeDiffRateChanged;
		public final String freeDiffRateString;
		public final boolean bFreeMFChanged;
		public final String freeMFString;
		public final boolean bComplexDiffRateChanged;
		public final String complexDiffRateString;
		public final boolean bComplexMFChanged;
		public final String complexMFString;
		public final boolean bBleachWhileMonitorChanged;
		public final String bleachWhileMonitorRateString;
		public final boolean bBSConcentrationChanged;
		public final String bsConcentrationString;
		public final boolean bOnRateChanged;
		public final String onRateString;
		public final boolean bOffRateChanged;
		public final String offRateString;
		public final boolean bStartIndexForRecoveryChanged;
		public final String startIndexForRecoveryString;
		public FrapChangeInfo(boolean bROIValuesChanged,boolean bROISizeChanged,
				boolean bFreeDiffRateChanged,String freeDiffRateString,
				boolean bFreeMFChanged,String freeMFString,
				boolean bComplexDiffRateChanged,String complexDiffRateString,
				boolean bComplexMFChanged,String complexMFString,
				boolean bBleachWhileMonitorChanged,String bleachWhileMonitorRateString,
				boolean bBSConcentrationChanged, String bsConcentrationString,
				boolean bOnRateChanged, String onRateString,
				boolean bOffRateChanged, String offRateString,
				boolean bStartIndexForRecoveryChanged,String startIndexForRecoveryString)
		{
			this.bROIValuesChanged = bROIValuesChanged;
			this.bROISizeChanged = bROISizeChanged;
			this.bFreeDiffRateChanged = bFreeDiffRateChanged;
			this.freeDiffRateString = freeDiffRateString;
			this.bFreeMFChanged = bFreeMFChanged;
			this.freeMFString = freeMFString;
			this.bComplexDiffRateChanged = bComplexDiffRateChanged;
			this.complexDiffRateString = complexDiffRateString;
			this.bComplexMFChanged = bComplexMFChanged;
			this.complexMFString = complexMFString;
			this.bBleachWhileMonitorChanged = bBleachWhileMonitorChanged;
			this.bleachWhileMonitorRateString = bleachWhileMonitorRateString;
			this.bBSConcentrationChanged = bBSConcentrationChanged;
			this.bsConcentrationString = bsConcentrationString;
			this.bOnRateChanged = bOnRateChanged;
			this.onRateString = onRateString;
			this.bOffRateChanged = bOffRateChanged;
			this.offRateString = offRateString;
			this.bStartIndexForRecoveryChanged = bStartIndexForRecoveryChanged;
			this.startIndexForRecoveryString = startIndexForRecoveryString;
			//Don't forget to change 'hasAnyChanges' if adding new parameters
		}
		
		public boolean hasAnyChanges(){
			return bROIValuesChanged || bROISizeChanged ||
				bFreeDiffRateChanged || bFreeMFChanged ||
				bComplexDiffRateChanged || bComplexMFChanged ||
				bBleachWhileMonitorChanged || bBSConcentrationChanged ||
				bOnRateChanged || bOffRateChanged ||
				bStartIndexForRecoveryChanged;
		}
		public boolean hasStartingIdxChanged()
		{
			return bStartIndexForRecoveryChanged;
		}
		public boolean hasROIChanged(){
			return bROISizeChanged || bROIValuesChanged;
		}
		
		public String getChangeDescription(){
			return
			(bROIValuesChanged?"(Cell,Bleach or Backgroung ROI)":" ")+
			(bROISizeChanged?"(Data Dimension)":" ")+
			(bFreeDiffRateChanged?"(Free Particle Diffusion Rate)":" ")+
			(bFreeMFChanged?"(Free Particle Mobile Fraction)":" ")+
			(bComplexDiffRateChanged?"(Binding Complex Diffusion Rate)":" ")+
			(bComplexMFChanged?"(Binding Complex Mobile Fraction)":" ")+
			(bBleachWhileMonitorChanged?"(BleachWhileMonitoring Rate)":" ")+
			(bBSConcentrationChanged?"(Binding Complex Concentration)":" ")+
			(bOnRateChanged?"(Reaction On Rate)":" ")+
			(bOffRateChanged?"(Reaction Off Rate)":" ")+
			(bStartIndexForRecoveryChanged?"(Start Index for Recovery)":" ");
		}
		public String getROIOrStartingIdxChangeInfo(){
			return
			(bROIValuesChanged?"Cell,Bleach or Backgroung ROI":" ")+
			(bROISizeChanged?"Data Dimension":" ")+
			(bStartIndexForRecoveryChanged?"Start Index for Recovery":" ");
		}
		
	};
	public static class SavedFrapModelInfo{
		public final KeyValue savedSimKeyValue;
		public final ROI lastCellROI;
		public final ROI lastBleachROI;
		public final ROI lastBackgroundROI;
		public final String lastFreeDiffusionrate;
		public final String lastFreeMobileFraction;
		public final String lastComplexDiffusionRate;
		public final String lastComplexMobileFraction;
		public final String lastBleachWhileMonitoringRate;
		public final String lastBSConcentration;
		public final String reactionOnRate;
		public final String reactionOffRate;
		public final String startingIndexForRecovery;
		public SavedFrapModelInfo(
			KeyValue savedSimKeyValue,
			ROI cellROI,
			ROI bleachROI,
			ROI backgroundROI,
			String freeDiffusionrate,
			String freeMobileFraction,
			String complexDiffusionRate,
			String complexMobileFraction,
			String bleachWhileMonitoringRate,
			String bsConcentration,
			String onRate,
			String offRate,
			String startingIndexForRecovery)
		{
//			if(savedSimKeyValue == null){
//				throw new IllegalArgumentException("SimKey cannot be null for a saved FrapModel.");
//			}
			this.savedSimKeyValue = savedSimKeyValue;
			this.lastCellROI = cellROI;
			this.lastBleachROI = bleachROI;
			this.lastBackgroundROI = backgroundROI;
			this.lastFreeDiffusionrate = freeDiffusionrate;
			this.lastFreeMobileFraction = freeMobileFraction;
			this.lastComplexDiffusionRate = complexDiffusionRate;
			this.lastComplexMobileFraction = complexMobileFraction;
			this.lastBleachWhileMonitoringRate = bleachWhileMonitoringRate;
			this.lastBSConcentration = bsConcentration;
			this.reactionOnRate = onRate;
			this.reactionOffRate = offRate;
			this.startingIndexForRecovery = startingIndexForRecovery;
		}
	};
	private FRAPStudyPanel.SavedFrapModelInfo savedFrapModelInfoNew2 = null;

	private static final String SAVE_FILE_OPTION = "Save";
			
	private class CurrentSimulationDataState{
		private final FrapChangeInfo frapChangeInfo;
		private final boolean areSimFilesOK;
		private final boolean areExtDataFileOK;
//		private final boolean isRefDataFileOK;
		public CurrentSimulationDataState() throws Exception{
			if(getSavedFrapModelInfo() == null){
				throw new Exception("CurrentSimulationDataState can't be determined because no savedFrapModelInfo available");
			}
			frapChangeInfo = getChangesSinceLastSave();
			areSimFilesOK = areSimulationFilesOKForSavedModel();
			areExtDataFileOK =
				areExternalDataOK(getLocalWorkspace(),
				getFrapStudy().getFrapDataExternalDataInfo(),
				getFrapStudy().getRoiExternalDataInfo());
//			isRefDataFileOK = areReferenceDataOK(getLocalWorkspace(), getFrapStudy().getRefExternalDataInfo());
		}
		public boolean isDataInvalidBecauseModelChanged(){
			return frapChangeInfo.hasAnyChanges();
		}
		public boolean isDataInvalidBecauseMissingOrCorrupt(){
			return !areSimFilesOK || !areExtDataFileOK;
		}
		public boolean isDataValid(){
			if(!frapChangeInfo.hasAnyChanges() && areSimFilesOK && areExtDataFileOK){
				return true;
			}
			return false;
		}
		//commented since it is not being used.
//		public String getDescription(){
//			if(isDataValid()){
//				return "Simulation Data are valid.";
//			}else if(isDataInvalidBecauseModelChanged()){
//				return "Sim Data invalid because Model has changes including:\n"+frapChangeInfo.getChangeDescription();
//			}else if(isDataInvalidBecauseMissingOrCorrupt()){
//				return "Sim Data are missing or corrupt";
//			}
//			throw new RuntimeException("Unknown description");
//		}
	};

	private	DataSetControllerImpl.ProgressListener progressListenerZeroToOne =
		new DataSetControllerImpl.ProgressListener(){
			public void updateProgress(double progress) {
				VirtualFrapMainFrame.updateProgress((int)Math.round(progress*100));
			}
			public void updateMessage(String message) {
				//ignore
			}
		};

	public static final int CURSOR_CELLROI = 0;
	public static final int CURSOR_BLEACHROI = 1;
	public static final int CURSOR_BACKGROUNDROI = 2;
	public static final Cursor[] ROI_CURSORS = new Cursor[]{
		Cursor.getDefaultCursor(),
		Cursor.getDefaultCursor(),
		Cursor.getDefaultCursor()
	};
	
	private UndoableEditSupport undoableEditSupport =
		new UndoableEditSupport();
	
	public static final UndoableEdit CLEAR_UNDOABLE_EDIT =
		new AbstractUndoableEdit(){
					public boolean canUndo() {
						return false;
					}
					public String getUndoPresentationName() {
						return null;
					}
					public void undo() throws CannotUndoException {
						super.undo();
					}
				};

	public static class MultiFileImportInfo{
		public final boolean isTimeSeries;
		public final double timeInterval;
		public final double zInterval;
		public MultiFileImportInfo(boolean isTimeSeries,double timeInterval,double zInterval){
			this.isTimeSeries = isTimeSeries;
			this.timeInterval = timeInterval;
			this.zInterval = zInterval;
		}
	}
	
	/**
	 * This method initializes 
	 * 
	 */
	public FRAPStudyPanel() {
		super();
		loadROICursors();
		initialize();
	}
	
	public void addUndoableEditListener(UndoableEditListener undoableEditListener){
		undoableEditSupport.addUndoableEditListener(undoableEditListener);
		getFRAPDataPanel().getOverlayEditorPanelJAI().setUndoableEditSupport(undoableEditSupport);
	}	
	
	private void setSavedFrapModelInfo(SavedFrapModelInfo savedFrapModelInfo) throws Exception{
		try{
			undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
		}catch(Exception e){
			e.printStackTrace();
		}
		savedFrapModelInfoNew2 = savedFrapModelInfo;
	}
	private SavedFrapModelInfo getSavedFrapModelInfo(){
		return savedFrapModelInfoNew2;
	}
	public static void loadROICursors(){
		for (int i = 0; i < ROI_CURSORS.length; i++) {
			URL cursorImageURL = null;
			if(i == CURSOR_CELLROI){
				cursorImageURL = FRAPStudyPanel.class.getResource("/images/cursorCellROI.gif");
			}else if(i == CURSOR_BLEACHROI){
				cursorImageURL = FRAPStudyPanel.class.getResource("/images/cursorBleachROI.gif");
			}else if(i == CURSOR_BACKGROUNDROI){
				cursorImageURL = FRAPStudyPanel.class.getResource("/images/cursorBackgroundROI.gif");
			}
			ImageIcon imageIcon = new ImageIcon(cursorImageURL);
			Image cellCursorImage = imageIcon.getImage();
			BufferedImage tempImage =
				new BufferedImage(cellCursorImage.getWidth(null),cellCursorImage.getHeight(null),BufferedImage.TYPE_INT_ARGB);
			tempImage.createGraphics().drawImage(
				cellCursorImage, 0, 0, tempImage.getWidth(), tempImage.getHeight(), null);
			//outline with black
			for (int y = 0; y < tempImage.getHeight(); y++) {
				for (int x = 0; x < tempImage.getWidth(); x++) {
//						System.out.print(Integer.toHexString(tempImage.getRGB(x, y))+" ");
					if((tempImage.getRGB(x, y)&0x00FFFFFF) == 0){
						tempImage.setRGB(x, y, 0x00000000);
						int xoff = x-1;
						if(xoff >= 0 && (tempImage.getRGB(xoff, y)&0x00FFFFFF) != 0){
							tempImage.setRGB(x, y, 0xFF000000);
						}
						xoff = x+1;
						if(xoff < tempImage.getWidth() && (tempImage.getRGB(xoff, y)&0x00FFFFFF) != 0){
							tempImage.setRGB(x, y, 0xFF000000);
						}
						int yoff = y-1;
						if(yoff >= 0 && (tempImage.getRGB(x,yoff)&0x00FFFFFF) != 0){
							tempImage.setRGB(x,y, 0xFF000000);
						}
						yoff = y+1;
						if(yoff < tempImage.getHeight() && (tempImage.getRGB(x,yoff)&0x00FFFFFF) != 0){
							tempImage.setRGB(x,y, 0xFF000000);
						}
	
					}else{
						tempImage.setRGB(x, y, 0xFFFFFFFF);
					}
				}
//					System.out.println();
			}
			ROI_CURSORS[i] = Toolkit.getDefaultToolkit().createCustomCursor(tempImage, new Point(0,0), "cellCursor");
		}			
	}
	public static SavedFrapModelInfo createSavedFrapModelInfo(FRAPStudy frapStudy) throws Exception{

		KeyValue savedSimKey = null;
		if(frapStudy != null && frapStudy.getBioModel() != null && frapStudy.getBioModel().getSimulations()!= null && frapStudy.getBioModel().getSimulations().length > 0)
		{
			savedSimKey = frapStudy.getBioModel().getSimulations()[0].getSimulationVersion().getVersionKey();
		}
		
		FRAPData frapData = frapStudy.getFrapData();
		FRAPModelParameters frapModelParameters = frapStudy.getFrapModelParameters();
		
		ROI savedCellROI = null;
		ROI savedBleachROI = null;
		ROI savedBackgroundROI = null;
		if(frapData != null){
			savedCellROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			savedCellROI = (savedCellROI == null?null:new ROI(savedCellROI));
			savedBleachROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
			savedBleachROI = (savedBleachROI == null?null:new ROI(savedBleachROI));
			savedBackgroundROI = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
			savedBackgroundROI = (savedBackgroundROI == null?null:new ROI(savedBackgroundROI));
		}
		
		String lastFreeDiffusionrate = null;
		String lastFreeMobileFraction = null;
		String lastComplexDiffusionRate = null;
		String lastComplexMobileFraction = null;
		String lastBleachWhileMonitoringRate = null;
		String lastBSConcentration = null;
		String reactionOnRate = null;
		String reactionOffRate = null;
		String startingIndexForRecovery = null;
		
		if(frapModelParameters != null)
		{
			if(frapModelParameters.getIniModelParameters()!= null)
			{
				startingIndexForRecovery = frapModelParameters.getIniModelParameters().startingIndexForRecovery;
			}
			if(frapModelParameters.getReacDiffModelParameters() != null)
			{
				FRAPStudy.ReactionDiffusionModelParameters reacDiffParams = frapModelParameters.getReacDiffModelParameters(); 
				lastFreeDiffusionrate = reacDiffParams.freeDiffusionRate;
				lastFreeMobileFraction = reacDiffParams.freeMobileFraction;
				lastComplexDiffusionRate = reacDiffParams.complexDiffusionRate;
				lastComplexMobileFraction = reacDiffParams.complexMobileFraction;
				lastBleachWhileMonitoringRate = reacDiffParams.monitorBleachRate;
				lastBSConcentration = reacDiffParams.bsConcentration;
				reactionOnRate = reacDiffParams.reacOnRate;
				reactionOffRate = reacDiffParams.reacOffRate;
			}
			
		}
		return new SavedFrapModelInfo(savedSimKey, savedCellROI, savedBleachROI, savedBackgroundROI, lastFreeDiffusionrate, lastFreeMobileFraction, lastComplexDiffusionRate, lastComplexMobileFraction, lastBleachWhileMonitoringRate, lastBSConcentration, reactionOnRate, reactionOffRate, startingIndexForRecovery);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(727, 607));
        this.setMinimumSize(new Dimension(640, 480));
        this.add(getJTabbedPane(), BorderLayout.CENTER);
//        this.add(getJPanel(), BorderLayout.SOUTH);
			
	}

	/**
	 * This method initializes FRAPDataPanel	
	 * 	
	 * @return cbit.vcell.virtualmicroscopy.gui.FRAPDataPanel	
	 */
	private FRAPDataPanel getFRAPDataPanel() {
		if (frapDataPanel == null) {
			frapDataPanel = new FRAPDataPanel();
			frapDataPanel.setBorder(TAB_LINE_BORDER);
			frapDataPanel.getOverlayEditorPanelJAI().addPropertyChangeListener(this);
			
			Hashtable<String, Cursor> cursorsForROIsHash = new Hashtable<String, Cursor>();
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_CELLROI]);
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_BLEACHROI]);
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_BACKGROUNDROI]);
			frapDataPanel.getOverlayEditorPanelJAI().setCursorsForROIs(cursorsForROIsHash);
			
			OverlayEditorPanelJAI.CustomROIImport importVFRAPROI = new OverlayEditorPanelJAI.CustomROIImport(){
				public boolean importROI(File inputFile) throws Exception{
					try{
//						File inputFile = null;
//						int option = VirtualFrapLoader.openFileChooser.showOpenDialog(null);
//						if (option == JFileChooser.APPROVE_OPTION){
//							inputFile = VirtualFrapLoader.openFileChooser.getSelectedFile();
//						}else{
//							throw UserCancelException.CANCEL_GENERIC;
//						}
						if(!VirtualFrapLoader.filter_vfrap.accept(inputFile)){
							return false;
						}
						String xmlString = XmlUtil.getXMLString(inputFile.getAbsolutePath());
						MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
						DataSetControllerImpl.ProgressListener progressListener =
							new DataSetControllerImpl.ProgressListener(){
								public void updateProgress(double progress) {
									VirtualFrapMainFrame.updateProgress((int)(progress*100));
								}
								public void updateMessage(String message) {
									//ignore
								}
							};
						FRAPStudy importedFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null),progressListener);
						VirtualFrapMainFrame.updateProgress(0);
						ROI roi = getFrapStudy().getFrapData().getCurrentlyDisplayedROI();
						if(importedFrapStudy.getFrapData() != null && importedFrapStudy.getFrapData().getRois() != null){
							if(!importedFrapStudy.getFrapData().getRois()[0].getISize().compareEqual(roi.getISize())){
								throw new Exception(
										"Imported ROI mask size ("+
										importedFrapStudy.getFrapData().getRois()[0].getISize().getX()+","+
										importedFrapStudy.getFrapData().getRois()[0].getISize().getY()+","+
										importedFrapStudy.getFrapData().getRois()[0].getISize().getZ()+")"+
										" does not match current Frap DataSet size ("+
										roi.getISize().getX()+","+
										roi.getISize().getY()+","+
										roi.getISize().getZ()+
										")");
							}
							for (int i = 0; i < importedFrapStudy.getFrapData().getRois().length; i++) {
								getFrapStudy().getFrapData().addReplaceRoi(importedFrapStudy.getFrapData().getRois()[i]);
	//							overlayEditorPanelJAI.firePropertyChange(OverlayEditorPanelJAI.FRAP_DATA_UNDOROI_PROPERTY, null,importedFrapStudy.getFrapData().getRois()[i]);								
							}
							undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
						}
						return true;
					} catch (Exception e1) {
						throw new Exception("VFRAP ROI Import - "+e1.getMessage());
					}
				}
			};
			frapDataPanel.getOverlayEditorPanelJAI().setCustomROIImport(importVFRAPROI);

		}
		return frapDataPanel;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	protected JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_IMAGES, null, getFRAPDataPanel(), null);
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_IniParameters, null, getFRAPParametersPanel(), null);
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_AdjParameters, null, getResultsSummaryPanel(), null);
			jTabbedPane.addTab(FRAPSTUDYPANEL_TABNAME_2DResults, null, getSimResultsViewPanel(), null);
			jTabbedPane.setModel(
				new DefaultSingleSelectionModel(){
					@Override
					public void setSelectedIndex(int index) {
						try{
							String exitTabTitle = (getSelectedIndex() == -1?null:getJTabbedPane().getTitleAt(getSelectedIndex()));
							if(updateTabbedView(exitTabTitle,getJTabbedPane().getTitleAt(index))){
								super.setSelectedIndex(index);
							}
						}catch(Exception e){
							DialogUtils.showWarningDialog(
								FRAPStudyPanel.this, "Can't switch view beacause:\n"+e.getMessage(),
								new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
						}finally{
							getFRAPDataPanel().getOverlayEditorPanelJAI().updateROICursor();
						}

					}
				}
			);
			jTabbedPane.setSelectedIndex(jTabbedPane.indexOfTab(FRAPSTUDYPANEL_TABNAME_IMAGES));
		}
		return jTabbedPane;
	}

	private FRAPSimDataViewerPanel getFRAPSimDataViewerPanel(){
		if(frapSimDataViewerPanel == null){
			frapSimDataViewerPanel = new FRAPSimDataViewerPanel();
			frapSimDataViewerPanel.setBorder(TAB_LINE_BORDER);
		}
		return frapSimDataViewerPanel;
	}
	
	private void checkROIExsitence()throws Exception
	{
		ROI cellROI = getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		ROI bleachROI = getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		ROI bgROI = getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
		String msg = "";
		if(cellROI.isAllPixelsZero())
		{
			msg = msg + "Cell ROI,";
		}
		if(bleachROI.isAllPixelsZero())
		{
			msg = msg + "Bleached ROI,";
		}
		if(bgROI.isAllPixelsZero())
		{
			msg = msg + "Background ROI,";
		}
		if(!msg.equals(""))
		{
			msg = msg.substring(0, msg.length()-1)+" have to be defined before performing any analysis.";
			throw new  Exception(msg);
		}
	}
	
	private boolean checkROIConstraints() throws Exception{
		short[] cellPixels = getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getPixelsXYZ();
		short[] bleachPixels = getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getPixelsXYZ();
		short[] backgroundPixels = getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).getPixelsXYZ();
		boolean bFixedBleach = false;
		boolean bFixedBackground = false;
		for (int i = 0; i < cellPixels.length; i++) {
			if(cellPixels[i] == 0 && bleachPixels[i] != 0){
				bFixedBleach = true;
				bleachPixels[i] = 0;
			}
			if(cellPixels[i] != 0 && backgroundPixels[i] != 0){
				bFixedBackground = true;
				backgroundPixels[i] = 0;
			}
		}
		if(bFixedBackground || bFixedBleach){
			final String FIX_AUTO = "Fix Automatically";
			String result = DialogUtils.showWarningDialog(this,
					(bFixedBleach?"Bleach ROI extends beyond Cell ROI":"")+
					(bFixedBackground &&bFixedBleach?" and" :"")+
					(bFixedBackground?"Background ROI overlaps Cell ROI":"")+
					".  Ensure that the Bleach ROI is completely inside the Cell ROI and the Background ROI is completely outside the Cell ROI.",
					new String[] {FIX_AUTO,UserMessage.OPTION_CANCEL}, FIX_AUTO);
			if(result != null && result.equals(FIX_AUTO)){
				if(bFixedBleach){
					UShortImage ushortImage =
						new UShortImage(bleachPixels,
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBleachROI = new ROI(ushortImage,FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
					getFrapStudy().getFrapData().addReplaceRoi(newBleachROI);
				}
				if(bFixedBackground){
					UShortImage ushortImage =
						new UShortImage(backgroundPixels,
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getOrigin(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getRoiImages()[0].getExtent(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getX(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getY(),
							getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getISize().getZ());
					ROI newBackgroundROI = new ROI(ushortImage,FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
					getFrapStudy().getFrapData().addReplaceRoi(newBackgroundROI);
				}
			}
			return true;
		}
		return false;
	}
	
	private void checkStartIndexforRecovery() throws Exception{
		if(getFrapStudy() != null && getFrapStudy().getFrapModelParameters() != null && getFrapStudy().getFrapModelParameters().getIniModelParameters() != null &&
		   getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery != null){
			
			int startIndexForRecoveryInt = Integer.parseInt(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery);
			if(startIndexForRecoveryInt > 0){
				return;
			}
		}
		throw new Exception("Starting Index for Recovery cannot be the first time point.  PRE-BLEACH data is required for anlaysis. \nPlease go to Initial Parameters tab to set the Starting Index for Recovery.");
	}
	
	private boolean updateTabbedView(String exitTabTitle,String enterTabTitle) throws Exception{
		if(!enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IMAGES) && (getFrapStudy() == null || getFrapStudy().getFrapData() == null)){
			throw new Exception("No data exists.  Use 'File->Open' menu to load data.");
		}
		try{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if(exitTabTitle != null){
				//we check the enter tab, because when load a new file, we have to get back to image tab although the current tab is in half way doing sth.
				if(exitTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IMAGES) && !enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IMAGES))
				{
					applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ROI);
					if(getFrapStudy() != null && getFrapStudy().getFrapData() != null)
					{
						try{
							checkROIExsitence();
						}catch(Exception ex){
							DialogUtils.showErrorDialog(ex.getMessage());
							return false;
						}
					}
					if(getFrapStudy() != null && getFrapStudy().getFrapData() != null && checkROIConstraints()){
						return false;
					}
				}
				else if(exitTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IniParameters) && !enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IMAGES))
				{
					applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_INI_PARAMS);
					if(!getFRAPParametersPanel().checkIniParameters())
					{
						DialogUtils.showErrorDialog("Some of Initial Parameters are empty or in illegal forms.\n Please correct them.");
						return false;
					}
					checkStartIndexforRecovery();
				}else if(exitTabTitle.equals(FRAPSTUDYPANEL_TABNAME_AdjParameters))
				{//save pure diff params only. they are saved in frapmodelparameters, plotting derived results need the saved parameters.
					applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ADJUST_PARAMS);
				}
			}
			if(enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_IniParameters)){
				try{
					getFRAPParametersPanel().refreshFRAPModelParameterEstimates(getFrapStudy().getFrapData());
				}catch(Exception e){
					DialogUtils.showWarningDialog(this, 
							"Some FRAP Model Parameter Estimation help won't be available because:\n"+e.getMessage(),
							new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
				}
			}
			else if(enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_AdjParameters))
			{
				if(!isSetTabIdxFromSpatialAnalysis)
				{
					checkStartIndexforRecovery(); // checks
					refreshBiomodel(); //check all necessary ROIs. BioModel can be null....here  !!!
					CurrentSimulationDataState currentSimulationDataState = null;
					if(getSavedFrapModelInfo() != null){
						currentSimulationDataState = new CurrentSimulationDataState();
					}
					//whenever ROI changed or starting index for recovery changed we'll have to save them and rerun the reference simulation or model sim(if selected)
					if(currentSimulationDataState != null)
					{	
						//to have external files ready
						if(!currentSimulationDataState.areExtDataFileOK ||
						   (currentSimulationDataState.frapChangeInfo != null && currentSimulationDataState.frapChangeInfo.hasROIChanged()))
						{
							//if external files are missing/currupt or ROIs are changed, create keys and save them
							getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
							getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
							getFrapStudy().saveROIsAsExternalData(localWorkspace,
									getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
							getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
									getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
							
						}
						//currentSimulationDataState != null (means old document), one of ROIs and starting idx has been changed, ref/sim must rerun
						if(currentSimulationDataState.frapChangeInfo != null && 
						   (currentSimulationDataState.frapChangeInfo.hasROIChanged() || currentSimulationDataState.frapChangeInfo.hasStartingIdxChanged()))
						{
							//New external data files have been saved already(see above)					
							//ROIs or starting index has changed. We have to update saved frap model info.
							setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(getFrapStudy()));
							//check if a simulation was there before the change  
							String run = "Run it now";
							String later = "No. I'll Run it later";
							if(getSavedFrapModelInfo().savedSimKeyValue != null)//if there was simulation, ask if users want to re-run reference and model simulations at the same time.
							{
								String choice = DialogUtils.showWarningDialog(this, currentSimulationDataState.frapChangeInfo.getROIOrStartingIdxChangeInfo()+" have been changed.\n Reference simulation need to rerun. Do you want to update reaction diffusion simulation as well?",
										                      new String[]{run, later}, run);
								if(choice.equals(run))//users choose to run referecne and model sim at the same time
								{
									if(getResultsSummaryPanel().getReactionDiffusionPanel().getCurrentParameters() == null)
									{   //unfortunately the parameters are not ready for reaction diffusion model sim, have to run reference sim first.
										String choice2 = DialogUtils.showWarningDialog(this, "Some of reaction diffusion parameters are empty/illegal.Cannot run reaction diffusion simulation. \n Please correct them(at Adjust Parameters tab -> Reaction Diffusion Model) and run reaction diffusion simulation later.\n Run reference simulation first?", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
										if(choice2.equals(UserMessage.OPTION_OK))
										{
											spatialAnalysis(null, true);
										}
									}
									else
									{
										runReactionDiffusionModel(getResultsSummaryPanel().getReactionDiffusionPanel().getCurrentParameters(), true);
									}
								}
								else if(choice.equals(later))
								{
									spatialAnalysis(null, true);
								}
							}
							else //no simulation existed before, run reference sim only
							{
								String choice3 = DialogUtils.showWarningDialog(this, currentSimulationDataState.frapChangeInfo.getROIOrStartingIdxChangeInfo()+" have been changed.\n Reference simulation need to rerun.", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
								if(choice3 == UserMessage.OPTION_OK)
								{
									spatialAnalysis(null, true);
								}
							}
						}
						else 
						{   //currentSimulationDataState != null (means document saved), NO ROIs and starting idx changes, 
//							if(!currentSimulationDataState.isRefDataFileOK)
//							{
//								String choice = DialogUtils.showWarningDialog(this, "Reference simulation has never run or Reference simulation files are missing/corrupt. Reference simulation has to re-run.", new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
//								if(choice.equals(UserMessage.OPTION_OK)) //rerun ref sim.
//								{
//									spatialAnalysis(null, true);
//								}
//							}
//							else//current sim status !=null, NO ROI and Starting Idx changes, NO ref file missing
//							{
								if(frapOptData == null)
								{   //have to create frapOptData if it is null.
									spatialAnalysis(null, false);
								}
								else
								{
									return true;
								}
//							}
						}
//						return false;
					}
					else //current sim status is null, model hasn't been saved or run, the ref  must be rerun
					{
						spatialAnalysis(null, true);
     				}
					return false;
				}
			}else if(enterTabTitle.equals(FRAPSTUDYPANEL_TABNAME_2DResults)){
//				checkStartIndexforRecovery();
//				refreshBiomodel();
//				CurrentSimulationDataState currentSimulationDataState = new CurrentSimulationDataState();
//				if(currentSimulationDataState.isDataValid()){
				if(areSimulationFilesOKForSavedModel())
				{
					refreshPDEDisplay(DisplayChoice.PDE);
					refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);
				}else{
					final String RUN_SIM = "Run Simulation...";
					String result = DialogUtils.showWarningDialog(this,
							"Simulation Data are not found.\nSimulation needs to be run to view Spatial Results.",
							new String[] {RUN_SIM,UserMessage.OPTION_CANCEL}, RUN_SIM);
					if(result != null && result.equals(RUN_SIM)){
						runReactionDiffusionModel( getResultsSummaryPanel().getReactionDiffusionPanel().getCurrentParameters(),false);
					}
					return false;
				}
			}
			return true;
		}finally{
			BeanUtils.setCursorThroughout(FRAPStudyPanel.this, Cursor.getDefaultCursor());
			getFRAPDataPanel().getOverlayEditorPanelJAI().updateROICursor();
		}
		
	}

	/**
	 * Changed in Feb, 2008. GridBagLayout to BorderLayout.
	 * Set splitPane and put MultisourcePlotPane on top and the scrollText, equation and radio button at bottom	
	 * @return javax.swing.JPanel	
	 */
	private FRAPParametersPanel getFRAPParametersPanel() {
		if (frapParametersPanel == null) {
			frapParametersPanel = new FRAPParametersPanel();
		}
		return frapParametersPanel;
	}

	/**
	 * This method initializes fitSpatialModelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFitSpatialModelPanel() {
		if (fitSpatialModelPanel == null) {
			fitSpatialModelPanel = new JPanel();
			fitSpatialModelPanel.setLayout(new CardLayout());
			fitSpatialModelPanel.add(getFRAPSimDataViewerPanel(), TABBEDVIEW);
			fitSpatialModelPanel.add(getSplitDataViewers(), SPLITEDVIEW);
		}
		return fitSpatialModelPanel;
	}
	
	/**
	 * This method initializes fitSpatialModelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSimResultsViewPanel() {
		if (simResultsViewPanel == null) {
			simResultsViewPanel = new JPanel();
			JPanel radioPanel = new JPanel();
			radioPanel.setLayout(new FlowLayout());
			tabRadio = new JRadioButton(TABBEDVIEW, true);
			tabRadio.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(((JRadioButton)e.getSource()).isSelected() && getFitSpatialModelPanel() != null)
					{
						CardLayout cl = (CardLayout)(getFitSpatialModelPanel().getLayout());
						cl.show(getFitSpatialModelPanel(), TABBEDVIEW);
					}
					
				}
			});
			splitRadio = new JRadioButton(SPLITEDVIEW);
			splitRadio.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(((JRadioButton)e.getSource()).isSelected() && getFitSpatialModelPanel() != null)
					{
						CardLayout cl = (CardLayout)(getFitSpatialModelPanel().getLayout());
						cl.show(getFitSpatialModelPanel(), SPLITEDVIEW);
					}
					
				}
			});
			ButtonGroup bg = new ButtonGroup();
			bg.add(tabRadio);
			bg.add(splitRadio);
			radioPanel.add(tabRadio);
			radioPanel.add(splitRadio);
			simResultsViewPanel.setLayout(new BorderLayout());
			simResultsViewPanel.add(radioPanel, BorderLayout.NORTH);
			simResultsViewPanel.add(getFitSpatialModelPanel(), BorderLayout.CENTER);
		}
		return simResultsViewPanel;
	}
	
	private ResultsSummaryPanel getResultsSummaryPanel(){
		if(resultsSummaryPanel == null){
			resultsSummaryPanel = new ResultsSummaryPanel();
		}
		return resultsSummaryPanel;
	}
	
	private FRAPStudy getFrapStudy() {
		return frapStudy;
	}

	public void setFrapStudy(final FRAPStudy argFrapStudy,boolean bNew) {
		SwingUtilities.invokeLater(new Runnable(){public void run(){
			getResultsSummaryPanel().clearData();//spatialAnalysisList = null;
		}});
		VirtualFrapMainFrame.enableSave(!(argFrapStudy == null || argFrapStudy.getFrapData() == null));
		try{
			undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
		}catch(Exception e){
			e.printStackTrace();
		}
		FRAPStudy oldFrapStudy = this.frapStudy;
		if(oldFrapStudy != null){
			oldFrapStudy.removePropertyChangeListener(this);
		}
		this.frapStudy = argFrapStudy;
		frapStudy.addPropertyChangeListener(this);
		getFRAPDataPanel().setFrapStudyNew(frapStudy,bNew);
		getResultsSummaryPanel().setFrapStudy(argFrapStudy);
	}
	
	protected void crop(Rectangle cropRectangle) throws ImageException {
		if (getFrapStudy() == null || getFrapStudy().getFrapData()==null){
			return;
		}
		getFRAPDataPanel().getOverlayEditorPanelJAI().saveUserChangesToROI();
		FRAPData frapData = getFrapStudy().getFrapData();
		FRAPData newFrapData = frapData.crop(cropRectangle);
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(newFrapData);
		newFrapStudy.setXmlFilename(getFrapStudy().getXmlFilename());
		newFrapStudy.setFrapDataExternalDataInfo(getFrapStudy().getFrapDataExternalDataInfo());
		newFrapStudy.setRoiExternalDataInfo(getFrapStudy().getRoiExternalDataInfo());
		newFrapStudy.setStoredRefData(getFrapStudy().getStoredRefData());
		setFrapStudy(newFrapStudy,false);
	}
	
	public void save(KeyValue argKey) throws Exception {
		if(SwingUtilities.isEventDispatchThread()){
			throw new Exception("Save not EventDispatchThread");
		}
		if(getFrapStudy().getFrapData() != null){
			saveAsInternal(getFrapStudy().getXmlFilename(), argKey);
//			try{
//				refreshBiomodel();
//			}catch(Exception e){
//				//save whatever we have
//			}
//			AsynchClientTask saveTask = createSaveTask(false,true);
//			ClientTaskDispatcher.dispatch(
//				this, new Hashtable<String, Object>(),
//				new AsynchClientTask[] {saveTask}, false);
		}else{
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				DialogUtils.showErrorDialog("No FRAP Data exists to save");
			}});
		}
	}
	
	public boolean saveAs(KeyValue argKey) throws Exception{
		if(SwingUtilities.isEventDispatchThread()){
			throw new Exception("SaveAs not EventDispatchThread");
		}
		if(getFrapStudy().getFrapData() != null){
			if(getJTabbedPane().getTitleAt(getJTabbedPane().getSelectedIndex()).equals(FRAPSTUDYPANEL_TABNAME_2DResults) ||
				getJTabbedPane().getTitleAt(getJTabbedPane().getSelectedIndex()).equals(FRAPSTUDYPANEL_TABNAME_AdjParameters)){
				//SaveASNew has empty sim data so can't be viewing these tabs after save
				final String CONTINUE_SAVING = "Switch and continue SaveAsNew";
				String result = DialogUtils.showWarningDialog(this,
						"Simulation Data will be empty for the new document.  Switch to '"+FRAPSTUDYPANEL_TABNAME_IMAGES+"' tab before saving?" ,
						new String[] {CONTINUE_SAVING,UserMessage.OPTION_CANCEL}, CONTINUE_SAVING);
				if(result == null || !result.equals(CONTINUE_SAVING)){
					return false;
				}
				SwingUtilities.invokeAndWait(
					new Runnable(){
						public void run(){getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_IMAGES));}
					}
				);
			}
			saveAsInternal(null, argKey);
//			try{
//				refreshBiomodel();
//			}catch(Exception e){
//				//Save whatever we have
//			}
//			AsynchClientTask saveTask = createSaveTask(true,true);
//			ClientTaskDispatcher.dispatch(
//				this, new Hashtable<String, Object>(),
//				new AsynchClientTask[] {saveTask}, false);
		}else{
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				DialogUtils.showErrorDialog("No FRAP Data exists to save");
			}});
		}
		return true;
	}
	private void applyUserChangesToCurrentFRAPStudy(int applyUserChangeFlags) throws Exception{
		if((applyUserChangeFlags&USER_CHANGES_FLAG_ROI) != 0){
			getFRAPDataPanel().saveROI();
		}
		if((applyUserChangeFlags&USER_CHANGES_FLAG_INI_PARAMS) != 0){
			getFRAPParametersPanel().insertFRAPIniModelParametersIntoFRAPStudy(getFrapStudy());	
		}
		if((applyUserChangeFlags&USER_CHANGES_FLAG_ADJUST_PARAMS) != 0){
			getResultsSummaryPanel().insertPureDiffusionParametersIntoFRAPStudy(getFrapStudy());
		}
	}
	private void saveAsInternal(String saveToFileName, KeyValue argKeyValue) throws Exception {

		applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ALL);
		boolean bSaveAs = saveToFileName == null;
		File outputFile = null;
		if(bSaveAs){
			final int[] retvalArr = new int[1];
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				retvalArr[0] = VirtualFrapLoader.saveFileChooser.showSaveDialog(FRAPStudyPanel.this);
			}});
			if (retvalArr[0] == JFileChooser.APPROVE_OPTION){
				String outputFileName = VirtualFrapLoader.saveFileChooser.getSelectedFile().getPath();
				outputFile = new File(outputFileName);
				if(!VirtualFrapLoader.filter_vfrap.accept(outputFile)){
					if(outputFile.getName().indexOf(".") == -1){
						outputFile = new File(outputFile.getParentFile(),outputFile.getName()+"."+VirtualFrapLoader.VFRAP_EXTENSION);
					}else{
						throw new Exception("Virtual FRAP document names must have an extension of ."+VirtualFrapLoader.VFRAP_EXTENSION);
					}
				}
			}else{
				throw UserCancelException.CANCEL_GENERIC;
			}
		}else{
			outputFile = new File(saveToFileName);
		}
		if(bSaveAs && outputFile.exists()){
			if(Compare.isEqualOrNull(outputFile.getAbsolutePath(), getFrapStudy().getXmlFilename())){
				throw new Exception("File name is same.  Use 'Save' to update current document file.");
			}
			final String OK_OPTION = "OK";
			final String[] resultArr = new String[1];
			final File outputFileFinal = outputFile;
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				resultArr[0] = DialogUtils.showWarningDialog(FRAPStudyPanel.this, "OverWrite file\n"+outputFileFinal.getAbsolutePath(),
						new String[] {OK_OPTION,"Cancel"}, "Cancel");
			}});
			if(!resultArr[0].equals(OK_OPTION)){
				throw UserCancelException.CANCEL_GENERIC;
			}
			//Remove overwritten vfrap document external and simulation files
			try{
				MicroscopyXmlReader.ExternalDataAndSimulationInfo externalDataAndSimulationInfo =
					MicroscopyXmlReader.getExternalDataAndSimulationInfo(outputFileFinal);
				FRAPStudy.removeExternalDataAndSimulationFiles(
						externalDataAndSimulationInfo.simulationKey,
						(externalDataAndSimulationInfo.frapDataExtDataInfo != null
							?externalDataAndSimulationInfo.frapDataExtDataInfo.getExternalDataIdentifier():null),
						(externalDataAndSimulationInfo.roiExtDataInfo != null
							?externalDataAndSimulationInfo.roiExtDataInfo.getExternalDataIdentifier():null),
						getLocalWorkspace());
			}catch(Exception e){
				System.out.println(
					"Error deleting externalData and simulation files for overwritten vfrap document "+
					outputFileFinal.getAbsolutePath()+"  "+e.getMessage());
				e.printStackTrace();
			}
		}
		saveProcedure(outputFile,bSaveAs,argKeyValue);
	}
	
	private void saveProcedure(File xmlFrapFile,boolean bSaveAsNew, KeyValue simKey) throws Exception{
		VirtualFrapMainFrame.updateStatus("Saving file " + xmlFrapFile.getAbsolutePath()+" ...");
		//save model parameters
		if(saveModelParameters())
		{
			//create biomodel before saving
			getFrapStudy().refreshDependentROIs();
			//key value, if saveProcedure is called from running reaction diffusion, new KeyValue should exist
			KeyValue localSimKey = null;
			if(simKey != null)
			{
				localSimKey = simKey;
			}
			else
			{
				if(bSaveAsNew || getSavedFrapModelInfo() == null || getSavedFrapModelInfo().savedSimKeyValue == null)
				{
					localSimKey = null;
				}
				else 
				{
					localSimKey = getSavedFrapModelInfo().savedSimKeyValue;
				}
			}
			BioModel newBioModel = null;
			try{
				Parameter[] parameters = getResultsSummaryPanel().getReactionDiffusionPanel().getCurrentParameters();
				if(parameters != null)
				{
					newBioModel = FRAPStudy.createNewSimBioModel(
					getFrapStudy(),
					parameters,
					null, 
					localSimKey,
					LocalWorkspace.getDefaultOwner(),
					new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery).intValue());
				}
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
			getFrapStudy().setBioModel(newBioModel);
	
			if(bSaveAsNew || getSavedFrapModelInfo() == null /*|| cleanupSavedSimDataFilesIfNotOK()*/){
				//Create new external data IDs
				getFrapStudy().setFrapDataExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.IMAGE_EXTDATA_NAME));
				getFrapStudy().setRoiExternalDataInfo(FRAPStudy.createNewExternalDataInfo(localWorkspace, FRAPStudy.ROI_EXTDATA_NAME));
				
			}
			
			MicroscopyXmlproducer.writeXMLFile(getFrapStudy(), xmlFrapFile, true,progressListenerZeroToOne,VirtualFrapMainFrame.SAVE_COMPRESSED);
			getFrapStudy().setXmlFilename(xmlFrapFile.getAbsolutePath());
			//create saved model info.
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				try{
				setSavedFrapModelInfo(FRAPStudyPanel.createSavedFrapModelInfo(getFrapStudy()));	
				}catch(Exception e){
					throw new RuntimeException(e.getMessage(),e);
				}
			}});
			VirtualFrapMainFrame.updateStatus("File " + xmlFrapFile.getAbsolutePath()+" has been saved.");
	        VirtualFrapLoader.mf.setMainFrameTitle(xmlFrapFile.getName());
	        VirtualFrapMainFrame.updateProgress(0);
		}
	}
	
	private boolean saveModelParameters() //called from save procedure
	{
		//for initial parameters
		try{
			getFRAPParametersPanel().insertFRAPIniModelParametersIntoFRAPStudy(getFrapStudy());
		}catch(Exception e)
		{
			DialogUtils.showErrorDialog("Illegal inputs in Initial FRAP Model Parameters. \nPlease correct them before saving.");
			return false; 
		}
		try{
			getResultsSummaryPanel().insertPureDiffusionParametersIntoFRAPStudy(getFrapStudy());
		}catch(Exception e)
		{
			DialogUtils.showErrorDialog("Illegal inputs in pure diffusion panel of Adjust Parameters tab . \nPlease correct them before saving.");
			return false; 
		}
		try{
			getResultsSummaryPanel().insertReactionDiffusionParametersIntoFRAPStudy(getFrapStudy());
		}catch(Exception e)
		{
			DialogUtils.showErrorDialog("Illegal inputs in reaction diffusion panel of Adjust Parameters tab . \nPlease correct them before saving.");
			return false; 
		}
		return true;
	}
	
	private boolean cleanupSavedSimDataFilesIfNotOK() throws Exception{
		CurrentSimulationDataState currentSimulationDataState =
			new CurrentSimulationDataState();
		if(!currentSimulationDataState.isDataValid()){
			//remove saved simulation files (they no longer apply to this model)
			ExternalDataIdentifier frapDataExtDataId =
				(getFrapStudy() != null && getFrapStudy().getFrapDataExternalDataInfo() != null
					?getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier():null);
			ExternalDataIdentifier roiExtDataId =
				(getFrapStudy() != null && getFrapStudy().getRoiExternalDataInfo() != null
					?getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier():null);
			KeyValue simulationKeyValue =
				(getSavedFrapModelInfo() != null?getSavedFrapModelInfo().savedSimKeyValue:null);
			FRAPStudy.removeExternalDataAndSimulationFiles(simulationKeyValue,frapDataExtDataId,roiExtDataId,getLocalWorkspace());
			return true;
		}
		return false;
	}
	
	private void clearCurrentLoadState() throws Exception{
		SwingUtilities.invokeAndWait(new Runnable(){public void run(){
			setFrapStudy(new FRAPStudy(), true);						
            VirtualFrapLoader.mf.setMainFrameTitle("");
            getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_IMAGES));
		}});
	}
	protected void load(final File[] inFileArr,final MultiFileImportInfo multiFileImportInfo) throws Exception{
				
		final String inFileDescription =
			(inFileArr.length == 1
				?"file "+inFileArr[0].getAbsolutePath()
				:"files from "+inFileArr[0].getParentFile().getAbsolutePath());

		final AsynchProgressPopup pp =
			new AsynchProgressPopup(
				FRAPStudyPanel.this,
				"Loading FRAP data...",
				"Working...",true,true
		);
		pp.start();
//		final boolean[] bTest = new boolean[1];
//		bTest[0] = true;
		new Thread(new Runnable(){
			public void run(){
//				try{
//					
//				}catch(UserCancelException e){
//					return;
//				}catch(Exception e){
//					//Shouldn't happen
//					DialogUtils.showErrorDialog("Error saving before run simulation\n"+e.getMessage());
//					return;
//				}

				try {
					saveIfNeeded();
//					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//						setFrapStudy(new FRAPStudy(), true);						
//			            VirtualFrapLoader.mf.setMainFrameTitle("");
//			            getJTabbedPane().setSelectedIndex(FRAPStudyPanel.INDEX_TAB_IMAGES);
//					}});

					
					final DataSetControllerImpl.ProgressListener loadFileProgressListener =
						new DataSetControllerImpl.ProgressListener(){
							public void updateProgress(double progress) {
								int percentProgress = (int)(progress*100);
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
							public void updateMessage(String message) {
								//ignore
							}
					};

					final String LOADING_MESSAGE = "Loading "+inFileDescription+"...";
					VirtualFrapMainFrame.updateStatus(LOADING_MESSAGE);
					pp.setMessage(LOADING_MESSAGE);
					
					FRAPStudy newFRAPStudy = null;
					SavedFrapModelInfo newSavedFrapModelInfo = null;
					String newVFRAPFileName = null;
					if(inFileArr.length == 1){
						File inFile = inFileArr[0];
						if(inFile.getName().endsWith("."+VirtualFrapLoader.VFRAP_EXTENSION) ||
							inFile.getName().endsWith(".xml")){
							clearCurrentLoadState();
							String xmlString = XmlUtil.getXMLString(inFile.getAbsolutePath());
							MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
							newFRAPStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null),loadFileProgressListener);
							if(!areExternalDataOK(getLocalWorkspace(),newFRAPStudy.getFrapDataExternalDataInfo(),newFRAPStudy.getRoiExternalDataInfo())){
								newFRAPStudy.setFrapDataExternalDataInfo(null);
								newFRAPStudy.setRoiExternalDataInfo(null);
							}
//							if(!areReferenceDataOK(getLocalWorkspace(),newFRAPStudy.getRefExternalDataInfo()))
//							{
//								newFRAPStudy.setRefExternalDataInfo(null);
//							}
							newSavedFrapModelInfo = FRAPStudyPanel.createSavedFrapModelInfo(newFRAPStudy);//this is based on frapModelParameters
							newVFRAPFileName = inFile.getAbsolutePath();
						}else if(inFile.getName().endsWith(SimDataConstants.LOGFILE_EXTENSION)){
							DataIdentifier[] dataIdentifiers =
								FRAPData.getDataIdentiferListFromVCellSimulationData(inFile, 0);
							String[][] rowData = new String[dataIdentifiers.length][1];
							for (int i = 0; i < dataIdentifiers.length; i++) {
								if(dataIdentifiers[i].getVariableType().equals(VariableType.VOLUME)){
									rowData[i][0] = dataIdentifiers[i].getName();
								}
							}
							int[] selectedIndexArr =
								DialogUtils.showComponentOKCancelTableList(
									getFRAPDataPanel(),
									"Select Volume Variable",
									new String[] {"Volume Variable Name"},
									rowData, ListSelectionModel.SINGLE_SELECTION);
							if(selectedIndexArr != null && selectedIndexArr.length > 0){
								clearCurrentLoadState();
								FRAPData newFrapData = 
									FRAPData.importFRAPDataFromVCellSimulationData(inFile,
										dataIdentifiers[selectedIndexArr[0]].getName(),
										loadFileProgressListener);
								newFRAPStudy = new FRAPStudy();
								newFRAPStudy.setFrapData(newFrapData);
							}else{
								throw UserCancelException.CANCEL_GENERIC;
							}
						}else{//.lsm or other image file extensions
							clearCurrentLoadState();
							ImageLoadingProgress myImageLoadingProgress =
								new ImageLoadingProgress(){
									public void setSubProgress(double mbprog) {
										loadFileProgressListener.updateProgress(mbprog);
									}
							};
//	        				ImageLoadingProgress imgProgress = new ImageLoadingProgress();
//	        				imgProgress.addPropertyChangeListener(getFRAPDataPanel());
	            			ImageDataset imageDataset = ImageDatasetReader.readImageDataset(inFile.getAbsolutePath(), myImageLoadingProgress);
	            			FRAPData newFrapData = FRAPData.importFRAPDataFromImageDataSet(imageDataset);
							newFRAPStudy = new FRAPStudy();
							newFRAPStudy.setFrapData(newFrapData);
						}
					}else{//multiple files
						clearCurrentLoadState();
	    				ImageLoadingProgress imgProgress = new ImageLoadingProgress();
	        			imgProgress.addPropertyChangeListener(getFRAPDataPanel());
	        			ImageDataset imageDataset = ImageDatasetReader.readImageDatasetFromMultiFiles(inFileArr, imgProgress, multiFileImportInfo.isTimeSeries, multiFileImportInfo.timeInterval, multiFileImportInfo.zInterval);
	        			FRAPData newFrapData = new FRAPData(imageDataset, new String[] { FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(),FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()});
//	        			frapData.setOriginalGlobalScaleInfo(null);
						newFRAPStudy = new FRAPStudy();
						newFRAPStudy.setFrapData(newFrapData);
					}

					final FRAPStudy finalNewFrapStudy = newFRAPStudy;
					final SavedFrapModelInfo finalNewSavedFrapModelInfo = newSavedFrapModelInfo;
					final String finalNewVFRAPFileName = newVFRAPFileName;
//					FRAPStudy.InitialModelParameters temIniParameters = null;
//					if((finalNewFrapStudy != null) && (finalNewFrapStudy.getFrapModelParameters() != null) && finalNewFrapStudy.getFrapModelParameters().getIniModelParameters() != null)
//					{
//						temIniParameters = finalNewFrapStudy.getFrapModelParameters().getIniModelParameters();
//					}
//					final FRAPStudy.InitialModelParameters finalIniParameters = temIniParameters;
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						finalNewFrapStudy.setXmlFilename(finalNewVFRAPFileName);
						setFrapStudy(finalNewFrapStudy,true);
						try{
							setSavedFrapModelInfo(finalNewSavedFrapModelInfo);
							applySavedParameters(finalNewFrapStudy);
//							getFRAPParametersPanel().updateSavedParameters(finalIniParameters, finalNewFrapStudy.getFrapData().getImageDataset().getImageTimeStamps());
						}catch(Exception e){
							throw new RuntimeException(e.getMessage());
						}
						
						FRAPStudyPanel.this.refreshUI();			

			            VirtualFrapLoader.mf.setMainFrameTitle((finalNewVFRAPFileName != null?finalNewVFRAPFileName:"New Document"));
					}});

		            VirtualFrapMainFrame.updateStatus("Loaded " + inFileDescription);
		            VirtualFrapMainFrame.updateProgress(0);
		            //after loading new file, we need to set frapOptData to null.
		            frapOptData = null;
				}catch(UserCancelException uce){
					//ignore
				}catch(final Exception e){
					pp.stop();
					try{
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							setFrapStudy(new FRAPStudy(), true);						
				            VirtualFrapLoader.mf.setMainFrameTitle("");
						}});
					}catch(Exception e2){
						e.printStackTrace();
					}
					VirtualFrapMainFrame.updateProgress(0);
					VirtualFrapMainFrame.updateStatus("Failed loading " + inFileDescription+".");
					e.printStackTrace();
					try{
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							DialogUtils.showErrorDialog("Failed loading " + inFileDescription+":\n"+e.getMessage());
						}});
						}catch(Exception e2){
							e2.printStackTrace();
						}				
				}finally{
					pp.stop();
				}
		}}).start();
	}
	
	private void saveIfNeeded() throws Exception{
		if(getFrapStudy() == null || getFrapStudy().getFrapData() == null){
			return;
		}
		boolean bNeedsSave = false;
		FrapChangeInfo frapChangeInfo = null;
		final String DONT_SAVE_CONTINUE = "Don't Save, Continue";
		try{
			frapChangeInfo = getChangesSinceLastSave();//refreshBiomodel();
			bNeedsSave = frapChangeInfo != null && frapChangeInfo.hasAnyChanges();
		}catch(Exception e){
			String errorDecision = PopupGenerator.showWarningDialog(this, null,
				new UserMessage(
					"Current document will be overwritten and can't be saved because:\n"+e.getMessage(),
					new String[] {DONT_SAVE_CONTINUE,UserMessage.OPTION_CANCEL},DONT_SAVE_CONTINUE),
				null);
			if(errorDecision.equals(UserMessage.OPTION_CANCEL)){
				throw UserCancelException.CANCEL_GENERIC;
			}else{
				return;
			}
		}
    	if(bNeedsSave){
    		final String[] saveMessageArr = new String[1];
    		if(getSavedFrapModelInfo() != null){
    			saveMessageArr[0] = "Changes in current document will be overwritten, Save current document?\n"+
    				"Changes include "+frapChangeInfo.getChangeDescription();
    		}else{
    			saveMessageArr[0] = "Current unsaved document will be overwritten,  Save current document?";
    		}
			final String[] resultArr = new String[1];
			SwingUtilities.invokeAndWait(new Runnable(){public void run(){
				resultArr[0] = PopupGenerator.showWarningDialog(FRAPStudyPanel.this, null,
						new UserMessage(saveMessageArr[0],
							new String[] {SAVE_FILE_OPTION,UserMessage.OPTION_SAVE_AS_NEW,DONT_SAVE_CONTINUE,UserMessage.OPTION_CANCEL},SAVE_FILE_OPTION),
						null);
			}});
			if(resultArr[0].equals(SAVE_FILE_OPTION)){
				save(null);
			}else if(resultArr[0].equals(UserMessage.OPTION_SAVE_AS_NEW)){
				saveAs(null);
			}else if(resultArr[0].equals(DONT_SAVE_CONTINUE)){
			}else if(resultArr[0].equals(UserMessage.OPTION_CANCEL)){
				throw UserCancelException.CANCEL_GENERIC;
			}else{
				throw new Exception("Unknown option in saveIfNeeded '"+resultArr[0]+"'");
			}
    	}
	}

	private Boolean areSimulationFilesOKForSavedModel(){
		if(getSavedFrapModelInfo() == null){
			return false;
		}
		String[] EXPECTED_SIM_EXTENSIONS =
			new String[] {
				SimDataConstants.ZIPFILE_EXTENSION,//may be more than 1 for big files
				SimDataConstants.FUNCTIONFILE_EXTENSION,
				".fvinput",
				SimDataConstants.LOGFILE_EXTENSION,
				SimDataConstants.MESHFILE_EXTENSION,
				".meshmetrics",
				".vcg",
				SimDataConstants.FIELDDATARESAMP_EXTENSION,//prebleach avg
				SimDataConstants.FIELDDATARESAMP_EXTENSION//postbleach avg
			};
		File[] simFiles = FRAPStudy.getSimulationFileNames(
			new File(getLocalWorkspace().getDefaultSimDataDirectory()),
			getSavedFrapModelInfo().savedSimKeyValue);
		//prebleach.fdat,postbleach.fdat,.vcg,.meshmetrics,.mesh,.log,.fvinput,.functions,.zip
		if(simFiles == null || simFiles.length < EXPECTED_SIM_EXTENSIONS.length){
			return false;
		}
		for (int i = 0; i < EXPECTED_SIM_EXTENSIONS.length; i++) {
			boolean bFound = false;
			for (int j = 0; j < simFiles.length; j++) {
				if(simFiles[j] != null && simFiles[j].getName().endsWith(EXPECTED_SIM_EXTENSIONS[i])){
					simFiles[j] = null;
					bFound = true;
					break;
				}
			}
			if(!bFound){
				return false;
			}
		}
		return true;
	}
	private static boolean areExternalDataOK(
			LocalWorkspace localWorkspace,ExternalDataInfo imgDataExtDataInfo,ExternalDataInfo roiExtDataInfo){
		if(imgDataExtDataInfo == null || imgDataExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] frapDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					imgDataExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;frapDataExtDataFiles != null && i < frapDataExtDataFiles.length; i++) {
			if(!frapDataExtDataFiles[i].exists()){
				return false;
			}
		}
		if(roiExtDataInfo == null || roiExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] roiDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					roiExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;roiDataExtDataFiles != null && i < roiDataExtDataFiles.length; i++) {
			if(!roiDataExtDataFiles[i].exists()){
				return false;
			}
		}

		return true;
	}
	
	private static boolean areReferenceDataOK(LocalWorkspace localWorkspace,ExternalDataInfo refDataExtDataInfo)
	{
		if(refDataExtDataInfo == null || refDataExtDataInfo.getExternalDataIdentifier() == null){
			return false;
		}
		File[] referenceDataExtDataFiles =
			FRAPStudy.getCanonicalExternalDataFiles(localWorkspace,
					refDataExtDataInfo.getExternalDataIdentifier());
		for (int i = 0;referenceDataExtDataFiles != null && i < referenceDataExtDataFiles.length; i++) {
			if(!referenceDataExtDataFiles[i].exists()){
				return false;
			}
		}
		return true;
	}
	
	protected void runSimulation(final boolean bSpatialAnalysis, final boolean bReferenceSim) throws Exception{
		
		final File simulationDataDir =
			new File(getLocalWorkspace().getDefaultSimDataDirectory());
		if(!simulationDataDir.exists()){
			final String CREATE_OPTION = "Create Directory";
			String result = DialogUtils.showWarningDialog(this,
					"Simulation data directory '"+
					getLocalWorkspace().getDefaultSimDataDirectory()+
					"' does not exits.  Create Simulation data directory now?",
					new String[] {CREATE_OPTION,UserMessage.OPTION_CANCEL}, CREATE_OPTION);
			if(result == null || !result.equals(CREATE_OPTION)){
				return;
			}
			if(!simulationDataDir.mkdirs()){
				DialogUtils.showWarningDialog(this,
						"Failed to create Simulation Data Directory\n"+
						simulationDataDir.getAbsolutePath()+"\n Simulation cannot run.",
						new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
				return;
			}
		}
		//save if the Model has changed
		final Boolean[] bSaveAsNew = new Boolean[] {null};
		try{
			FrapChangeInfo frapChangeInfo = refreshBiomodel();
			boolean bNeedsExtData =
				!areExternalDataOK(getLocalWorkspace(),
					frapStudy.getFrapDataExternalDataInfo(),frapStudy.getRoiExternalDataInfo());
			if(frapChangeInfo.hasAnyChanges() || bNeedsExtData){
				bSaveAsNew[0] = new Boolean(false);
				if(frapChangeInfo.hasAnyChanges()){
		    		String saveMessage = null;
		    		if(getSavedFrapModelInfo() != null){
		    			saveMessage = "Current document has changes that must be saved before running simulation.\n"+
		    				"Changes include "+frapChangeInfo.getChangeDescription();
		    		}else{
		    			saveMessage = "Current unsaved document must be saved before running simulation.";
		    		}

					String result = DialogUtils.showWarningDialog(
						this,
						saveMessage,
						(getSavedFrapModelInfo() == null
							?new String[] {UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL}
							:new String[] {SAVE_FILE_OPTION,UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL})
						
						,UserMessage.OPTION_OK);
					if(result.equals(SAVE_FILE_OPTION)){
						bSaveAsNew[0] = new Boolean(false);
					}else if(result.equals(UserMessage.OPTION_SAVE_AS_NEW)){
						bSaveAsNew[0] = new Boolean(true);
					}else{
						return;
					}
				}
			}
		}catch(Exception e){
			throw new Exception("Error checking save before running simulation:\n"+e.getMessage());
		}

//		final boolean[] genericProgressStopSignal = new boolean[1];

		final AsynchProgressPopup pp =
			new AsynchProgressPopup(
				FRAPStudyPanel.this,
				"Running FRAP Model Simulation",
				"Working...",true,true
		);
		pp.start();
		
		new Thread(new Runnable(){
			public void run(){
				try {
					boolean executed = false;
					if(bSaveAsNew[0] != null){
						final String SAVING_BEFORE_RUN_MESSAGE = "Saving before simulation runs...";
						VirtualFrapMainFrame.updateStatus(SAVING_BEFORE_RUN_MESSAGE);
						pp.setMessage(SAVING_BEFORE_RUN_MESSAGE);
						if(bSaveAsNew[0]){
							executed = saveAs(null);
						}else {
							save(null);
						}
					}
					if(bSaveAsNew[0] != null && bSaveAsNew[0] && !executed) //cancelled saveas, then nothing should happen.
					{
						pp.stop();
					}
					else
					{
						// Reset spatial analysis
						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
							getResultsSummaryPanel().clearData();//spatialAnalysisList = null;
						}});
						final String SAVING_EXT_DATA_MESSAGE = "Saving ROI and initial conditions...";
						VirtualFrapMainFrame.updateStatus(SAVING_EXT_DATA_MESSAGE);
						pp.setMessage(SAVING_EXT_DATA_MESSAGE);
						VirtualFrapMainFrame.updateProgress(0);
//						genericProgress(10,genericProgressStopSignal);
						if(getSavedFrapModelInfo() == null || cleanupSavedSimDataFilesIfNotOK()){
							getFrapStudy().saveROIsAsExternalData(localWorkspace,
								getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
							getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
								getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
						}
//						genericProgressStopSignal[0] = true;
						
						final double RUN_SIM_PROGRESS_FRACTION = (bSpatialAnalysis?.2:1.0);
						DataSetControllerImpl.ProgressListener runSimProgressListener =
							new DataSetControllerImpl.ProgressListener(){
								public void updateProgress(double progress) {
									int percentProgress = (int)(progress*100*RUN_SIM_PROGRESS_FRACTION);
									VirtualFrapMainFrame.updateProgress(percentProgress);
									pp.setProgress(percentProgress);
								}
								public void updateMessage(String message) {
									VirtualFrapMainFrame.updateStatus(message);
									pp.setMessage(message);
								}
						};
	
						final String RUNNING_SIM_MESSAGE = "Running simulation...";
						VirtualFrapMainFrame.updateStatus(RUNNING_SIM_MESSAGE);
						pp.setMessage(RUNNING_SIM_MESSAGE);
						Simulation simulation =
							getFrapStudy().getBioModel().getSimulations()[0];
						FRAPStudy.runFVSolverStandalone(
							simulationDataDir,
							new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
							simulation,
							getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
							getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
							runSimProgressListener);
						
						if(!bSpatialAnalysis){
							pp.stop();
							SwingUtilities.invokeAndWait(new Runnable(){public void run(){
								try{
									VirtualFrapMainFrame.updateStatus("Updating Simulation Data display.");
									refreshPDEDisplay(DisplayChoice.PDE);//upper data viewer has simulated data and masks
									refreshPDEDisplay(DisplayChoice.EXTTIMEDATA);//lower data viewer has original data and masks
									getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_2DResults));
									VirtualFrapMainFrame.updateProgress(0);
									VirtualFrapMainFrame.updateStatus("");
								}catch(Exception e){
									throw new RuntimeException("Error Updating simData display.  "+e.getMessage(),e);
								}
							}});
							return;
						}
						
						final String FINISHED_MESSAGE = "Finished simulation, running spatial analysis...";
						VirtualFrapMainFrame.updateStatus(FINISHED_MESSAGE);
						pp.setMessage(FINISHED_MESSAGE);
//						VirtualFrapMainFrame.updateProgress(100);
//						pp.setProgress(100);
						
						
						DataSetControllerImpl.ProgressListener optimizationProgressListener =
							new DataSetControllerImpl.ProgressListener(){
								public void updateProgress(double progress) {
									if(progress == 1.0){
										VirtualFrapMainFrame.updateProgress(0);
										pp.stop();
										return;
									}
									int percentProgress = (int)(100*(RUN_SIM_PROGRESS_FRACTION+progress*(1-RUN_SIM_PROGRESS_FRACTION)));
									VirtualFrapMainFrame.updateProgress(percentProgress);
									pp.setProgress(percentProgress);
								}
								public void updateMessage(String message) {
									VirtualFrapMainFrame.updateStatus(message);
									pp.setMessage(message);
								}
						};
						
						spatialAnalysis(optimizationProgressListener, bReferenceSim);
						
	
//						SwingUtilities.invokeAndWait(new Runnable(){public void run(){
//						getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_REPORT));
//						VirtualFrapMainFrame.updateProgress(0);
//						VirtualFrapMainFrame.updateStatus("");
//						}});
					}		
				}catch(UserCancelException uce){
					pp.stop();
					return;
				}catch (final Exception e) {
					pp.stop();
//					genericProgressStopSignal[0] = true;
					VirtualFrapMainFrame.updateProgress(0);
					VirtualFrapMainFrame.updateStatus("Error running simulation/spatial analysis: "+e.getMessage());
					e.printStackTrace();
					try{
					SwingUtilities.invokeAndWait(new Runnable(){public void run(){
						DialogUtils.showErrorDialog("Error running simulation/spatial analysis:\n"+e.getMessage());
					}});
					}catch(Exception e2){
						e2.printStackTrace();
					}
				}finally{
//					pp.stop();
				}
			}
		}).start();
	}	
	
	protected void refreshPDEDisplay(DisplayChoice choice) throws Exception {
		Simulation sim = null;
		if (getFrapStudy()==null || getFrapStudy().getBioModel()==null || getFrapStudy().getBioModel().getSimulations()==null){
			return;
		}else{
			sim = getFrapStudy().getBioModel().getSimulations()[0];
		}
		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier());
		}
		PDEDataViewer[] PDEviewers = new PDEDataViewer[]{getPDEDataViewer(), getPDEDataViewer2()};
		PDEDataViewer[] flourViewers = new PDEDataViewer[]{getFlourDataViewer(), getFlourDataViewer2()};
		DataManager dataManager = null;
		if (choice == DisplayChoice.PDE){
			
			for(int j=0; j<PDEviewers.length; j++)
			{
				PDEviewers[j].setSimulation(null);
				PDEviewers[j].setPdeDataContext(null);
				
				PDEviewers[j].setDataIdentifierFilter(
						new PDEPlotControlPanel.DataIdentifierFilter(){
							private String ALL_DATAIDENTIFIERS = "All";
							private String NORM_FLUOR = "Normalized Fluor.";
							private String[] filterSetNames = new String[] {ALL_DATAIDENTIFIERS,NORM_FLUOR};
							public boolean accept(String filterSetName,DataIdentifier dataidentifier) {
								if(dataidentifier.getName().endsWith(MathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION)){
									//Temporary fix for bug that occurs with FieldFunctions in DatasetcontrollerImpl because
									//DSCI requires a database to deal with fieldfunctions.
									//VirtualFRAP has no database connection.
									//Remove _initConc variables saved by solver because they are functions of FieldData
									return false;
								}
								if(filterSetName.equals(ALL_DATAIDENTIFIERS)){
									return true;
								}else if(filterSetName.equals(NORM_FLUOR)){
									return
										dataidentifier.getName().equals(FRAPStudy.SPECIES_NAME_PREFIX_COMBINED) ||
										dataidentifier.getName().equals(FRAPStudy.SPECIES_NAME_PREFIX_IMMOBILE) ||
										dataidentifier.getName().equals(FRAPStudy.SPECIES_NAME_PREFIX_MOBILE)||
										dataidentifier.getName().equals(FRAPStudy.SPECIES_NAME_PREFIX_SLOW_MOBILE)||
										dataidentifier.getName().equals(FRAPStudy.SPECIES_NAME_PREFIX_BINDING_SITE);
								}
								throw new IllegalArgumentException("DataIdentifierFilter: Unknown filterSetName "+filterSetName);
							}
							public String getDefaultFilterName() {
								return NORM_FLUOR;
							}
							public String[] getFilterSetNames() {
								return filterSetNames;
							}
							public boolean isAcceptAll(String filterSetName){
								return false;//return filterSetName.equals(ALL_DATAIDENTIFIERS);
							}
						}
					);
	
				int jobIndex = 0;
				SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
				dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(), simJob.getVCDataIdentifier());
				PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
	
				PDEviewers[j].setSimulation(sim);
				PDEviewers[j].setPdeDataContext(pdeDataContext);
				SimulationModelInfo simModelInfo =
					new SimulationWorkspaceModelInfo(
							getFrapStudy().getBioModel().getSimulationContext(sim),
							sim.getName()
					);
				PDEviewers[j].setSimulationModelInfo(simModelInfo);
				try {
					getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(PDEviewers[j]);
					((VirtualFrapWindowManager)PDEviewers[j].getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PDEviewers[j].repaint();
		    }
		}
		else if (choice == DisplayChoice.EXTTIMEDATA){
			for(int j=0; j<flourViewers.length; j++)
			{
				flourViewers[j].setSimulation(null);
				flourViewers[j].setPdeDataContext(null);
				
				final String NORM_FLUOR_VAR = "norm_fluor";
				flourViewers[j].setDataIdentifierFilter(
					new PDEPlotControlPanel.DataIdentifierFilter(){
						private String ALL_DATAIDENTIFIERS = "All";
						private String EXP_NORM_FLUOR = "Exp. Norm. Fluor";
						private String[] filterSetNames = new String[] {ALL_DATAIDENTIFIERS,EXP_NORM_FLUOR};
						public boolean accept(String filterSetName,DataIdentifier dataidentifier) {
							if(filterSetName.equals(ALL_DATAIDENTIFIERS)){
								return true;
							}else if(filterSetName.equals(EXP_NORM_FLUOR)){
								return
									dataidentifier.getName().indexOf(NORM_FLUOR_VAR) != -1;
	//								dataidentifier.getName().indexOf("Data2.") == -1 ||
	//								dataidentifier.getName().indexOf(".ring") != -1;
							}
							throw new IllegalArgumentException("DataIdentifierFilter: Unknown filterSetName "+filterSetName);
						}
						public String getDefaultFilterName() {
							return EXP_NORM_FLUOR;
						}
						public String[] getFilterSetNames() {
							return filterSetNames;
						}
						public boolean isAcceptAll(String filterSetName){
							return filterSetName.equals(ALL_DATAIDENTIFIERS);
						}
					}
				);
				
				ExternalDataIdentifier timeSeriesExtDataID = getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier();
				ExternalDataIdentifier maskExtDataID = getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier();
				//add sim
				int jobIndex = 0;
				SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
				
				VCDataIdentifier[] dataIDs = new VCDataIdentifier[] {timeSeriesExtDataID, maskExtDataID, simJob.getVCDataIdentifier()};
				VCDataIdentifier vcDataId = new MergedDataInfo(LocalWorkspace.getDefaultOwner(),dataIDs, MergedDataInfo.createDefaultPrefixNames(dataIDs.length));
				dataManager = new MergedDataManager(getLocalWorkspace().getVCDataManager(),vcDataId);
				PDEDataContext pdeDataContext = new NewClientPDEDataContext(dataManager);
				
				
				
				
				// add function to display normalized fluorence data 
				Expression norm_fluor = new Expression("((Data2.cell_mask*((max((Data1.fluor-Data1.bg_average),0)+1)/Data2.prebleach_avg))*(Data2.cell_mask > 0))");
				AnnotatedFunction[] func = {new AnnotatedFunction("norm_fluor", norm_fluor, null, VariableType.VOLUME, false)};
				boolean isExisted = false;
				for(int i=0; i < pdeDataContext.getFunctions().length; i++)
				{
					if(func[0].getName().equals(pdeDataContext.getFunctions()[i].getName()))
					{
						isExisted = true;
						break;
					}
				}
				if(!isExisted)
				{
					pdeDataContext.addFunctions(func, new boolean[] {false});
					pdeDataContext.refreshIdentifiers();
				}
				
				flourViewers[j].setSimulation(sim);
				flourViewers[j].setPdeDataContext(pdeDataContext);
				SimulationModelInfo simModelInfo =
					new SimulationWorkspaceModelInfo(
							getFrapStudy().getBioModel().getSimulationContext(sim),
							sim.getName()
					);
				flourViewers[j].setSimulationModelInfo(simModelInfo);
				try {
					getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(flourViewers[j]);
					((VirtualFrapWindowManager)flourViewers[j].getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				flourViewers[j].repaint();
			}
		}
	}
	
	private FrapChangeInfo getChangesSinceLastSave() throws Exception{
//		if (getFrapStudy() == null || getFrapStudy().getFrapData() == null){
//			if(getSavedFrapModelInfo() == null){
//				return null;
//			}
//			throw new Exception("Missing Frap Data Unexpected while refreshing BioModel");
//		}
		
		applyUserChangesToCurrentFRAPStudy(USER_CHANGES_FLAG_ALL);
		
		ROI lastCellROI = null;
		ROI lastBleachROI = null;
		ROI lastBackgroundROI = null;
		if(getSavedFrapModelInfo() != null){
			lastCellROI = getSavedFrapModelInfo().lastCellROI;
			lastBleachROI = getSavedFrapModelInfo().lastBleachROI;
			lastBackgroundROI = getSavedFrapModelInfo().lastBackgroundROI;
		}

		boolean bROISameSize =
			Compare.isEqualOrNull(
				getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getISize(),
				(lastCellROI == null?null:lastCellROI.getISize()));
		boolean bCellROISame =
			Compare.isEqualOrNull(lastCellROI,getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
		boolean bBleachROISame =
			Compare.isEqualOrNull(lastBleachROI,getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()));
		boolean bBackgroundROISame =
			Compare.isEqualOrNull(lastBackgroundROI,getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()));
		
		String userStartingIndexForRecovery = getFRAPParametersPanel().getUserStartIndexForRecoveryString();
		
		return getResultsSummaryPanel().getReactionDiffusionPanel().createCompleteFRAPChangeInfo(getSavedFrapModelInfo(),
				bCellROISame,bBleachROISame,bBackgroundROISame,bROISameSize, userStartingIndexForRecovery);
	}
	
	protected FrapChangeInfo refreshBiomodel() throws Exception{
		try{

			//check if all the data for generating spatial model are ready.
			FrapChangeInfo frapChangeInfo = getChangesSinceLastSave();

			if(getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).isAllPixelsZero()){
					throw new Exception(VirtualFrapMainFrame.ROIErrorString);
			}
			if(getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).isAllPixelsZero()){
					throw new Exception(VirtualFrapMainFrame.ROIErrorString);
			}
			if(getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).isAllPixelsZero()){
				throw new Exception(VirtualFrapMainFrame.ROIErrorString);
			}
			Point internalVoidLocation =
				ROI.findInternalVoid(getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
			if(internalVoidLocation != null){
				throw new Exception("CELL ROI has unfilled internal void area at image location "+
						"x="+internalVoidLocation.x+",y="+internalVoidLocation.y+"\n"+
						"Use ROI editing tools to completely define the CELL ROI");
			}
			Point[] distinctCellAreaLocations =
				ROI.checkContinuity(getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
			if(distinctCellAreaLocations != null){
				throw new Exception("CELL ROI has at least 2 discontinuous areas at image locations \n"+
						"x="+distinctCellAreaLocations[0].x+",y="+distinctCellAreaLocations[0].y+
						" and "+
						"x="+distinctCellAreaLocations[1].x+",y="+distinctCellAreaLocations[1].y+"\n"+
				"Use ROI editing tools to define a single continuous CELL ROI");				
			}
			
			getFrapStudy().refreshDependentROIs();
						
			Parameter[] parameters = getResultsSummaryPanel().getReactionDiffusionPanel().getCurrentParameters();
			if(parameters != null)
			{
				BioModel newBioModel = FRAPStudy.createNewSimBioModel(
						getFrapStudy(),
						parameters,
						null, 
						(getSavedFrapModelInfo() == null || getSavedFrapModelInfo().savedSimKeyValue == null?null:getSavedFrapModelInfo().savedSimKeyValue),
						LocalWorkspace.getDefaultOwner(),
						new Integer(frapChangeInfo.startIndexForRecoveryString).intValue());
				getFrapStudy().setBioModel(newBioModel);
			}
						
			return frapChangeInfo;

		}catch(Exception e){
			throw e;
		}
	}	

	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(LocalWorkspace arg_localWorkspace) {
		this.localWorkspace = arg_localWorkspace;
		getFRAPDataPanel().setLocalWorkspace(localWorkspace);
		getFRAPDataPanel().getOverlayEditorPanelJAI().setDefaultImportDirAndFilters(
			new File(getLocalWorkspace().getDefaultWorkspaceDirectory()),
			new FileFilter[] {VirtualFrapLoader.filter_vfrap});

		try {
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getPDEDataViewer2());
			((VirtualFrapWindowManager)getPDEDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			((VirtualFrapWindowManager)getPDEDataViewer2().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getFlourDataViewer());
			getLocalWorkspace().getDataSetControllerImpl().addDataJobListener(getFlourDataViewer2());
			((VirtualFrapWindowManager)getFlourDataViewer().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
			((VirtualFrapWindowManager)getFlourDataViewer2().getDataViewerManager()).setLocalWorkSpace(getLocalWorkspace());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes PDEDataViewer	
	 * 	
	 * @return cbit.vcell.client.data.PDEDataViewer	
	 */
	private PDEDataViewer getPDEDataViewer() {
		return getFRAPSimDataViewerPanel().getSimulationDataViewer();
	}

	private PDEDataViewer getPDEDataViewer2()
	{
		if (pdeDataViewer == null) {
			pdeDataViewer = new PDEDataViewer();
			VirtualFrapWindowManager dataViewerManager = new VirtualFrapWindowManager();
			try {
				pdeDataViewer.setDataViewerManager(dataViewerManager);
				dataViewerManager.addDataJobListener(pdeDataViewer);
				dataViewerManager.addExportListener(pdeDataViewer);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pdeDataViewer;
	}

	private PDEDataViewer getFlourDataViewer() {
		return getFRAPSimDataViewerPanel().getOriginalDataViewer();
	}
	
	private PDEDataViewer getFlourDataViewer2()
	{
		if (flourDataViewer == null) {
			flourDataViewer = new PDEDataViewer();
			VirtualFrapWindowManager dataViewerManager = new VirtualFrapWindowManager();
			try {
				flourDataViewer.setDataViewerManager(dataViewerManager);
				dataViewerManager.addDataJobListener(flourDataViewer);
				dataViewerManager.addExportListener(flourDataViewer);
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return flourDataViewer;
	}
	//Added Feb, 2008.
	private JSplitPane getSplitDataViewers()
	{
		//Added margins in both viewers to make it look nicer
		JPanel upPanel = new JPanel();
		PDEDataViewer pdeViewer = getPDEDataViewer2();
		JScrollPane upScroll = new JScrollPane(pdeViewer);
		upScroll.setAutoscrolls(true);
		upPanel.setLayout(new BorderLayout());
		upPanel.add(upScroll, BorderLayout.CENTER);
		upPanel.add(new JLabel("  "), BorderLayout.WEST);
		upPanel.add(new JLabel("  "), BorderLayout.EAST);
		upPanel.add(new JLabel(" "), BorderLayout.NORTH);
		upPanel.add(new JLabel(" "), BorderLayout.SOUTH);
		upPanel.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),"Simulation Results", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));
		
		
		JPanel botPanel = new JPanel();		
		PDEDataViewer fluorViewer = getFlourDataViewer2();
		JScrollPane botScroll = new JScrollPane(fluorViewer);
		botPanel.setAutoscrolls(true);
		botPanel.setLayout(new BorderLayout());
		botPanel.add(botScroll, BorderLayout.CENTER);
		botPanel.add(new JLabel("  "), BorderLayout.WEST);
		botPanel.add(new JLabel("  "), BorderLayout.EAST);
		botPanel.add(new JLabel(" "), BorderLayout.NORTH);
		botPanel.add(new JLabel(" "), BorderLayout.SOUTH);
		botPanel.setBorder(new TitledBorder(new LineBorder(new Color(168,168,255)),"Original Virtual Microscopy Data and Masks", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 12)));
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upPanel, botPanel);
	    split.setDividerLocation(((int)(VirtualFrapMainFrame.INIT_WINDOW_SIZE.getHeight()/2))-85);
	    split.setDividerSize(4);
	    return split;
	}
	
	private DataManager getDataManager(Simulation sim) throws Exception{
		FieldFunctionArguments[] fieldFunctionArgs = sim.getMathDescription().getFieldFunctionArguments();
		FieldDataIdentifierSpec[] fieldDataIdentifierSpecs = new FieldDataIdentifierSpec[fieldFunctionArgs.length];
		for (int i = 0; i < fieldDataIdentifierSpecs.length; i++) {
			fieldDataIdentifierSpecs[i] = new FieldDataIdentifierSpec(fieldFunctionArgs[i],frapStudy.getFrapDataExternalDataInfo().getExternalDataIdentifier());
		}		
		int jobIndex = 0;
		SimulationJob simJob = new SimulationJob(sim,fieldDataIdentifierSpecs,jobIndex);
		DataManager dataManager = new PDEDataManager(getLocalWorkspace().getVCDataManager(), simJob.getVCDataIdentifier());
		return dataManager;
	}
	
//	private double[] getPreBleachAverageXYZ(Integer startingIndexForRecovery /*boolean bUserDefined*/) throws Exception{
////		int startingIndexForRecovery = -1;
////		if(bUserDefined){
////			startingIndexForRecovery = Integer.parseInt(getFrapStudy().getFrapModelParameters().startIndexForRecovery);
////		}else{
////			startingIndexForRecovery = FRAPDataAnalysis.getRecoveryIndex(getFrapStudy().getFrapData());
////		}
//		if(startingIndexForRecovery != null/* && getFrapStudy().getRoiExternalDataInfo() == null*//* || !bUserDefined*/){
//			return getFrapStudy().calculatePreBleachAverageXYZ(getFrapStudy().getFrapData(),startingIndexForRecovery);
//		}else{
//			VCDataManager testVCDataManager = getLocalWorkspace().getVCDataManager();
//			return testVCDataManager.getSimDataBlock(
//					getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();
//		}
//	}
	public void spatialAnalysis(final DataSetControllerImpl.ProgressListener progressListener,final boolean bRefSimulation) throws Exception{
		
		final AsynchProgressPopup pp =
			(progressListener != null?null:
			new AsynchProgressPopup(
				FRAPStudyPanel.this,
				"Running FRAP Spatial Analysis",
				"Working...",true,true)
			);
		if(pp != null){pp.start();}
		final FRAPStudy fStudy = getFrapStudy();
		new Thread(new Runnable(){public void run(){
			try{
				if(getSavedFrapModelInfo()==null)//has not been saved yet
				{
					String saveMessage = "Current unsaved document must be saved before running simulation.";
					String choice = DialogUtils.showWarningDialog(FRAPStudyPanel.this, saveMessage,
							        new String[] {UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL}, UserMessage.OPTION_SAVE_AS_NEW);
					if(choice.equals(UserMessage.OPTION_CANCEL))
					{
						pp.stop();
						return;
					}
					else
					{
						pp.setMessage("Saving model information...");
						saveAs(null);//for new document the external data info. is created in saveProcedure.
						getFrapStudy().saveROIsAsExternalData(localWorkspace,
								getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
						getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
								getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
					}
				}
				final double SPATIAL_ANLYSIS_PROGRESS_FRACTION = .5;
				final double SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM = 1;
				DataSetControllerImpl.ProgressListener runspatialAnalysisProgressListener =
					new DataSetControllerImpl.ProgressListener(){
						public void updateProgress(double progress) {
							if(pp != null){
								int percentProgress = (int)(progress*100*SPATIAL_ANLYSIS_PROGRESS_FRACTION);
								if(!bRefSimulation && fStudy.getStoredRefData()!= null)
								{
									percentProgress = (int)(progress*100*SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM);
								}
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
							if(progressListener != null){
								if(!bRefSimulation && fStudy.getStoredRefData()!= null)
								{
									progressListener.updateProgress(progress*SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM);
								}
								else
								{
									progressListener.updateProgress(progress*SPATIAL_ANLYSIS_PROGRESS_FRACTION);
								}
							}
						}
						public void updateMessage(String message) {
							if(pp != null){
								VirtualFrapMainFrame.updateStatus(message);
								pp.setMessage(message);
							}
							if(progressListener != null){
								progressListener.updateMessage(message);
							}
						}
				};

//				VCDataManager testVCDataManager = getLocalWorkspace().getVCDataManager();
				//the prebleachAverage
				final int startIndexForRecovery = new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery).intValue();
				double[] prebleachAverage = FRAPStudy.calculatePreBleachAverageXYZ(getFrapStudy().getFrapData(), startIndexForRecovery);
//				double[] prebleachAverage = testVCDataManager.getSimDataBlock(
//						getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(), "prebleach_avg", 0).getData();

//				double[] prebleachAverage = getPreBleachAverageXYZ(null);
				
//				simulationDataManager = getDataManager(frapSimulation);
				DataManager simulationDataManager = null;
				if(frapStudy.getBioModel() != null && frapStudy.getBioModel().getSimulations()!=null &&
						frapStudy.getBioModel().getSimulations().length > 0)
				{
					Simulation frapSimulation = frapStudy.getBioModel().getSimulations()[0];
					simulationDataManager = getDataManager(frapSimulation);
					try{
						if(simulationDataManager.getDataSetTimes() == null)
						{
							simulationDataManager = null;
						}
					}catch(Exception e)
					{
						simulationDataManager = null;
					}
				}
				
				
//				if(frapStudy.getBioModel() != null && frapStudy.getBioModel().getSimulations()!=null && )
//				{	
//					Simulation frapSimulation = frapStudy.getBioModel().getSimulations()[0];
//					simulationDataManager = getDataManager(frapSimulation);
//				}
//				
				final double[] frapDataTimeStamps = getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
				FRAPStudy.SpatialAnalysisResults spatialAnalysisResults = null; 
				if(getFrapStudy().getFrapModelParameters() != null && getFrapStudy().getFrapModelParameters().getReacDiffModelParameters()!=null)
				{
					spatialAnalysisResults =
						FRAPStudy.spatialAnalysis(
							simulationDataManager,
							startIndexForRecovery,
							frapDataTimeStamps[startIndexForRecovery],
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().freeDiffusionRate,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().freeMobileFraction,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().complexDiffusionRate,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().complexMobileFraction,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().monitorBleachRate,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().bsConcentration,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().reacOnRate,
							getFrapStudy().getFrapModelParameters().getReacDiffModelParameters().reacOffRate,
							getFrapStudy().getFrapData(),
							prebleachAverage,
							runspatialAnalysisProgressListener);
				}
				else
				{
					spatialAnalysisResults =
						FRAPStudy.spatialAnalysis(
							simulationDataManager,
							startIndexForRecovery,
							frapDataTimeStamps[startIndexForRecovery],
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							getFrapStudy().getFrapData(),
							prebleachAverage,
							runspatialAnalysisProgressListener);
				}
				
				//Optimization
				DataSetControllerImpl.ProgressListener optimizationProgressListener =
					new DataSetControllerImpl.ProgressListener(){
						public void updateProgress(double progress) {
							if(pp != null){
								int percentProgress = (int)(50+progress*100*(1-SPATIAL_ANLYSIS_PROGRESS_FRACTION));
								if(frapOptData != null && !bRefSimulation)
								{
									percentProgress = (int)(50+progress*100*(1-SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM));
								}
								VirtualFrapMainFrame.updateProgress(percentProgress);
								pp.setProgress(percentProgress);
							}
							if(progressListener != null){
								if(frapOptData != null && !bRefSimulation)
								{
									progressListener.updateProgress(.5+progress*(1-SPATIAL_ANLYSIS_PROGRESS_WITHOUT_REF_SIM));
								}
								else
								{
									progressListener.updateProgress(.5+progress*(1-SPATIAL_ANLYSIS_PROGRESS_FRACTION));
								}
							}
						}
						public void updateMessage(String message) {
							if(pp != null){
								VirtualFrapMainFrame.updateStatus(message);
								pp.setMessage(message);
							}
							if(progressListener != null){
								progressListener.updateMessage(message);
							}
						}
				};
				if(bRefSimulation)
				{
					frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), optimizationProgressListener/*, bRefSimulation*/);
				}
				else
				{
					if(frapOptData == null)
					{
						if(getFrapStudy().getStoredRefData() != null)
						{
							frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), getFrapStudy().getStoredRefData());
						}
						else
						{
							//reference data is null, it is not stored, we have to run ref simulation then
							frapOptData = new FRAPOptData(getFrapStudy(), FRAPOptData.NUM_PARAMS_FOR_ONE_DIFFUSION_RATE, getLocalWorkspace(), optimizationProgressListener/*, bRefSimulation*/);
						}
					}
				}
				if(progressListener != null){
					progressListener.updateProgress(1.0);
				}

				NewFRAPFromParameters newFRAPFromParameters =
					new NewFRAPFromParameters (){
						public void create(Parameter[] newParameters, String arg_modelType) {
							createNewFRAPFromParameters(newParameters, arg_modelType);
						}
					};
					
				//Report initialization //commented in Dec 2008
				getResultsSummaryPanel().setData(newFRAPFromParameters,frapOptData,
						spatialAnalysisResults,frapDataTimeStamps,startIndexForRecovery,
						(simulationDataManager==null)?false:true);
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
					isSetTabIdxFromSpatialAnalysis = true;
					getJTabbedPane().setSelectedIndex(getJTabbedPane().indexOfTab(FRAPSTUDYPANEL_TABNAME_AdjParameters));
					isSetTabIdxFromSpatialAnalysis = false;
				}});
				
				
				VirtualFrapMainFrame.updateProgress(0);
				VirtualFrapMainFrame.updateStatus("Finished Spatial analysis.");
			}catch(final Exception e){
				if(pp != null){pp.stop();}
				VirtualFrapMainFrame.updateProgress(0);
				VirtualFrapMainFrame.updateStatus("Error running spatial analysis: "+e.getMessage());
				e.printStackTrace();
				try{
				SwingUtilities.invokeAndWait(new Runnable(){public void run(){
					DialogUtils.showErrorDialog("Error running spatial analysis:\n"+e.getMessage());
				}});
				}catch(Exception e2){
					e2.printStackTrace();
				}
		}finally{
				if(pp != null){pp.stop();}
			}
		}}).start();
	}

	private void createNewFRAPFromParameters(Parameter[] newParameters, String modelType){
		if(modelType.equals(ResultsSummaryPanel.STR_REACTION_DIFFUSION))
		{
			try {
				runReactionDiffusionModel(newParameters, false);
			} catch (Exception e) {
				DialogUtils.showErrorDialog("Error occurred when running reaction diffusion simulation:" + e.getMessage());
				return;
			}
			return;
		}
//		String msg = "The CURRENT FRAP document will be replaced using new parameters:\n"+
//					 "Primary diffusion Rate="+newParameters[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX].getInitialGuess()+"\n"+
//					 "Primary mobile Fraction="+newParameters[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX].getInitialGuess()+"\n"+	
//					 "Monitor Bleach Rate="+newParameters[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess();
//		if(newParameters.length == 5)
//		{
//			msg = msg + "\nSecondary diffusion Rate="+newParameters[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess()+"\n"+
//						"Secondary mobile Fraction="+newParameters[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess();
//			            
//		}
//		msg = msg + "\n\nSimulation needs to be updated with new parameters...";
//		String result = DialogUtils.showWarningDialog(this,msg,new String[] {UserMessage.OPTION_OK,UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
//
//		if(result == null || !result.equals(UserMessage.OPTION_OK)){
//			return;
//		}
//		
////		FRAPModelParameters origFRAPModelParameters = getFrapStudy().getFrapModelParameters();
//		try {
//			String secRate = null;
//			String secFraction = null;
//			if(newParameters.length == 5)
//			{
//				secRate = newParameters[FRAPOptData.TWODIFFRATES_SLOW_DIFFUSION_RATE_INDEX].getInitialGuess()+"";
//				secFraction = newParameters[FRAPOptData.TWODIFFRATES_SLOW_MOBILE_FRACTION_INDEX].getInitialGuess()+"";
//			}
// 			getFRAPParametersPanel().changeCoreFRAPModelParameters( 
//					newParameters[FRAPOptData.ONEDIFFRATE_DIFFUSION_RATE_INDEX].getInitialGuess()+"",
//					newParameters[FRAPOptData.ONEDIFFRATE_MOBILE_FRACTION_INDEX].getInitialGuess()+"",
//					newParameters[FRAPOptData.ONEDIFFRATE_BLEACH_WHILE_MONITOR_INDEX].getInitialGuess()+"",
//					secRate,
//					secFraction
//			);
//			runSimulation(true, false);
//		} catch (Exception e) {
////			getFrapStudy().setFrapModelParameters(origFRAPModelParameters);
//			if(!(e instanceof UserCancelException)){
//				e.printStackTrace();
//				DialogUtils.showErrorDialog("Creating New FRAP failed.  "+e.getMessage());
//			}
//		}

	}
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == getFRAPDataPanel().getOverlayEditorPanelJAI()){
			if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
				try {
					crop((Rectangle) evt.getNewValue());
				} catch (Exception e) {
					PopupGenerator.showErrorDialog("Error Cropping:\n"+e.getMessage());
				}
			}else if(evt.getPropertyName().equals(OverlayEditorPanelJAI.FRAP_DATA_AUTOROI_PROPERTY)){
				if(!getFrapStudy().getFrapData().getCurrentlyDisplayedROI().getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()) &&
					getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).isAllPixelsZero()
					){
					DialogUtils.showInfoDialog("Define '"+OverlayEditorPanelJAI.WHOLE_CELL_AREA_TEXT+"'"+
							" ROI using ROI Tools or '"+OverlayEditorPanelJAI.ROI_ASSIST_TEXT+"'"+
							" before using '"+OverlayEditorPanelJAI.ROI_ASSIST_TEXT+"' to define Bleach or Backgroun ROIs");
					return;
				}
				JDialog roiDialog = new JDialog((JFrame)BeanUtils.findTypeParentOfComponent(this, JFrame.class));
				roiDialog.setTitle("Create Region of Interest (ROI) using intensity thresholding");
				roiDialog.setModal(true);
				ROIAssistPanel roiAssistPanel = new ROIAssistPanel();
				ROI originalROI = null;
				try{
					originalROI = new ROI(getFrapStudy().getFrapData().getCurrentlyDisplayedROI());
				}catch(Exception e){
					e.printStackTrace();
					//can't happen
				}
				roiAssistPanel.init(roiDialog,originalROI,
						getFrapStudy().getFrapData(),getFRAPDataPanel().getOverlayEditorPanelJAI());
				roiDialog.setContentPane(roiAssistPanel);
				roiDialog.pack();
				roiDialog.setSize(400,200);
				ZEnforcer.showModalDialogOnTop(roiDialog, this);
				
				if(!originalROI.compareEqual(getFrapStudy().getFrapData().getCurrentlyDisplayedROI())){
					final ROI finalOriginalROI = originalROI;
					undoableEditSupport.postEdit(
						new AbstractUndoableEdit(){
							public boolean canUndo() {
								return true;
							}
							public String getUndoPresentationName() {
								return "ROI Threshold "+finalOriginalROI.getROIName();
							}
							public void undo() throws CannotUndoException {
								super.undo();
								getFrapStudy().getFrapData().addReplaceRoi(finalOriginalROI);
							}
						}
					);					
				}else{
					undoableEditSupport.postEdit(CLEAR_UNDOABLE_EDIT);
				}
			}
		}
	}

	private void refreshUI()
	{
		VirtualFrapMainFrame.enableSave(true);
		//TODO: not sure why we need to do refreshBioModel and clear the model again here???
//		try{
//			refreshBiomodel();
//		}catch(Exception e){
//			getFrapStudy().clearBioModel();
//		}

	}
	
	private void runReactionDiffusionModel(Parameter[] params, boolean bRefSim) throws Exception
	{
		if(params != null)
		{
			String choice = DialogUtils.showWarningDialog(this, 
				   "Are you sure to run reaction diffusion simulation with parameters:\n" + getResultsSummaryPanel().getReactionDiffusionPanel().getFullParamDescritpion(),
				   new String[]{UserMessage.OPTION_YES, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_YES);
			if(choice.equals(UserMessage.OPTION_CANCEL))
			{
				return;
			}
			else
			{
				//check simulation directory
				final boolean runRef = bRefSim;
				final File simulationDataDir =
					new File(getLocalWorkspace().getDefaultSimDataDirectory());
				if(!simulationDataDir.exists()){
					final String CREATE_OPTION = "Create  Directory";
					String result = DialogUtils.showWarningDialog(this,
							"Simulation data directory '"+
							getLocalWorkspace().getDefaultSimDataDirectory()+
							"' does not exits.  Create Simulation data directory now?",
							new String[] {CREATE_OPTION,UserMessage.OPTION_CANCEL}, CREATE_OPTION);
					if(result == null || !result.equals(CREATE_OPTION)){
						return;
					}
					if(!simulationDataDir.mkdirs()){
						DialogUtils.showWarningDialog(this,
								"Failed to create Simulation Data Directory\n"+
								simulationDataDir.getAbsolutePath()+"\n Simulation cannot run.",
								new String[] {UserMessage.OPTION_OK}, UserMessage.OPTION_OK);
						return;
					}
				}
				//biomodel will be refreshed in saveProcedure.	
				//always allow to run new simulation  REMEMBER: to remove old sim files when new sim is done.
				KeyValue aSimKey = LocalWorkspace.createNewKeyValue();
				KeyValue oldSimKey = null;
				if(getFrapStudy() != null && getFrapStudy().getBioModel() != null && getFrapStudy().getBioModel().getSimulations()!=null &&
				   getFrapStudy().getBioModel().getSimulations().length > 0 && getFrapStudy().getBioModel().getSimulations()[0].getVersion()!= null &&
				   getFrapStudy().getBioModel().getSimulations()[0].getVersion().getVersionKey() != null)
				{
					oldSimKey = getFrapStudy().getBioModel().getSimulations()[0].getVersion().getVersionKey();
				}
				//save model 
				final boolean bNewFile = (getFrapStudy().getXmlFilename() == null || getFrapStudy().getXmlFilename().length()<1)?true:false;
				try{
					FrapChangeInfo frapChangeInfo = refreshBiomodel();
					boolean bNeedsExtData =
						!areExternalDataOK(getLocalWorkspace(),
							frapStudy.getFrapDataExternalDataInfo(),frapStudy.getRoiExternalDataInfo());
					if(frapChangeInfo.hasAnyChanges() || bNeedsExtData)
					{//simulation conditions have changed
						String saveMessage = null;
			    		if(bNewFile){
			    			saveMessage = "Current unsaved document must be saved before running simulation.";
			    		}else{
			    			
			    			saveMessage = "Current document has changes must be saved before running simulation.\nChanges include :" + frapChangeInfo.getChangeDescription()+
  			              	(bNeedsExtData?"and external data info. missing":"") + ".";
			    		}
			    		String result = DialogUtils.showWarningDialog(this, saveMessage,
								bNewFile?new String[] {UserMessage.OPTION_SAVE_AS_NEW,UserMessage.OPTION_CANCEL}
								:new String[] {SAVE_FILE_OPTION,UserMessage.OPTION_CANCEL}
								,UserMessage.OPTION_OK);
						if(result.equals(UserMessage.OPTION_CANCEL)){
							return;
						}
					}
				}catch(Exception e){
					throw new Exception("Error checking save before running simulation:\n"+e.getMessage());
				}

				//-------------------------------------------------------------------------
				//run simulation 
				final KeyValue finalOldSimKey = oldSimKey;
				final KeyValue finalSimKey = aSimKey;
				final AsynchProgressPopup pp =
					new AsynchProgressPopup(
						FRAPStudyPanel.this,
						"Running FRAP Model Simulation",
						"Working...",true,true
				);
				pp.start();
				
				new Thread(new Runnable(){
					public void run(){
						try 
						{
							boolean executed = false;
							final String SAVING_BEFORE_RUN_MESSAGE = "Saving before simulation runs...";
							VirtualFrapMainFrame.updateStatus(SAVING_BEFORE_RUN_MESSAGE);
							pp.setMessage(SAVING_BEFORE_RUN_MESSAGE);
							if(bNewFile){
								executed = saveAs(finalSimKey);
							}
							else 
							{
								save(finalSimKey);
							}
							if(bNewFile && !executed) //cancelled saveas, then nothing should happen.
							{
								pp.stop();
							}
							else //new file and saveAs cancelled, should execute the following code
							{
								// Reset spatial analysis
								SwingUtilities.invokeAndWait(new Runnable(){public void run(){
									getResultsSummaryPanel().clearData();//spatialAnalysisList = null;
								}});
								final String SAVING_EXT_DATA_MESSAGE = "Saving ROI and initial conditions...";
								VirtualFrapMainFrame.updateStatus(SAVING_EXT_DATA_MESSAGE);
								pp.setMessage(SAVING_EXT_DATA_MESSAGE);
								VirtualFrapMainFrame.updateProgress(0);
								CurrentSimulationDataState currSimState = null;
								if(getSavedFrapModelInfo() != null)
								{
									currSimState = new CurrentSimulationDataState();
								}
								if(getSavedFrapModelInfo() == null || (currSimState != null && !currSimState.areExtDataFileOK)){
									getFrapStudy().saveROIsAsExternalData(localWorkspace,
										getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
									getFrapStudy().saveImageDatasetAsExternalData(localWorkspace,
										getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),new Integer(getFrapStudy().getFrapModelParameters().getIniModelParameters().startingIndexForRecovery));
								}
								
								final double RUN_SIM_PROGRESS_FRACTION = 0.2;
								DataSetControllerImpl.ProgressListener runSimProgressListener =
									new DataSetControllerImpl.ProgressListener(){
										public void updateProgress(double progress) {
											int percentProgress = (int)(progress*100*RUN_SIM_PROGRESS_FRACTION);
											VirtualFrapMainFrame.updateProgress(percentProgress);
											pp.setProgress(percentProgress);
										}
										public void updateMessage(String message) {
											VirtualFrapMainFrame.updateStatus(message);
											pp.setMessage(message);
										}
								};
			
								final String RUNNING_SIM_MESSAGE = "Running simulation...";
								VirtualFrapMainFrame.updateStatus(RUNNING_SIM_MESSAGE);
								pp.setMessage(RUNNING_SIM_MESSAGE);
								Simulation simulation =
									getFrapStudy().getBioModel().getSimulations()[0];
								FRAPStudy.runFVSolverStandalone(
									simulationDataDir,
									new StdoutSessionLog(LocalWorkspace.getDefaultOwner().getName()),
									simulation,
									getFrapStudy().getFrapDataExternalDataInfo().getExternalDataIdentifier(),
									getFrapStudy().getRoiExternalDataInfo().getExternalDataIdentifier(),
									runSimProgressListener);
										
								final String FINISHED_MESSAGE = "Finished simulation, loading data...";
								VirtualFrapMainFrame.updateStatus(FINISHED_MESSAGE);
								pp.setMessage(FINISHED_MESSAGE);
										
								DataSetControllerImpl.ProgressListener optimizationProgressListener =
									new DataSetControllerImpl.ProgressListener(){
										public void updateProgress(double progress) {
											if(progress == 1.0){
												VirtualFrapMainFrame.updateProgress(0);
												pp.stop();
												return;
											}
											int percentProgress = (int)(100*(RUN_SIM_PROGRESS_FRACTION+progress*(1-RUN_SIM_PROGRESS_FRACTION)));
											VirtualFrapMainFrame.updateProgress(percentProgress);
											pp.setProgress(percentProgress);
										}
										public void updateMessage(String message) {
											VirtualFrapMainFrame.updateStatus(message);
											pp.setMessage(message);
										}
								};
								if(runRef)
								{
									spatialAnalysis(optimizationProgressListener, true);
								}	
								else
								{
									spatialAnalysis(optimizationProgressListener, false);
								}
								//remove old Simulation files
								FRAPStudy.removeExternalDataAndSimulationFiles(finalOldSimKey, null, null, getLocalWorkspace());
							}		
						}catch(UserCancelException uce){
							pp.stop();
							return;
						}catch (final Exception e) {
							pp.stop();
							VirtualFrapMainFrame.updateProgress(0);
							VirtualFrapMainFrame.updateStatus("Error running simulation/spatial analysis: "+e.getMessage());
							e.printStackTrace();
							try{
							SwingUtilities.invokeAndWait(new Runnable(){public void run(){
								DialogUtils.showErrorDialog("Error running simulation/spatial analysis:\n"+e.getMessage());
							}});
							}catch(Exception e2){
								e2.printStackTrace();
							}
						}finally{
	//						pp.stop();
						}
					}
				}).start();
			}
		}
		else
		{
			throw new Exception("Some of reaction diffusion parameters are empty or in illegal forms. \nPlease go to Adjust Parameters tab -> Reaction Ddiffustion Model to correct them.");
		}
	}

	private boolean areReactionDiffusionParametersChangedSinceLastRun(Parameter[] parameters) throws NumberFormatException 
	{
		SavedFrapModelInfo savedInfo = getSavedFrapModelInfo();
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_FREE_DIFF_RATE].getInitialGuess() != Double.parseDouble(savedInfo.lastFreeDiffusionrate))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_FREE_FRACTION].getInitialGuess() != Double.parseDouble(savedInfo.lastFreeMobileFraction))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_COMPLEX_DIFF_RATE].getInitialGuess() != Double.parseDouble(savedInfo.lastComplexDiffusionRate))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_COMPLEX_FRACTION].getInitialGuess() != Double.parseDouble(savedInfo.lastComplexMobileFraction))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess() != Double.parseDouble(savedInfo.lastBleachWhileMonitoringRate))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess() != Double.parseDouble(savedInfo.lastBSConcentration))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_ON_RATE].getInitialGuess() != Double.parseDouble(savedInfo.reactionOnRate))
		{
			return true;
		}
		if(parameters[FRAPReactionDiffusionParamPanel.INDEX_OFF_RATE].getInitialGuess() != Double.parseDouble(savedInfo.reactionOffRate))
		{
			return true;
		}
		return false;
	}

	public void applySavedParameters(FRAPStudy frapStudy)
	{
		if(frapStudy != null && frapStudy.getFrapModelParameters() != null)
		{
			if(frapStudy.getFrapData() != null && getFrapStudy().getFrapData().getImageDataset() != null &&
	           getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps() != null)
			{
				if(frapStudy.getFrapModelParameters().getIniModelParameters() != null)
				{
					getFRAPParametersPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getIniModelParameters(),getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps());
				}
				else
				{
					getFRAPParametersPanel().updateSavedParameters(null,getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps());
				}
			}
			else //clear the text fields
			{
				if(frapStudy.getFrapModelParameters().getIniModelParameters() != null)
				{
					getFRAPParametersPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getIniModelParameters(), null);
				}
				else
				{
					getFRAPParametersPanel().updateSavedParameters(null, null);
				}
			}
			if(frapStudy.getFrapModelParameters().getPureDiffModelParameters() != null)
			{
				getResultsSummaryPanel().getPureDiffusionPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getPureDiffModelParameters());
			}
			else //clear the text fields
			{
				getResultsSummaryPanel().getPureDiffusionPanel().updateSavedParameters(null);
			}
			if(frapStudy.getFrapModelParameters().getReacDiffModelParameters() != null)
			{
				getResultsSummaryPanel().getReactionDiffusionPanel().updateSavedParameters(frapStudy.getFrapModelParameters().getReacDiffModelParameters());
			}
			else //clear the text fields
			{
				getResultsSummaryPanel().getReactionDiffusionPanel().updateSavedParameters(null);
			}
		}
		else //model parameters is null, clear all the text fields
		{
			if(frapStudy.getFrapData() != null && getFrapStudy().getFrapData().getImageDataset() != null &&
			           getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps() != null)
			{
				getFRAPParametersPanel().updateSavedParameters(null,getFrapStudy().getFrapData().getImageDataset().getImageTimeStamps());
			}
			else
			{
				getFRAPParametersPanel().updateSavedParameters(null, null);
			}
			getResultsSummaryPanel().getReactionDiffusionPanel().updateSavedParameters(null);
			getResultsSummaryPanel().getPureDiffusionPanel().updateSavedParameters(null);
		}
	}
}
