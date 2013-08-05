/*
 * Created on Aug 27, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import java.util.Set;

import de.torstennahm.integrate.IntegrationFailedException;
import de.torstennahm.integrate.sparse.evaluateindex.Evaluator;
import de.torstennahm.integrate.sparse.index.FlatIndexGenerator;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.integrate.sparse.index.WeightedIndexGenerator;
import de.torstennahm.integrate.sparse.visualize.ContributionVisualizer.IndexEntry;
import de.torstennahm.series.Series;


class ContributionWorker extends Thread {
	/* synchronized by class */
	private boolean restart;
	private boolean active;
	private TaskData newTaskData;
	private boolean terminate = false;
	
	final private ContributionVisualizer visualizer;
	
	ContributionWorker(ContributionVisualizer visualizer) {
		super("IndexWorker");
		this.visualizer = visualizer;
		setDaemon(true);
		setPriority(Thread.MIN_PRIORITY);
	}
	
	@Override
	public void run() {
		TaskData taskData = null;
		
		while (! terminate) {
			synchronized (this) {
				while (! restart && ! terminate) {
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				
				if (! terminate) {
					restart = false;
					taskData = newTaskData;
				}
			}
			
			if (! terminate) {
				fillTailList(taskData);
			}
		}
	}
	
	private void fillTailList(TaskData taskData) {
		Series<Index> indexIter;
		long lastRedraw = System.currentTimeMillis();
		int dimension = taskData.evaluator.dimension();
		
		if (dimension == 0) {
			indexIter = new WeightedIndexGenerator();
		} else {
			indexIter = new FlatIndexGenerator(dimension);
		}
		
		while (! restart) {
			synchronized (this) {
				while (! active && ! restart) {
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
			
			Index index = indexIter.next();
			if (! taskData.evaluatedIndices.contains(index)) {
				try {
					double value = taskData.evaluator.deltaEvaluate(index);
					double relResult = value / taskData.evaluator.pointsForIndex(index);
					synchronized (taskData.tailSet) {
						taskData.tailSet.add(new IndexEntry(relResult));
					}
				} catch (IntegrationFailedException e) {
				}
			}
			
			if (System.currentTimeMillis() > lastRedraw + 100){
				visualizer.updateDisplay();
				lastRedraw = System.currentTimeMillis();
			}
			
			yield();
		}
	}
	
	synchronized public void startWorker(Evaluator e, Set<Index> ei, Set<IndexEntry> ts) {
		newTaskData = new TaskData();
		newTaskData.evaluator = e;
		newTaskData.evaluatedIndices = ei;
		newTaskData.tailSet = ts;
		
		restart = true;
		notifyAll();
	}
	
	synchronized public void terminate() {
		terminate = true;
		notifyAll();
	}
	
	synchronized public void setActive(boolean active) {
		this.active = active;
		notifyAll();
	}
	
	static private class TaskData {
		private Evaluator evaluator;
		private Set<Index> evaluatedIndices;
		private Set<IndexEntry> tailSet;
	}
}