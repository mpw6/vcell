package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.model.gui.ParameterTableModel;


@SuppressWarnings("serial")

public class ReactionRuleKineticsPropertiesPanel extends DocumentEditorSubPanel {
	
	private BioModel bioModel = null;
	private ReactionRule reactionRule = null;
	private JLabel titleLabel = null;

	private ReactionRulePropertiesTableModel tableModel = null;
	private ScrollTable table = null;
	private JTextField nameTextField = null;

	private InternalEventHandler eventHandler = new InternalEventHandler();

	private class BioModelNodeEditableTree extends JTree {
		@Override
		public boolean isPathEditable(TreePath path) {
			Object object = path.getLastPathComponent();
			return object instanceof BioModelNode;
		}
	}
	private class InternalEventHandler implements PropertyChangeListener, ActionListener, FocusListener, MouseListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionRule) {
				if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_REACTANT_WARNING)) {

				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_PRODUCT_WARNING)) {

				} else if (evt.getPropertyName().equals(ReactionRule.PROPERTY_NAME_NAME)) {
					changeName();
				}
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
		}
		@Override
		public void mouseClicked(MouseEvent e) {			
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			}			
		}
	}
	
	public ReactionRuleKineticsPropertiesPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		try {

			setName("KineticsTypeTemplatePanel");
			setLayout(new java.awt.GridBagLayout());
			
			nameTextField = new JTextField();
			nameTextField.setEditable(false);
			nameTextField.addFocusListener(eventHandler);
			nameTextField.addActionListener(eventHandler);

			
			int gridy = 0;
			GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(0, 4, 4, 4);
			gbc.anchor = GridBagConstraints.LINE_END;
			add(new JLabel("Reaction Name"), gbc);
			
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 1; 
			gbc.gridy = gridy;
			gbc.insets = new java.awt.Insets(0, 4, 4, 4);
			gbc.weightx = 1.0;
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(nameTextField, gbc);
		
			gridy ++;
			gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = gridy;
			gbc.fill = java.awt.GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.gridwidth = 3;
			add(getScrollPaneTable().getEnclosingScrollPane(), gbc);
			
			setBackground(Color.white);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	private ScrollTable getScrollPaneTable() {
		if (table == null) {
			try {
				table = new ScrollTable();
				table.setModel(getReactionRulePropertiesTableModel());
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return table;
	}
	private ReactionRulePropertiesTableModel getReactionRulePropertiesTableModel() {
		if (tableModel == null) {
			try {
				tableModel = new ReactionRulePropertiesTableModel(getScrollPaneTable(), true);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return tableModel;
	}


	
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}

	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}
		bioModel = newValue;
	}	
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			setReactionRule(null);
		} else if (selectedObjects[0] instanceof ReactionRule) {
			setReactionRule((ReactionRule) selectedObjects[0]);
		} else {
			setReactionRule(null);
		}
	}

	private void changeName() {
		if (reactionRule == null) {
			return;
		}
		String newName = nameTextField.getText();
		if (newName == null || newName.length() == 0) {
			nameTextField.setText(reactionRule.getName());
			return;
		}
		if (newName.equals(reactionRule.getName())) {
			return;
		}
		try {
			reactionRule.setName(newName);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			DialogUtils.showErrorDialog(this, e1.getMessage());
		}
	}
	
	public void setReactionRule(ReactionRule newValue) {
		if (reactionRule == newValue) {
			return;
		}
		ReactionRule oldValue = reactionRule;
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
		}
		
		reactionRule = newValue;
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
		}
		tableModel.setReactionRule(reactionRule);
		refreshInterface();
	}

	protected void refreshInterface() {
		if (reactionRule == null){	// sanity check
			nameTextField.setEditable(false);
			nameTextField.setText(null);
			return;
		}
//		ArrayList<ReactionRuleObjectProperty> propertyList = new ArrayList<ReactionRuleObjectProperty>();
//
//		if(!(reactionRule instanceof SBEntity)) {
//			tableModel.setData(propertyList);
//			return;
//		}
//		SBEntity sbEntity = (SBEntity) reactionRule;
//		if (!(sbEntity instanceof Entity)){
//			tableModel.setData(propertyList);
//			return;
//		}
//		Entity entity = (Entity) sbEntity;
//		tableModel.setData(propertyList);
		
		nameTextField.setEditable(true);
		nameTextField.setText(reactionRule.getName());
	}
	

	
}