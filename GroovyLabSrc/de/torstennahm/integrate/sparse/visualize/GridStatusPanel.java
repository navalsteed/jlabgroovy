/*
 * Created on Mar 12, 2004
 */
package de.torstennahm.integrate.sparse.visualize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import de.torstennahm.integrate.sparse.index.FastIndex;
import de.torstennahm.integrate.sparse.index.Index;
import de.torstennahm.math.MathTN;
import de.torstennahm.util.ColorScale;

/**
 * @author Torsten Nahm
 */
public class GridStatusPanel extends JPanel {
    static final long serialVersionUID = -1595286858496114081L;
    
	private GridVisualizer parent;
	
	/* synchronized by lock */
	Object lock = new Object();
	private int time;
	private Map<Index, List<StatusData>> statusMap = new HashMap<Index, List<StatusData>>();
	
	/* Colors for status numbers */
	private static Color[] statusColors = { Color.BLUE, Color.RED, Color.CYAN, Color.MAGENTA };
	
	GridStatusPanel(GridVisualizer parent) {
		this.parent = parent;
		
		initGUI();
	}
	
	private void initGUI() {
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				int[] pos = pixelToPosition(new int[] { e.getX(), e.getY() });
				if (pos != null) {
					parent.showText(pos[0], pos[1]);
				}
			}
					
			public void mouseDragged(MouseEvent e) {}
		});

	}
	
	void destroy(){
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int dim0 = parent.dim0Bar.getValue();
		int dim1 = parent.dim1Bar.getValue();
		int timeLimit = parent.timeBar.getValue();
		double colorFactor = (10 - parent.colorFactorBar.getValue()) / 5.0;
		ColorScale colorScale = parent.getColorScale();
		int gridSize = GridVisualizer.GRIDSIZE;
		
		synchronized (parent.statusGrid) {
			for (int i = 0; i < gridSize; i++) {
				Arrays.fill(parent.statusGrid[i], null);
			}
		}
		
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (dim0 != dim1 || i == j) {
					Index index = new FastIndex();
					index = index.set(dim0, i);
					index = index.set(dim1, j);
					List<StatusData> indexList;
					synchronized (lock) {
						indexList = statusMap.get(index);
					
						if (indexList != null) {
							DecimalFormat df = new DecimalFormat("0.###E0");
							
							int[] pos = new int[] { index.get(dim0), index.get(dim1) };
							int[] pixel = positionToPixel(pos);
							
							StringBuffer statusText = new StringBuffer();
							int statusCount = 0;
							for (StatusData statusData : indexList) {
								if (statusData.time <= timeLimit) {
									IndexVisualizerData data = statusData.data;
									if (data instanceof IndexContribution || data instanceof IndexContributionEstimate) {
										double contribution;
										if (data instanceof IndexContribution) {
											contribution = ((IndexContribution) data).contribution;
										} else {
											contribution = ((IndexContributionEstimate) data).estimate;
											statusText.append(" Estimate: " + df.format(((IndexContributionEstimate) data).estimate));
										}
										
										double r = Math.abs(contribution);
										double b = 0.7 - 0.7 * MathTN.log10(r) / MathTN.log10(MathTN.FUDGE);
										g.setColor(colorScale.getColor(b * colorFactor));
										if (data instanceof IndexContribution) {
											g.fillRect(pixel[0], pixel[1], getBoxSize(), getBoxSize());
										} else {
											int size = getBoxSize();
											g.fillRect(pixel[0] + size / 4, pixel[1] + size / 4, size / 2, size / 2);
										}
									}
								}
							}
							
							for (StatusData statusData : indexList) {
								if (statusData.time <= timeLimit) {
									IndexVisualizerData data = statusData.data;
	
									if (data instanceof IndexStatus) {
										String s = ((IndexStatus) data).status;
										Color color = statusColors[MathTN.mod(s.hashCode(), statusColors.length)];
										g.setColor(color);
										g.drawRect(pixel[0] + statusCount, pixel[1] + statusCount,
												   getBoxSize() - statusCount * 2 - 1, getBoxSize() - statusCount * 2 - 1);
										statusCount++;
										
										statusText.append(" Status: " + ((IndexStatus) data).status);
									}
								}
							}
							
							synchronized (parent.statusGrid) {
								parent.statusGrid[pos[0]][pos[1]] = statusText.toString().trim();
							}
						}
					}
				}
			}
		}
	}
	
	private int getBoxDist() {
		return Math.min(getWidth(), getHeight()) / (GridVisualizer.GRIDSIZE + 1);
	}
		
	private int getBoxSize() {
		return (int) (getBoxDist() * GridVisualizer.BOXSIZE);
	}
	
	private int[] positionToPixel(int[] pos) {
		int boxDist = getBoxDist();
		return new int[] { pos[0] * boxDist + 2, getHeight() - 2 - pos[1] * boxDist - boxDist }; 
	}
	
	private int[] pixelToPosition(int[] pixel) {
		int x = pixel[0];
		int y = getHeight() - 2 - 1 - pixel[1];
		int boxDist = getBoxDist();
			
		if (x % boxDist >= getBoxSize() || y % boxDist >= getBoxSize()) {
			return null;
		}
		else {
			return new int[] { x / boxDist, y / boxDist };
		}
	}
	
	void start() {
		synchronized (lock) {
			statusMap = new HashMap<Index, List<StatusData>>();
			time = 0;
		}
	}
	
	void submit(IndexVisualizerData data) {
		synchronized (lock) {
			if (data instanceof IndexContribution) {
				time++;
			}
			
			Index index = data.index;
			StatusData statusData = new StatusData(time, data);
			
			List<StatusData> list = statusMap.get(index);
			if (list == null) {
				list = new LinkedList<StatusData>();
				list.add(statusData);
				statusMap.put(index, list);
			} else {
				list.add(statusData);
			}
		}
	}
	
	static private class StatusData {
		int time;
		IndexVisualizerData data;
		
		StatusData(int time, IndexVisualizerData data) {
			this.time = time;
			this.data = data;
		}
	}
}
