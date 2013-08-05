package groovySci.math.plot;
import java.awt.Color;

// a class to plot in an object-oriented way
public class PlotController  {

    //  define the possible line types
    enum  LineTypes {
        ContinuousLine, DottedLine, RoundDottedLine, CrossDotLine, ThickLine, PatternLine
    };
    
    // keeps track of the line type of the controller context
    LineTypes lineType = LineTypes.ContinuousLine;  
    
// these routines affect AbstractDrawer properties and can be used to change the defaults 
// for external plotting routines that do not specify their own values
       // set drawing to continuous lines
    
    // resets the default settings of the graphics context
   void resetDefaultSettings()   {
       setContinuousLine();
       lineType = LineTypes.ContinuousLine;
       setlineWidth(1);
       setColor(Color.RED);
       setxlabel("X-axis");
       setylabel("Y-axis");
       setzlabel("Z-axis");
       }
   
 // this routine is useful when we have a particular PlotController context that we want to
// use for some plot operations  
   void  passControllerContextToPlotContext()  {
       switch (lineType)  {
         case  ContinuousLine:  setContinuousLine(); break;
         case  DottedLine:  setDottedLine(); break;
         case  RoundDottedLine:   setRoundDotLine();  break;
         case  CrossDotLine:  setCrossDotLine();  break;
         case  ThickLine:  setThickLine();   break;
         case  PatternLine:  setPatternLine(); break;
         default:  setContinuousLine();
     }
   }
   

       
    void  setContinuousLine()  {
        groovySci.math.plot.render.AbstractDrawer.line_type = groovySci.math.plot.render.AbstractDrawer.CONTINOUS_LINE; 
        lineType = LineTypes.ContinuousLine;
    }
       // set drawing to dotted lines
    void  setDottedLine()  { 
        groovySci.math.plot.render.AbstractDrawer.line_type = groovySci.math.plot.render.AbstractDrawer.DOTTED_LINE; 
        lineType =  LineTypes.DottedLine;
    }
       // set drawing to Round Dot
    void  setRoundDotLine()  { 
        groovySci.math.plot.render.AbstractDrawer.dot_type = groovySci.math.plot.render.AbstractDrawer.ROUND_DOT;
        lineType = LineTypes.RoundDottedLine;
    }
       // set drawing to cross dot
    void  setCrossDotLine()  { 
        groovySci.math.plot.render.AbstractDrawer.dot_type = groovySci.math.plot.render.AbstractDrawer.CROSS_DOT; 
        lineType = LineTypes.CrossDotLine;
    }
    // set drawing to thick line
    void setThickLine() {
        groovySci.math.plot.render.AbstractDrawer.line_type = groovySci.math.plot.render.AbstractDrawer.THICK_LINE;
        lineType = LineTypes.ThickLine;
    }
        // set drawing to pattern line
    void setPatternLine() {
        groovySci.math.plot.render.AbstractDrawer.line_type = groovySci.math.plot.render.AbstractDrawer.PATTERN_LINE;
        lineType = LineTypes.PatternLine;
    }

    
       // set font
    void  setFont(java.awt.Font font)  { groovySci.math.plot.PlotGlobals.defaultAbstractDrawerFont = font; } 
       // set Color
    void  setColor(java.awt.Color  color)   { groovySci.math.plot.PlotGlobals.defaultAbstractDrawerColor = color; } 
       // set line width
    void  setlineWidth(int lw)   { groovySci.math.plot.render.AbstractDrawer.line_width = lw; } 
    
      // references to the data for plot
    double []  xData = new double [1];
    double []  yData = new  double [1];
    double []  zData = new double [1]; 
        
      // adjust the data references for plotting to the actual data   
    void   setX( double [] x )  { xData = x; }  // sets the x-data for plotting
    void   setY( double [] y )  { yData = y; }  // sets the y-data for plotting
    void   setZ( double [] z )   { zData = z;  }  // sets the z-data for plotting
    
    void  setX( groovySci.math.array.Vec  x)  { xData = x.getv(); }  // sets the x-data using Vec 
    void  setY( groovySci.math.array.Vec  y)  { yData = y.getv(); }  // sets the y-data using Vec 
    void  setZ( groovySci.math.array.Vec  z)  { zData = z.getv(); }  // sets the z-data using Vec 
    
    void  setX( groovySci.math.array.Matrix  x)  { xData = x.getv(); }  // sets the x-data using Matrix
    void  setY( groovySci.math.array.Matrix  y)  { yData = y.getv(); }  // sets the y-data using Matrix
    void  setZ( groovySci.math.array.Matrix  z)  { zData = z.getv(); }  // sets the z-data using Matrix 
    
    String  xlabelStr = "X-axis";
    void  setxlabel( String  xl )  {  xlabelStr = xl; }
    
    String   ylabelStr = "Y-axis";
    void  setylabel( String yl )  { ylabelStr = yl; }
    
    String  zlabelStr = "Z-axis";
    void  setzlabel( String zl )  {  zlabelStr = zl; }
    
    String   plotTitle2D = "2-D Plot";
    void   setplotTitle2D(String  plTitle)  { plotTitle2D = plTitle; }
    String   plotTitle3D = "3-D Plot";
    void   setplotTitle3D(String  plTitle)  { plotTitle3D = plTitle; }
   
       
    // perform the plot using the object's properties
    void mkplot()  { 
        groovySci.math.plot.plot.plot(xData, yData, groovySci.math.plot.PlotGlobals.defaultAbstractDrawerColor, plotTitle2D);
        groovySci.math.plot.plot.xlabel(xlabelStr);
        groovySci.math.plot.plot.ylabel(ylabelStr);
        
       }
 
 // perform the plot using the object's properties
    void  mkplot3D()  { 
        
        groovySci.math.plot.plot.plot(xData, yData, zData, groovySci.math.plot.PlotGlobals.defaultAbstractDrawerColor,  plotTitle3D);
        groovySci.math.plot.plot.xlabel(xlabelStr);
        groovySci.math.plot.plot.ylabel(ylabelStr);
        groovySci.math.plot.plot.zlabel(zlabelStr);
       }
 }
    
