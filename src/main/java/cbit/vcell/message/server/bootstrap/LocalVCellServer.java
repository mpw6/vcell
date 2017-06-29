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
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.vcell.util.AuthenticationException;
import org.vcell.util.BeanUtils;
import org.vcell.util.CacheStatus;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.server.ServerInfo;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellServer;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
/**
 * This class was generated by a SmartGuide.
 * 
 */
@SuppressWarnings("serial")
public class LocalVCellServer extends UnicastRemoteObject implements VCellServer {
	private java.util.Vector<VCellConnection> vcellConnectionList = new Vector<VCellConnection>();
	private String hostName = null;
	private AdminDatabaseServer adminDbServer = null;
	private SessionLog sessionLog = null;
	private Cachetable dataCachetable = null;
	private DataSetControllerImpl dscImpl = null;
	private VCMessagingService vcMessagingService = null;
	private ExportServiceImpl exportServiceImpl = null;
	private java.util.Date bootTime = new java.util.Date();
	private SimulationDatabase simulationDatabase = null;
	private ClientTopicMessageCollector clientTopicMessageCollector = null;
	private int rmiPort;

	private long CLEANUP_INTERVAL = TimeUnit.MINUTES.toMillis(10);
//	private long CLEANUP_INTERVAL = 5000;//TimeUnit.MINUTES.toMillis(10);
	private static Logger lg = Logger.getLogger(LocalVCellServer.class);
/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalVCellServer(String argHostName, VCMessagingService vcMessagingService, AdminDatabaseServer dbServer, SimulationDatabase simulationDatabase, int argRmiPort) throws RemoteException, FileNotFoundException {
	super(argRmiPort);
	this.rmiPort = argRmiPort;
	this.hostName = argHostName;
	this.vcMessagingService = vcMessagingService;
	this.sessionLog = new StdoutSessionLog(PropertyLoader.ADMINISTRATOR_ACCOUNT);
	if (this.vcMessagingService!=null){
		clientTopicMessageCollector = new ClientTopicMessageCollector(vcMessagingService,sessionLog);
		clientTopicMessageCollector.init();
	}
	adminDbServer = dbServer;
	this.dataCachetable = new Cachetable(10*Cachetable.minute);
	this.dscImpl = new DataSetControllerImpl(sessionLog,dataCachetable, 
			new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty)), 
			new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty)));
	this.simulationDatabase = simulationDatabase;
//	this.simControllerImpl = new SimulationControllerImpl(sessionLog, this.simulationDatabase, this);
	this.exportServiceImpl = new ExportServiceImpl(sessionLog);
	
	if (vcMessagingService != null) {
		Thread cleanupThread = new Thread() { 
			public void run() {
				setName("CleanupThread");
				cleanupConnections();
			}
		};
		cleanupThread.start();
	}
}

/**
 * This method was created in VisualAge.
 * @param userid java.lang.String
 * @param password java.lang.String
 */
