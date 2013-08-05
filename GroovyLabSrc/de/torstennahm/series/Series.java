/*
 * Created on Jan 25, 2005
 */
package de.torstennahm.series;

/**
 * A simple iterator that returns a series of <code>T</code> objects.
 * The series is of indefinite length.
 * 
 * @author Torsten Nahm
 */
public interface Series<T> {
	T next();
	boolean hasNext();
}
