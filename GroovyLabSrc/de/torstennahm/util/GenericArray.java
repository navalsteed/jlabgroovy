/*
 * Created on Jul 6, 2006
 */
package de.torstennahm.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/*
 * Fixed length array of elements.
 * 
 * Primitive arrays exhibit some non-intuitive behavior when combined with generics.
 * This class works like an array, but behaves like a <code>List</code> with regard to generics.
 * It implements the <code>Iterable</code> interface.
 */

public class GenericArray<E> implements Iterable<E> {
	final Object[] array;
	int modCount = 0;
	
	public GenericArray(int size) {
		array = new Object[size];
	}
	
	public int size() {
		return array.length;
	}
	
	@SuppressWarnings("unchecked")
	public E get(int i) {
		return (E) array[i]; 
	}
	
	public E set(int i, E e) {
		E old = get(i);
		array[i] = e;
		modCount++;
		
		return old;
	}

	public Iterator<E> iterator() {
		return new GenericArrayIterator();
	}
	
	private class GenericArrayIterator implements Iterator<E> {
		int startModCount = modCount;
		int i = 0;
		
		public boolean hasNext() {
			checkForModification();
			return i < array.length;
		}
		
		@SuppressWarnings("unchecked")
		public E next() {
			checkForModification();
			return (E) array[i++];
		}
		
		private void checkForModification() {
			if (startModCount != modCount) {
				throw new ConcurrentModificationException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
