/*
 * Created on Jan 9, 2004
 */
package de.torstennahm.integrate.visualize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import de.torstennahm.integrate.IntegrationResult;
import de.torstennahm.integrate.error.FastConvergenceEstimator;
import de.torstennahm.integrate.visualizerdata.CorrectValue;
import de.torstennahm.integrate.visualizerdata.NewResult;
import de.torstennahm.integrate.visualizerdata.VisualizerData;
import de.torstennahm.math.MathTN;

/**
 * Plots the estimated integration result as function of the number of evaluations.
 * If the correct result is known and provided via <code>CorrectValue</code>, this
 * is displayed as well.
 * 
 * @see de.torstennahm.integrate.visualizerdata.CorrectValue
 * 
 * @author Torsten Nahm
 */
public class ResultVisualizer implements Visualizer {
	static private final double SAMPLEFACTOR = 1.05;
	
	private final JFrame frame;
	
	private boolean destroyed = false;
	
	private JPanel resultPanel;
	private JPanel mainPanel = new JPanel();
	private JScrollBar zoomXBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 1, 1, 500);
	private JScrollBar zoomYBar = new JScrollBar(JScrollBar.VERTICAL, -101, 1, -500, -100);
	private JScrollBar shiftYBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, -100, 100);
	private JTextField yField = new JTextField();
	private JTextField callsField = new JTextField();
	private JPanel plotPanel = new JPanel();
	private JToggleButton[] plotButton;
	private String[] plotButtonName = { "Error", "Var1", "Var2", "Reg", "Error Est" };
	
	/* synchronized by resultList */
	private List<ResultEntry> resultList = new LinkedList<ResultEntry>();
	private double correctResult = Double.NaN;
	long calls;
	private FastConvergenceEstimator estimator = new FastConvergenceEstimator();
	
	/**
	 * Creates the visualizer within the specified <code>JFrame</code>.
	 * 
	 * @param frame <code>JFrame</code> for the visualizer's graphical output.
	 */
	public ResultVisualizer(JFrame frame) {
		this.frame = frame;
	}
	
	public void init() {
		if (destroyed) { return; }
		
		resultPanel = new ResultPanel();
		
		plotPanel.setLayout(new GridLayout(1, 0));
		plotButton = new JToggleButton[plotButtonName.length];
		for (int i = 0; i < plotButton.length; i++) {
			plotButton[i] = new JToggleButton(plotButtonName[i]);
			plotButton[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resultPanel.repaint();
				}});
			plotPanel.add(plotButton[i]);
		}
		plotButton[0].setSelected(true);
		plotButton[4].setSelected(true);
		
		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		mainPanel.setLayout(g);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.0;
		mainPanel.add(plotPanel, c);
		yField.setEditable(false);
		callsField.setEditable(false);
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(yField);
		panel.add(callsField);
		mainPanel.add(panel, c);
		mainPanel.add(zoomXBar, c);
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 1.0;
		mainPanel.add(resultPanel, c);
		c.weightx = 0.0;
		mainPanel.add(zoomYBar, c);
		mainPanel.add(shiftYBar, c);
		
		AdjustmentListener listener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				resultPanel.repaint();
			}
		};
		zoomXBar.addAdjustmentListener(listener);
		zoomYBar.addAdjustmentListener(listener);
		shiftYBar.addAdjustmentListener(listener);
		
		frame.getContentPane().add(mainPanel);
		
		frame.setVisible(true);
	}
	
	public void destroy() {
		if (destroyed) { return; }
		
		destroyed = true;
		frame.dispose();
	}
	
	public void start() {
		if (destroyed) { return; }
		
		synchronized (resultList) {
			resultList = new LinkedList<ResultEntry>();
			correctResult = Double.NaN;
			calls = 0;
			estimator = new FastConvergenceEstimator();
		}
		resultPanel.repaint();
	}
	
	public void stop() {
	}
	
	public void submit(VisualizerData data) {
		if (destroyed) { return; }
		
		if (data instanceof CorrectValue) {
			correctResult = ((CorrectValue) data).correctValue;
		} else if (data instanceof NewResult) {
			IntegrationResult result = ((NewResult) data).result;
			synchronized (resultList) {
				if (calls < 100 || result.functionCalls() >= calls * SAMPLEFACTOR) {
					calls = result.functionCalls();
					resultList.add(new ResultEntry(calls, result.value(), result.errorEstimate()));
					resultPanel.repaint();
				}
				estimator.log(result.functionCalls(), result.value());
			}
		}
	}
	
	private class ResultPanel extends JPanel implements MouseMotionListener {
		private static final long serialVersionUID = -9206803170275527901L;
		
		ResultPanel() {
			addMouseMotionListener(this);
		}
		
		@Override
		synchronized public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int width = getWidth();
			int height = getHeight();
				
			for (int i = -20; i < 30; i++) {
				g.setColor(i % 5 == 0 ? new Color(0, 0, 0.6f) : Color.GRAY);
				int y = (int) (-foldY(Math.pow(0.1, i)) * height) + height;
				g.drawLine(0, y, width - 1, y);
			}
			synchronized (resultList) {
				g.setColor(Color.GRAY);
				for (long callRun = 1; callRun < calls * 10; callRun *= 10) { 
					int x = (int) (foldX(callRun) * width);
					g.drawLine(x, 0, x, height);
				}
				
				int x, y;
				ListIterator<ResultEntry> iter;
				Connect connect;
				
				if (plotButton[0].isSelected() && ! Double.isNaN(correctResult)) {
					connect = new Connect(g);
					iter = resultList.listIterator();
					while (iter.hasNext()) {
						ResultEntry entry = iter.next();
						double err = entry.result - correctResult;
						x = (int) (foldX(entry.points) * width);
						y = (int) (-foldY(Math.abs(err)) * height) + height;
						
						g.setColor(err >= 0 ? Color.BLACK : Color.RED);
						connect.to(x, y);
					}
				}
				
				if (plotButton[4].isSelected()) {
					g.setColor(Color.MAGENTA);
					connect = new Connect(g);
					iter = resultList.listIterator();
					while (iter.hasNext()) {
						ResultEntry entry = iter.next();
						if (! Double.isNaN(entry.estimate)){
							x = (int) (foldX(entry.points) * width);
							y = (int) (-foldY(entry.estimate) * height) + height;
							connect.to(x, y);
						} else {
							connect.init();
						}
					}
				}
				
				if (plotButton[1].isSelected()) {
					g.setColor(new Color(0.0f, 1.0f, 0.0f));
					connect = new Connect(g);
					double max = 0.0;
					double curResult = Double.NaN;
					iter = resultList.listIterator(resultList.size());
					while (iter.hasPrevious()) {
						ResultEntry entry = iter.previous();
						if (Double.isNaN(curResult)) {
							curResult = entry.result;
						} else {
							max = Math.max(max, Math.abs(entry.result - curResult));
						}
						
						if (max > 0.0) {
							x = (int) (foldX(entry.points) * width);
							y = (int) (-foldY(max) * height) + height;
							connect.to(x, y);
						}
					}
				}
				
				if (plotButton[2].isSelected()) {
					List<Double> varList = new LinkedList<Double>();
					
					g.setColor(Color.BLUE);
					connect = new Connect(g);
					iter = resultList.listIterator(resultList.size());
					double eMax = Double.NEGATIVE_INFINITY, eMin = Double.POSITIVE_INFINITY;
					while (iter.hasPrevious()) {
						ResultEntry entry = iter.previous();
						eMax = Math.max(eMax, entry.result);
						eMin = Math.min(eMin, entry.result);
						
						if (eMin != eMax) {
							varList.add(0, new Double(eMax - eMin));
							x = (int) (foldX(entry.points) * width);
							y = (int) (-foldY(eMax - eMin) * height) + height;
							connect.to(x, y);
						}
					}
				}
				
				if (plotButton[3].isSelected()) {
					double currErr = estimator.getEstimate();
					double slope = estimator.getSlope();
					
					g.setColor(Color.CYAN);
					x = (int) (foldX(calls) * width);
					y = (int) (-foldY(currErr) * height) + height;
					int x2 = (int) (foldX(1) * width);
					int y2 = (int) (-foldY(currErr * Math.pow(calls, -slope)) * height) + height;
					g.drawLine(x, y, x2, y2);
				}
			}
		}

		public void mouseDragged(MouseEvent e) {}

		public void mouseMoved(MouseEvent e) {
			int width = getWidth();
			int height = getHeight();
			double x = unfoldX((double) e.getX() / width);
			double y = unfoldY(-((double) e.getY() - height) / height);
			yField.setText("Y: " + y);
			callsField.setText("Calls: " + (long) x);
		}
	}
	
	private double foldX(double x) {
		double maxX;
		synchronized (resultList) {
			maxX = calls;
		}
		double mul = zoomXBar.getValue() / 100.0;
		return MathTN.log10(x / maxX) / 10 * mul + 0.98;
	}
	
	private double foldY(double y) {
		double mul = -zoomYBar.getValue() / 2000.0;
		return (MathTN.log10(y + 1e-100) + 15 + (shiftYBar.getValue() / 10.0)) * mul;
	}
	
	private double unfoldX(double foldedX) {
		double maxX;
		synchronized (resultList) {
			maxX = calls;
		}
		double mul = zoomXBar.getValue() / 100.0;
		return maxX * Math.pow(10.0, (foldedX - 0.98) * 10.0 / mul);
	}
	
	private double unfoldY(double foldedY) {
		double mul = -zoomYBar.getValue() / 2000.0;
		return Math.pow(10.0, foldedY / mul - 15 - shiftYBar.getValue() / 10.0) + 1e-100;
	}
	
	static private class Connect {
		int lastx, lasty;
		Graphics g;
		
		Connect(Graphics g) {
			this.g = g;
			init();
		}
		
		void init() {
			lastx = -1;
		}
		
		void to(int x, int y) {
			if (lastx != -1) {
				g.drawLine(lastx, lasty, x, y);
			}
					
			lastx = x;
			lasty = y;
		}
	}
	
	static private class ResultEntry {
		long points;
		double result;
		double estimate;
		
		ResultEntry(long calls, double result, double estimate) {
			points = calls;
			this.result = result;
			this.estimate = estimate;
		}
	}
}
