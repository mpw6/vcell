package cbit.vcell.simdata;

import cbit.gui.PropertyLoader;
import cbit.util.SessionLog;
import cbit.util.StdoutSessionLog;
import cbit.util.User;
import cbit.util.VCDataIdentifier;
import java.io.File;
/**
 * Insert the type's description here.
 * Creation date: (10/10/2003 11:48:54 AM)
 * @author: Anuradha Lakshminarayana
 */
public class MergedDataTest {
/**
 * Insert the method's description here.
 * Creation date: (10/10/2003 11:49:18 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	final User user = new cbit.util.User("anu",new cbit.util.KeyValue("2302355"));
	File userFile = new File("\\\\fs2\\RAID\\vcell\\users");
	VCDataIdentifier vcData1 = new VCDataIdentifier() {
		public String getID() {
			return "SimID_6389673";
		}
		public cbit.util.User getOwner() {
			return user;
		}
	};
	VCDataIdentifier vcData2 = new VCDataIdentifier() {
		public String getID() {
			return "SimID_6383968";
		}
		public cbit.util.User getOwner() {
			return user;
		}
	};
	SimulationData simData1 = null;
	SimulationData simData2 = null;
	// SimulationData simData2 = new SimulationData(user, userFile, "SimID_6384031");

	SessionLog sessionLog = new StdoutSessionLog(PropertyLoader.ADMINISTRATOR_ACCOUNT);
	Cachetable dataCachetable = new Cachetable(10*Cachetable.minute);
	DataSetControllerImpl dscImpl = null;
	try {
		simData1 = new SimulationData(vcData1, userFile);
		simData2 = new SimulationData(vcData2, userFile);
		dscImpl = new DataSetControllerImpl(sessionLog,dataCachetable,userFile);
	} catch (java.io.IOException e) {
		e.printStackTrace(System.out);
	} catch (cbit.util.DataAccessException e) {
		e.printStackTrace(System.out);
	}

//	MergedData MergedData = new MergedData("MergedData1", dscImpl, new SimulationData[] {simData1, simData2});
	try {
		MergedDataInfo mergedInfo = new MergedDataInfo(user, new VCDataIdentifier[] {simData1.getResultsInfoObject(), simData2.getResultsInfoObject()});
		MergedData mergedData = (MergedData)dscImpl.getVCData(mergedInfo);
		ODEDataBlock combinedODEDataBlk = mergedData.getODEDataBlock();
		ODESimData combinedODESimData = combinedODEDataBlk.getODESimData();
		ODESolverResultSetTest.plot(combinedODESimData);
	} catch (cbit.util.DataAccessException e1) {
		e1.printStackTrace(System.out);
	} catch (java.io.IOException e2) {
		e2.printStackTrace(System.out);
	} catch (cbit.vcell.parser.ExpressionException e3) {
		e3.printStackTrace(System.out);
	} 
}
}
