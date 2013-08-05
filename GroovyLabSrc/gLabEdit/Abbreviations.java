package gLabEdit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.*;

   
// routines for replacing abbreviations defined in "Abbreviations.txt" file     
public class Abbreviations {
    
    static boolean  abbreviationsMapInited = false;
    
  // the abbreviations are kept with a Scala Map 
    static Hashtable <String, String>   abbreviationsMap = new Hashtable<String, String> ();
    
    //  build our abbreviations map from the contents of the file fileName.
//  the abbreviations file is comma separated:
//  we list first the abbreviation, then a comma (i.e. ",") and then the replacement, e.g.
//     aad, Array[Array[Double]]
  static  Hashtable  <String, String>  buildAbbreviationsMap( String fileName )   {
    
    
         // Location of file to read
        File file = new File(fileName);

        StringBuilder sb = new StringBuilder();
        Hashtable <String, String> abbrTable =  new Hashtable<String, String>();
        
        try {

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                StringTokenizer stk = new StringTokenizer(line, ",");
                if (stk.countTokens() == 2) {  // a valid line
                String abbr  = stk.nextToken();   // the abbreviation
                String replacement = stk.nextToken(); // the replacement
                abbrTable.put(abbr, replacement);
              }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }         
        
    return  abbrTable;
    }
    
    
public static boolean initAbbreviationMap()   {
         // build the abbreviations map at the first time
      if (abbreviationsMapInited == false) {
         abbreviationsMap =  buildAbbreviationsMap(gExec.Interpreter.GlobalValues.workingDir+java.io.File.separatorChar+"Abbreviations.txt");
         //println("Abbreviations map = "+abbreviationsMap.toString())
         abbreviationsMapInited = true;
       }
        return abbreviationsMapInited;    // should be true for successful init
      }
      
  public static  void  displayAbbreviations()   {
     JFrame   abbrFrame = new JFrame("Abbreviations for editors");
     StringBuilder  abbrevsAll = new StringBuilder();
     Enumeration<String> abbrevs = abbreviationsMap.keys();  // the abbreviations
     while (abbrevs.hasMoreElements()) {
         String currentAbbrev = abbrevs.nextElement();
         String  replacement = abbreviationsMap.get(currentAbbrev);
         abbrevsAll.append(currentAbbrev +" -> "+replacement+"\n");   
     }
     
     JTextArea   jt = new JTextArea(abbrevsAll.toString());
     JScrollPane  jscr = new JScrollPane(jt);
     abbrFrame.add(jscr);
     abbrFrame.pack();
     abbrFrame.setVisible(true);
    }
  
  // detects and returns the word at the current caret location
  public static  String  detectAndReplaceWordAtCaret()    {
     int caretPosition = gExec.Interpreter.GlobalValues.globalEditorPane.getCaretPosition()-1;
  
    String  txt = gExec.Interpreter.GlobalValues.globalEditorPane.getText();  // the whole editor's text
    
    boolean  exited = false;
    String  wb = "";
    int  offset = caretPosition;
    while (offset >= 0 && exited==false) {
         char  ch = txt.charAt(offset);
         boolean isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9');
         if (!isalphaNumeric)  exited=true;
          else {
           wb = wb + ch;
           offset--;
             }
          }
          
    gExec.Interpreter.GlobalValues.globalEditorPane.setSelectionStart(offset+1);
          
       String  wa = "";
       int  docLen = txt.length();
       offset = caretPosition+1;
       exited = false;
       while (offset < docLen && exited==false) {
         char  ch = txt.charAt(offset);
         boolean  isalphaNumeric = ( ch >= 'a' && ch <='z')  || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9');
         if (!isalphaNumeric)  exited = true;
           else {
         wa = wa + ch;
         offset++;
           }
         }
       // reverse wb
        int length = wb.length();
        String reversewb="";
      for ( int i = length - 1 ; i >= 0 ; i-- )
         reversewb = reversewb + wb.charAt(i);
 
        // build the whole word at caret position
         String  wordAtCursor = reversewb+wa;         

    gExec.Interpreter.GlobalValues.globalEditorPane.setSelectionEnd(offset);
    
    initAbbreviationMap();  // if not inited init the abbreviations map
  
    wordAtCursor = wordAtCursor.trim();
    // System.out.println("wordAtCursor = "+wordAtCursor);
    
    if (abbreviationsMap.containsKey(wordAtCursor))  {
        String replacingText =  abbreviationsMap.get(wordAtCursor);
        //System.out.println("replacing:  "+wordAtCursor+"  with: "+replacingText);
        gExec.Interpreter.GlobalValues.globalEditorPane.replaceSelection( replacingText);
    }
  
    return wordAtCursor;
   }
}
