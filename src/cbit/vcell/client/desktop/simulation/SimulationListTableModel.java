package cbit.vcell.client.desktop.simulation;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vcell.util.NumberUtils;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.MultiLineToolTip;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.gui.OutputOptionsPanel;
import cbit.vcell.solver.ode.gui.SimulationStatus;
/**
 * Insert the type's description here.
 * Creation date: (5/7/2004 4:07:40 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class SimulationListTableModel extends VCellSortTableModel<Simulation> implements PropertyChangeListener {
	private static final String PROPERTY_NAME_SIMULATION_WORKSPACE = "simulationWorkspace";
	private final static int COLUMN_NAME = 0;
	private final static int COLUMN_ENDTIME = 1;
	private final static int COLUMN_OUTPUT = 2;
	private final static int COLUMN_SOLVER = 3;
	private final static int COLUMN_STATUS = 4;
	private final static int COLUMN_RESULTS = 5;
	
	private static final String[] columnNames = new String[] {"Name", "End Time", "Output Option", "Solver", "Running Status", "Results"};
	private SimulationWorkspace simulationWorkspace = null;

	private class SimulationStatusCellRenderer extends DefaultScrollTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Object obj = getSimulationStatusDisplay(row);
			if (obj instanceof JProgressBar) {
				return (JProgressBar)obj;
			}
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
			if (value instanceof SimulationStatus) {
				setText(obj.toString());
				String details = ((SimulationStatus) value).getDetails();
				setToolTipText(details);
			}
			return this;
		}
	}
/**
 * SimulationListTableModel constructor comment.
 */
public SimulationListTableModel(ScrollTable table) {
	super(table, columnNames);
	table.setDefaultRenderer(SimulationStatus.class, new SimulationStatusCellRenderer());
	addPropertyChangeListener(this);
}


/**
 * getRowCount method comment.
 */
private void refreshData() {
	List<Simulation> simList = null;
	if (getSimulationWorkspace() != null && getSimulationWorkspace().getSimulations() != null) {
		simList = Arrays.asList(getSimulationWorkspace().getSimulations());
	}
	setData(simList);
	GuiUtils.flexResizeTableColumns(ownerTable);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 4:22:06 AM)
 * @return cbit.vcell.client.desktop.simulation.SimulationWorkspace
 */
private SimulationWorkspace getSimulationWorkspace() {
	return simulationWorkspace;
}

/**
 * Comment
 */
Object getSimulationStatusDisplay(int row) {
	Simulation simulation = getValueAt(row);
	SimulationStatus simStatus = getSimulationWorkspace().getSimulationStatus(simulation);
	boolean displayProgress = (simStatus.isRunning() || (simStatus.isFailed() && simStatus.numberOfJobsDone() < simulation.getScanCount()))
							  && simStatus.getProgress() != null && simStatus.getProgress().doubleValue() >= 0;
	if (displayProgress){
		double progress = simStatus.getProgress().doubleValue() / simulation.getScanCount();
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setValue((int)(progress * 100));
		if (simStatus.isFailed()) {
			progressBar.setString("One or more jobs failed");
		} else {
			progressBar.setString(NumberUtils.formatNumber(progress * 100, 4) + "%");
		}
		return progressBar;
	} else if (simStatus.isFailed()) {
		return simStatus.getStatusString();
	} else {
		return simStatus.getDetails();
	}	
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int column) {
	try{
		if (row >= 0 && row < getRowCount()) {
			Simulation simulation = getValueAt(row);
			switch (column) {
				case COLUMN_NAME: {
					return simulation.getName();
				} 
				case COLUMN_ENDTIME: {
					return simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
				} 
				case COLUMN_OUTPUT: {
					return simulation.getSolverTaskDescription().getOutputTimeSpec();
				} 
				case COLUMN_SOLVER: {
					return simulation.getSolverTaskDescription().getSolverDescription().getDisplayLabel();
				} 
				case COLUMN_STATUS: {
					return getSimulationWorkspace().getSimulationStatus(simulation);
				} 
				case COLUMN_RESULTS: {
					return getSimulationWorkspace().getSimulationStatus(simulation).getHasData() ? "yes" : "no";
				} 
			}
		}
	} catch(Exception e){
		e.printStackTrace();
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 1:56:12 PM)
 * @return boolean
 * @param rowIndex int
 * @param columnIndex int
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	switch (columnIndex){
		case COLUMN_NAME: 
			return true;
		case COLUMN_ENDTIME:
			return true;
		case COLUMN_OUTPUT:
			return true;
		default: {
			return false;
		}
	}
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_WORKSPACE)) {
		SimulationWorkspace oldValue = (SimulationWorkspace) evt.getOldValue();
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
			for (Simulation simulation : oldValue.getSimulations()) {
				simulation.removePropertyChangeListener(this);
				simulation.getSolverTaskDescription().removePropertyChangeListener(this);
			}
		}
		SimulationWorkspace newValue = (SimulationWorkspace) evt.getNewValue();
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			for (Simulation simulation : newValue.getSimulations()) {
				simulation.addPropertyChangeListener(this);
				simulation.getSolverTaskDescription().addPropertyChangeListener(this);
			}
		}
		refreshData();
	} else if (evt.getSource() == getSimulationWorkspace() && evt.getPropertyName().equals("simulations")) {
		Simulation[] oldValue = (Simulation[]) evt.getOldValue();
		if (oldValue != null) {
			for (Simulation simulation : oldValue) {
				simulation.removePropertyChangeListener(this);
				simulation.getSolverTaskDescription().removePropertyChangeListener(this);
			}
		}
		Simulation[] newValue = (Simulation[]) evt.getNewValue();
		if (newValue != null) {
			for (Simulation simulation : newValue) {
				simulation.addPropertyChangeListener(this);
				simulation.getSolverTaskDescription().addPropertyChangeListener(this);
			}
		}
		refreshData();
	} else {
		if (evt.getSource() instanceof Simulation && evt.getPropertyName().equals(Simulation.PROPERTY_NAME_SOLVER_TASK_DESCRIPTION)) {
			SolverTaskDescription oldValue = (SolverTaskDescription)evt.getOldValue();
			if (oldValue != null) {
				oldValue.removePropertyChangeListener(this);
			}
			SolverTaskDescription newValue = (SolverTaskDescription)evt.getNewValue();
			if (newValue != null) {
				newValue.addPropertyChangeListener(this);
			}			
		}
		fireTableRowsUpdated(0, getRowCount() - 1);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 6:32:09 PM)
 * @param newSimContext cbit.vcell.mapping.SimulationContext
 */
