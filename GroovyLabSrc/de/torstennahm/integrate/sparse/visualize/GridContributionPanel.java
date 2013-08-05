/*
 * Created on Aug 26, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.FastIndex;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.sparse.visualize.GridVisualizer.EvalData;
import de.torstennahm.math.MathTN;
import de.torstennahm.util.ColorScale;

class GridContributionPanel extends JPanel {
	private static final long serialVersionUID = -7538424015006920610L;
	
	private final GridVisualizer parent;
	private final GridContributionWorker worker;
	
	/* synchronized by valueMap */
	Map<Index, EvalData> valueMap = new HashMap<Index, EvalData>();
	
	/* synchronized by indicesEvaluated */
	private Map<Index, Integer> indicesEvaluated = new HashMap<Index, Integer>();
	private int time;
	
	/* synchronized by class */
	private Evaluator evaluator;
	
	GridContributionPanel(GridVisualizer parent) {
		this.parent = parent;
		
		worker = new GridContributionWorker(this);
		worker.start();
		
		initGUI();
	}
	
	private void initGUI() {
		addMouseMotionListener(new GridMouseListener());
	}
	
	void start() {
		synchronized (indicesEvaluated) {
			indicesEvaluated = new HashMap<Index, Integer>();
			time = 0;
		}
		
		synchronized (valueMap) {
			valueMap = new HashMap<Index, EvalData>();
		}
		
		synchronized (this) {
			evaluator = null;
		}
		
		worker.restart();
	}
	
	synchronized void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	void destroy() {
		worker.terminate();
	}
	
	void stop() {
		worker.hold();
	}
	
	public synchronized void indexEvaluated(Index index, double contribution) {
		synchronized (indicesEvaluated) {
			time++;
			indicesEvaluated.put(index, new Integer(time));
		}
		synchronized (valueMap) {
			valueMap.put(index, new EvalData(contribution, evaluator == null ? 0 : evaluator.pointsForIndex(index)));
		}
	}
	
	public boolean isEvaluated(Index index) {
		synchronized (valueMap) {
			return valueMap.containsKey(index);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int dim0, dim1;
		int mode = parent.modeBox.getSelectedIndex();
		int boxSize = getBoxSize();
		ColorScale colorScale = parent.getColorScale();
		double colorFactor = (10 - parent.colorFactorBar.getValue()) / 5.0;
		double colorOffset = parent.colorOffsetBar.getValue() / 10.0;
		int timeLimit = parent.timeBar.getValue();
		
		synchronized (this) {
			if (evaluator == null) {
				return;
			}
		}
		
		dim0 = parent.dim0Bar.getValue();
		dim1 = parent.dim1Bar.getValue();
		
		boolean calc = false;
		
		for (int i = 0; i < GridVisualizer.GRIDSIZE; i++) {
			for (int j = 0; j < GridVisualizer.GRIDSIZE; j++) {
				if (dim0 != dim1 || i == j) {
					Index index = new FastIndex();
					index = index.set(dim0, i);
					index = index.set(dim1, j);
					
					int[] pixel = positionToPixel(new int[] {i, j});
					
					EvalData data;
					synchronized (valueMap) {
						data = valueMap.get(index);
					}
					if (data == null) {
						calc = true;
					} else {
						double contribution = data.contribution;
						if (! Double.isNaN(contribution)) {
							double b = 0.0;
							double integrate = Math.abs(contribution);
							int evals = evaluator.pointsForIndex(index);
							
							if (mode == 0) {
								b = 0.7 - 0.7 * MathTN.log10(integrate) / MathTN.log10(MathTN.FUDGE);
							} else if (mode == 1) {
								b = 0.4 + 0.1 * MathTN.log10(evals);
							} else if (mode == 2) {
								b = 0.7 - 0.7 * MathTN.log10(integrate / evals) / MathTN.log10(MathTN.FUDGE);
							}
							
							g.setColor(colorScale.getColor((b + colorOffset) * colorFactor));
							g.fillRect(pixel[0], pixel[1], boxSize, boxSize);
//							if (contribution > 0) {
//								g.setColor(Color.BLACK);
//								g.fillRect(pixel[0] + (boxSize * 3) / 8, pixel[1] + (boxSize * 3) / 8, boxSize / 4, boxSize / 4);
//							}
						}
					}
				}
			}
		}
		
		g.setColor(Color.RED);
		
		for (int i = 0; i < GridVisualizer.GRIDSIZE; i++) {
			for (int j = 0; j < GridVisualizer.GRIDSIZE; j++) {
				if (dim0 != dim1 || i == j) {
					Index index = new FastIndex();						
					index = index.set(dim0, i);
					index = index.set(dim1, j);
					
					int[] pixel = positionToPixel(new int[] {i, j});
					
					boolean borderX = false, borderY = false;
					boolean evaluated;
					synchronized (indicesEvaluated) {
						 evaluated = isEvaluatedInTimeLimit(index, timeLimit);
						 if (! evaluated) {
							if (isEvaluatedInTimeLimit(index.add(dim0, -1), timeLimit)) {
								borderX = true;
							}
							
							if (isEvaluatedInTimeLimit(index.add(dim1, -1), timeLimit)) {
								borderY = true;
							}
						 }
					}
					
					if (dim0 == dim1) {
						if (borderX) {
							g.drawLine(pixel[0], pixel[1] + boxSize - 1, pixel[0], pixel[1] + 2 * boxSize - 1);
							g.drawLine(pixel[0] - boxSize, pixel[1] + boxSize - 1, pixel[0], pixel[1] + boxSize - 1);
						}
					} else {
						if (borderX) {
							g.drawLine(pixel[0], pixel[1] - 1, pixel[0], pixel[1] + boxSize - 1);
						}
						if (borderY) {
							g.drawLine(pixel[0], pixel[1] + boxSize - 1, pixel[0] + boxSize, pixel[1] + boxSize - 1);
						}
					}
				}
			}
		}
		
		if (calc) {
			synchronized (this) {
				if (evaluator != null) {
					worker.calc(evaluator, dim0, dim1, valueMap);
				}
			}
		}
	}
	
	private boolean isEvaluatedInTimeLimit(Index index, int timeLimit) {
		Integer time = indicesEvaluated.get(index);
		
		return ! (time == null) && time.intValue() <= timeLimit;
	}
	
	private class GridMouseListener implements MouseMotionListener {
		public void mouseMoved(MouseEvent e) {
			int[] pos = pixelToPosition(new int[] { e.getX(), e.getY() });
			if (pos != null) {
				parent.showText(pos[0], pos[1]);
			}
		}
				
		public void mouseDragged(MouseEvent e) {
		}
	}
	
	private int[] positionToPixel(int[] pos) {
		int boxDist = getBoxDist();
		return new int[] { 2 + pos[0] * boxDist, getHeight() - 2 - pos[1] * boxDist - boxDist }; 
	}
	
	private int[] pixelToPosition(int[] pixel) {
		int x = pixel[0] - 2;
		int y = getHeight() - 2 - 1 - pixel[1];
		int boxDist = getBoxDist();
		
		if (x % boxDist >= getBoxSize() || y % boxDist >= getBoxSize()) {
			return null;
		}
		else {
			return new int[] { x / boxDist, y / boxDist };
		}
	}
	
	private int getBoxDist() {
		return Math.min(getWidth(), getHeight()) / (GridVisualizer.GRIDSIZE + 1);
	}
	
	private int getBoxSize() {
		return (int) (getBoxDist() * GridVisualizer.BOXSIZE);
	}
}