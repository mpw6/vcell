package cbit.vcell.export.gloworm.atoms;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/

import java.io.*;
/**
 * This type was created in VisualAge.
 */
public abstract class Atoms implements AtomConstants {

	protected int size;
		
/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public abstract boolean writeData(DataOutputStream out);
}
