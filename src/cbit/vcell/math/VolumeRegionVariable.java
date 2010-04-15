package cbit.vcell.math;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class VolumeRegionVariable extends Variable {
/**
 * MembraneVariable constructor comment.
 * @param name java.lang.String
 */
public VolumeRegionVariable(String name) {
	super(name);
	throw new RuntimeException("VolumeRegionVariable is not supported in VCell 4.7. Please use VCell 4.8 or later.");
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj Matchable
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (!(obj instanceof VolumeRegionVariable)){
		return false;
	}
	if (!compareEqual0(obj)){
		return false;
	}
	
	return true;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
	return VCML.VolumeRegionVariable+"   "+getName();
}
}