public void setSimulationWorkspace(SimulationWorkspace newSimulationWorkspace) {
	SimulationWorkspace oldValue = this.simulationWorkspace;
	simulationWorkspace = newSimulationWorkspace;
	firePropertyChange(PROPERTY_NAME_SIMULATION_WORKSPACE, oldValue, newSimulationWorkspace);
}


/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 2:01:23 PM)
 * @param aValue java.lang.Object
 * @param rowIndex int
 * @param columnIndex int
 */
public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	Simulation simulation = getValueAt(rowIndex);
	try {		
		switch (columnIndex){
			case COLUMN_NAME:
				if (aValue instanceof String){
					String newName = (String)aValue;
					if (!simulation.getName().equals(newName)){
						simulation.setName(newName);
					}
				}
				break;
			case COLUMN_ENDTIME:
				if (aValue instanceof Double){
					SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
					double newEndTime = (Double) aValue;
					ClientTaskManager.changeEndTime(ownerTable, solverTaskDescription, newEndTime);
					simulation.setIsDirty(true);
				}
				break;
			case COLUMN_OUTPUT:
				if (aValue instanceof String){
					SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
					OutputTimeSpec ots = solverTaskDescription.getOutputTimeSpec();
					OutputTimeSpec newOts = null;
					if (ots instanceof DefaultOutputTimeSpec) {
						int newValue = Integer.parseInt((String)aValue);
						newOts = new DefaultOutputTimeSpec(newValue, ((DefaultOutputTimeSpec) ots).getKeepAtMost());
					} else if (ots instanceof UniformOutputTimeSpec) {
						try {
							boolean bValid = true;
							double outputTime = Double.parseDouble((String)aValue);
							if (solverTaskDescription.getOutputTimeSpec().isUniform() && !solverTaskDescription.getSolverDescription().hasVariableTimestep()) {
								double timeStep = solverTaskDescription.getTimeStep().getDefaultTimeStep();
								
								double suggestedInterval = outputTime;
								if (outputTime < timeStep) {
									suggestedInterval = timeStep;
									bValid = false;
								} else {
									double n = outputTime/timeStep;
									int intn = (int)Math.round(n);
									if (intn != n) {
										bValid = false;
										suggestedInterval = (intn * timeStep);
									}
								} 
								if (bValid) {
									newOts = new UniformOutputTimeSpec(outputTime);
								} else {		
									String ret = PopupGenerator.showWarningDialog(ownerTable, "Output Interval", "Output Interval must " +
											"be integer multiple of time step.\n\nChange Output Interval to " + suggestedInterval + "?", 
											new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
									if (ret.equals(UserMessage.OPTION_YES)) {
										newOts = new UniformOutputTimeSpec(suggestedInterval);
									} 
								}
							}
						} catch (NumberFormatException ex) {
							DialogUtils.showErrorDialog(ownerTable, "Wrong number format " + ex.getMessage().toLowerCase());
						}
					} else if (ots instanceof ExplicitOutputTimeSpec) {
						newOts = ExplicitOutputTimeSpec.fromString((String) aValue);
					}
					if (newOts != null) {
						solverTaskDescription.setOutputTimeSpec(newOts);
						simulation.setIsDirty(true);
					}
				}
				break;
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(ownerTable, ex.getMessage());
	}
}

@Override
protected Comparator<Simulation> getComparator(int col, boolean ascending) {
	return null;
}

@Override
public boolean isSortable(int col) {
	return false;
}


@Override
public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	case COLUMN_STATUS:
		return SimulationStatus.class;
	case COLUMN_ENDTIME:
		return Double.class;
	case COLUMN_OUTPUT:
		return OutputTimeSpec.class;
	default:
		return String.class;
	}
}
}