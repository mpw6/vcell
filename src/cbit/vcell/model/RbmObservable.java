package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.common.RbmEventHandler;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.document.PropertyConstants;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

public class RbmObservable implements Serializable, Matchable, SymbolTableEntry, PropertyChangeListener {
	public static enum ObservableType {
		Molecules,
		Species;
	}

	private String name;
	private List<SpeciesPattern> speciesPatternList; 
	private RbmObservable.ObservableType type;
	private Structure structure;
	private transient Model model = null;
	
	public RbmObservable(Model model, String name, Structure structure, RbmObservable.ObservableType t) {
		this.name = name;
		this.type = t;
		this.model = model;
		this.structure = structure;
		speciesPatternList = new ArrayList<SpeciesPattern>();
//		speciesPatternList.addPropertyChangeListener(this);
	}
	// TODO: this is wrong, get rid of it
	public RbmObservable(String name) {
		this(null, name, null, null);
	}
	
	public void setModel(Model argModel) {
		model = argModel;
	}

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public final String getName() {
		return name;
	}
	
	public void setName(String newValue) throws PropertyVetoException {
		String oldValue = name;
		fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
		name = newValue;
		firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, newValue);
	}
	
	public final RbmObservable.ObservableType getType() {
		return type;
	}
	
	public void setType(RbmObservable.ObservableType newValue) throws PropertyVetoException {
		RbmObservable.ObservableType oldValue = newValue;
		type = newValue;
		firePropertyChange(RbmObservable.PROPERTY_NAME_TYPE, oldValue, newValue);
	}
	
	public final SpeciesPattern getSpeciesPattern(int index) {
		if(!speciesPatternList.isEmpty()) {
			return speciesPatternList.get(index);
		}
		return null;
	}
	
	// TODO: why did we stop using setSpeciesPattern and instead made addSpeciesPattern  ???
	public boolean addSpeciesPattern(SpeciesPattern newValue) {
		boolean ret = speciesPatternList.add(newValue);
		if (newValue != null && ret == true) {
			newValue.addPropertyChangeListener(this);
			resolveBonds();
		}
		if(ret == true) {
			firePropertyChange(PROPERTY_NAME_SPECIES_PATTERN_LIST, null, newValue);	// is this correct?
		}
		return ret;
	}
	public final List<SpeciesPattern> getSpeciesPatternList() {
		return speciesPatternList;
	}
	public void setSpeciesPatternList(List<SpeciesPattern> newValue) {
		List<SpeciesPattern> oldValue = speciesPatternList;
		if (oldValue != null) {
			for(SpeciesPattern sp : oldValue) {
				sp.removePropertyChangeListener(this);
			}
		}
		speciesPatternList = newValue;
		if (newValue != null) {
			for(SpeciesPattern sp : newValue) {
				sp.addPropertyChangeListener(this);
			}
			resolveBonds();
		}
		firePropertyChange(RbmObservable.PROPERTY_NAME_SPECIES_PATTERN_LIST, oldValue, newValue);
	}

	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof RbmObservable)) {
			return false;
		}
		RbmObservable that = (RbmObservable)aThat;

		if (!Compare.isEqual(name, that.name)) {
			return false;
		}
		if (!Compare.isEqual(type, that.type)) {
			return false;
		}
		if (!Compare.isEqual(structure.getName(), that.structure.getName())) {
			return false;
		}
		// can't compare the full model, we'd enter an infinite loop
		if(model == null && that.model == null) {
			;
		} else if((model == null && that.model != null) || (model != null && that.model == null)) {
			return false;
		} else if (!Compare.isEqual(model.getName(), that.model.getName())) {
			return false;
		}
		if (!Compare.isEqual(speciesPatternList, that.speciesPatternList)) {
			return false;
		}
		return true;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == speciesPatternList && evt.getPropertyName().equals(SpeciesPattern.PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS)) {
			resolveBonds();
		}		
	}
	
	private void resolveBonds() {
		for(SpeciesPattern sp : speciesPatternList) {
			List<MolecularTypePattern> molecularTypePatterns = sp.getMolecularTypePatterns();
			for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
				molecularTypePatterns.get(i).setIndex(i+1);
			}
			sp.resolveBonds();
		}
	}
	

	//=========================================================================================================
	// adding common event handling (event aspect)
	//=========================================================================================================
	private final RbmEventHandler eventHandler = new RbmEventHandler(this);
	public static final String PROPERTY_NAME_TYPE = "type";
	public static final String PROPERTY_NAME_SPECIES_PATTERN_LIST = "speciesPatternList";
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		eventHandler.addPropertyChangeListener(listener);
	}

	public void addVetoableChangeListener(VetoableChangeListener listener) {
		eventHandler.addVetoableChangeListener(listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		eventHandler.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
		eventHandler.fireVetoableChange(propertyName, oldValue, newValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		eventHandler.removePropertyChangeListener(listener);
	}

	public void removeVetoableChangeListener(VetoableChangeListener listener) {
		eventHandler.removeVetoableChangeListener(listener);
	}

	@Override
	public double getConstantValue() throws ExpressionException {
		throw new ExpressionException(getName()+" is not constant");
	}

	@Override
	public Expression getExpression() {
		return null;
	}

	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public NameScope getNameScope() {
		if (model != null){
			return model.getNameScope();
		}else{
			return null;
		}
	}

	@Override
	public VCUnitDefinition getUnitDefinition() {
		if (model != null) {
			return model.getUnitSystem().getConcentrationUnit(structure);
		}
		return null;
	}

	@Override
	public boolean isConstant() throws ExpressionException {
		return false;
	}
	
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		issueContext = issueContext.newChildContext(ContextType.RBMObservable, this);
		if(name == null) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of Observable is null", Issue.SEVERITY_ERROR));
		} else if(name.equals("")) {
			issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Name of Observable is empty", Issue.SEVERITY_WARNING));
		} else {
			if(speciesPatternList == null) {
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Observable '" + name + "' Species Pattern List is null", Issue.SEVERITY_ERROR));
			} else if(speciesPatternList.isEmpty()) {
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Observable '" + name + "' Species Pattern List is empty", Issue.SEVERITY_WARNING));
			} else {
				for(SpeciesPattern sp : speciesPatternList) {
					sp.checkSpeciesPattern(issueContext, issueList);
					sp.gatherIssues(issueContext, issueList);
					for(MolecularTypePattern mtpThis : sp.getMolecularTypePatterns()) {
						checkComponentStateConsistency(issueContext, issueList, mtpThis);
					}
				}
			}			
			if(type == null) {
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Observable '" + name + "' Type is null", Issue.SEVERITY_ERROR));
			}			
		}
	}
	public void checkComponentStateConsistency(IssueContext issueContext, List<Issue> issueList, MolecularTypePattern mtpThis) {
		if(issueList == null) {
			return;		// this may be called during parsing before the model is consistent 
		}
		MolecularType mtThat = mtpThis.getMolecularType();
		for(MolecularComponentPattern mcpThis : mtpThis.getComponentPatternList()) {
			if(mcpThis.isImplied()) {
				continue;
			}
			ComponentStatePattern cspThis = mcpThis.getComponentStatePattern();
			String mcNameThis = mcpThis.getMolecularComponent().getName();
			MolecularComponent[] mcThatList = mtThat.getMolecularComponents(mcNameThis);
			if(mcThatList.length == 0) {
				System.out.println("we already fired an issue about component missing");
				continue;	// nothing to do here, we already fired an issue about component missing 
			} else if(mcThatList.length > 1) {
				String msg = "Multiple components with the same name are not yet supported.";
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
			} else {	// found exactly 1 component
				MolecularComponent mcThat = mcThatList[0];
				List<ComponentStateDefinition> csdListThat = mcThat.getComponentStateDefinitions();
				if(csdListThat.size() == 0) {	// component has no states, we check if mcpThis has any states... it shouldn't
					if(cspThis == null) {
						continue;				// all is well
					}
					if(!cspThis.isAny() || (cspThis.getComponentStateDefinition() != null)) {
						String msg = "Component pattern " + mcNameThis + " is in an invalid State.";
						issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
					}
				} else {						// we check if mcpThis has any of these states... it should!
					if((cspThis == null) || cspThis.isAny() || (cspThis.getComponentStateDefinition() == null)) {
//						String msg = "Component pattern " + mcNameThis + " must be in an explicit State.";
//						issueList.add(new Issue(this, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
					} else {
						String csdNameThis = cspThis.getComponentStateDefinition().getName();
						if(csdNameThis.isEmpty() || (mcThat.getComponentStateDefinition(csdNameThis) == null) ) {
							String msg = "Invalid State " + csdNameThis + " for component pattern " + mcNameThis;
							issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
						}
					}
				}
			}
		}
	}

}
