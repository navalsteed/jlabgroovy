/*
 * Created on Jul 20, 2003
 */
package de.torstennahm.integrate.sparse.visualize;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.visualize.Visualizer;
import de.torstennahm.integrate.visualizerdata.Integrand;
import de.torstennahm.integrate.visualizerdata.VisualizerData;
import de.torstennahm.math.Function;
import de.torstennahm.math.IntEntry;
import de.torstennahm.math.SparseIntVector;

/**
 * @author Torsten Nahm
 */
public class ExtentVisualizer implements Visualizer {
	private final JFrame frame;
	private JPanel panel;
	
	private boolean destroyed = false;
	
	/* synchronized by lock */
	Object lock = new Object();
	private SparseIntVector extent;
	private int maxSum;
	private int dimension;
	
	public ExtentVisualizer(JFrame frame) {
		this.frame = frame;
	}
	
	public void init() {
		if (destroyed) { return; }
		
		panel = new ExtentPanel();
		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}
	
	public void start() {
		if (destroyed) { return; }
		
		synchronized (lock) {
			extent = new SparseIntVector();
			maxSum = 0;
			dimension = 0;
		}
		
		frame.repaint();
	}
	
	public void stop() {
	}
	
	public void destroy() {
		if (destroyed) { return; }
		
		frame.dispose();
		destroyed = true;
	}
	
	public void submit(VisualizerData data) {
		if (destroyed) { return; }
		
		if (data instanceof Integrand) {
			synchronized (lock) {
				Object integrand = ((Integrand) data).integrand;
				if (integrand instanceof Function) {
					dimension = ((Function) integrand).inputDimension();
				} else if (integrand instanceof Evaluator) {
					dimension = ((Evaluator) integrand).dimension();
				}
			}
		} else if (data instanceof IndexContribution) {
			Index index = ((IndexContribution) data).index;
			boolean repaint = false;
			
			synchronized (lock) {
				for (IntEntry entry : index) {
					if (entry.getValue() > extent.get(entry.getNumber())) {
						extent.set(entry.getNumber(), entry.getValue());
						repaint = true;
					}
				}
				
				if (index.sum() > maxSum) {
					maxSum = index.sum();
					repaint = true;
				}
			}
			
			if (repaint) {
				panel.repaint();
			}
		}
	}
	
	private class ExtentPanel extends JPanel {
	    static final long serialVersionUID = 7799300299700571253L;
		static final int LEVELHEIGHT = 10;
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int width = getWidth();
			int height = getHeight();
			
			g.setColor(Color.BLACK);
			int x, lastx = 0, y, lasty = 0;
			
			SparseIntVector extentCopy;
			int maxDimension;
			synchronized (lock) {
				if (extent == null) {
					return;
				}
				extentCopy = extent.duplicate();
				maxDimension = dimension != 0 ? dimension : extentCopy.size();
			}
			
			if (maxDimension <= 32) {
				lastx = 0;
				for (int i = 0; i < maxDimension; i++) {
					x = ((i + 1) * width) / maxDimension;
					for (int j = 0; j < extentCopy.get(i); j++) {
						g.fillRect(lastx, height - (j + 1) * LEVELHEIGHT, x - lastx - 1, LEVELHEIGHT - 1);
					}
					lastx = x;
				}
			} else {
				for (int i = 0; i < maxDimension; i++) {
					x = (int)(((i + 0.5) * width) / maxDimension);
					y = height - extentCopy.get(i) * LEVELHEIGHT;
					if (i > 0) {
						g.drawLine(lastx, lasty, x, y);
					}
					lastx = x;
					lasty = y;
				}
			}
			g.setColor(Color.RED);
			int xSize = width / 16;
			for (int i = 0; i < maxSum; i++) {
				g.fillRect(2 + i * xSize, 1, xSize - 2, 10);
			}
		}
	}
}