/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import org.vcell.util.Matchable;


/**
 * Insert the type's description here.
 * Creation date: (10/3/00 2:07:11 PM)
 * @author: 
 */
public class VariableType implements java.io.Serializable, org.vcell.util.Matchable {

	private int type = -1;
	private VariableDomain variableDomain;

	private static final int MIN_PDE_TYPE = 1;
	private static final int MAX_PDE_TYPE = 6;
	
	private static final int UNKNOWN_TYPE = 0;
	private static final int VOLUME_TYPE = 1;
	private static final int MEMBRANE_TYPE = 2;
	private static final int CONTOUR_TYPE = 3;
	private static final int VOLUME_REGION_TYPE = 4;
	private static final int MEMBRANE_REGION_TYPE = 5;
	private static final int CONTOUR_REGION_TYPE = 6;
	private static final int NONSPATIAL_TYPE = 7;
	private static final int POSTPROCESSING_TYPE = 8;
	
	private static final String[] NAMES = {"Unknown","Volume","Membrane","Contour","Volume_Region","Membrane_Region","Contour_Region","Nonspatial","Post_Process"};
	private static final String[] LABEL = {"Unknown","Conc","Density","Density","Conc","Density","Density","Conc","Unknown"};
	private static final String[] UNITS = {"Unknown","uM","molecules/um^2","molecules/um","uM","molecules/um^2","molecules/um","uM","Unknown"};
	
	public static final VariableType UNKNOWN = new VariableType(UNKNOWN_TYPE);
	public static final VariableType VOLUME = new VariableType(VOLUME_TYPE);
	public static final VariableType MEMBRANE = new VariableType(MEMBRANE_TYPE);
	public static final VariableType CONTOUR = new VariableType(CONTOUR_TYPE);
	public static final VariableType VOLUME_REGION = new VariableType(VOLUME_REGION_TYPE);
	public static final VariableType MEMBRANE_REGION = new VariableType(MEMBRANE_REGION_TYPE);
	public static final VariableType CONTOUR_REGION = new VariableType(CONTOUR_REGION_TYPE);
	public static final VariableType NONSPATIAL = new VariableType(NONSPATIAL_TYPE);
	public static final VariableType POSTPROCESSING = new VariableType(POSTPROCESSING_TYPE);
	
	public enum VariableDomain {
		VARIABLEDOMAIN_POSTPROCESSING("PostProcessing"),
		VARIABLEDOMAIN_UNKNOWN("Unknown"),
		VARIABLEDOMAIN_VOLUME("Volume"),
		VARIABLEDOMAIN_MEMBRANE("Membrane"),
		VARIABLEDOMAIN_CONTOUR("Contour"),
		VARIABLEDOMAIN_NONSPATIAL("Nonspatial");
		
