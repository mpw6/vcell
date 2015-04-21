/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.solver.nfsim.NFsimXMLWriter;
import org.vcell.solver.smoldyn.SmoldynFileWriter;
import org.vcell.util.FileUtils;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.FileFilters;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.geometry.surface.Node;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.matlab.MatlabOdeFileCoder;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.xml.XmlHelper;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class ExportToXML extends AsynchClientTask {
	
	public ExportToXML() {
		super("Exporting document to XML", TASKTYPE_NONSWING_BLOCKING);
	}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2004 12:29:53 PM)
 */
private String exportMatlab(File exportFile, javax.swing.filechooser.FileFilter fileFilter, MathDescription mathDesc) throws ExpressionException, MathException {
	Simulation sim = new Simulation(mathDesc);
	MatlabOdeFileCoder coder = new MatlabOdeFileCoder(sim);
	java.io.StringWriter sw = new java.io.StringWriter();
	java.io.PrintWriter pw = new java.io.PrintWriter(sw);
	String functionName = exportFile.getName();
	if (functionName.endsWith(".m")){
		functionName = functionName.substring(0,functionName.length()-2);
	}
	if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV6.getDescription())){
		coder.write_V6_MFile(pw,functionName);
	}
	pw.flush();
	pw.close();
	return sw.getBuffer().toString();
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	VCDocument documentToExport = (VCDocument)hashTable.get("documentToExport");
	File exportFile = (File)hashTable.get("exportFile");
	FileFilter fileFilter = (FileFilter)hashTable.get("fileFilter");
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	String resultString = null;
	if (documentToExport instanceof BioModel) {
		BioModel bioModel = (BioModel)documentToExport;
		// check format requested
		if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV6.getDescription())){
			// matlab from application; get application
			Integer chosenSimContextIndex = (Integer)hashTable.get("chosenSimContextIndex");
			SimulationContext chosenSimContext = bioModel.getSimulationContext(chosenSimContextIndex.intValue());
			// regenerate a fresh MathDescription
			MathMapping mathMapping = chosenSimContext.createNewMathMapping();
			MathDescription mathDesc = mathMapping.getMathDescription();
			if(mathDesc != null && !mathDesc.isSpatial() && !mathDesc.isNonSpatialStoch()){
				// do export
				resultString = exportMatlab(exportFile, fileFilter, mathDesc);
			}else{
				throw new Exception("Matlab export failed: NOT an non-spatial deterministic application!");
			}
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {   
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(exportFile);
				documentManager.generatePDF(bioModel, fos);				
			} finally {
				if(fos != null) {
					fos.close();					
				}
			}
			return; 									//will take care of writing to the file as well.
		}
		//Export a simulation to Smoldyn input file, if there are parameter scans
		//in simulation, we'll export multiple Smoldyn input files.
		else if (fileFilter.equals(FileFilters.FILE_FILTER_SMOLDYN_INPUT)) 
		{ 
			Simulation selectedSim = (Simulation)hashTable.get("selectedSimulation");
			if (selectedSim != null) {
				int scanCount = selectedSim.getScanCount();
				if(scanCount > 1) // has parameter scan
				{
					String baseExportFileName = exportFile.getPath().substring(0, exportFile.getPath().indexOf("."));
					for(int i=0; i<scanCount; i++)
					{
						SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, i, null),0);
						// Need to export each parameter scan into a separate file
						String newExportFileName = baseExportFileName + "_" + i + ".smoldynInput";
						exportFile = new File(newExportFileName);
						
						PrintWriter pw = new PrintWriter(exportFile);
						SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
						smf.write();
						pw.close();	
					}
				}
				else if(scanCount == 1)// regular simulation, no parameter scan
				{
					SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, 0, null),0);
					// export the simulation to the selected file
					PrintWriter pw = new PrintWriter(exportFile);
					SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
					smf.write();
					pw.close();
				}
				else
				{
					throw new Exception("Simulation scan count is smaller than 1.");
				}
			}
			return;
												
		} else {
			// convert it if other format
			if (!fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
				// SBML or CellML; get application name
				if ((fileFilter.equals(FileFilters.FILE_FILTER_SBML_12)) || (fileFilter.equals(FileFilters.FILE_FILTER_SBML_21)) || 
					(fileFilter.equals(FileFilters.FILE_FILTER_SBML_22)) || (fileFilter.equals(FileFilters.FILE_FILTER_SBML_23)) || 
					(fileFilter.equals(FileFilters.FILE_FILTER_SBML_24)) || (fileFilter.equals(FileFilters.FILE_FILTER_SBML_31_CORE)) || 
					(fileFilter.equals(FileFilters.FILE_FILTER_SBML_31_SPATIAL)) ) {
					SimulationContext selectedSimContext = (SimulationContext)hashTable.get("selectedSimContext");
					Simulation selectedSim = (Simulation)hashTable.get("selectedSimulation");
					int sbmlLevel = 0;
					int sbmlVersion = 0;
					int sbmlPkgVersion = 0;
					boolean bIsSpatial = false;
					if ((fileFilter.equals(FileFilters.FILE_FILTER_SBML_12))) {
						sbmlLevel = 1;
						sbmlVersion = 2;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_21)) {
						sbmlLevel = 2;
						sbmlVersion = 1;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_22)) {
						sbmlLevel = 2;
						sbmlVersion = 2;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_23)) {
						sbmlLevel = 2;
						sbmlVersion = 3;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_24)) {
						sbmlLevel = 2;
						sbmlVersion = 4;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_31_CORE)) {
						sbmlLevel = 3;
						sbmlVersion = 1;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_31_SPATIAL)) {
						sbmlLevel = 3;
						sbmlVersion = 1;
						sbmlPkgVersion = 1;
						bIsSpatial = true;
					}
					if (selectedSim == null) {
						resultString = XmlHelper.exportSBML(bioModel, sbmlLevel, sbmlVersion, sbmlPkgVersion, bIsSpatial, selectedSimContext, null);
						XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
						return;
					} else {
						for (int sc = 0; sc < selectedSim.getScanCount(); sc++) {
							SimulationJob simJob = new SimulationJob(selectedSim, sc, null);
							resultString = XmlHelper.exportSBML(bioModel, sbmlLevel, sbmlVersion, sbmlPkgVersion, bIsSpatial, selectedSimContext, simJob);
							// Need to export each parameter scan into a separate file 
							String newExportFileName = exportFile.getPath().substring(0, exportFile.getPath().indexOf(".xml")) + "_" + sc + ".xml";
							exportFile.renameTo(new File(newExportFileName));
							XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
						}
						return;
					}
				} else if (fileFilter.equals(FileFilters.FILE_FILTER_BNGL)) {
					RbmModelContainer rbmModelContainer = bioModel.getModel().getRbmModelContainer();
					StringWriter bnglStringWriter = new StringWriter();
					PrintWriter pw = new PrintWriter(bnglStringWriter);
					RbmNetworkGenerator.writeBngl(bioModel, pw);
					resultString = bnglStringWriter.toString();
					pw.close();
					
				} else if (fileFilter.equals(FileFilters.FILE_FILTER_NFSIM)) {
					// TODO: get the first thing we find for now, in the future we'll need to modify ChooseFile 
					//       to only offer the applications / simulations with bngl content
					SimulationContext simContexts[] = bioModel.getSimulationContexts();
					SimulationContext aSimulationContext = simContexts[0];
					Simulation selectedSim = aSimulationContext.getSimulations(0);
					//Simulation selectedSim = (Simulation)hashTable.get("selectedSimulation");
					SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, 0, null),0);
					long randomSeed = 0;	// a fixed seed will allow us to run reproducible simulations
					//long randomSeed = System.currentTimeMillis();
					NFsimSimulationOptions nfsimSimulationOptions = new NFsimSimulationOptions();
					// we get the data we need from the math description
					Element root = NFsimXMLWriter.writeNFsimXML(simTask, randomSeed, nfsimSimulationOptions);
					Document doc = new Document();
					doc.setRootElement(root);
					XMLOutputter xmlOut = new XMLOutputter();
					resultString = xmlOut.outputString(doc);

				} else if (fileFilter.equals(FileFilters.FILE_FILTER_CELLML)) {
					Integer chosenSimContextIndex = (Integer)hashTable.get("chosenSimContextIndex");
					String applicationName = bioModel.getSimulationContext(chosenSimContextIndex.intValue()).getName();
					resultString = XmlHelper.exportCellML(bioModel, applicationName);
					// cellml still uses default character encoding for now ... maybe UTF-8 in the future
				} else if (fileFilter.equals(FileFilters.FILE_FILTER_SEDML)) {
					// export the entire biomodel to a SEDML file (for now, only non-spatial,non-stochastic applns)
					int sedmlLevel = 1;
					int sedmlVersion = 1;
					String sPath = FileUtils.getFullPathNoEndSeparator(exportFile.getAbsolutePath());
					String sFile = FileUtils.getBaseName(exportFile.getAbsolutePath());
					String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
					
					SEDMLExporter sedmlExporter = null;
					if (bioModel instanceof BioModel) {
						sedmlExporter = new SEDMLExporter(bioModel, sedmlLevel, sedmlVersion);
						resultString = sedmlExporter.getSEDMLFile(sPath);
					} else {
						throw new RuntimeException("unsupported Document Type " + bioModel.getClass().getName() + " for SedML export");
					}
					if(sExt.equals("sedx")) {
						sedmlExporter.createManifest(sPath, sFile);
						String sedmlFileName = sPath + FileUtils.WINDOWS_SEPARATOR + sFile + ".sedml";
						XmlUtil.writeXMLStringToFile(resultString, sedmlFileName, true);
						sedmlExporter.addSedmlFileToList(sFile + ".sedml");
						sedmlExporter.addSedmlFileToList("manifest.xml");
						sedmlExporter.createZipArchive(sPath, sFile);
						return;
					} else {
						XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
					}
				}
			} else {
				// if format is VCML, get it from biomodel.
				bioModel.getVCMetaData().cleanupMetadata();
				resultString = XmlHelper.bioModelToXML(bioModel);
				XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
				return;
			}
		}
	} else if (documentToExport instanceof MathModel) {
		MathModel mathModel = (MathModel)documentToExport;
		// check format requested
		if (fileFilter.equals(FileFilters.FILE_FILTER_MATLABV6)){
			//check if it's ODE
			if(mathModel.getMathDescription() != null && 
			  (!mathModel.getMathDescription().isSpatial() && !mathModel.getMathDescription().isNonSpatialStoch())){
				MathDescription mathDesc = mathModel.getMathDescription();
				resultString = exportMatlab(exportFile, fileFilter, mathDesc);
			}else{
				throw new Exception("Matlab export failed: NOT an non-spatial deterministic model.");
			}
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {            
			FileOutputStream fos = new FileOutputStream(exportFile);
			documentManager.generatePDF(mathModel, fos);
			fos.close();
			return;                                                       //will take care of writing to the file as well.
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
			resultString = XmlHelper.mathModelToXML(mathModel);
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_CELLML)) {
			resultString = XmlHelper.exportCellML(mathModel, null);
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_23)) {
			resultString = XmlHelper.exportSBML(mathModel, 2, 3, 0, false, null, null);
		} 
		//Export a simulation to Smoldyn input file, if there are parameter scans
		//in simulation, we'll export multiple Smoldyn input files.
		else if (fileFilter.equals(FileFilters.FILE_FILTER_SMOLDYN_INPUT)) 
		{ 
			Simulation selectedSim = (Simulation)hashTable.get("selectedSimulation");
			if (selectedSim != null) {
				int scanCount = selectedSim.getScanCount();
				if(scanCount > 1) // has parameter scan
				{
					String baseExportFileName = exportFile.getPath().substring(0, exportFile.getPath().indexOf("."));
					for(int i=0; i<scanCount; i++)
					{
						SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, i, null),0);
						// Need to export each parameter scan into a separate file
						String newExportFileName = baseExportFileName + "_" + i + ".smoldynInput";
						exportFile = new File(newExportFileName);
						
						PrintWriter pw = new PrintWriter(exportFile);
						SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
						smf.write();
						pw.close();	
					}
				}
				else if(scanCount == 1)// regular simulation, no parameter scan
				{
					SimulationTask simTask = new SimulationTask(new SimulationJob(selectedSim, 0, null),0);
					// export the simulation to the selected file
					PrintWriter pw = new PrintWriter(exportFile);
					SmoldynFileWriter smf = new SmoldynFileWriter(pw, true, null, simTask, false);
					smf.write();
					pw.close();
				}
				else
				{
					throw new Exception("Simulation scan count is smaller than 1.");
				}
			}
			return;
		}
	} else if (documentToExport instanceof Geometry){
		Geometry geom = (Geometry)documentToExport;
		if (fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {            
			FileOutputStream fos = new FileOutputStream(exportFile);
			documentManager.generatePDF(geom, fos);
			fos.close();
			return;                                                       //will take care of writing to the file as well.
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
			resultString = XmlHelper.geometryToXML(geom);
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_AVS)) {
			FileWriter fileWriter = new java.io.FileWriter(exportFile);
			cbit.vcell.export.AVS_UCD_Exporter.writeUCDGeometryOnly(geom.getGeometrySurfaceDescription(),fileWriter);
			fileWriter.flush();
			fileWriter.close();
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_STL)) {
			cbit.vcell.geometry.surface.StlExporter.writeBinaryStl(geom.getGeometrySurfaceDescription(),exportFile);
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_PLY)) {
			FileWriter fileWriter = new java.io.FileWriter(exportFile);
			writeStanfordPolygon(geom.getGeometrySurfaceDescription(), fileWriter);
			fileWriter.flush();
			fileWriter.close();
		}
	}
	if(resultString != null){
		FileWriter fileWriter = new java.io.FileWriter(exportFile);
		fileWriter.write(resultString);
		fileWriter.flush();
		fileWriter.close();
	}
}

	public static void writeStanfordPolygon(GeometrySurfaceDescription geometrySurfaceDescription,Writer writer) throws IOException{
		int faceCount = geometrySurfaceDescription.getSurfaceCollection().getTotalPolygonCount();
		int vertCount = geometrySurfaceDescription.getSurfaceCollection().getNodes().length;
		writer.write("ply\n");
		writer.write("format ascii 1.0\n");
		writer.write("comment "+geometrySurfaceDescription.getGeometry().getName()+"\n");
		writer.write("element vertex "+vertCount+"\n");
		writer.write("property float x\n");
		writer.write("property float y\n");
		writer.write("property float z\n");
		writer.write("element face "+faceCount+"\n");
		writer.write("property list uchar int vertex_index\n");
		writer.write("end_header\n");
		//write verts x y z
		for (int i = 0; i < vertCount; i++) {
			Node vertex = geometrySurfaceDescription.getSurfaceCollection().getNodes()[i];
			writer.write((float)vertex.getX()+" "+(float)vertex.getY()+" "+(float)vertex.getZ()+"\n");
		}
		//write faces as a set of connected vertex indexes
		for (int i = 0; i < geometrySurfaceDescription.getSurfaceCollection().getSurfaceCount(); i++) {
			for (int j = 0; j < geometrySurfaceDescription.getSurfaceCollection().getSurfaces(i).getPolygonCount(); j++) {
				Quadrilateral quad = (Quadrilateral)geometrySurfaceDescription.getSurfaceCollection().getSurfaces(i).getPolygons(j);
				writer.write(quad.getNodeCount()+"");
				for (int k = 0; k < quad.getNodeCount(); k++) {
					writer.write(" ");
					writer.write(quad.getNodes(k).getGlobalIndex()+"");
				}
				writer.write("\n");
			}
		}
	}
}