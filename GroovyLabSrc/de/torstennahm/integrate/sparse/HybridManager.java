/*
 * Created on Jul 6, 2007
 */
package de.torstennahm.integrate.sparse;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import de.torstennahm.integrate.sparse.index.Index;

/**
 * Package helper class for controlling index priorities.
 * 
 * @author Torsten Nahm
 */
class HybridManager {
	private final double simplexQuota;
	
	private Queue<HybridData> importanceQueue;
	private Queue<HybridData> simplicialQueue;
	private long importanceCalls, allCalls;
	
	public HybridManager(double simplexQuota) {
		this.simplexQuota = simplexQuota;
		
		importanceQueue = new PriorityQueue<HybridData>(11, new ImportanceComparator());
		simplicialQueue = new PriorityQueue<HybridData>(11, new LengthComparator());
		importanceCalls = 0;
		allCalls = 0;
	}
	
	public void enqueue(Index index, double importance, long calls) {
		HybridData data = new HybridData(index, importance, calls);
		importanceQueue.add(data);
		simplicialQueue.add(data);
	}
	
	public Index nextIndex() {
		HybridData simplicialData = getUnfinished(simplicialQueue);
		HybridData importanceData = getUnfinished(importanceQueue);
		
		if (simplicialData == null || importanceData == null) {
			return null;
		}
		
		HybridData data;
		
		if (importanceCalls + importanceData.calls <= (allCalls + importanceData.calls) * (1 - simplexQuota)) {
			importanceCalls += importanceData.calls;
			data = importanceData;
		} else {
			data = simplicialData;
		}
		
		allCalls += data.calls;
		
		data.finished = true;
		return data.index;
	}
	
	private HybridData getUnfinished(Queue<HybridData> queue) {
		HybridData data;
		boolean done = false;
		
		do { 
			data = queue.peek();
			if (data == null || ! data.finished) {
				done = true;
			} else {
				queue.poll();
			}
		} while (! done);
		
		return data;
	}
	
	private class ImportanceComparator implements Comparator<HybridData> {
		public int compare(HybridData data1, HybridData data2) {
			/* The queue is a minimum queue, so thru the minus sign greatest importance come first */
			return -Double.compare(data1.importance, data2.importance);
		}
	}
	
	private class LengthComparator implements Comparator<HybridData> {
		public int compare(HybridData data1, HybridData data2) {
			return data1.index.sum() - data2.index.sum();
		}
	}
	
	static private class HybridData {
		private Index index;
		private double importance;
		private long calls;
		private boolean finished;
		
		HybridData(Index index, double importance, long calls) {
			this.index = index;
			this.importance = importance;
			this.calls = calls;
			finished = false;
		}
	}
}