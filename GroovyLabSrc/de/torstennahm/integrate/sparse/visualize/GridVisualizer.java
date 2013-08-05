/*
 * Created on Apr 8, 2003
 */
package de.torstennahm.integrate.sparse.visualize;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.FastIndex;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.visualize.Visualizer;
import de.torstennahm.integrate.visualizerdata.Integrand;
import de.torstennahm.integrate.visualizerdata.VisualizerData;
import de.torstennahm.math.Function;
import de.torstennahm.util.ColorScale;
import de.torstennahm.util.ColorScales;

/**
 * @author Torsten Nahm
 */
public class GridVisualizer implements Visualizer {
	static final int GRIDSIZE = 10;
	static final double BOXSIZE = 1.0;
	
	private final JFrame frame;
	private final String title;
	
	private JPanel indexPanel = new JPanel();
	private GridContributionPanel contributionPanel;
	private GridStatusPanel statusPanel;
	
	final JComboBox modeBox = new JComboBox(new String[] { "Integral", "Time", "Both" } );
	final JComboBox colorBox = new JComboBox(ColorScales.values());
	final JTextField resultField = new JTextField();
	final JTextField statusField = new JTextField();
	final JScrollBar dim0Bar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
	final JScrollBar dim1Bar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
	final JScrollBar timeBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
	final JScrollBar colorFactorBar = new JScrollBar(JScrollBar.VERTICAL, 4, 1, 0, 9);
	final JScrollBar colorOffsetBar = new JScrollBar(JScrollBar.VERTICAL, 0, 1, -9, 9);
	
	/* synchronized by class */
	private boolean destroyed = false;
	
	/* synchronized by lock */
	Object lock = new Object();
	private int dimension;
	private int maxDimension;
	private int indexCount;
	
	/* synchronized by statusGrid */
	String[][] statusGrid = new String[GridVisualizer.GRIDSIZE][GridVisualizer.GRIDSIZE];
	
	public GridVisualizer(JFrame frame) {
		this.frame = frame;
		title = frame.getTitle();
	}
	
	public void init() {
		if (destroyed) { return; }
		
		contributionPanel = new GridContributionPanel(this);
		statusPanel = new GridStatusPanel(this);

		frame.getContentPane().add(indexPanel);
		
		indexPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.0;
		indexPanel.add(modeBox, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		indexPanel.add(colorBox, c);
		
		indexPanel.add(dim0Bar, c);
		indexPanel.add(dim1Bar, c);
		indexPanel.add(resultField, c);
		indexPanel.add(statusField, c);
		
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new GridBagLayout());
		contributionPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		c.gridwidth = 1;
		c.weighty = 1.0;
		viewPanel.add(contributionPanel, c);
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		viewPanel.add(statusPanel, c);
		c.weightx = 0.0;
		viewPanel.add(colorOffsetBar, c);
		viewPanel.add(colorFactorBar, c);
		
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		indexPanel.add(viewPanel, c);

		c.weightx = 1.0;
		c.weighty = 0.0;
		indexPanel.add(timeBar, c);
			
		AdjustmentListener barListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				contributionPanel.repaint();
				statusPanel.repaint();
			}
		};
		dim0Bar.addAdjustmentListener(barListener);
		dim1Bar.addAdjustmentListener(barListener);
		timeBar.addAdjustmentListener(barListener);
		colorOffsetBar.addAdjustmentListener(barListener);
		colorFactorBar.addAdjustmentListener(barListener);
		
		ActionListener comboListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contributionPanel.repaint();
				statusPanel.repaint();
			}
		};
		modeBox.addActionListener(comboListener);
		colorBox.addActionListener(comboListener);
		
		frame.setVisible(true);
	}
	
	public void start() {
		if (destroyed) { return; }
		
		frame.setTitle(title);
		
		synchronized (lock) {
			dimension = -1;
			maxDimension = 1;
			
			setBarDimension(maxDimension);
			timeBar.setMaximum(0);
			indexCount = 0;
		}
		
		statusPanel.start();
		contributionPanel.start();
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
					contributionPanel.setEvaluator((Evaluator) integrand);
				}
				if (dimension > 0) {
					maxDimension = dimension;
					setBarDimension(maxDimension);
				}
			}
		} else if (data instanceof IndexContribution) {
			Index index = ((IndexContribution) data).index;
			double contribution = ((IndexContribution) data).contribution;
			
			contributionPanel.indexEvaluated(index, contribution);
			
			synchronized (lock) {
				if (dimension == 0 && index.lastEntry() >= maxDimension) {
					maxDimension = index.lastEntry() + 1;
					setBarDimension(maxDimension);
				}
				indexCount++;
				timeBar.setMaximum(indexCount);
				timeBar.setValue(indexCount);
			}
			
			synchronized (lock) {
				if (index.lastEntry() >= maxDimension) {
					maxDimension = index.lastEntry() + 1;
					setBarDimension(maxDimension);
				}
			}
		}
		
		if (data instanceof IndexVisualizerData) {
			statusPanel.submit((IndexVisualizerData) data);
		}
	}
	
	public void stop() {
		if (destroyed) { return; }
		
		frame.setTitle(title + " (stopped)");
		
		contributionPanel.stop();
	}
	
	synchronized public void destroy() {
		if (destroyed) { return; }
		
		destroyed = true;
		
		statusPanel.destroy();
		contributionPanel.destroy();
		
		frame.dispose();
	}
	
	private void setBarDimension(int dimension) {
		dim0Bar.setMaximum(dimension - 1);
		dim0Bar.setValue(0);
		dim1Bar.setMaximum(dimension - 1);
		dim1Bar.setValue(1);
	}
	
	void showText(int x, int y) {
		DecimalFormat df = new DecimalFormat("0.###E0");
		String resultText = "";
		String statusText = "";
		
		int dim0 = dim0Bar.getValue();
		int dim1 = dim1Bar.getValue();
		
		Index v;
		synchronized (this) {
			if (! (dim0 == dim1 && x != y)) {
				EvalData data;
				v = new FastIndex();
				v = v.set(dim0, x);
				v = v.set(dim1, y);
				
				synchronized (contributionPanel.valueMap) {
					data = contributionPanel.valueMap.get(v);
				}
				if (data != null) {
					resultText = "Integral: " + df.format(data.contribution)
						 + " Evals: " + df.format(data.calls);
				}
				synchronized (statusGrid) {
					if (x < GridVisualizer.GRIDSIZE && y < GridVisualizer.GRIDSIZE) {
						statusText = statusGrid[x][y];
					}
				}
			}
		}
		
		resultField.setText(resultText);
		statusField.setText(statusText);
		
	}
	
	ColorScale getColorScale() {
		return (ColorScale) colorBox.getSelectedItem();
	}
	
	static class EvalData {
		double contribution;
		int calls;
		
		EvalData(double contribution, int calls) {
			this.contribution = contribution;
			this.calls = calls;
		}
	}
}