private synchronized void addVCellConnection(UserLoginInfo userLoginInfo) throws RemoteException, java.sql.SQLException, FileNotFoundException {
	if (getVCellConnection0(userLoginInfo) == null) {
		VCellConnection localConn = null;
		if (vcMessagingService == null){
			localConn = new LocalVCellConnection(userLoginInfo, hostName, new StdoutSessionLog(userLoginInfo.getUser().getName()), simulationDatabase, getDataSetControllerImpl(), getExportServiceImpl());
		} else {
			localConn = new LocalVCellConnectionMessaging(userLoginInfo, new StdoutSessionLog(userLoginInfo.getUser().getName()), vcMessagingService, clientTopicMessageCollector, this, rmiPort);
			VCMongoMessage.sendClientConnectionNew(localConn.getUserLoginInfo());
		}
		vcellConnectionList.addElement(localConn);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/16/2004 10:19:42 AM)
 */
public void cleanupConnections() {	
	if (vcMessagingService == null) {
		return;
	}
	
	while (true) {
		try {
			Thread.sleep(CLEANUP_INTERVAL);
		} catch (InterruptedException ex) {
		}

		sessionLog.print("Starting to clean up stale connections...");
		VCellConnection[] connections = new VCellConnection[vcellConnectionList.size()];
		vcellConnectionList.copyInto(connections);

		for (int i = 0; i < connections.length; i++){
			try {
				if (connections[i] instanceof LocalVCellConnectionMessaging) {
					LocalVCellConnectionMessaging messagingConnection = (LocalVCellConnectionMessaging)connections[i];

					if (messagingConnection != null && messagingConnection.isTimeout()) {
						UserLoginInfo userLoginInfo = messagingConnection.getUserLoginInfo();
						VCMongoMessage.sendClientConnectionClosing(userLoginInfo);
						synchronized (this) {
							System.out.println("CLEANUP.  Removing connection from: "+userLoginInfo.getUser().getName());
							vcellConnectionList.remove(messagingConnection);
							messagingConnection.close();							
						}
						sessionLog.print("Removed connection from " + userLoginInfo.getUser());						
					}

				}
			} catch (Throwable ex) {
				sessionLog.exception(ex);
			}
		}
	}
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.AdminDatabaseServer
 */
private AdminDatabaseServer getAdminDatabaseServer() {
	try {
		return adminDbServer;
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.CacheStatus
 */
public CacheStatus getCacheStatus() {
	try {
		return dataCachetable.getCacheStatus();
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.ConnectionPoolStatus
 * @exception java.rmi.RemoteException The exception description.
 */
public User[] getConnectedUsers() {
	try {
		Vector<User> userList = new Vector<User>();
		for (VCellConnection vcConn : vcellConnectionList) {
			if (!userList.contains(vcConn.getUserLoginInfo().getUser())){
				userList.addElement(vcConn.getUserLoginInfo().getUser());
			}
		}
		return (User[])BeanUtils.getArray(userList,User.class);
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.DataSetControllerImpl
 */
public DataSetControllerImpl getDataSetControllerImpl() {
	return dscImpl;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 4:04:58 PM)
 * @return cbit.vcell.export.server.ExportServiceImpl
 */
public ExportServiceImpl getExportServiceImpl() {
	return exportServiceImpl;
}

/**
 * Insert the method's description here.
 * Creation date: (12/9/2002 12:58:00 AM)
 * @return cbit.vcell.server.ServerInfo
 */
public ServerInfo getServerInfo() {
	return new ServerInfo(hostName,getCacheStatus(),getConnectedUsers());
}

///**
// * This method was created in VisualAge.
// * @return cbit.vcell.simdata.DataSetControllerImpl
// */
//SimulationControllerImpl getSimulationControllerImpl() {
//	return simControllerImpl;
//}

/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @throws DataAccessException 
 * @throws RemoteException 
 * @throws AuthenticationException 
 * @throws SQLException 
 * @throws FileNotFoundException 
 * @exception java.lang.Exception The exception description.
 */
public VCellConnection getVCellConnection(UserLoginInfo userLoginInfo) throws RemoteException, DataAccessException, AuthenticationException, FileNotFoundException, SQLException {
	VCellConnection localConnection = null;
	//Authenticate User
	User user = null;
	boolean runningLocal = false; 
	try {
		String h= getClientHost();
		lg.info(h);
		
		try {
			InetAddress ia = InetAddress.getByName(h);
			runningLocal = ia.isSiteLocalAddress();
			if (lg.isInfoEnabled()) {
				lg.info(ia.getHostAddress() + " local check returns " + runningLocal);
			}
		} catch (UnknownHostException e) {
			System.out.println("unknown client host " + h + e.getMessage());
		}
	} catch (ServerNotActiveException e1) {
		runningLocal = true;
		lg.info("Running local because ServerNotActive");
	}
	synchronized (adminDbServer) {
		user = adminDbServer.getUser(userLoginInfo.getUserName(),userLoginInfo.getDigestedPassword(), runningLocal);
	}
	if (user == null){
		throw new AuthenticationException(VCellErrorMessages.getErrorMessage(VCellErrorMessages.AUTHEN_FAIL_MESSAGE, userLoginInfo.getUserName()));
	}
	try{
		userLoginInfo.setUser(user);
	}catch(Exception e){
		throw new DataAccessException(VCellErrorMessages.NETWORK_FAIL_MESSAGE, e);
	}
	//
	// get existing VCellConnection
	//
	localConnection = getVCellConnection0(userLoginInfo);

	//
	// if doesn't exist, create new one
	//
	if (localConnection == null) {
		addVCellConnection(userLoginInfo);
		localConnection = getVCellConnection0(userLoginInfo);
		if (localConnection==null){
			sessionLog.print("LocalVCellServer.getVCellConnecytion("+user.getName()+") unable to create VCellConnection");
			throw new DataAccessException("unable to create VCellConnection");
		}
	}

	//
	//Update UserStat.  Do not fail login if UserStat fails.
	//
	try{
		getAdminDatabaseServer().updateUserStat(userLoginInfo);
	}catch(Exception e){
		e.printStackTrace();
		//Ignore
	}
	return localConnection;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.DataSetController
 * @exception java.lang.Exception The exception description.
 */
private synchronized VCellConnection getVCellConnection0(UserLoginInfo userLoginInfo) {
	//
	// Lookup existing VCellConnections
	//
	for (VCellConnection vcc : vcellConnectionList) {
		if (vcc instanceof LocalVCellConnection){
			LocalVCellConnection lvcc = (LocalVCellConnection)vcc;
			if (lvcc.getUserLoginInfo().getUser().compareEqual(userLoginInfo.getUser()) && lvcc.getUserLoginInfo().getClientId() == userLoginInfo.getClientId()) {
				return lvcc;
			}
		}else if (vcc instanceof LocalVCellConnectionMessaging){
			LocalVCellConnectionMessaging lvccm = (LocalVCellConnectionMessaging)vcc;
			try {
				if (lvccm.getUserLoginInfo().getUser().compareEqual(userLoginInfo.getUser()) && lvccm.getUserLoginInfo().getClientId() == userLoginInfo.getClientId()) {
					return lvccm;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	return null;
}

public Date getBootTime() throws RemoteException {
	return bootTime;
}
}
