package com.nr.sp;
import static java.lang.Math.*;

public class Hann implements WindowFun{
  private int nn;
  private double[] win;
  
  // compute and store the window function in a table
  public Hann(final int n) {
    nn = n;
    win = new double[n];
    double twopi = 8.*atan(1.);
    for (int i=0;i<nn;i++) win[i] = 0.5*(1.-cos(i*twopi/(nn-1.)));
  }
  
  // return values from the window as a function
  public double window (final int j, final int n) {
    if (n != nn) throw new IllegalArgumentException("incorrect n for this Hann");
    return win[j];
  }
}
