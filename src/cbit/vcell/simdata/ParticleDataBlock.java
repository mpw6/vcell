/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import java.util.*;
import java.io.*;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Coordinate;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
public class ParticleDataBlock implements java.io.Serializable {

	private ParticleDataInfo particleDataInfo = null;
	private ParticleData[] particleData = null;
	private Coordinate volumeExtents = null;

	private int sizeofParticleData = 40; // approximate in bytes
/**
 * SimDataBlock constructor comment.
 */
public ParticleDataBlock(User user, String simIdentifier, double time, File file, File zipFile) throws DataAccessException, IOException {
	long timeStamp = file.lastModified();
	read(file, zipFile);
	this.particleDataInfo = new ParticleDataInfo(user,simIdentifier,time,timeStamp);
}
/**
 * SimDataBlock constructor comment.
 */
public ParticleDataBlock(User user, String simIdentifier, ParticleDataInfo particleDataInfo, ParticleData[] particleData, Coordinate volumeExtents) {
	this.particleDataInfo = particleDataInfo;
	this.particleData = particleData;
	this.volumeExtents = volumeExtents;
}
/**
 * SimDataBlock constructor comment.
 */
public ParticleDataBlock(ParticleDataInfo particleDataInfo, long timeStamp, ParticleData[] particleData, Coordinate volumeExtents) {
	this.particleDataInfo = particleDataInfo;
	this.particleData = particleData;
	this.volumeExtents = volumeExtents;
}
/**
 * This method was created in VisualAge.
 * @return double[]
 */
public ParticleData[] getParticleData() {
	return particleData;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.SimDataInfo
 */
public ParticleDataInfo getParticleDataInfo() {
	return particleDataInfo;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getSizeInBytes() {
	return particleData.length * sizeofParticleData;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
public long getTimeStamp() {
	return particleDataInfo.getTimeStamp();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.SimDataInfo
 */
public Coordinate getVolumeExtents() {
	return volumeExtents;
}
private void read(File file, File zipFile) throws DataAccessException, IOException {

	Vector lines = new Vector();
	BufferedReader reader = null;
	org.apache.commons.compress.archivers.zip.ZipFile zipZipFile = null;
	
	String fileName = file.getPath();
	if (zipFile != null) {
		zipZipFile = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
		java.util.zip.ZipEntry dataEntry = zipZipFile.getEntry(file.getName());
		InputStream is = zipZipFile.getInputStream((ZipArchiveEntry) dataEntry);
		reader = new BufferedReader(new InputStreamReader(is));
	} else {
		System.out.println("ParticleDataSet.read() open '" + fileName + "'");
		if (!file.exists()) {
			throw new FileNotFoundException("file " + fileName + " does not exist");
		}
		reader = new BufferedReader(new FileReader(file));
	}

	try {
		String newLine = reader.readLine();
		while (newLine != null) {
			lines.addElement(newLine);
			newLine = reader.readLine();
		}
	} finally {
		if (reader != null) {
			reader.close();
		}
		if (zipZipFile != null) {
			zipZipFile.close();
		}
	}
	if (lines.size() < 3) {
		throw new DataAccessException("No particle data found in file " + file.getPath());
	}
	System.out.println("ParticleDataSet.read() done");

	this.volumeExtents = readVolumeExtents(lines);
	this.particleData = readParticleData(lines);
	
}   
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.ParticleData[]
 */
private ParticleData[] readParticleData(Vector lines) throws DataAccessException {
	ParticleData[] particleData = new ParticleData[lines.size() - 2];
	for (int i=2;i<lines.size();i++) {
		try {
			CommentStringTokenizer st = new CommentStringTokenizer((String)lines.elementAt(i));
			double x = Double.valueOf(st.nextToken()).doubleValue();
			double y = Double.valueOf(st.nextToken()).doubleValue();
			double z = Double.valueOf(st.nextToken()).doubleValue();
			int state = Integer.valueOf(st.nextToken()).intValue();
			int context = Integer.valueOf(st.nextToken()).intValue();
			particleData[i - 2] = new ParticleData(new Coordinate(x,y,z), state, context);
		} catch (Exception exc) {
			throw new DataAccessException("Particle data file invalid. " + exc.getMessage());
		}
	}
	return particleData;
}
/**
 * This method was created in VisualAge.
 * @return Coordinate
 */
private Coordinate readVolumeExtents(Vector lines) throws DataAccessException {
	try {
		CommentStringTokenizer st = new CommentStringTokenizer((String)lines.elementAt(0));
		double x = Double.valueOf(st.nextToken()).doubleValue();
		double y = Double.valueOf(st.nextToken()).doubleValue();
		double z = Double.valueOf(st.nextToken()).doubleValue();
		return new Coordinate(x, y, z);
	} catch (Exception exc) {
		throw new DataAccessException("Particle data file invalid. " + exc.getMessage());
	}
}
}