/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class StructurePropertiesPanel extends DocumentEditorSubPanel {
	private Structure structure = null;
	private EventHandler eventHandler = new EventHandler();
	private JTextArea annotationTextArea;
	private JTextField nameTextField = null;
	private Model fieldModel = null;
	private JTextField sizeTextField;
	private JLabel voltageLabel;
	private JTextField voltageTextField;

	private class EventHandler implements ActionListener, FocusListener, PropertyChangeListener {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeAnnotation();
			} else if (e.getSource() == nameTextField) {
				changeName();
			}
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == structure || evt.getSource() == structure.getStructureSize()
					|| structure instanceof Membrane && evt.getSource() == ((Membrane)structure).getMembraneVoltage()) {
				updateInterface();
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			}		
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public StructurePropertiesPanel() {
	super();
	initialize();
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	annotationTextArea.addFocusListener(eventHandler);
	nameTextField.addFocusListener(eventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SpeciesEditorPanel");
		setLayout(new GridBagLayout());
		
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		nameTextField.addActionListener(eventHandler);
		sizeTextField = new JTextField();
		sizeTextField.setEditable(false);
		
		voltageLabel = new JLabel("Voltage Variable Name");
		voltageTextField = new JTextField();
		voltageTextField.setEditable(false);
		
		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setEditable(false);

		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("<html><u>Select only one structure to edit properties</u></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Structure Name");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(nameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Size Variable Name");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(sizeTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(voltageLabel, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(voltageTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Annotation"), gbc);

		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 1;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(jsp, gbc);
		
		setBackground(Color.white);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void changeAnnotation() {
	try{
		if (structure == null || fieldModel == null) {
			return;
		}
		VCMetaData vcMetaData = fieldModel.getVcMetaData();
		vcMetaData.setFreeTextAnnotation(structure, annotationTextArea.getText());
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
}

public void setModel(Model model) {
	fieldModel = model;
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
void setStructure(Structure newValue) {
	if (newValue == structure) {
		return;
	}
	Structure oldValue = structure;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
		oldValue.getStructureSize().removePropertyChangeListener(eventHandler);
		if (oldValue instanceof Membrane) {
			((Membrane) oldValue).getMembraneVoltage().removePropertyChangeListener(eventHandler);
		}
	}
	// commit the changes before switch to another structure
	changeName();
	changeAnnotation();
	structure = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
		newValue.getStructureSize().addPropertyChangeListener(eventHandler);
		if (newValue instanceof Membrane) {
			((Membrane) newValue).getMembraneVoltage().addPropertyChangeListener(eventHandler);
		}		
	}
	updateInterface();
}

/**
 * Comment
 */
private void updateInterface() {
	boolean bNonNullStructure = structure != null && fieldModel != null;
	nameTextField.setEditable(bNonNullStructure);
	annotationTextArea.setEditable(bNonNullStructure);
	boolean bMembrane = bNonNullStructure && structure instanceof Membrane;
	voltageLabel.setVisible(bMembrane);
	voltageTextField.setVisible(bMembrane);
	if (bNonNullStructure) {
		nameTextField.setText(structure.getName());
		annotationTextArea.setText(fieldModel.getVcMetaData().getFreeTextAnnotation(structure));
		sizeTextField.setText(structure.getStructureSize().getName() + " [" + (bMembrane ? VCUnitDefinition.UNIT_um2.getSymbol() : VCUnitDefinition.UNIT_um3.getSymbol()) + "]");
		if (bMembrane) {
			voltageTextField.setText(((Membrane)structure).getMembraneVoltage().getName() + " [" + VCUnitDefinition.UNIT_mV.getSymbol() + "]");
		}
	} else {
		annotationTextArea.setText(null);
		nameTextField.setText(null);
		sizeTextField.setText(null);
		voltageTextField.setText(null);
	}
}

private void changeName() {
	if (structure == null) {
		return;
	}
	String newName = nameTextField.getText();
	if (newName == null || newName.length() == 0) {
		nameTextField.setText(structure.getName());
		return;
	}
	if (newName.equals(structure.getName())) {
		return;
	}
	try {
		structure.setName(newName);
		structure.getStructureSize().setName(Structure.getDefaultStructureSizeName(newName));
		if (structure instanceof Membrane) {
			((Membrane)structure).getMembraneVoltage().setName(Membrane.getDefaultMembraneVoltageName(newName));
		}
	} catch (PropertyVetoException e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(StructurePropertiesPanel.this, e1.getMessage());
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		return;
	}
	if (selectedObjects[0] instanceof Structure) {
		setStructure((Structure) selectedObjects[0]);
	} else {
		setStructure(null);
	}
	
}
}
