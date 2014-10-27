/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.viewer;

import java.awt.*;
import javax.swing.*;
/**
 * This type was generated by a SmartGuide.
 */
public class XmlTreeViewerApplication extends JFrame {
	private JMenuItem ivjAbout_BoxMenuItem = null;
	private JMenuItem ivjBooks_OnlineMenuItem = null;
	private JButton ivjCopyButton = null;
	private JButton ivjCutButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JMenuItem ivjExitMenuItem = null;
	private JMenu ivjFileMenu = null;
	private JMenuItem ivjHelp_TopicsMenuItem = null;
	private JMenu ivjHelpMenu = null;
	private JPanel ivjJFrameContentPane = null;
	private JMenuItem ivjOpenMenuItem = null;
	private JButton ivjPasteButton = null;
	private JMenuItem ivjStatusbarMenuItem = null;
	private JPanel ivjStatusBarPane = null;
	private JLabel ivjStatusMsg1 = null;
	private JLabel ivjStatusMsg2 = null;
	private JMenuItem ivjToolbarMenuItem = null;
	private JToolBar ivjToolBarPane = null;
	private JMenu ivjViewMenu = null;
	private JMenuBar ivjXmlTreeViewerApplicationJMenuBar = null;
	private JPanel ivjXmlTreeViewerApplicationPane = null;
	private JFileChooser chooser =null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == XmlTreeViewerApplication.this.getExitMenuItem()) 
				connEtoM1(e);
			if (e.getSource() == XmlTreeViewerApplication.this.getToolbarMenuItem()) 
				connEtoC1(e);
			if (e.getSource() == XmlTreeViewerApplication.this.getStatusbarMenuItem()) 
				connEtoC2(e);
			if (e.getSource() == XmlTreeViewerApplication.this.getAbout_BoxMenuItem()) 
				connEtoC3(e);
			if (e.getSource() == XmlTreeViewerApplication.this.getOpenMenuItem()) 
				connEtoC4(e);
		};
	};
	private XmlTreeViewer ivjXmlTreeViewer = null;
/**
 * XmlTreeViewerApplication constructor comment.
 */
public XmlTreeViewerApplication() {
	super();
	initialize();
}
/**
 * XmlTreeViewerApplication constructor comment.
 * @param title java.lang.String
 */
public XmlTreeViewerApplication(String title) {
	super(title);
}
/**
 * connEtoC1:  (ToolbarMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> XmlTreeViewerApplication.viewToolBar()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.viewToolBar();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (StatusbarMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> XmlTreeViewerApplication.viewStatusBar()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.viewStatusBar();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (About_BoxMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> XmlTreeViewerApplication.showAboutBox()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.showAboutBox();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (OpenMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> XmlTreeViewerApplication.fileOpen(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fileOpen(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ExitMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> XmlTreeViewerApplication.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void fileOpen(java.awt.event.ActionEvent actionEvent) {
	try {
		//  Automatically open two files and do:
		java.io.File file = getfile();
		if (file == null) {
			getStatusMsg1().setText("Warning");
			getStatusMsg2().setText("No baseline file selected!");
		} else {
			org.jdom.input.SAXBuilder saxBuilder = new org.jdom.input.SAXBuilder();
			org.jdom.Document document = null;
			org.jdom.Element root = null;
			//
			try {
				getXmlTreeViewer().setDocument(saxBuilder.build(file));
			} catch (org.jdom.JDOMException jdomexception) {
				getStatusMsg1().setText("Error");
				if (jdomexception.getCause() != null) {
					getStatusMsg2().setText(jdomexception.getCause().getMessage());
				} else {
					getStatusMsg2().setText(jdomexception.getMessage());
				}
			}
		}
	} catch (Exception exception) {
		getStatusMsg1().setText("Error");
		getStatusMsg2().setText(exception.getMessage());
	}
}
/**
 * Return the About_BoxMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAbout_BoxMenuItem() {
	if (ivjAbout_BoxMenuItem == null) {
		try {
			ivjAbout_BoxMenuItem = new javax.swing.JMenuItem();
			ivjAbout_BoxMenuItem.setName("About_BoxMenuItem");
			ivjAbout_BoxMenuItem.setText("About Box");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAbout_BoxMenuItem;
}
/**
 * Return the Books_OnlineMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getBooks_OnlineMenuItem() {
	if (ivjBooks_OnlineMenuItem == null) {
		try {
			ivjBooks_OnlineMenuItem = new javax.swing.JMenuItem();
			ivjBooks_OnlineMenuItem.setName("Books_OnlineMenuItem");
			ivjBooks_OnlineMenuItem.setText("Books Online");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBooks_OnlineMenuItem;
}
/**
 * Return the CopyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCopyButton() {
	if (ivjCopyButton == null) {
		try {
			ivjCopyButton = new javax.swing.JButton();
			ivjCopyButton.setName("CopyButton");
			ivjCopyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copy.gif")));
			ivjCopyButton.setText("");
			ivjCopyButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjCopyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjCopyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCopyButton;
}
/**
 * Return the CutButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCutButton() {
	if (ivjCutButton == null) {
		try {
			ivjCutButton = new javax.swing.JButton();
			ivjCutButton.setName("CutButton");
			ivjCutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cut.gif")));
			ivjCutButton.setText("");
			ivjCutButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjCutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjCutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCutButton;
}
/**
 * Return the ExitMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getExitMenuItem() {
	if (ivjExitMenuItem == null) {
		try {
			ivjExitMenuItem = new javax.swing.JMenuItem();
			ivjExitMenuItem.setName("ExitMenuItem");
			ivjExitMenuItem.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMenuItem;
}
/**
 * Insert the method's description here.
 * Creation date: (4/2/2001 12:37:30 PM)
 * @return java.io.File
 */