		private String name = null;
		private VariableDomain(String arg_name) {
			name = arg_name;
		}
		public String getName() {
			return name;
		}
	}
/**
 * PDEVariableType constructor comment.
 */
private VariableType(int varType) {
	super();
	this.type = varType;
	switch (type) {
	case UNKNOWN_TYPE:
		variableDomain = VariableDomain.VARIABLEDOMAIN_UNKNOWN;
		break;
	case POSTPROCESSING_TYPE:
		variableDomain = VariableDomain.VARIABLEDOMAIN_POSTPROCESSING;
		break;
	case VOLUME_TYPE:
	case VOLUME_REGION_TYPE:
		variableDomain = VariableDomain.VARIABLEDOMAIN_VOLUME;
		break;
	case MEMBRANE_TYPE:
	case MEMBRANE_REGION_TYPE:
		variableDomain = VariableDomain.VARIABLEDOMAIN_MEMBRANE;
		break;
	case CONTOUR_TYPE:
	case CONTOUR_REGION_TYPE:
		variableDomain = VariableDomain.VARIABLEDOMAIN_CONTOUR;
		break;
	case NONSPATIAL_TYPE:
		variableDomain = VariableDomain.VARIABLEDOMAIN_NONSPATIAL;
		break;
	default:
		throw new RuntimeException("Unknown variable type " + type);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2001 9:28:51 PM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(Matchable obj) {
	return equals(obj);
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:42:33 PM)
 */
public boolean equals(Object obj) {
	if (obj == null) {
		return false;
	}
	if (!(obj instanceof VariableType)) {
		return false;
	}
	VariableType pdeVT = (VariableType) obj;
	if (type!=pdeVT.type) {
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:10:25 PM)
 * @return java.lang.String
 */
public String getDefaultLabel() {
	return LABEL[type];
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:10:37 PM)
 * @return java.lang.String
 */
public String getDefaultUnits() {
	return UNITS[type];
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:38:35 PM)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:13:10 PM)
 * @return java.lang.String
 */
public String getTypeName() {
	return NAMES[type];
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:48:55 PM)
 * @return cbit.vcell.simdata.PDEVariableType
 * @param mesh cbit.vcell.solvers.CartesianMesh
 * @param dataLength int
 */
public static final VariableType getVariableTypeFromInteger(int varType) {
	if (varType==VOLUME.type){
		return VOLUME;
	}else if (varType==MEMBRANE.type){
		return MEMBRANE;
	}else if (varType==CONTOUR.type){
		return CONTOUR;
	}else if (varType==VOLUME_REGION.type){
		return VOLUME_REGION;
	}else if (varType==MEMBRANE_REGION.type){
		return MEMBRANE_REGION;
	}else if (varType==CONTOUR_REGION.type){
		return CONTOUR_REGION;
	}else{
		throw new IllegalArgumentException("varType="+varType+" is undefined");
	}
}

public static final VariableType getVariableTypeFromVariableTypeName(String type) {	
	for (int i = 0; i < NAMES.length; i ++) {
		if (type.equals(NAMES[i])) {
			return new VariableType(i);
		}		
	}
	
	throw new IllegalArgumentException("varType="+type+" is undefined");	
}

public static final VariableType getVariableTypeFromVariableTypeNameIgnoreCase(String type) {	
	for (int i = 0; i < NAMES.length; i ++) {
		if (type.equalsIgnoreCase(NAMES[i])) {
			return new VariableType(i);
		}		
	}
	
	throw new IllegalArgumentException("varType="+type+" is undefined");	
}

/**
 * Insert the method's description here.
 * Creation date: (10/5/00 11:01:55 AM)
 * @return int
 */
public int hashCode() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/01 4:05:42 PM)
 * @return boolean
 * @param varType cbit.vcell.simdata.VariableType
 */
public boolean isExpansionOf(VariableType varType) {
	//
	// an enclosing domain (e.g. VOLUME) is an expansion of an enclosed region (e.g. VOLUME_REGION).
	//
	// example: if VOLUME_REGION and VOLUME data are used in same function,
	// then function must be evaluated at each volume index (hence VOLUME wins).
	//
	if (type == VOLUME_TYPE && varType.type == VOLUME_REGION_TYPE) return true;
	if (type == MEMBRANE_TYPE && varType.type == MEMBRANE_REGION_TYPE) return true;
	if (type == CONTOUR_TYPE && varType.type == CONTOUR_REGION_TYPE) return true;
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/01 2:12:01 PM)
 * @return java.lang.String
 */
public String toString() {
	return getTypeName()+"_VariableType";
}
public final VariableDomain getVariableDomain() {
	return variableDomain;
}

public static VariableType getVariableType(Variable var) {
	if (var instanceof VolVariable || var instanceof VolumeParticleVariable) {
		return VariableType.VOLUME;
	} else if (var instanceof VolumeRegionVariable) {
		return VariableType.VOLUME_REGION;
	} else if (var instanceof MemVariable || var instanceof MembraneParticleVariable) {
		return VariableType.MEMBRANE;
	} else if (var instanceof MembraneRegionVariable) {
		return VariableType.MEMBRANE_REGION;
	} else if (var instanceof FilamentVariable) {
		return VariableType.CONTOUR;
	} else if (var instanceof FilamentRegionVariable) {
		return VariableType.CONTOUR_REGION;
	} else if (var instanceof InsideVariable) {
		return VariableType.MEMBRANE;
	} else if (var instanceof OutsideVariable) {
		return VariableType.MEMBRANE;
	} else if (var instanceof DataGenerator){
		return VariableType.POSTPROCESSING;
	} else {
		return VariableType.UNKNOWN;
	}
}
public boolean incompatibleWith(VariableType funcType) {
	if ((this.type == POSTPROCESSING_TYPE || funcType.type == POSTPROCESSING_TYPE) && this.type != funcType.type){
		return false;
	}
	return true;
}
}