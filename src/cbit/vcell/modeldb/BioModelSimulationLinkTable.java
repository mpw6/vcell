/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import java.sql.*;

import org.vcell.util.document.KeyValue;

import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class BioModelSimulationLinkTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_biomodelsim";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field bioModelRef 		= new Field("biomodelRef",		"integer",	"NOT NULL "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field simRef			= new Field("simRef",			"integer",	"NOT NULL "+SimulationTable.REF_TYPE);

	private final Field fields[] = {bioModelRef,simRef};
	
	public static final BioModelSimulationLinkTable table = new BioModelSimulationLinkTable();
/**
 * ModelTable constructor comment.
 */
private BioModelSimulationLinkTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 8:08:50 AM)
 * @return cbit.sql.KeyValue
 * @param rset java.sql.ResultSet
 */
public KeyValue getSimulationKey(ResultSet rset) throws SQLException {
	
	KeyValue key = new KeyValue(rset.getBigDecimal(table.simRef.getUnqualifiedColName()));
	if (rset.wasNull()){
		key = null;
	}
	return key;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue bioModelKey, KeyValue simulationKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(bioModelKey+",");
	buffer.append(simulationKey+")");

	return buffer.toString();
}
}