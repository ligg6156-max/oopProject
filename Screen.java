// Screen.java
// Represents the screen of the ATM
import java.awt.TextArea;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class Screen
{
   private TextArea textArea; // Reference to the TextArea in ATM
   private JTextArea jTextArea; // Reference to the JTextArea in ATM
   // Constructor that accepts TextArea from ATM
   public Screen(TextArea textArea) {
      this.textArea = textArea;
   }
   public Screen(JTextArea textArea) {
      this.jTextArea = textArea;
   }
   // displays a message without a carriage return
   public void displayMessage( String message ) 
   {
      System.out.print( message );
      if (textArea != null || jTextArea != null) {
         if (textArea != null) {
            SwingUtilities.invokeLater(() -> textArea.append(message));
         } else {
            SwingUtilities.invokeLater(() -> jTextArea.append(message));
         }
      }
   } // end method displayMessage

   // display a message with a carriage return
   public void displayMessageLine( String message ) 
   {
      System.out.println( message );
      if (textArea != null || jTextArea != null) {
         if (textArea != null) {
            SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
         } else {
            SwingUtilities.invokeLater(() -> jTextArea.append(message + "\n"));
         }
      }
   } // end method displayMessageLine

   // display a dollar amount
   public void displayDollarAmount( double amount )
   {
      System.out.printf( "HK$%,.2f", amount );
      if (textArea != null || jTextArea != null) {
         String formatted = String.format("HK$%,.2f", amount);
         if (textArea != null) {
            SwingUtilities.invokeLater(() -> textArea.append(formatted));
         } else {
            SwingUtilities.invokeLater(() -> jTextArea.append(formatted));
         }
      }
   } // end method displayDollarAmount 
   
   // clear the screen
   public void clear()
   {
      if (textArea != null || jTextArea != null) {
         if (textArea != null) {
            SwingUtilities.invokeLater(() -> textArea.setText(""));
         } else {
            SwingUtilities.invokeLater(() -> jTextArea.setText(""));
         }
      }
   } // end method clear
} // end class Screen



/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/