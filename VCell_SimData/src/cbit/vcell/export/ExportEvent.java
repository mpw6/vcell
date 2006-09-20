package cbit.vcell.export;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.util.User;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.util.events.MessageData;
import cbit.vcell.util.events.MessageEvent;
import cbit.vcell.util.events.MessageSource;
/**
 * This is the event class to support the cbit.vcell.desktop.controls.ExportListener interface.
 */
public class ExportEvent extends MessageEvent {
	private int eventType = 0;
	private Double progress = null;
	private String format = null;
	private String location = null;
	private User user = null;
	private long jobID = 0L;
	private VCSimulationIdentifier vcSimulationIdentifier = null;

/**
 * ExportEvent constructor comment.
 */
public ExportEvent(Object source, long jobID, User user, VCSimulationIdentifier vcsID, int argEventType, String format, String location, Double argProgress) {
	super(source, new MessageSource(source, vcsID.getID()), new MessageData(argProgress));
	this.eventType = argEventType;
	this.format = format;
	this.location = location;
	this.progress = argProgress;
	this.jobID = jobID;
	this.user = user;
	this.vcSimulationIdentifier = vcsID;
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/01 1:24:16 PM)
 * @return int
 */
public int getEventTypeID() {
	return eventType;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:51:54 PM)
 * @return java.lang.String
 */
public String getFormat() {
	return format;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 5:08:40 PM)
 * @return long
 */
public long getJobID() {
	return jobID;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:51:54 PM)
 * @return java.lang.String
 */
public String getLocation() {
	return location;
}


/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:53:53 PM)
 * @return java.lang.Double
 */
public Double getProgress() {
	return progress;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:50:43 PM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return user;
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 8:59:46 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	return vcSimulationIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (8/28/2002 2:39:37 PM)
 * @return boolean
 */
public boolean isConsumable() {
	if (eventType == EXPORT_PROGRESS) {
		return true;
	}

	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 9:55:32 AM)
 * @return java.lang.String
 */
public String toString() {
	return "ExportEvent: location="
		+ getLocation()
		+ ", jobID="
		+ getJobID()
		+ ", progress=\""
		+ getProgress()
		+ "\", user="
		+ getUser()
		+ ", simID="
		+ (getVCSimulationIdentifier() != null ?
			getVCSimulationIdentifier().getID() : null);
}
}