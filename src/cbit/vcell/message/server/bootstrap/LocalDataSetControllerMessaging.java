/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.gui.SpatialSelection;
import cbit.vcell.solvers.CartesianMesh;

/**
 * This interface was generated by a SmartGuide.
 * 
 */
public class LocalDataSetControllerMessaging extends UnicastRemoteObject implements DataSetController {
    private RpcDataServerProxy dataServerProxy = null;
    private SessionLog sessionLog = null;

/**
 * This method was created by a SmartGuide.
 */
public LocalDataSetControllerMessaging (UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, SessionLog sLog, int rmiPort) throws RemoteException {
	super(rmiPort);
	this.sessionLog = sLog;
	this.dataServerProxy = new RpcDataServerProxy(userLoginInfo, vcMessageSession, sessionLog);
}



public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.fieldDataFileOperationSpec(...)");
	try {
		return dataServerProxy.fieldDataFileOperation(fieldDataFileOperationSpec);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String[]
 */
public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,VCDataIdentifier vcdID) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getDataIdentifiers(vcdID=" + vcdID + ")");
	try {
		return dataServerProxy.getDataIdentifiers(outputContext,vcdID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}

/**
 * This method was created by a SmartGuide.
 * @return double[]
 */
public double[] getDataSetTimes(VCDataIdentifier vcdID) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getDataSetTimes(vcdID=" + vcdID + ")");
	try {
		return dataServerProxy.getDataSetTimes(vcdID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2004 1:05:01 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */

public cbit.vcell.solver.AnnotatedFunction[] getFunctions(OutputContext outputContext,org.vcell.util.document.VCDataIdentifier vcdataID) throws org.vcell.util.DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getFunctions(vcdataID=" + vcdataID + ")");
	try {
		return dataServerProxy.getFunctions(outputContext,vcdataID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.plot.PlotData
 * @param varName java.lang.String
 * @param spatialSelection cbit.vcell.simdata.gui.SpatialSelection
 */
public PlotData getLineScan(OutputContext outputContext,VCDataIdentifier vcdID, String varName, double time, SpatialSelection spatialSelection) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getLineScan(vcdID=" + vcdID + ", " + varName + ", " + time + ", at " + spatialSelection+")");
	try {
		return dataServerProxy.getLineScan(outputContext,vcdID, varName, time, spatialSelection);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return int[]
 */
public CartesianMesh getMesh(VCDataIdentifier vcdID) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getMesh(vcdID=" + vcdID + ")");
	try {
		return dataServerProxy.getMesh(vcdID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/14/00 11:20:51 AM)
 * @return cbit.vcell.export.data.ODESimData
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solver.ode.ODESimData getODEData(VCDataIdentifier vcdID) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getODEData(vcdID=" + vcdID + ")");
	try {
		return dataServerProxy.getODEData(vcdID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param time double
 */
public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getParticleDataBlock(vcdID=" + vcdID + ",time=" + time + ")");
	try {
		return dataServerProxy.getParticleDataBlock(vcdID,time);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean getParticleDataExists(VCDataIdentifier vcdID) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getParticleDataExists(vcdID=" + vcdID + ")");
	try {
		return dataServerProxy.getParticleDataExists(vcdID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param time double
 */
public SimDataBlock getSimDataBlock(OutputContext outputContext,VCDataIdentifier vcdID, String varName, double time) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getSimDataBlock(vcdID=" + vcdID + ", varName=" + varName + ", time=" + time + ")");
	try {
		return dataServerProxy.getSimDataBlock(outputContext,vcdID,varName,time);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return double[]
 * @param varName java.lang.String
 * @param index int
 */
public org.vcell.util.document.TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext,VCDataIdentifier vcdID,org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getTimeSeriesValues(vcdID=" + vcdID + ", " + timeSeriesJobSpec + ")");
	try {
		return dataServerProxy.getTimeSeriesValues(outputContext,vcdID,timeSeriesJobSpec);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @param simInfo cbit.vcell.solver.SimulationInfo
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public ExportEvent makeRemoteFile(OutputContext outputContext,ExportSpecs exportSpecs) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.makeRemoteFile(vcdID=" + exportSpecs.getVCDataIdentifier() + ")");
	try {
		return dataServerProxy.makeRemoteFile(outputContext,exportSpecs);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}



public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.doDataOperation(...)");
	try {
		return dataServerProxy.doDataOperation(dataOperation);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}



@Override
public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID) throws DataAccessException {
	sessionLog.print("LocalDataSetControllerMessaging.getDataSetMetadata(vcdataID=" + vcdataID + ")");
	try {
		return dataServerProxy.getDataSetMetadata(vcdataID);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}



@Override
public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID, String[] variableNames) throws DataAccessException, RemoteException {
	sessionLog.print("LocalDataSetControllerMessaging.getDataSetMetadata(vcdataID=" + vcdataID + ")");
	try {
		return dataServerProxy.getDataSetTimeSeries(vcdataID, variableNames);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}

}
