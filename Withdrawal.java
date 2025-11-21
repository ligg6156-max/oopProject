// Withdrawal.java
// Represents a withdrawal ATM transaction
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.TextArea;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class Withdrawal extends Transaction
{
   private int amount; // amount to withdraw
   private Keypad keypad; // reference to keypad
   private Screen screen; // reference to screen
   private CashDispenser cashDispenser; // reference to cash dispenser
   public JPanel screenPanel; // reference to screen_panel from ATM   
   private ATM atm; // reference to the ATM
   private Thread TakeCardThread;

   protected JProgressBar progressBar; // reference to progress bar
   private int Count100;
   // constant corresponding to menu option to cancel
   private final static int CANCELED = -1;
   private int cashCount[] = {0,0,0};
   // Withdrawal constructor
   public Withdrawal( int userAccountNumber, Screen atmScreen, 
      BankDatabase atmBankDatabase, Keypad atmKeypad, 
      CashDispenser atmCashDispenser, JPanel screenPanel, ATM atm )
   {
      // initialize superclass variables
      super( userAccountNumber, atmScreen, atmBankDatabase );
      
      // initialize references to keypad and cash dispenser
      keypad = atmKeypad;
      cashDispenser = atmCashDispenser;
      this.screenPanel = screenPanel;
      this.atm = atm;
      UIManager.put("Label.background", atm.SCREEN_PANEL_COLOR);
      UIManager.put("ScreenPanel.background", atm.SCREEN_PANEL_COLOR);
      UIManager.put("Label.font", atm.MODERN_FONT);
   } // end Withdrawal constructor
   
   public void withdrawalUI() {
      if (screenPanel != null) {
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         screenPanel.setBackground(atm.SCREEN_PANEL_COLOR);
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1;
         c.weighty = 10;
         // Add title
         Border lineborder = javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5);
         JLabel title = new JLabel("SELECT the amount to withdraw", JLabel.CENTER);
         title.setForeground(new Color(255, 255, 255));
         title.setPreferredSize(new Dimension(50, 50));
         title.setBorder(lineborder);
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 4;  // Span all 4 columns
         screenPanel.add(title, c);
         
         // Reset gridwidth for options
         c.gridwidth = 2;
         c.gridy = 1;
         c.weightx = 0.5;
         // Left column options
         JLabel option1 = new JLabel("HK$200", JLabel.CENTER);
         option1.setForeground(new Color(255, 255, 255));
         option1.setPreferredSize(new Dimension(50, 50)); 
         option1.setBorder(lineborder);
         c.gridwidth = 2;
         c.gridx = 0;  // Column 1
         screenPanel.add(option1, c);
         
         JLabel option2 = new JLabel("HK$400", JLabel.CENTER);
         option2.setForeground(new Color(255, 255, 255));
         option2.setPreferredSize(new Dimension(50, 50));
         option2.setBorder(lineborder);
         c.gridx = 2;
         c.gridwidth = 2; // Width 2
         screenPanel.add(option2, c);
         
         JLabel option3 = new JLabel("HK$800", JLabel.CENTER);
         option3.setForeground(new Color(255, 255, 255));
         option3.setPreferredSize(new Dimension(50, 50)); 
         option3.setBorder(lineborder);
         c.gridy = 2;  // Column 3
         c.gridx = 0;
         c.gridwidth = 2; // Width 2
         screenPanel.add(option3, c);
         
         JLabel option4 = new JLabel("HK$1,000", JLabel.CENTER);
         option4.setForeground(new Color(255, 255, 255));
         option4.setPreferredSize(new Dimension(50, 50));
         option4.setBorder(lineborder);
         c.gridx = 2;  // Column 1
         c.gridwidth = 2; // Width 2
         screenPanel.add(option4, c);
         
         // Manual entry option (spans both columns)
         c.gridy = 3;
         c.gridx = 0;
         c.gridwidth = 4;
         JLabel option5 = new JLabel("<html><p align=center>or</p><br><b>Enter the amount and press ENTER</b></html>", JLabel.CENTER);
         option5.setForeground(new Color(255, 255, 255));
         option5.setPreferredSize(new Dimension(50, 50));
         option5.setBorder(lineborder);
         screenPanel.add(option5, c);

         c.gridy= 4;
         c.gridx = 0;
         c.gridwidth = 3;
         JLabel inputThing = new JLabel("Input Amount Below:", JLabel.CENTER);
         inputThing.setForeground(new Color(0, 0, 0));
         inputThing.setBackground(atm.GREEN_COLOR);
         inputThing.setPreferredSize(new Dimension(75, 50));
         inputThing.setOpaque(true);
         screenPanel.add(inputThing, c);

         // Manual entry option (spans both columns)
         c.gridx = 3;
         c.gridwidth = 1;
         JPanel inputPanel = new JPanel();
         inputPanel.setForeground(new Color(255,255,255));
         inputPanel.setPreferredSize(new Dimension(25, 50));
         inputPanel.setLayout(new BorderLayout());
         inputPanel.setBorder(lineborder);
         screenPanel.add(inputPanel, c);

         TextArea inputField = new TextArea("HK$", 2, 10, TextArea.SCROLLBARS_NONE);
         inputField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
         inputField.setEditable(false); // Must be non-editable so KeyListener controls all input
         inputField.setBackground(atm.SCREEN_PANEL_COLOR);
         inputField.setForeground(new Color(255, 255, 255));
         inputField.setFocusable(true); // Ensure it can receive focus
         inputPanel.add(inputField, BorderLayout.CENTER);
         screen = new Screen(inputField, atm);
         // Update existing keypad to use new inputField (keeps button connections)
         keypad.setTextArea(inputField);
         inputField.addKeyListener(keypad);
         inputField.requestFocusInWindow(); // Request focus so it receives key events
         screenPanel.revalidate();
         screenPanel.repaint();
         atm.userExited = true; // Reset for next session
      }
   }

   public void comfirmationUI() {
      if (screenPanel != null) {
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1;
         c.weighty = 10.0;
         JLabel ComfirmDollar = new JLabel("You are about to withdraw ", JLabel.CENTER);
         ComfirmDollar.setText(ComfirmDollar.getText() + "HK$" + amount);
         ComfirmDollar.setForeground(new Color(255, 255, 255)); // Modern cyan
         ComfirmDollar.setPreferredSize(new Dimension(50, 50));
         c.gridx = 0;
         c.gridy = 1;
         screenPanel.add(ComfirmDollar, c);
         // Add title
         JLabel title1 = new JLabel("Press ENTER to confirm withdrawal", JLabel.CENTER);

         Border lineborder = javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5);
         JLabel title = new JLabel("Press ENTER to confirm withdrawal", JLabel.CENTER);
         title.setForeground(new Color(255, 255, 255));
         title.setPreferredSize(new Dimension(50, 50));
         title.setBorder(lineborder);
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 2;
         screenPanel.add(title, c);
         
         // Reset gridwidth for options
         c.gridwidth = 1;
         
         screenPanel.revalidate();
         screenPanel.repaint();
      }
   }

   public void ProcessingUI(){
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1.0;
         c.weighty = 10.0;
         c.gridx = 0;
         c.gridy = 0;
         c.gridheight = 1;
         JLabel processingLabel = new JLabel("<html><b align=center>Your request is being processed.</b><br><b align=center>Please wait...</b></html>", 
         JLabel.CENTER);
         
         c.gridy = 1;
         screenPanel.add(Box.createVerticalStrut(200), c);
         processingLabel.setBackground(atm.GREEN_COLOR);
         processingLabel.setForeground(new Color(0,0,0));
         processingLabel.setPreferredSize(new Dimension(50, 50));
         screenPanel.add(processingLabel, c);
         c.gridy = 2;
         c.gridheight = 1;
         progressBar = new JProgressBar(0,Count100);
         screenPanel.add(progressBar, c);
         c.gridy = 3;
         c.gridheight = 2;
         screenPanel.add(Box.createVerticalStrut(200), c);
         screenPanel.revalidate();
         screenPanel.repaint();
   }
      public void TakeCardUI(){
         TakeCardThread = new Thread(() -> {
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1.0;
         c.weighty = 10.0;
         c.gridx = 0;
         c.gridy = 0;
         c.gridheight = 1;
         
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         c.gridwidth = 2;
         JLabel accepted = new JLabel("<html><b>Your withdraw is accepted</b></html>", JLabel.CENTER);
         accepted.setBackground(atm.GREEN_COLOR);
         accepted.setForeground(new Color(0,0,0));
         accepted.setPreferredSize(new Dimension(50, 25));
         screenPanel.add(accepted, c);
         c.gridy = 1;
         screenPanel.add(Box.createVerticalStrut(100), c);
         JLabel PleaseSelect = new JLabel("<html><b>Please select</b></html>", JLabel.CENTER);
         PleaseSelect.setForeground(new Color(255,255,255));
         PleaseSelect.setPreferredSize(new Dimension(50, 50));
         c.gridy = 2;
         PleaseSelect.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5));
         screenPanel.add(PleaseSelect, c);
         c.gridx = 0;
         c.gridy = 3;
         c.gridwidth = 1;
         JLabel blank = new JLabel("", JLabel.CENTER);
         blank.setForeground(new Color(255,255,255));
         blank.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5));
         screenPanel.add(blank, c);
         c.gridx = 1;
         JLabel print_advice = new JLabel("<html><b>Print advice & take card</b></html>", JLabel.CENTER);
         print_advice.setForeground(new Color(255,255,255));
         print_advice.setPreferredSize(new Dimension(50, 50));
         print_advice.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5));
         screenPanel.add(print_advice, c);
         
         c.gridx = 0;
         c.gridy = 4;
         JLabel blank1 = new JLabel("", JLabel.CENTER);
         blank1.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5));
         screenPanel.add(blank1, c);
         c.gridx = 1;
         JLabel no_advice = new JLabel("<html><b>Take card</b></html>", JLabel.CENTER);
         no_advice.setForeground(new Color(255,255,255));
         no_advice.setPreferredSize(new Dimension(50, 50));
         no_advice.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5));
         screenPanel.add(no_advice, c);

         screenPanel.revalidate();
         screenPanel.repaint();
         Boolean waitForTakeCard = true;
         while(waitForTakeCard == true){
         keypad.waitAction();
         System.out.printf("%d", keypad.ButtonPressedMemory);
         switch(keypad.getButtonPressed()){
         case 7://temporary set value change later
             System.out.println("Print advice & take card selected.");
             waitForTakeCard = false;
             break;
         case 8:
             System.out.println("Take card selected.");
             waitForTakeCard = false;
             break;
         case 0,1,2,3,4,5,6:
            System.out.println("Invalid selection, please select again.");
            break;
                }
         }
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         c = new GridBagConstraints();  // Reset constraints
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1.0;
         c.weighty = 5.0;
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 1;
         JLabel takeCardLabel = new JLabel("<html><b>Thank you for choosing ATM</b></html>", JLabel.CENTER);
         takeCardLabel.setBackground(atm.GREEN_COLOR);
         takeCardLabel.setForeground(new Color(0,0,0));
         takeCardLabel.setPreferredSize(new Dimension(50, 50));
         takeCardLabel.setOpaque(true);
         screenPanel.add(takeCardLabel, c);
         c.gridy = 1;
         screenPanel.add(Box.createVerticalStrut(100), c);
         c.gridy=2;
         JLabel takeyourCard = new JLabel("<html><b>Please take your card</b></html>", JLabel.CENTER);
         takeyourCard.setForeground(new Color(255,255,255));
         screenPanel.add(takeyourCard, c);
         c.gridy=3;
         Icon takecardIcon = new ImageIcon(getClass().getResource("takeoutCard.png"));
         Image img = ((ImageIcon) takecardIcon).getImage();
         Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
         Icon scaledIcon = new ImageIcon(scaledImg);
         screenPanel.add(new JLabel(scaledIcon), c);
         c.gridy=4;
         screenPanel.add(Box.createVerticalStrut(100), c);
         screenPanel.revalidate();
         screenPanel.repaint();
         keypad.waitAction();

         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         JLabel WithdrawAountLabel = new JLabel("You get "+ cashCount[0] +" HKD1000, "+cashCount[1] + " HKD500" + cashCount[2] + " HKD100", JLabel.CENTER);
         WithdrawAountLabel.setFont(atm.MODERN_FONT);
         WithdrawAountLabel.setForeground(new Color(255,255,255));
         WithdrawAountLabel.setPreferredSize(new Dimension(50, 50));
         c.gridy = 0;
         c.gridx = 0;
         screenPanel.add(WithdrawAountLabel, c);
         JLabel takeyourCash = new JLabel("<html><b>Please take your cash</b></html>", JLabel.CENTER);
         takeyourCash.setForeground(new Color(255,255,255));
         c.gridy = 1;
         screenPanel.add(takeyourCash, c);
         screenPanel.revalidate();
         screenPanel.repaint();
         keypad.waitAction();
         
      });
      TakeCardThread.start();
   }
   // perform transaction
   public void execute()
   {
      keypad.buttonPressState = true;
      Screen screen = getScreen();
      screen.clear();
      withdrawalUI();
      boolean cashDispensed = false; // cash was not dispensed yet
      double availableBalance; // amount available for withdrawal

      // get references to bank database and screen
      BankDatabase bankDatabase = getBankDatabase();
      Account account = bankDatabase.getAccount(getAccountNumber());
      // loop until cash is dispensed or the user cancels
      do
      {
         // obtain a chosen withdrawal amount from the user 
         amount = displayMenuOfAmounts();
         
         // check whether user chose a withdrawal amount or canceled
         if ( amount != CANCELED )
         {
            // get available balance of account involved

               
            if (account instanceof Cheque_Account) {
               Cheque_Account chequeAccount = (Cheque_Account) account;
               if (amount > chequeAccount.getLimit_per_cheque()) {
                  screen.displayMessageLine( 
                     "\nAmount exceeds cheque limit of " );
                  screen.displayDollarAmount(chequeAccount.getLimit_per_cheque());
                  screen.displayMessageLine( "\nPlease choose a smaller amount." );
                  screen.MessagePopup("Amount exceeds cheque limit of HKD 50000\nPlease choose a smaller amount.");
                  continue;
                  // will loop again for user to type another amount
               }
            }
            
            availableBalance = 
               bankDatabase.getAvailableBalance( getAccountNumber() );
               
            // check whether the user has enough money in the account 
            if ( amount <= availableBalance )
            {  
               int tmp = amount;
               Count100 = amount / 100;
               // check whether the cash dispenser has enough money
               if ( cashDispenser.isSufficientCashAvailable( amount ) )
               {
                  comfirmationUI();
                  // wait for user to press enter to confirm
                  keypad.waitAction();
                  while(true){
                  if (keypad.getButtonPressed() == 2)//temporary set value change later
                  {
                      screen.displayMessageLine( "\nCanceling transaction..." );
                      screen.MessagePopup("Transaction canceled.");
                  } 
                  else if (keypad.getButtonPressed() == 4 || keypad.getButtonPressed() == 0){
                     ProcessingUI();
                     while (tmp > 0){
                     if (tmp >= 1000 && cashCount[0] < cashDispenser.getCashCount(0)){
                        cashCount[0] ++;
                        progressBar.setValue(progressBar.getValue() + 10);
                        tmp -=1000;
                        try { Thread.sleep(200); } catch (InterruptedException e) {} // Delay to show progress
                        }
                        else if (tmp >= 500 && cashCount[1] < cashDispenser.getCashCount(1)){
                        cashCount[1] ++;
                        progressBar.setValue(progressBar.getValue() + 5);
                        tmp -= 500;
                        try { Thread.sleep(200); } catch (InterruptedException e) {} // Delay to show progress
                        }
                        else if(tmp >=100 &&  cashCount[2] < cashDispenser.getCashCount(2)){
                        cashCount[2] ++;
                        progressBar.setValue(progressBar.getValue() + 1);
                        tmp -= 100;
                        try { Thread.sleep(200); } catch (InterruptedException e) {} // Delay to show progress
                     }
                     }
                     break;
                  } else {
                     continue;
                  }
               }
                   // update the account involved to reflect withdrawal
                  bankDatabase.debit( getAccountNumber(), amount );
                  
                  cashDispenser.dispenseCash( cashCount[0], cashCount[1], cashCount[2] ); // dispense cash
                  cashDispensed = true; // cash was dispensed
                  // instruct user to take cash
                  System.out.printf( 
                     "\nPlease take your cash now. \nYou Get %d HK$100, %d HK$500 and %d HK$1,000 withdraw from ",cashCount[2],cashCount[1],cashCount[0]);
                     screen.displayDollarAmount(amount);
                     TakeCardUI();
                      try {
                        TakeCardThread.join(); // Wait for TakeCardThread to finish
                        System.out.println("Main thread continues after TakeCardThread finished.");
                           } catch (InterruptedException e) {
                              System.out.println("Main thread interrupted while waiting.");
                              Thread.currentThread().interrupt();
                           } // end if
                        }
               else // cash dispenser does not have enough cash
                  screen.displayMessageLine( 
                     "\nInsufficient cash available in the ATM." +
                     "\n\nPlease choose a smaller amount." );
                     screen.MessagePopup("Insufficient cash available in the ATM.\nPlease choose a smaller amount.");
            } // end if
            else // not enough money available in user's account
            {
               screen.displayMessageLine( 
                  "\nInsufficient funds in your account." +
                  "\n\nPlease choose a smaller amount." );
               screen.MessagePopup("Insufficient funds in your account.\nPlease choose a smaller amount.");
            } // end else
         } // end if
         else // user chose cancel menu option 
         {
            screen.displayMessageLine( "\nCanceling transaction..." );
            screen.MessagePopup("Transaction canceled.");
            return; // return to main menu because user canceled
         } // end else
      } while ( !cashDispensed );

   } // end method execute

   // display a menu of withdrawal amounts and the option to cancel;
   // return the chosen amount or 0 if the user chooses to cancel
   private int displayMenuOfAmounts()
   {
      int userChoice = 0; // local variable to store return value

      Screen screen = getScreen(); // get screen reference
      
      // array of amounts to correspond to menu numbers
      int amounts[] = { 0, 200, 400, 800, 1000, 0};

      // loop while no valid choice has been made
      while ( userChoice == 0 )
      {
         // display the menu
         screen.displayMessageLine( "\nWithdrawal Menu:" );
         screen.displayMessageLine( "1 - HK$200" );
         screen.displayMessageLine( "2 - HK$400" );
         screen.displayMessageLine( "3 - HK$800" );
         screen.displayMessageLine( "4 - HK$1,000" );
         screen.displayMessageLine( "5 - Type out the amount of cash withdraw manually" );
         screen.displayMessageLine( "6 - Cancel transaction" );
         screen.displayMessage( "\nChoose a withdrawal amount: " );
         int input = keypad.getIntInput(); // get user input through keypad
         boolean times = true;
         int tempory = keypad.getButtonPressed();
            while(true){
               
               if (input%100 == 0 || tempory != 0){
                  System.out.println("Debug: Valid input received: " + input);
                  break;
               }
               else if (input <= 0){
               break;
               }
               else{
               screen.displayMessage( "\nThe amount must be the mutiple of HK$100, try again.\n Or press 0 to return Withdrawal Menu.\n");
               screen.MessagePopup("The amount must be the mutiple of HK$100");
               this.screen.clear();
               this.screen.displayMessage("HK$");
               input = keypad.getIntInput();

                }
            }
         if (tempory == 1) {
             input = 1;
             System.out.println("Debug: Button 1 pressed, setting input to 1");
         } else if (tempory == 2) {
            System.out.println("Debug: Button 2 pressed, setting input to 3");
             input = 3;
         } else if (tempory == 3) {
             input = 5;
             System.out.println("Debug: Button 3 pressed, setting input to 5");
         } else if (tempory == 4) {
             input = 7;
             System.out.println("Debug: Button 4 pressed, setting input to 7");
         } else if (tempory == 5) {
             input = 2;
             System.out.println("Debug: Button 5 pressed, setting input to 2");
         } else if (tempory == 6) {
             input = 4;
         } else if (tempory == 7) {
             input = 6; // Invalid input
         } else if (tempory == 8) {
             input = 8; // Invalid input
         } else {
               // No button pressed, keep input as is
         }
         switch ( input )
         {
            case 1: // if the user chose a withdrawal amount 
            case 2: // (i.e., chose option 1, 2, 3, 4 or 5), return the
            case 3: // corresponding amount from amounts array
            case 4:
               userChoice = amounts[ input ]; // save user's choice
               break; 
            case 5:
               screen.displayMessageLine( "Type out the amount of cash withdraw manually" );
               this.screen.clear();
               this.screen.displayMessage("HK$");
               while(times == true){
                   input = keypad.getIntInput();
                   this.screen.clear();
                   this.screen.displayMessage("HK$");
               if (input%100 == 0){
                   userChoice = input;
                   times = false;
                   break;
               }
               else if (input <= 0){
               break;
               }
               else{
               screen.displayMessage( "\nThe amount must be the mutiple of HK$100, try again.\n Or press 0 to return Withdrawal Menu.\n"); 
               screen.MessagePopup("The amount must be the mutiple of HK$100");
               
                }
               
            }
            break;
            case 6: // the user chose to cancel
               userChoice = CANCELED; // save user's choice
               break;
            default: // the user did not enter a value from 1-6
               times = true;
               while(times == true){
                   
               if (input%100 == 0){
                   userChoice = input;
                   times = false;
                   break;
               }
               else if (input <= 0){
               break;
               }
               else{
               screen.displayMessage( "\nThe amount must be the mutiple of HK$100, try again.\n Or press 0 to return Withdrawal Menu.\n");
               screen.MessagePopup("The amount must be the mutiple of HK$100");
               this.screen.clear();
               this.screen.displayMessage("HK$");
               input = keypad.getIntInput();

                }
            }
            break;
         } // end switch
      } // end while

      return userChoice; // return withdrawal amount or CANCELED
   } // end method displayMenuOfAmounts
} // end class Withdrawal



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