protected java.io.File getfile() {
	if (this.chooser == null) {
		this.chooser = new org.vcell.util.gui.VCFileChooser(".");
		this.chooser.setFileSelectionMode(chooser.FILES_ONLY);
		this.chooser.addChoosableFileFilter(org.vcell.util.gui.FileFilters.FILE_FILTER_EXTERNALDOC);
		this.chooser.setFileFilter(org.vcell.util.gui.FileFilters.FILE_FILTER_EXTERNALDOC);
	}
	
	int returnval = chooser.showOpenDialog(null);
	if (returnval == chooser.APPROVE_OPTION) {
		return ( chooser.getSelectedFile() );
	}
	
	return null;
}
/**
 * Return the FileMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getOpenMenuItem());
			ivjFileMenu.add(getExitMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileMenu;
}
/**
 * Return the Help_TopicsMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getHelp_TopicsMenuItem() {
	if (ivjHelp_TopicsMenuItem == null) {
		try {
			ivjHelp_TopicsMenuItem = new javax.swing.JMenuItem();
			ivjHelp_TopicsMenuItem.setName("Help_TopicsMenuItem");
			ivjHelp_TopicsMenuItem.setText("Help Topics");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelp_TopicsMenuItem;
}
/**
 * Return the HelpMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getHelpMenu() {
	if (ivjHelpMenu == null) {
		try {
			ivjHelpMenu = new javax.swing.JMenu();
			ivjHelpMenu.setName("HelpMenu");
			ivjHelpMenu.setText("Help");
			ivjHelpMenu.add(getHelp_TopicsMenuItem());
			ivjHelpMenu.add(getBooks_OnlineMenuItem());
			ivjHelpMenu.add(getAbout_BoxMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpMenu;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJFrameContentPane().add(getToolBarPane(), "North");
			getJFrameContentPane().add(getStatusBarPane(), "South");
			getJFrameContentPane().add(getXmlTreeViewerApplicationPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the OpenMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getOpenMenuItem() {
	if (ivjOpenMenuItem == null) {
		try {
			ivjOpenMenuItem = new javax.swing.JMenuItem();
			ivjOpenMenuItem.setName("OpenMenuItem");
			ivjOpenMenuItem.setText("Open");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpenMenuItem;
}
/**
 * Return the PasteButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getPasteButton() {
	if (ivjPasteButton == null) {
		try {
			ivjPasteButton = new javax.swing.JButton();
			ivjPasteButton.setName("PasteButton");
			ivjPasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paste.gif")));
			ivjPasteButton.setText("");
			ivjPasteButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjPasteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjPasteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasteButton;
}
/**
 * Return the StatusbarMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getStatusbarMenuItem() {
	if (ivjStatusbarMenuItem == null) {
		try {
			ivjStatusbarMenuItem = new javax.swing.JMenuItem();
			ivjStatusbarMenuItem.setName("StatusbarMenuItem");
			ivjStatusbarMenuItem.setText("Statusbar");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusbarMenuItem;
}
/**
 * Return the StatusBarPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getStatusBarPane() {
	if (ivjStatusBarPane == null) {
		try {
			ivjStatusBarPane = new javax.swing.JPanel();
			ivjStatusBarPane.setName("StatusBarPane");
			ivjStatusBarPane.setLayout(new java.awt.BorderLayout());
			getStatusBarPane().add(getStatusMsg1(), "West");
			getStatusBarPane().add(getStatusMsg2(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusBarPane;
}
/**
 * Return the StatusMsg1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStatusMsg1() {
	if (ivjStatusMsg1 == null) {
		try {
			ivjStatusMsg1 = new javax.swing.JLabel();
			ivjStatusMsg1.setName("StatusMsg1");
			ivjStatusMsg1.setBorder(new javax.swing.border.EtchedBorder());
			ivjStatusMsg1.setText("StatusMsg1    ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusMsg1;
}
/**
 * Return the StatusMsg2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStatusMsg2() {
	if (ivjStatusMsg2 == null) {
		try {
			ivjStatusMsg2 = new javax.swing.JLabel();
			ivjStatusMsg2.setName("StatusMsg2");
			ivjStatusMsg2.setBorder(new javax.swing.border.EtchedBorder());
			ivjStatusMsg2.setText("StatusMsg2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusMsg2;
}
/**
 * Return the ToolbarMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getToolbarMenuItem() {
	if (ivjToolbarMenuItem == null) {
		try {
			ivjToolbarMenuItem = new javax.swing.JMenuItem();
			ivjToolbarMenuItem.setName("ToolbarMenuItem");
			ivjToolbarMenuItem.setText("Toolbar");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjToolbarMenuItem;
}
/**
 * Return the ToolBarPane property value.
 * @return javax.swing.JToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToolBar getToolBarPane() {
	if (ivjToolBarPane == null) {
		try {
			ivjToolBarPane = new javax.swing.JToolBar();
			ivjToolBarPane.setName("ToolBarPane");
			ivjToolBarPane.add(getCutButton());
			ivjToolBarPane.add(getCopyButton());
			ivjToolBarPane.add(getPasteButton());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjToolBarPane;
}
/**
 * Return the ViewMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getViewMenu() {
	if (ivjViewMenu == null) {
		try {
			ivjViewMenu = new javax.swing.JMenu();
			ivjViewMenu.setName("ViewMenu");
			ivjViewMenu.setText("View");
			ivjViewMenu.add(getToolbarMenuItem());
			ivjViewMenu.add(getStatusbarMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewMenu;
}
/**
 * Return the XmlTreeViewer property value.
 * @return cbit.xml.viewer.XmlTreeViewer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private XmlTreeViewer getXmlTreeViewer() {
	if (ivjXmlTreeViewer == null) {
		try {
			ivjXmlTreeViewer = new cbit.xml.viewer.XmlTreeViewer();
			ivjXmlTreeViewer.setName("XmlTreeViewer");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXmlTreeViewer;
}
/**
 * Return the XmlTreeViewerApplicationJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getXmlTreeViewerApplicationJMenuBar() {
	if (ivjXmlTreeViewerApplicationJMenuBar == null) {
		try {
			ivjXmlTreeViewerApplicationJMenuBar = new javax.swing.JMenuBar();
			ivjXmlTreeViewerApplicationJMenuBar.setName("XmlTreeViewerApplicationJMenuBar");
			ivjXmlTreeViewerApplicationJMenuBar.add(getFileMenu());
			ivjXmlTreeViewerApplicationJMenuBar.add(getViewMenu());
			ivjXmlTreeViewerApplicationJMenuBar.add(getHelpMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXmlTreeViewerApplicationJMenuBar;
}
/**
 * Return the XmlTreeViewerApplicationPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getXmlTreeViewerApplicationPane() {
	if (ivjXmlTreeViewerApplicationPane == null) {
		try {
			ivjXmlTreeViewerApplicationPane = new javax.swing.JPanel();
			ivjXmlTreeViewerApplicationPane.setName("XmlTreeViewerApplicationPane");
			ivjXmlTreeViewerApplicationPane.setLayout(new java.awt.BorderLayout());
			getXmlTreeViewerApplicationPane().add(getXmlTreeViewer(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjXmlTreeViewerApplicationPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getExitMenuItem().addActionListener(ivjEventHandler);
	getToolbarMenuItem().addActionListener(ivjEventHandler);
	getStatusbarMenuItem().addActionListener(ivjEventHandler);
	getAbout_BoxMenuItem().addActionListener(ivjEventHandler);
	getOpenMenuItem().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("XmlTreeViewerApplication");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setJMenuBar(getXmlTreeViewerApplicationJMenuBar());
		setSize(460, 300);
		setTitle("XmlTreeViewerApplication");
		setContentPane(getJFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		/* Set native look and feel */
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		/* Create the frame */
		XmlTreeViewerApplication aXmlTreeViewerApplication = new XmlTreeViewerApplication();

		/* Calculate the screen size */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		/* Create the splash screen */
		XmlTreeViewerApplicationSplashScreen aXmlTreeViewerApplicationSplashScreen = new XmlTreeViewerApplicationSplashScreen();
		aXmlTreeViewerApplicationSplashScreen.pack();

		/* Center splash screen */
		Dimension splashScreenSize = aXmlTreeViewerApplicationSplashScreen.getSize();
		if (splashScreenSize.height > screenSize.height)
				splashScreenSize.height = screenSize.height;
		if (splashScreenSize.width > screenSize.width)
				splashScreenSize.width = screenSize.width;
		aXmlTreeViewerApplicationSplashScreen.setLocation((screenSize.width - splashScreenSize.width) / 2, (screenSize.height - splashScreenSize.height) / 2);
		aXmlTreeViewerApplicationSplashScreen.setVisible(true);
		try {;
				Thread.sleep(3000);
		} catch (InterruptedException ie) {};
		aXmlTreeViewerApplicationSplashScreen.dispose();

		/* Pack frame on the screen */
		aXmlTreeViewerApplication.pack();

		/* Center frame on the screen */
		Dimension frameSize = aXmlTreeViewerApplication.getSize();
		if (frameSize.height > screenSize.height)
				frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
				frameSize.width = screenSize.width;
		aXmlTreeViewerApplication.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		/* Add a windowListener for the windowClosedEvent */
		aXmlTreeViewerApplication.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aXmlTreeViewerApplication.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of XmlTreeViewerApplication");
		exception.printStackTrace(System.out);
	}
}
public void showAboutBox() {
	/* Create the AboutBox dialog */
	XmlTreeViewerApplicationAboutBox aXmlTreeViewerApplicationAboutBox = new XmlTreeViewerApplicationAboutBox(this,true);
	Dimension dialogSize = aXmlTreeViewerApplicationAboutBox.getPreferredSize();
	Dimension frameSize = getSize();
	Point loc = getLocation();
	aXmlTreeViewerApplicationAboutBox.setLocation((frameSize.width - dialogSize.width) / 2 + loc.x, (frameSize.height - dialogSize.height) / 2 + loc.y);
	aXmlTreeViewerApplicationAboutBox.show();
}
public void viewStatusBar() {
	/* Hide or show the statusbar */
	getStatusBarPane().setVisible(!(getStatusBarPane().isVisible()));
}
public void viewToolBar() {
	/* Hide or show the toolbar */
	getToolBarPane().setVisible(!(getToolBarPane().isVisible()));
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D4D715FCDDF4F1BFD43622D2B7EBD90A95B5F663AE552C321F35C5CD373426D92838D20BAD3A29C645AD6DD26553C7FECAD2822490EA5A52B685EAC321A17C3405701F9FC8426407E4128CCDC272B2731219E4E65EE466CDE600A0FB4E3D6F5CF9B3F913C928AC5FF7386F1E7BF34EBD775CF34F3977CE24625767662D4FAE162475F9127E6F7D75125452A2C943A597F389F7642D0C8249706F
	8D009724BFEF408AF5D016B21AD1D0A47D17AF9F725D105F27E5947C893E37C90F7996DB60A38E1DA4A87B58F77FA6EFE2BAAB6A084E86162F3094C23A99208FF018A63F05746BACA5BAFE3A0EA7D81FADC9B34A300E5D32C3472D06BA79002B813015717ABEC8B9839F29BC22576B7E7B85521A7F70E7EF42FE8CFD92246BED5768E929743B75C9FB1056CC46A7365987F987G8EB4C7BA71DCB92447DB0687CF5A7BDDDAF95863AE9FF5A9A145DFD9E973382DFEC569E059B3BE1F5BE517B5176A15241237314F
	2516FC5607A42D047E66B3DCD29869F4433786B073ED425BAFA3BE933E253D9985A5FBE9ECA75EBA2E73EE3B7A8DE969C1CF6E16B8E32B584B47E45B9B995B1221C733DFE27330238165BD9B4AD28116G2C86A85A17D1D081708A1B7FDD75EFC3FA26DF2BD6BD9E558BA32AF2BB7C0D2AC3F143F7E5A5146838D6452E7A9D12446778D64BBE93FEA6825EEE6E145BF119ACB8C4731D5CFA173462DFBF133D41C4A6AB96DC4D2E5B47E55137AFA293CE7BC7F1E9CF5B972D0F5134BBA5A36D3411E5B9F9A634D7DD
	DA16B3DB6FE701017672FDB857C5EC2E5360FB3D2E9F9C7FB2436FF8A702673AB90F698C6677C1592CF7C8B7EC1B0997CB6EBC296840E7CD65B0675E1959EB5E61BC9416477272C3C673C8B92739D17072B3063799701C979743E3BE8CE5ABG38FE3D49566A697D99855DG9A40CC00E400B400957BC9475C757BA729E3AD32DF2B8A3A5C8E45AF7136EFB63C882992567CF23DB72049DE3B52A17BDDF23FDB89C8FA1D728614C1FFD43BC3CC776DC0631C62D57CEC0DC26F32C75194FF0057B95E70825671AB2C
	546A72A8C0406313285F1A06ED103A6500566EF3481A9229414BAD8DB8A723F2877448CC004465525A0872DA8763AF2E48A858DDC1F278BE6B6F3C6277AA6E4ACA0B2A8E87FDD69C0924D5201CD7B35BF1843EDBAAC846CDCD08CB05FC3A68678F8DDB8D7D54386C483E6C3FCCEB6C454BE80FD7D5104E7C5AC6BAD333692B523EE51FB65599DB6F321C7C833A2E9C08DDBB252CCF640B679704EDD3DC47D1EB2542FC3DC55B0A6FFCA89AB9894A39FC97B563DC5541F89DG538F100C0F1EAF65BADBA587945252
	E6BFA8186208C81C73796865E2DD1EC8B3CF673FC00C63172CBC897ADE8C1085D0FCB0236078C11225FA5996277D0D0EFD3CACE7B09A317DEF7CAB5160B25BEF41FDCA99D53CDA0D62D33C8E788868E56FD9101E3E0E87D45166B8437BE05DD743C28B88FC158557262CE9FED7FFD0D35A94CDF3F987A3658D0CD6A0586F0B188C22F54102E3D4C3306EC57D8E8B2E993B6A75AAEC72C4933D5C46562C62D6BC40F4359A74EA9CFF299175FED8392C5B0C7E4667A4DC736EA05A9F6EAF2448A8C71F5F658115C093
	A6ADBC086BF9C3E309C1C68E40215FB38352240334B6C33BE8ED6E3E72A5691EDEDF36595EB2678B1F4CDEF7102F1922C3113549F9EAEBB2677DA2339DBABE40F93DB20CBC4573E2781AF83A857B2BGBAB5G5D07E8CDACEB42F232BB7AE228D65DEEAED2A16BFB1B0BF0EE14112082669A8D5CF9A13F7B1B374614F5683264658FB723AC5D865B18FB98E5D9FA057CC28D78198910FC085B49ACDD9E73D93DD522DE8B641D07F9BDA6774346FAF32E8ABF9372796FC66A153DEB2CE79923FAB5006FB6549B0B2A
	A7DD23FAF3G1F82107EAE2FD7749E2F47657F6C4BA8FF66DB8E4923F238BC90823D2E2CECB80BFB9377A1C79A737575D9739E790A6F74601C0CC1FEB6C072FB24BF8FEFA2DFF1767082E93EFFE9CEBCBFF8314EC766FB917D5957974A61F3FE2A95E9163D1FD1F064FD1AEFCB0BF13FBA938828F697F7F6F9796FDB0C7A45F1520567A19DF07983DABD9800B04D6BF39710DF5CCBF5346EDB54DFA1BF4782F427836CB80278C7AE949BF8DF8478D5G79D016437ACB39F2906D83D33CF21E34A87E81556FD19CE5
	A859F2B92415B3DBD4FE06955762779E1A07BB7F04FCBADC811F9AD04A76F4102F775557CE6BB4175CB5CF4A17BF12ED66FB4FFCF2B05BF1044BF1661108BC4BB29E4DBE8E40473669AA0DEDA954A9BCCAE3CB37460E2D864A1CC7A97EF8FA987198F1D8D555DDA57B8D7C4D5BE6646F7875CB267C4D58624C9EBB2A6B675188FF77BC7CE8765207093F6F887ED6C31D32CA626FB36DE67C75D592FF732E95697C35E932968CF08EF91F41B16AD3037A49224F07BE98B71FD016FF0C7A5C53446C39D38D1D69D703
	DA159A46C173BE77DCAB54574F8E28EFBDC6FD96196A484CE324A3332EFDB89D39F77DDBCC175DCAB36CE6E51678CD0E812FFC1F9DED4498E3AE70307058F89FA7AD863782725C777EA7363ED79F0B0D8F3E42705271587860D08F4939884A346364AF3EE6C57B329C72EBG8ACF007F89D08360B8C16B7A93978EC9897C71FAAF3849DE595D96354112DF5C0AF4E1CF5473F52DA8AF36A7CA64EFB4DFC99467CDBB91AD8B5E2EA7E13B79B16D78DC2565A1AFA6F635D171866BB545236F491FE8B66E490B202F15
	G39A7A17EC6E036763ED60A61ABCE121D3DF656E8E797ED39163D51E45DA1B8CFF27EE61C0C2C3B2226E7F4BB1B560E7BC49A142D82D807B4CF01AF88E08398BBC5F374F06B31C4F344B44F2AF2B52C0E75ECF4F9EE3BFAE37370C642F6C927225BF17936361949F7A154CDBBC5725D50AE4EC8CE115E1E6AC439141C4EA8B88CE081F0824C84D8G3064B449E5E75B09C94A259AF0F560C70C178C0D717BB7A81BE5895BE51D0EEE47477D2098F76E99727D2F0E5D988F9FCB586E78993379696EB01B9FAB54F5
	1E2179496BA2BE2789BE77DF0B25179A533FB91FA74635FBADA63F740C192C4EDDA09E32201C47479FE7FE516E2A0C023AAA422DE2386EAABE8E0E1B4D787781EE0628F7F7B76212A1FFCF950D690BBD685BAF91792F31FC16489F6DC994FFE25B094AA782CE23B5A10DC4F16CC440574CDB5678F1D93955BCAEBBECB50F4BAA2DAFF0BC74E1BC2D91F11BB5C15CE6B50B5BDEE5637CEFED3BE49223B501E32C4BE11B7451AB85FA1DDACDEDBCA0117DAF78931EE747317F12339AF5E0EB9369FE91644B2A795C35
	D473B8026B5D771AC96FD22A49773E486221CC48CBB5D02F0674A32D55EC8DD654445789EE6B3EE7C33F25866A75810481E6812454100DDBE9CD38BF5706A7581FB35A634F770F5A4D677B31F60C9B82EA50EFD7E26774676DA522ACE2CDF1D3452961F5FE435AEB32FFD09927934F30763CACDE7BBF3647539BBE6F8DD78D73DEFDC058093FEB2379CA2F2179FA490A7315DF1BD1D08AF02416662B305DEC3EDAEA634F97672D3EB33EBCDBBA4D65595199DF1EBD1D0965B95099DF1E1E4E44729C6D1CD81EFF08
	1267C1E10FB2CCE56418C0C63C3FFF99B376F7C84C4F6C8E1A1FF0AD4D4F2ECE1C1F944827812C947373FC17996D35935066F6637E9737B01B9488A881367957481A2C73B5F04D4857E1B1CE4B85B3DA05E7930DF3FEA35AA807B2A0875DE09AA1F80458402FDF81C93633B83EEF33BD896DD269D90AC5FE4FF04C9AC13844ED5657C268C38EC0608FD35366D36CD245B96673F7111DCA0276A9G69G2B316FF3545F12EE6CC3F3E9EE340D61F3F8AF77D02FA478FC258F671F1FAA000EE84CDCFB31AE77839E6F
	0972834EF19B389252B5GBC461853C477E305F590C354D12CF64B511725383696C687BEBA1B67E52C7D98341B8D10DCC73EF4784F64CBA75FB9CF1A63B5BF33D8E93D163D38CEBF33280B7852AB079E4D66F378C033599C6E2E0FBF07BC26FA105973A375BC264A9378658CEFB160399CFADBA87E6C0332457564D3BC464FCC9A4046B4106DFE48965FD6AC33195B0A9F5B4C775A1F5AA65EEB1F3445DB63DCBE1FECB313CFCDC3A279FC1C59CFDBC32CFC66B03C3BA1D6BE2FDBC9BE5320ECD58349E7D7BB4927
	6EBC611E94B86BF9DA7737F5187159F7BE911F1FE5F6C4BB4F7959A070F7B07CCC83DEBFC7E0F84C4F0732FC8B7174C697716496B87B854205AD34669EE1313DC1654BBBDDDE079A027505B7D3DA203C263E2D25392D36373929375A829FDC5F67060CF755C71E3F45740C97E13A050FF90E1E2E5B7E39755C0EBE4CF844B669D0165F0838BFF6994FBF10470B5912D48EA9AFBF50EDACC7DD59A3994B6DD165486BEDB94672CFC71567995A73F1BD70A62D63F58F24C86E3B27473D03EFE964635174F4562A5B56
	E77E73159C5E4F8F3A0C72A97A1CBBFBFD1CFE127576290D91FB40ED52D3FB621C2B2255F3F3993EFC096CF2D6A36DC5F77522CE6CEE02989C405AC4BA7979269B3B5FCE1EF2BB2E33756C9E4A5D942B4B6D8CBF3DA9D617FF5CC43A1C84E5D2B345518F49A887876427814C81C881C88748EAA69B353D2FC2CA60DFDAD51F850C185BE0F6783AF83C19560554C2327ACFF68EDC88795D8277930643F3F0BC2B67B82545986BE0DD316F365CF89C95AD7F3FC451480BD99F9F168637834FB6E31C2801A1677B6EB5
	BBEAB1174F1DEDA41F63C2961F32A22E8E728E017BBC43B9753B870EFB53BA157B4E3A9819B992481053B8B9A59A31B24BCBD01F4BE32EC3925F77A9915924F410EC268BB97CA3334933A01F8210AE70DFE2785510AF3A406FDD74F3084E295E8927CCA017D9BA4DE89955CD1946C4F249517B0D16D149E0C4C6471C784D57EF2693E5D481E336DC20755A533797528EF9C8DF29358E17267A59D285E40BDF858FD43B4C316CB6CD9F47A3267E74C4406D71F7B610DDDF784DAFCB299F2D09FB56693E205F0168E9
	5993372D5FAC4652BA0436E89114AD87D887D05C8AF1B74069D69A5B7CBE6491A3DAE07F0C2649F6A73BCDA2C304D71491B4BA493C6F60B0F24A7B78C55F1B716483BDB440F6505CBF84C112D59E94F37C7A25D859D8DBA3FA60EC9D2FB35C9E6F6CE3BEEDEB2C1DAEE778592D31F6FAE1AF5969E4A8ABEAA3BDEEE6F6DD037C8C00A4016FE278D57A1D8B5E11F07CCF1BD0A7315E0735E1841A5E578CC17BF70C46063FD27FC67B11A7666A07F2FC7F78A73239FF3CCAB6770FFFA1CFFC5674BFF27CB7824F4A46
	B782FC9DAEF3CB925978ADEF60BC603D8FDDA0F07C47DA91DF9BF6E90467ED57FAA2EDEFE7F7F2F8F757AF7B457D83AF7B42D664195F1B99CB09078AB552CF49E13CFF2DD35C3EDE580FDD76C0CCDFA535681FD5296AF02037596BF6F915E8DE6F6EF8012DADB52875D629E183ADFEFF3622536CCEAE250D6E5766594C4A9737515A7D15F333D17E5C746872DD634436AE2FCBF349EE579596A037C1C56FA0DF3F128F4B17C3DFEBGCA2C99857BGCEDB296F30EBA7378BA6B62156B128104FCD743A06626BD57F
	D062B7E443C3F89F5C2FEA1A6A417E89BFB2046BC1D3FD8C291F2D8C21BE7ADD03CE0DD75571E3C3E83B5D4AGC373BD6FBD66F3F64338FCD60AAD46FA69FCFA361556DE99339F2910CF87489278438CDF5C8EB1A6C0F7BB613F59BB19BDAAD56F8B3F532D13379D3ADFB7A99A467DAD11FF905BEE1B74EEB14A622C4B4DF417632685900716BF02BB3FABDFDF8B08637E7CB61B3827E827735B9F89DCEABB69D23EAF3EEEEC71A5560D9276EEDA01B935DE766967EB2F7A709D10BAB090D0048E6E749589DCFD20
	C58D38D06BB97F0D17303EDB3E8C0BD1524F239D17688CE6B5701A5F41672782D23C03286B40BA1F6AADC9A8434D5E0D3A3D29127D6C14C737B51BB789DBC3A554E6E5986F5B623D83422A7C1CE8304FC8FF164EDBAA24CBGF81DDD1E5833A42C338A4A570ABAA7B533BAC51D90B3F412CE17584C7C15631D7175D60FB5CC5B35CC50CEFF7FFD094E379CD0B729134E37FEDE9EF5170CD77E1515D5AA3EF73270896431637D2C7DD2E8178510EB23732D21E1721FF6FC658E9623460B0BF758B80F27ED11F81656
	D10E54F7BD4EB91BBE55F446FAFB1FD887B60AEB1F1319BD01FCBAC0260D56C71707E2E069A2554DD159F99864DBG1C97296E28C6F527093A2D232CDF482783E4DEA41B365FA662428F8877344095FEC0F3FC6BB83FEBF236E86E145BF13F6C84733FCABE60FE59FA01BF4B63FE831E6B448F04CED4C0596C8FA82E7E7C886A6FDA4897F701AD86E881F0G0CF5111D79068F4F3D6379692D2A4A0EE071B4D6063D5A4F36B0BFFCEA7A9839AF9CEFBC333A627924D758DC4F6F0A75C9BF4A700B3AE2FD52C219FC
	5225B816EE1A1BBECFAC7D246E884D146EF874C3CCFF96F7471E975E42704B3BE34F8B8BD4BAAF4C0232169EF1972E4552DF5893211956930F7E7718CE2E6809FDBB134E70D9BD31EFE7EC2374F6E69D1475DD4AA8603E577B68BA6AFE585D1AA4F42F3AB772EDB1FCAF3B74E15EA7CFCE6FFFFDD3E9F03D7D62C87C7D712E11447B63B7C64CFD27FF9F996FBB5DBBE26EBB250D0C771D3E55C7FE5262CB24A353186E2E06FCFEEFC6C1E9AF61E7B27C91485BG42823FD51E2C7C30CFCC315F49540F505EBBE99A
	46F8E832BA40E59172109FB927176CEB2A176433C870F2CDC5DCA66425BE424DE03892489F9138AD6AD4748A7B1ACC3DCCDDF63ACFBBA59A13158949104B60D60D64E26BA339940E12DC4644F8170C62195D8211FF0465970B7C25D1A32F8BA6B95E292D65D0940D3F46F9249156756158B37ACAA71A9FFDDC5C873C4F01EB31BA8831B1ACDD2F40E7DC4775DC2D7AAED381473F768A7A19ADF2C0D374829EA7FEDBB64609B5B26C11604F0E4994C756854D4AE70972D7AF1A154F07147B49F305FF19AAC77C64F4
	1947C4FC5CBF70CEB8EE59B86E4737637838DF5C9898BCC078BBDE0A429F24B8791B2A113FF5BA5D52FE627F8FA3E665077B097FEF895F372E1F735F8729564F63B94E7F2E5109781F59EF647F59E174A723FCF7096EF5B7040CFE76DC1DC6BA242B7A699E6BB7FD38561AD43F06870F7A6F9094234F3A63755359F8F6E066332E53F9A9331B792CD9A5F8C744FFBB29FA4ADD7D1E72602827DC6E574A95F007A24E2B50B96EEBBEA3631AC57D6A36137FF9BDC8385982E73DC838A5F63A27CE73104F166B48A8B8
	6EA0DFB54F4B4E58A0BF93F18E9AFF1697F9ED83C75EA978FEEAA04E7D11C56E277B235FB2BB0AEBF9110378F957CF38B58E626789CD7C2ED4819FD1213AFB03F40717240837C5C196C3C0BE8BA0F700F8FCCB43F831DA41F73844E326ED17B4295F256AB2BD952238E047G51FBB204742C10F783CC9FA07F7631A06ADF9264D381D6G2C81489F84BD87B88CE099A4BF77D92DCC0A6F6772C0A9625CF25B77FB99FDBB67E034DD9D53733374F43E1E268E12DD1E9E44F6CBE25A2D5473EB74B45F49539DCEEAF7
	71A2362BF0C6372B5173B6BDF56B69B4516E21F1BE7064EC7B0AA93763BE6AE52623331D317149AD8CBF57999B1FEC7412EEA5C3D9110B78FEC6B55257DC7A592E1EA639466FE73CDD255F58AEC52F37D8CF1747ED7764B8DFFDF2F27A5F9BECD7913CB179BE4856D716ABD63E0FB07CDAD72CFCFB0342168C017D9EA2795604A6223FE4A8DEEC5243FE473BF2A8167EG43E78D45527FCD08682F05326EE1DAF33F88467751FF9BCC6C237FAEE86E23CB2363FD743CD1F39FFD632851C7673822D15E2FDFBC5464
	7897C7F10F6267DD0D32D79EE4FB94774F3676E3BF774058520749E7BBB9CAE7852B846EF606DB8D79DC37F8834C7C3DA2489F963847992E8E72CE017B07A051989338FDE122B1AB52DF18E8A4895C3F59CCE858465370DDA49A8B846EA98F51D8A4F0D6AF51C89338EF3B475348F49BE9F03D3B0EDBA47D0653707D027766471EEF5FD49AFC4C1B58596AD298EF2610C35B08B8B77290EE61887974A582F741CFEDEB846E56G35F58A5CD381EA2BF9441B05E0C416AB8C5FCA70664B75AFB715869FF3D70846BC
	D70C599E0E0C53ED78FEBAF473473C79C37D16BB91F01FE9CFBF46F4AE3B6A656FB0CFB1BB3384463F5CC3E7BCFF9B12C47D2F0F220F65117D03AEAF1D7BFCB99CA91F2F21DDF52AFE5795BC33F3DB414452293EF86B2911CF375BCB3E5B339A4683230ADFF35923DB71B65B0264B3DA218D47ADE63869101F65A5DE9F3C0C3C706F55D7701B573D0D591D95D0AFD3546D9C0B547D63354898F633FB8AEE1F695E61180FE20443AA78F52A6E83C10AE7843C0F1D57318F9E1BBCB0D2BA016C67686DF95FD77C5477
	82C35F77287C6C0177DDABC57AFEDE1B28EF0AFBF8DDFF086AAED7476F37D9BA0E53F8B2BBA2033D23781BA12F92AAB53E5D2B774026D48F4AD2064F2871E239ECCFB9FC36AA7CE5F6594E7D7620D6BE689A5839E70F686B918D7F660D311FB67CBF7284303A3649DA5BAA6A3F9DCCD03F2A59EAEDEE94FCBBF3A3FC3F94BEF903FCC368AE98677D7EB8AF526FCA4F299B6B570721BFF54C7D953B0F7C954987F36C23BB143CD2FC13919BCF15F3C52C2C2C556CCE45D1252B25FE1FB217756503BEE6G4C7551FA
	4A9CC121F4AA81CD7F5BA8FE22198AF5D67BA836F90C614BC6B28ACE8358C6A86E7ADC086EB1FCA354E7C64898DFF19B65725D18EF9A1AF2BB6E83960FA03F8BC6E2FD43D799FE61C82CEFF8271075BDD0365BAF7E96077F46787E7A145BF17E5A8248478CFFAC5F5D8CBF5B9F4B774F44FC4D05325CG71FDFB14CF6D847CD8E0B2EF58DE9E114477AE43F7326F666FE35F7DFF20F1B19099D33F617B177E1BCFBBB7F073FD132F3375B88B66FAB81BF05FE2388500FBA21B4EBB64BF235E68EFC8BADD8E4D4971
	BB421BA278BA85639448733FE7F6AEAF8C6D13FFA565E9D2C93C6BEDCD4A4597BC1A34C1FF2423C98562C98EFC33A7B325D5EA98AA6293A0C8700582A45895A4788EC113B6E2D24A1F66E8D2A1FB08D34A9F62GC3813F34C57CE29C62B2C58B2C9CBDD41973449DB959D2EE3D749C8F466A25DC0C286A25BC96F055CBB9900C05710F90C57D438EC7243FC805D3A8589CB962AC05FF1A74FC64EC93385BE5F6C0D70EC11CC36C859F5C57176A3CFF7B4EECA92FDE5AB860D25C0EB6369BD824A2162BD182F63F4B
	47253A157DC4FE3FDB768E6E670FFD0ED92497D8BD5AC530FBE4F73BC55AE6688E456B0D94EDB6943537DAE0A83CE7906CFE489FE372D92FC94565655D23F6456DAE8DFB5C5D7AF62DC97934F3E3F7A5BEA61FE25EE2FBF48BFD2304B9971BA6D6FEA52A4D0BD1ED44A688720C6C08D87DB59FBFCB5DF35D781EFD6DC76CF15F7B2F99554F2FC21133D4FE9E77EABE2E079A407705688C4033619DA97EBB47B60F6CF60317BAFC466B40B70FD517B5A57296EEE7BE769F20BAFAA1AF5B130FFC8F995A7C9FD0CB87
	88DE6177A6E39AGGF8D0GGD0CB818294G94G88G88GF3FBB0B6DE6177A6E39AGGF8D0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1D9AGGGG
**end of data**/
}
}
