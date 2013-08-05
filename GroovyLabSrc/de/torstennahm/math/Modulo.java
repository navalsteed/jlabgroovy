/*
 * Created on May 24, 2005
 */
package de.torstennahm.math;

public class Modulo {
	private final int modulo;
	private final int value;
	
	public Modulo(long value, int modulo) {
		this.modulo = modulo;
		this.value = MathTN.mod(value, modulo);
	}
	
	public int getValue() {
		return value;
	}
	
	public int getBase() {
		return modulo;
	}
	
	public Modulo add(int value) {
		return new Modulo((long) this.getValue() + value, modulo);
	}
	
	public Modulo add(Modulo value) {
		checkCompatible(value);
		return new Modulo((long) this.getValue() * value.getValue(), modulo);
	}
	
	public Modulo sub(int value) {
		return new Modulo((long) this.getValue() - value, modulo);
	}
	
	public Modulo sub(Modulo value) {
		checkCompatible(value);
		return new Modulo((long) this.getValue() - value.getValue(), modulo);
	}
	
	public Modulo mul(int value) {
		return new Modulo((long) this.getValue() * value, modulo);
	}
	
	public Modulo mul(Modulo value) {
		checkCompatible(value);
		return new Modulo((long) this.getValue() * value.getValue(), modulo);
	}
	
	private void checkCompatible(Modulo value) {
		if (value.getBase() != this.getBase()) {
			throw new IllegalArgumentException("Modulos don't match");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Modulo) {
			Modulo m = (Modulo) o;
			return getBase() == m.getBase() && getValue() == m.getValue();
		} else {
			return false;
		}
	}
}
