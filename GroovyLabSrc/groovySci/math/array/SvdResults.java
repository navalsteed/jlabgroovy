package groovySci.math.array;

public class SvdResults {
  public double [][] U;
  public double []W;
  public double [][] V;
  public double conditionNumber;
  public double norm;
  
  @Override
  public String toString() {
      StringBuilder sb = new StringBuilder("\nU = \n"+Matrix.printArray(U));
      sb.append("\nW = "+Matrix.printArray(W));
      sb.append("\nV = "+ Matrix.printArray(V));
      return sb.toString();
  }
}
