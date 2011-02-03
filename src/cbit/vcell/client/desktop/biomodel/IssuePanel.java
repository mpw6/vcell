package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.Issue;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.sorttable.JSortTable;

@SuppressWarnings("serial")
public class IssuePanel extends DocumentEditorSubPanel {

	private JSortTable issueTable = null;
	private IssueTableModel issueTableModel = null;
	private JButton refreshButton = null;
	private JCheckBox showWarningCheckBox;
	private Icon errorIcon, warningIcon, infoIcon;
	
	public IssuePanel() {
		super();
		initialize();
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		issueTableModel.setIssueManager(issueManager);
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {	
	}	
	
	private void initialize() {
		refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (issueManager != null) {
					issueManager.updateIssues();
				}			
			}
		});
		showWarningCheckBox = new JCheckBox("Show Warnings");
		showWarningCheckBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				issueTableModel.setShowWarning(showWarningCheckBox.isSelected());
				
			}
		});
		issueTable = new JSortTable();
		issueTableModel = new IssueTableModel(issueTable);
		issueTable.setModel(issueTableModel);
		
		setLayout(new GridBagLayout());
		int gridy = 0;
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(0,10,0,0);
		add(showWarningCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0,0,0,10);
		add(refreshButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		add(issueTable.getEnclosingScrollPane(), gbc);
		
		DefaultTableCellRenderer tableRenderer = new DefaultScrollTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, column);
				setIcon(null);
				switch (column) {
				case IssueTableModel.COLUMN_DESCRIPTION: {
					Issue issue = (Issue)value;
					int severity = issue.getSeverity();
					Icon icon = null;
					switch (severity) {
					case Issue.SEVERITY_INFO:
						icon = getInfoIcon();
						break;
					case Issue.SEVERITY_WARNING:
						icon = getWarningIcon();
						break;					
					case Issue.SEVERITY_ERROR:
						icon = getErrorIcon();
						break;
					}
					setIcon(icon);
					setText(issue.getMessage());
					break;
				}								
				}
				return this;
			}			
		};
		issueTable.getColumnModel().getColumn(IssueTableModel.COLUMN_DESCRIPTION).setCellRenderer(tableRenderer);
	}
	
	private Icon getErrorIcon() {
		if (errorIcon == null) {
			errorIcon = UIManager.getIcon("OptionPane.errorIcon");
			if (errorIcon instanceof ImageIcon) {
				Image image = ((ImageIcon)errorIcon).getImage();
				image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);  
				errorIcon = new ImageIcon(image);
			}
		}
		return errorIcon;
	}

	private Icon getWarningIcon() {
		if (warningIcon == null) {
			warningIcon = UIManager.getIcon("OptionPane.warningIcon");
			if (warningIcon instanceof ImageIcon) {
				Image image = ((ImageIcon)warningIcon).getImage();
				image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);  
				warningIcon = new ImageIcon(image);
			}
		}
		return warningIcon;
	}
	
	private Icon getInfoIcon() {
		if (infoIcon == null) {
			infoIcon = UIManager.getIcon("OptionPane.informationIcon");
			if (infoIcon instanceof ImageIcon) {
				Image image = ((ImageIcon)infoIcon).getImage();
				image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);  
				infoIcon = new ImageIcon(image);
			}
		}
		return infoIcon;
	}
}
