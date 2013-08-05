package groovySci.math.array;

import static groovySci.math.array.Matrix.printArray;
        
public class EigResults {
  public double [] realEvs;
  public  double [] imEvs;
  public  double [][] leftEvecs;
  public double [][] rightEvecs;

  
  @Override
  public  String toString() {
      StringBuilder sb = new StringBuilder();
      String realEvsStr = "real eigenvalues: "+ printArray(realEvs);
      String imEvsStr = "imaginary eigenvalues: "+ printArray(realEvs);
      String leftEvecsStr = "left eigenvectors: "+ printArray(leftEvecs);
      String rightEvecsStr = "right eigenvectors: "+printArray(rightEvecs);
      
      sb.append(realEvsStr); sb.append(imEvsStr); sb.append(leftEvecsStr); sb.append(rightEvecsStr);
      return sb.toString();
  }
  
}
