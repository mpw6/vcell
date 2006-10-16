package cbit.vcell.simdata;

import org.vcell.expression.ExpressionFactory;

import edu.uchc.vcell.expression.internal.*;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
/**
 * Insert the type's description here.
 * Creation date: (6/25/2001 3:05:25 PM)
 * @author: John Wagner
 */
public class RowColumnResultSetTest {
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 12:10:58 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		java.util.Random random = new java.util.Random();
		RowColumnResultSet r = new RowColumnResultSet();
		r.addDataColumn(new cbit.vcell.simdata.ODESolverResultSetColumnDescription ("t"));
		r.addDataColumn(new cbit.vcell.simdata.ODESolverResultSetColumnDescription ("x"));
		r.addDataColumn(new cbit.vcell.simdata.ODESolverResultSetColumnDescription ("y"));
		int N = 500;
		double w = 80.0/N;
		int SAMPLING = 150;    // 50;
		double t[] = new double[N];
		double x[] = new double[N];
		double y[] = new double[N];
		for (int i = 0; i < N; i++) {
			t[i] = ((double)i)*w;
			x[i] = 1e-5*Math.sin(t[i]);
			y[i] = 1e-5*Math.exp(-t[i]*10);
			r.addRow(new double[] {t[i], x[i], y[i]});
		}
		r.addFunctionColumn(new FunctionColumnDescription(ExpressionFactory.createExpression("1e-5*pow(sin(.3*t), 2)"),"newFunc", null, "newFunc", false));
		long startTime = System.currentTimeMillis();
		r.trimRows(SAMPLING);
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed Time: " + elapsedTime);
		double t2[] = r.extractColumn(0);
		double x2[] = r.extractColumn(1);
		double y2[] = r.extractColumn(2);
		double f2[] = r.extractColumn(3);
	System.out.println("size: rows="+r.getRowCount()+", columns="+r.getColumnDescriptionsCount());
		
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			cbit.plot.Plot2DPanel aPlot2DPanel;
			aPlot2DPanel = new cbit.plot.Plot2DPanel();
			frame.setContentPane(aPlot2DPanel);
			frame.setSize(aPlot2DPanel.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.setVisible(true);
			aPlot2DPanel.setPlot2D(new cbit.plot.Plot2D(new String[] {"plot1","plot2","plot3","plot4", "plot5"},new cbit.plot.PlotData[] { new cbit.plot.PlotData(t,x), new cbit.plot.PlotData(t2,x2), new cbit.plot.PlotData(t,y), new cbit.plot.PlotData(t2,y2), new cbit.plot.PlotData(t2,f2) }));
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
