/*
 * Created on Feb 8, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import java.util.Map;

import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.FastIndex;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.sparse.visualize.GridVisualizer.EvalData;

/**
 * @author Torsten Nahm
 */

public class GridContributionWorker extends Thread {
	private final GridContributionPanel panel;
	
	/* synchronized by class */
	private Task syncTask;
	private boolean stopped = true;
	private boolean terminate = false;
	
	public GridContributionWorker(GridContributionPanel panel) {
		super("GridWorker");
		
		this.panel = panel;
		
		setDaemon(true);
		setPriority(Thread.NORM_PRIORITY - 1);
	}
	
	synchronized void calc(Evaluator evaluator, int dim0, int dim1, Map<Index, EvalData> valueMap) {
		syncTask = new Task();
		
		syncTask.dim0 = dim0;
		syncTask.evaluator = evaluator;
		syncTask.dim0 = dim0;
		syncTask.dim1 = dim1;
		syncTask.valueMap = valueMap;

		notifyAll();
	}
	
	synchronized void restart() {
		stopped = false;
		notifyAll();
	}
	
	synchronized void hold() {
		stopped = true;
		notifyAll();
	}
	
	synchronized void terminate() {
		terminate = true;
		notifyAll();
	}
	
	@Override
	public void run() {
		Task task = null;
		
		while (! terminate) {
			synchronized (this) {
				if (! terminate && syncTask == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				
				if (! terminate) {
					task = syncTask;
					syncTask = null;
				}
			}
			
			if (! terminate && ! stopped && task != null) {
				calcGrid(task);
			}
		}
	}
		
	private void calcGrid(Task task) {
		boolean abort = false;
		long start = System.currentTimeMillis();
		boolean newIndex = false;
		
		for (int k = 0; ! abort && k < GridVisualizer.GRIDSIZE * 2 - 1; k++) {
			for (int j = Math.max(0, k - GridVisualizer.GRIDSIZE - 1);
			! abort && j <= Math.min(k, GridVisualizer.GRIDSIZE - 1); j++) {
				synchronized (this) {
					if (syncTask != null) {
						abort = true;
					}
				}
				
				if (! abort) {
					int i = k - j;
					if (task.dim0 != task.dim1 || i == j) {
						Index index = new FastIndex();
						index = index.set(task.dim0, i);
						index = index.set(task.dim1, j);
						
						boolean done = false;
						
						synchronized (task.valueMap) {
							if (task.valueMap.containsKey(index)) {
								done = true;
							}
						}
						
						if (! done) {
							double contribution = Double.NaN;
							try {
								contribution = task.evaluator.deltaEvaluate(index);
							} catch (Exception e) {
							}
							
							synchronized (task.valueMap) {
								int calls = task.evaluator.pointsForIndex(index);
								task.valueMap.put(index, new EvalData(contribution, calls));
							}
							newIndex = true;
						}
					}
						
					yield();
					if (newIndex && System.currentTimeMillis() > start + 100){
						abort = true;
					}
				}
			}
		}
		
		panel.repaint();
	}
	
	private static class Task {
		private Evaluator evaluator;
		private int dim0, dim1;
		private Map<Index, EvalData> valueMap;
	}
}