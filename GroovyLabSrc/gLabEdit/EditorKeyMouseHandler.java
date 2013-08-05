package gLabEdit;

import gExec.Interpreter.GlobalValues;
import gExec.gui.AutoCompletionFrame;
import gExec.gui.DetailHelpFrame;
import java.awt.Container;
import java.awt.Point;

import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class EditorKeyMouseHandler extends MouseAdapter implements KeyListener
{
        int caretPos = 0;      // track the cursor position 
        int prevCaretPos = 0;
        int  textLen = 0;  // the text lenngth
        int fromLoc = 0;
        int toLoc = 0;
       
        public RSyntaxTextArea  editorPane=null;    // the component that keeps and handles the editing text
        public RSyntaxDocument  docVar=null; 
        public RSyntaxDocument syntaxDocument=null;
        
public EditorKeyMouseHandler()
	{
	}


// update fields denoting the document in the editor, necessary when a new document is edited
  public  RSyntaxDocument updateDocument()  {
         
          docVar = (RSyntaxDocument) editorPane.getDocument();
          syntaxDocument = docVar;
          
          return syntaxDocument;
  }
               
     
  
   public  String  getCurrentLine() {
       if (docVar==null)
           updateDocument();
           
       RSyntaxDocument  myDoc = syntaxDocument;
       
       int caretpos = editorPane.getCaretPosition();
       int startpos = editorPane.getCaretOffsetFromLineStart();
       int scanpos = caretpos-startpos;
       String s = "";
       try {
            char ch = myDoc.charAt(scanpos);
       while (ch!='\n') {
                s += myDoc.charAt(scanpos);
            
           scanpos += 1;
           ch = myDoc.charAt(scanpos);
       }
       } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
       
       return s;
   }
       
     
   public  String   getSelectedTextOrCurrentLine() {
       String selectedTextOrCurrentLine = editorPane.getSelectedText();
       if (selectedTextOrCurrentLine==null)
           selectedTextOrCurrentLine = getCurrentLine();
       
       return selectedTextOrCurrentLine;
   }
     

        
         
    public void keyTyped(KeyEvent e){
       /* int  keyValue = e.getKeyChar();
       
        if (keyValue == KeyEvent.VK_F10);
                 display_help();      */
   }

	/**Interpret key presses*/
    public void keyPressed(KeyEvent e)
    {
        gLabEditor.documentEditsPendable = true;
        int keyValue = e.getKeyCode();
        editorPane  = (RSyntaxTextArea)e.getSource();  // editor's keystrokes have as source the inputTextComponent JTextComponent
        prevCaretPos = caretPos;   
        
        switch (keyValue) {
                        

            case   KeyEvent.VK_ENTER:
                caretPos = editorPane.getCaretPosition();
                String text = editorPane.getText();
                int newLineCnt = 0;
                int idx = 0;
                while (idx<caretPos)   {
                    if (text.charAt(idx) == '\n') 
                       newLineCnt++;
                    idx++;
                    
                }
                break;

                
          case KeyEvent.VK_F10:
                text = editorPane.getSelectedText();
     
                text = text.trim();
          if (text.isEmpty()==false)  {
            Object selectedObject = gExec.Interpreter.GlobalValues.groovyBinding.getVariable(text);
            if (selectedObject != null)
                groovy.inspect.swingui.ObjectBrowser.inspect(selectedObject);
          }

                e.consume();
                break;

           case KeyEvent.VK_SPACE:
                if (GlobalValues.rsyntaxInGroovyCompletionMode  == true)
                {
            
              if (e.isControlDown()) {
                new RSyntaxGroovyCompletionAction().performCompletion();
                  }
                }
              break;

                        
            case KeyEvent.VK_F6:
     
                gLabEdit.gLabEditor.progressComputationLabel.setVisible(true);
     
                  SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
     String currentText = getSelectedTextOrCurrentLine();
            
     String result = gExec.Interpreter.Interpreter.execWithGroovyShell(currentText);
     if (result != null)  {
        GlobalValues.consoleOutputWindow.output.append(result+"\n\n");
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
         //gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
     }
 
            gLabEdit.gLabEditor.progressComputationLabel.setVisible(false);
     
             }
        }
                          );
 
     
     e.consume();
    
    break;
    
     case KeyEvent.VK_F8:
     
                gLabEdit.gLabEditor.progressComputationLabel.setVisible(true);
     SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new Thread(new Runnable() {
         public void run() {
     
     String currentText = getSelectedTextOrCurrentLine();
            
     String result = gExec.Interpreter.Interpreter.execWithGroovyShell(currentText);
     if (result != null)  {
        GlobalValues.consoleOutputWindow.output.append(result+"\n\n");
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
         //gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
     }
 
            gLabEdit.gLabEditor.progressComputationLabel.setVisible(false);
     
             }
         
     }).start();
                }
     });
     
     e.consume();
    break;
                
                // compile a standalone application that witll execute the GroovySci script
            case KeyEvent.VK_F7:
                 
       StandAloneApplicationActionGroovy standAloneAppl = new StandAloneApplicationActionGroovy();
       standAloneAppl.transformScriptToStandAlone();
       break;
                
            case KeyEvent.VK_F12:
                gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
                gExec.Interpreter.GlobalValues.gLabMainFrame.repaint();
                
                break;
                
            
             case KeyEvent.VK_F4:
                if (e.isShiftDown())  {   // treat the identifier under the cursor as a variable, e.g.  var  jf = new javax.swing.JFrame(), jf is a variable
                 GlobalValues.inspectClass = true;
                gLabEdit.RSyntaxInspectCompletionListAction inspect = new gLabEdit.RSyntaxInspectCompletionListAction();
                inspect.actionPerformed(null);
                }
                else {   //  treat the identifier under the cursor as a type, e.g. var jf = new javax.swing.JFrame(), javax.swing.JFrame() is a type
                if (e.isAltDown())
                    GlobalValues.gLabMainFrame.closeGUI();
                else {         
                GlobalValues.inspectClass = false;   
                gLabEdit.RSyntaxInspectCompletionListAction  inspect = new gLabEdit.RSyntaxInspectCompletionListAction();
                inspect.actionPerformed(null);
                  }
                }
                e.consume();            
                break;
            
                 
     case KeyEvent.VK_F9:
        if (e.isShiftDown())  {   // we compile for Embedded Java
          new gLabEdit.compileExecuteTextJava().compileExecuteTextJavaEmbedded();
          e.consume();
        }
        else {  
         new gLabEdit.compileExecuteTextJava().compileExecuteTextJava();
          e.consume();
        }     
          break;
            
     case KeyEvent.VK_F11:
         if (e.isShiftDown()==false)  {   // treat the identifier under the cursor as a variable, e.g.  var  jf = new javax.swing.JFrame(), jf is a variable
                   Abbreviations.detectAndReplaceWordAtCaret();
         }
         else {
         new gLabEdit.compileExecuteTextJava().executeTextExternalJava(); 
         e.consume();
         }
         break;

          
        
     case KeyEvent.VK_F5:
         GlobalValues.consoleOutputWindow.resetText( " ");
         e.consume();
         break;
        
            case KeyEvent.VK_F2:
     String etext =  editorPane.getText();
     int currentTextLen = etext.length();
     if  (currentTextLen != textLen)   // text altered at the time between F2 clicks
      {
         fromLoc = 0;    // reset
     }
    
     int cursorLoc = editorPane.getCaretPosition();
     if (cursorLoc < toLoc)  {
     // reset if cursor is within the already executed part
         fromLoc = 0;
     }
     toLoc = cursorLoc;
     String textToExec = etext.substring(fromLoc, toLoc);
     
     editorPane.setSelectionStart(fromLoc);
     editorPane.setSelectionEnd(toLoc);
     editorPane.setSelectedTextColor(java.awt.Color.RED);
     textToExec = textToExec.substring(0, textToExec.lastIndexOf("\n"));
     fromLoc += textToExec.length();
     
      String result = gExec.Interpreter.Interpreter.execWithGroovyShell(textToExec);
     GlobalValues.consoleOutputWindow.output.append("\n"+result);
     GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());

        gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
      e.consume();
    break;

            
            case KeyEvent.VK_F1:
            case KeyEvent.VK_F3:    
                    e.consume();  // consume this event so it will not be processed in the default manner by the source that originated it
                  		//get the text on the current line
    
                    String inputString  = editorPane.getSelectedText();
                    if (inputString != null)   {   // some text is selected
               String [] matches = null;
               if (keyValue==KeyEvent.VK_F1) 
                       matches = gExec.Interpreter.GlobalValues.AutoCompletionGroovySci.getMatched(inputString);
               else 
                       matches = gExec.Interpreter.GlobalValues.AutoCompletionGroovySci.getMatchedRegEx(inputString);
                     
                    
                     final JList  resultsList = new JList(matches);
                     autocompleteListHandler  detailHelpAdapter = new autocompleteListHandler();
                     resultsList.addKeyListener(detailHelpAdapter); 
                     
                     
                     resultsList.addListSelectionListener(new ListSelectionListener() {
                         public void valueChanged(ListSelectionEvent e) {
                             String  selValue = resultsList.getSelectedValue().toString();
                             GlobalValues.selectedStringForAutoCompletion = selValue;
                        
                         }
                     }
                            );
                            
                GlobalValues.autoCompletionFrame = new AutoCompletionFrame("Glab editor autocompletion, Press F1  for detailed help on the selected entry");
                GlobalValues.autoCompletionFrame.displayMatches(resultsList);
               }    // some text is selected   
                    e.consume(); 
                    break;
                     
                    
            default:
                caretPos = editorPane.getCaretPosition();
                
          }
    }
    
         
    public void mouseClicked(MouseEvent me)
        { 
            
   if (me.getClickCount()>=2)  {  //only on ndouble-clicks
       RSyntaxTextArea    editor = (RSyntaxTextArea) me.getSource();
       Point  pt = new Point(me.getX(), me.getY());
       int  pos = editor.viewToModel(pt);
       javax.swing.text.Document  doc = editor.getDocument();
       
       boolean  exited = false;
       String  wb = "";
       int  offset = pos;
         // extract the part of the word before the mouse click position
       while (offset >= 0 && exited==false) {
         char  ch=' ';
                try {
                    ch = doc.getText(offset, 1).charAt(0);
                } catch (BadLocationException ex) {
                    System.out.println("Bad Location exception");
                    ex.printStackTrace();
                }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9');
         if (!isalphaNumeric)  exited=true;
          else {
           wb = wb + ch;
           offset -= 1;
           }
          }
       
       String  wa = "";
       int  docLen = doc.getLength();
       offset = pos+1;
       exited = false;
         // extract the part of the word after the mouse click position
       while (offset < docLen && exited==false) {
         char ch=' ';
                try {
                    ch = doc.getText(offset, 1).charAt(0);
                } catch (BadLocationException ex) {
                     System.out.println("Bad Location exception");
                     ex.printStackTrace();
               }
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9');
         if (!isalphaNumeric)  exited=true;
           else {
         wa = wa + ch;
         offset += 1;
           }
         }
         
         StringBuffer wbreverse = new StringBuffer();
         for (int k=wb.length()-1; k>=0; k--)
             wbreverse.append(wb.charAt(k));
         
         String  wordAtCursor = wbreverse.toString()+wa;       
          
         Object clickedVar = GlobalValues.GroovyShell.getVariable(wordAtCursor);
         
         
     String result = gExec.Interpreter.Interpreter.execWithGroovyShell(wordAtCursor);
     if (result != null)  {
        GlobalValues.consoleOutputWindow.output.append("\n"+result+"\n");
        GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
            gExec.gui.WatchWorkspace.displayGroovySciBinding(gExec.gLab.gLab.variablesWorkSpacePanel);
     }
      
       if (clickedVar != null) {
            GlobalValues.consoleOutputWindow.output.setCaretPosition(GlobalValues.consoleOutputWindow.output.getText().length());
         }
       }
      }
              
    
    
    void display_help() {
        JFrame helpFrame = new JFrame("Glab help");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(400, 400);
        Container container = helpFrame.getContentPane();
        JTextArea helpText = new JTextArea();

        int classCnt = 0;
        Hashtable  clTable= new Hashtable(); 
        Enumeration enumer = clTable.elements();
        TreeSet  sortedClasses =  new TreeSet();
        while(enumer.hasMoreElements())
		{
		    Object next = (Object)enumer.nextElement();
		    Class currentClass = (Class)next;
                    String className = currentClass.getCanonicalName();
                    sortedClasses.add(className);
                    classCnt++;
        }

          Iterator iter = sortedClasses.iterator();
          while (iter.hasNext()) {
                    String className = (String)iter.next();
                    helpText.append(className+"\n");
            }
          JScrollPane  helpPane = new JScrollPane(helpText);
        
        container.add(helpPane);
        helpFrame.setVisible(true);  
                
      }
    
        
    
    public void keyReleased(KeyEvent e)
    {
    	        
    }	
    
    class autocompleteListHandler extends KeyAdapter {
        public void keyPressed(KeyEvent ktev) {
            int  keyCode = ktev.getKeyCode();
            if (keyCode == KeyEvent.VK_F1) {
                display_detailed_help(GlobalValues.selectedStringForAutoCompletion);
            }
            if (keyCode == KeyEvent.VK_SPACE) {
                ktev.consume();
                GlobalValues.autoCompletionFrame.dispose();
            }
            
        }
        
   
    
        
}
    
    
    
    // displays detailed help for the selected item
    public static void display_detailed_help(String selectedItem) {
GlobalValues.detailHelpStringSelected = selectedItem;
DetailHelpFrame detailFrame = new DetailHelpFrame();
detailFrame.setVisible(true);
        
      }

       
 
}
