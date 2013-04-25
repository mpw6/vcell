/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.db;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import java.math.BigDecimal;

import org.vcell.util.ComparableObject;
import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.SolverTaskDescription;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatus implements ComparableObject {
	private String simname = null;
	private String userID = null;
	private SimulationJobStatus jobStatus = null;
	private SolverTaskDescription solverTaskDesc = null;
	private Long elapsedTime = null;
	private Integer meshSpecX = null;
	private Integer meshSpecY= null;
	private Integer meshSpecZ = null;
	private KeyValue maxBioModelID = null;
	private KeyValue maxMathModelID = null;

/**
 * SimpleJobStatus constructor comment.
 */
public SimpleJobStatus(String simname, String user, SimulationJobStatus arg_jobStatus, SolverTaskDescription arg_solverTaskDesc, Integer meshSpecX, Integer meshSpecY, Integer meshSpecZ, KeyValue maxBioModelID, KeyValue maxMathModelID) {	
	super();
	this.simname = simname;
	this.userID = user;
	this.jobStatus = arg_jobStatus;
	this.solverTaskDesc = arg_solverTaskDesc;
	this.elapsedTime = null;
	if (getStartDate()!=null){
		if (getEndDate()!=null){
			this.elapsedTime = ((getEndDate().getTime()-getStartDate().getTime()));
		}else if (jobStatus.getSchedulerStatus().isRunning()){
			this.elapsedTime = ((System.currentTimeMillis()-getStartDate().getTime()));
		}
	}
	this.meshSpecX = meshSpecX;
	this.meshSpecY = meshSpecY;
	this.meshSpecZ = meshSpecZ;
	this.maxBioModelID = maxBioModelID;
	this.maxMathModelID = maxMathModelID;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getComputeHost() {
	if (jobStatus == null) {
		return null;
	}	
	return jobStatus.getComputeHost();
}

public SchedulerStatus getSchedulerStatus(){
	return jobStatus.getSchedulerStatus();
}

public boolean hasData(){
	return jobStatus.hasData();
}

public String getSimName(){
	return this.simname;
}

public KeyValue getMaxBioModelID(){
	return this.maxBioModelID;
}

public KeyValue getMaxMathModelID(){
	return this.maxMathModelID;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getEndDate() {
	if (jobStatus == null) {
		return null;
	}
	return jobStatus.getEndDate();
}

public Integer getMeshSpecX(){
	return this.meshSpecX;
}

public Integer getMeshSpecY(){
	return this.meshSpecY;
}

public Integer getMeshSpecZ(){
	return this.meshSpecZ;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public Integer getJobIndex() {
	if (jobStatus == null || jobStatus.getServerID() == null) {
		return null;
	}	
	return new Integer(jobStatus.getJobIndex());
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getServerID() {
	if (jobStatus == null || jobStatus.getServerID() == null) {
		return null;
	}	
	return jobStatus.getServerID().toString();
}


/**
 * Insert the method's description here.
 * Creation date: (7/8/2004 1:29:11 PM)
 * @return java.lang.String
 */
public String getSolverDescriptionVCML() {
	if (solverTaskDesc == null) {
		return "Error: Null Solver Description";
	}
	return solverTaskDesc.getVCML();
}

public String getMeshSampling(){
	if (this.meshSpecX==null){
		return "no mesh";
	}else if (this.meshSpecY!=null){
		if (this.meshSpecZ!=null){
			return "mesh ("+meshSpecX.intValue()+","+meshSpecY.intValue()+","+meshSpecZ.intValue()+") = "+getMeshSize()+" volume elements";
		}else{
			return "mesh ("+meshSpecX.intValue()+","+meshSpecY.intValue()+") = "+getMeshSize()+" volume elements";
		}
	}else{
		return "mesh ("+meshSpecX.intValue()+") = "+getMeshSize()+" volume elements";
	}
}

public long getMeshSize(){
	if (meshSpecX!=null){
		long size = meshSpecX.intValue();
		if (meshSpecY!=null){
			size *= meshSpecY.intValue();
		}
		if (meshSpecZ!=null){
			size *= meshSpecZ.intValue();
		}
		return size;
	}else{
		return 0;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getStartDate() {
	if (jobStatus == null) {
		return null;
	}
	return jobStatus.getStartDate();
}

/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 2:47:11 PM)
 * @return java.lang.String
 */
public String getStatusMessage() {
	return jobStatus.getSimulationMessage().getDisplayMessage();
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getSubmitDate() {
	return jobStatus.getSubmitDate();
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public Integer getTaskID() {
	if (jobStatus == null || jobStatus.getServerID() == null) {
		return null;
	}	
	return new Integer(jobStatus.getTaskID());
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getUserID() {
	return userID;
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 2:54:17 PM)
 * @return cbit.sql.KeyValue
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	return jobStatus.getVCSimulationIdentifier();
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:21:23 PM)
 * @return boolean
 */
public boolean isDone() {
	return jobStatus.getSchedulerStatus().isDone();
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 8:53:02 AM)
 * @return boolean
 */
public boolean isRunning() {
	return jobStatus.getSchedulerStatus().isRunning();
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 10:45:39 AM)
 * @return java.lang.String[]
 */
public Object[] toObjects() {	
	return new Object[] {userID,  new BigDecimal(getVCSimulationIdentifier().getSimulationKey().toString()), getJobIndex(), 
		solverTaskDesc == null || solverTaskDesc.getSolverDescription() == null ? "" : solverTaskDesc.getSolverDescription().getDisplayLabel(), 		
		getStatusMessage(), getComputeHost(), getServerID(), getTaskID(), getSubmitDate(), getStartDate(), getEndDate(),
		elapsedTime, new Long(getMeshSize())};
}


public HtcJobID getHtcJobID() {
	return jobStatus.getSimulationExecutionStatus().getHtcJobID();
}

}
