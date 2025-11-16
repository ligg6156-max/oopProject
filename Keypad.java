// Keypad.java
// Represents the keypad of the ATM
import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import javax.swing.JTextArea; // program uses Scanner to obtain user input

public class Keypad implements KeyListener
{
   private Scanner input; // reads data from the command line
   private int keypressed; // stores the key press state
   private TextArea textArea; // reference to GUI TextArea (optional)
   private JTextArea jTextArea; // reference to GUI JTextArea (optional)
   private StringBuilder currentInput; // stores current input being typed
   private boolean passwordMode; // whether to mask input as asterisks
   private int inputStartPosition; // position where user input starts
                         
   // constructor that accepts optional TextArea
   public Keypad(TextArea textArea)
   {
      input = new Scanner( System.in );
      keypressed = 0; // initialize to 0
      this.textArea = textArea;
      this.jTextArea = null;
      this.currentInput = new StringBuilder();
      this.passwordMode = false;
      this.inputStartPosition = 0;
   }
   
   // constructor that accepts optional JTextArea
   public Keypad(JTextArea jTextArea)
   {
      input = new Scanner( System.in );
      keypressed = 0; // initialize to 0
      this.textArea = null;
      this.jTextArea = jTextArea;
      this.currentInput = new StringBuilder();
      this.passwordMode = false;
      this.inputStartPosition = 0;
   }
   // no-argument constructor for terminal-only mode
   public Keypad()
   {
      input = new Scanner( System.in );
      keypressed = 0;
      this.textArea = null;
      this.jTextArea = null;
      this.currentInput = new StringBuilder();
      this.passwordMode = false;
      this.inputStartPosition = 0;
   } // end Keypad constructor

   // KeyListener interface methods
   @Override
   public void keyPressed(KeyEvent event){
      int keyCode = event.getKeyCode();
      if (keyCode == KeyEvent.VK_ENTER) {
         System.out.println("Enter key pressed");
         keypressed = 1;
         // Add newline before processing next input
         if (textArea != null || jTextArea != null) {
            if (textArea != null) {
               textArea.append("\n");
            } else {
               jTextArea.append("\n");
            }
         }
         event.consume(); // Consume event to prevent beep
      } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
         // Handle backspace - remove last character from user input only
         if (textArea != null || jTextArea != null) {
            String text = textArea != null ? textArea.getText() : jTextArea.getText();
            // Only delete if we're in the user input area
            if (text.length() > inputStartPosition && currentInput.length() > 0) {
               if (textArea != null) {
                  textArea.setText(text.substring(0, text.length() - 1));
               } else {
                  jTextArea.setText(text.substring(0, text.length() - 1));
               }
               currentInput.deleteCharAt(currentInput.length() - 1);
            }
         } else if (jTextArea != null) {
            String text = jTextArea.getText();
            // Only delete if we're in the user input area
            if (text.length() > inputStartPosition && currentInput.length() > 0) {
               jTextArea.setText(text.substring(0, text.length() - 1));
               currentInput.deleteCharAt(currentInput.length() - 1);
            }
         } else if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
         }
         event.consume(); // Consume event to prevent beep
      }
   }
   
   @Override
   public void keyReleased(KeyEvent event) {

   }
   
   public int getkeyPressed(){
        int value = keypressed;
        keypressed = 0;
        return value;
   }
   
   public void setPasswordMode(boolean enabled) {
      this.passwordMode = enabled;
   }
   
   public boolean isPasswordMode() {
      return this.passwordMode;
   }
   
   @Override
   public void keyTyped(KeyEvent event) {
      char keyChar = event.getKeyChar();
      // Append typed character to TextArea/JTextArea and currentInput (except Enter and backspace which are handled in keyPressed)
      if (keyChar != KeyEvent.CHAR_UNDEFINED && keyChar != '\n' && keyChar != '\b') {
         if (textArea != null || jTextArea != null) {
            // Display asterisk if in password mode, otherwise display actual character
            if (passwordMode) {
               if (textArea != null) {
                  textArea.append("*");
               } else {
                  jTextArea.append("*");
               }
            } else {
               if (textArea != null) {
                  textArea.append(String.valueOf(keyChar));
               } else {
                  jTextArea.append(String.valueOf(keyChar));
               }
            }
            currentInput.append(keyChar);
         } else if (jTextArea != null) {
            // Display asterisk if in password mode, otherwise display actual character
            if (passwordMode) {
               jTextArea.append("*");
            } else {
               jTextArea.append(String.valueOf(keyChar));
            }
            currentInput.append(keyChar);
         }
      }
      event.consume(); // Consume event to prevent beep when typing in non-editable TextArea
   }

   public int getIntInput()
   {
    
    for(int i = 2; i > -1; i--){
    try{
        String inputLine = "";
        
        // If TextArea or JTextArea is available, wait for GUI input
        if (textArea != null || jTextArea != null) {
            // Clear previous input
            currentInput.setLength(0);
            // Mark where user input starts
            if (textArea != null) {
                inputStartPosition = textArea.getText().length();
            } else {
                inputStartPosition = jTextArea.getText().length();
            }
            
            // Wait for Enter key in GUI
            while (keypressed != 1) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            keypressed = 0; // Reset
            
            // Get input from currentInput buffer
            inputLine = currentInput.toString().trim();
            currentInput.setLength(0); // Clear for next input
        } else {
            // Terminal mode - use Scanner
            inputLine = input.nextLine();
        }
        
        int num = Integer.parseInt(inputLine);
        if (num < 0){
            if(i > 0){
                System.out.printf("\nThe number cannot be negative, %d try remaining\n", i);
                System.out.print("Try again: ");
                continue;
            }
            else
            {
                return -1;
            }   
        }
        return num;
    }
    catch (java.lang.NumberFormatException e)
    {
        if(i > 0){
            System.out.printf("The following input is not an integer! %d try remaining\n", i);
            System.out.print("Try again: ");
        }
        else
        {
            return -1;
        }
    }
   }
   return 0;
}// end method getInput
   public double getDoubleInput()
   {
    // Add newline after message, before input starts
    if (textArea != null) {
        textArea.append("\n");
    } else if (jTextArea != null) {
        jTextArea.append("\n");
    }
    
    for(int i = 2; i > -1; i--){
    try{
        String inputLine = "";
        
        // If TextArea or JTextArea is available, wait for GUI input
        if (textArea != null || jTextArea != null) {
            // Clear previous input
            currentInput.setLength(0);
            // Mark where user input starts
            if (textArea != null) {
                inputStartPosition = textArea.getText().length();
            } else {
                inputStartPosition = jTextArea.getText().length();
            }
            
            // Wait for Enter key in GUI
            while (keypressed != 1) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            keypressed = 0; // Reset
            
            // Get input from currentInput buffer
            inputLine = currentInput.toString().trim();
            currentInput.setLength(0); // Clear for next input
        } else {
            // Terminal mode - use Scanner
            inputLine = input.nextLine();
        }
        
        double num = Double.parseDouble(inputLine);
        if (num < 0){
            if(i > 0){
                System.out.printf("\nThe number cannot be negative, %d try remaining\n", i);
                System.out.print("Try again: ");
                continue;
            }
            else
            {
                return -1;
            }   
        }
        return num;
    }
    catch (java.lang.NumberFormatException e)
    {
        if(i > 0){
            System.out.printf("The following input is not a number! %d try remaining\n", i);
            System.out.print("Try again: ");
        }
        else
        {
            return 0;
        }
    }
   }
   return 0;
}
} // end class Keypad  



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