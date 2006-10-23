package cbit.vcell.model;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.beans.*;
import java.util.*;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.NameScope;
import org.vcell.expression.ScopedSymbolTable;

import cbit.util.Cacheable;
import cbit.util.Compare;
import cbit.util.Matchable;
import cbit.util.document.KeyValue;

public abstract class Structure implements java.io.Serializable, ScopedSymbolTable, Matchable, Cacheable, java.beans.VetoableChangeListener
{
	private String fieldName = new String();
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.util.document.KeyValue fieldKey = null;
	private StructureNameScope fieldNameScope = new Structure.StructureNameScope();
	private transient Model fieldModel = null;

	public class StructureNameScope extends BioNameScope {
		private NameScope children[] = new NameScope[0];
		
		public StructureNameScope(){
			super();
		}
		public org.vcell.expression.NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return cbit.util.TokenMangler.fixTokenStrict(Structure.this.getName());
		}
		public org.vcell.expression.NameScope getParent() {
			if (Structure.this.fieldModel != null){
				return Structure.this.fieldModel.getNameScope();
			}else{
				return null;
			}
		}
		public org.vcell.expression.ScopedSymbolTable getScopedSymbolTable() {
			return Structure.this;
		}
	}

	
	
protected Structure(KeyValue key){
	this.fieldKey = key;
	addVetoableChangeListener(this);
}                        
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
protected boolean compareEqual0(Structure s) {
	if (s == null){
		return false;
	}

	if (!getName().equals(s.getName())){
		return false;
	}
	return true;
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public abstract boolean enclosedBy(Structure parentStructure);
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * getEntry method comment.
 */
public org.vcell.expression.SymbolTableEntry getEntry(java.lang.String identifierString) throws org.vcell.expression.ExpressionBindingException {
	
	org.vcell.expression.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString);
}
/**
 * Gets the key property (cbit.sql.KeyValue) value.
 * @return The key property value.
 * @see #setKey
 */
public cbit.util.document.KeyValue getKey() {
	return fieldKey;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 9:59:19 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public org.vcell.expression.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws org.vcell.expression.ExpressionBindingException {
	
	org.vcell.expression.SymbolTableEntry ste = ReservedSymbol.fromString(identifier);
	if (ste != null){
		ReservedSymbol rs = (ReservedSymbol)ste;
		if (rs.isX() || rs.isY() || rs.isZ()){
			throw new ExpressionBindingException("can't use x, y, or z, Physiological Models must be spatially independent");
		}
		return rs;
	}	

	if (this instanceof Membrane){
		MembraneVoltage membraneVoltage = ((Membrane)this).getMembraneVoltage();
		if (membraneVoltage.getName().equals(identifier)){
			return membraneVoltage;
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 10:09:25 AM)
 * @return cbit.vcell.model.Model
 */
Model getModel() {
	return fieldModel;
}
/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public String getName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 9:59:19 AM)
 * @return cbit.vcell.parser.NameScope
 */
public org.vcell.expression.NameScope getNameScope() {
	return fieldNameScope;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure
 */
public abstract Structure getParentStructure();
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
protected void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in Feature");
	exception.printStackTrace(System.out);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 10:09:25 AM)
 * @param newModel cbit.vcell.model.Model
 */
void setModel(Model newModel) {
	fieldModel = newModel;
}
/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}
/**
 * This method was created in VisualAge.
 * @param structure cbit.vcell.model.Structure
 */
public abstract void setParentStructure(Structure structure) throws ModelException;
/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 */
public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	if (e.getPropertyName().equals("name")){
		if (e.getNewValue()==null || ((String)(e.getNewValue())).trim().length()==0){
			throw new PropertyVetoException("structure name is not specified (null)",e);
		}
	}
}
/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public abstract void writeTokens(java.io.PrintWriter pw, Model model);
}
