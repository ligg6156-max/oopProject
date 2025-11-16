// Withdrawal.java
// Represents a withdrawal ATM transaction
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
public class Withdrawal extends Transaction
{
   private int amount; // amount to withdraw
   private Keypad keypad; // reference to keypad
   private CashDispenser cashDispenser; // reference to cash dispenser
   public JPanel screenPanel; // reference to screen_panel from ATM
   
   // constant corresponding to menu option to cancel
   private final static int CANCELED = -1;

   // Withdrawal constructor
   public Withdrawal( int userAccountNumber, Screen atmScreen, 
      BankDatabase atmBankDatabase, Keypad atmKeypad, 
      CashDispenser atmCashDispenser, JPanel screenPanel )
   {
      // initialize superclass variables
      super( userAccountNumber, atmScreen, atmBankDatabase );
      
      // initialize references to keypad and cash dispenser
      keypad = atmKeypad;
      cashDispenser = atmCashDispenser;
      this.screenPanel = screenPanel;
   } // end Withdrawal constructor
   
   public void withdrawalUI() {
      if (screenPanel != null) {
         screenPanel.removeAll();
         screenPanel.setLayout(new GridBagLayout());
         screenPanel.setBackground(new Color(0, 0, 255)); // Match modern theme
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1.0;
         c.weighty = 10.0;
         // Add title
         Border lineborder = javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5);
         JLabel title = new JLabel("Withdrawal - Please select amount:", JLabel.CENTER);
         title.setForeground(new Color(255, 255, 255)); // Modern cyan
         title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
         title.setPreferredSize(new Dimension(50, 50));
         title.setBorder(lineborder);
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 2;
         screenPanel.add(title, c);
         
         // Reset gridwidth for options
         c.gridwidth = 1;
         c.gridy = 1;
         
         // Left column options
         JLabel option1 = new JLabel("1 - HK$200", JLabel.CENTER);
         option1.setForeground(new Color(255, 255, 255));
         option1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option1.setPreferredSize(new Dimension(50, 50)); 
         option1.setBorder(lineborder);
         c.gridx = 0;
         screenPanel.add(option1, c);
         
         JLabel option3 = new JLabel("3 - HK$800", JLabel.CENTER);
         option3.setForeground(new Color(255, 255, 255));
         option3.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option3.setPreferredSize(new Dimension(50, 50)); 
         option3.setBorder(lineborder);
         c.gridx = 1;
         screenPanel.add(option3, c);
         
         // Second row
         c.gridy = 2;
         
         JLabel option2 = new JLabel("2 - HK$400", JLabel.CENTER);
         option2.setForeground(new Color(255, 255, 255));
         option2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option2.setPreferredSize(new Dimension(50, 50));
         option2.setBorder(lineborder);
         c.gridx = 0;
         screenPanel.add(option2, c);
         
         JLabel option4 = new JLabel("4 - HK$1,000", JLabel.CENTER);
         option4.setForeground(new Color(255, 255, 255));
         option4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option4.setPreferredSize(new Dimension(50, 50));
         option4.setBorder(lineborder);
         c.gridx = 1;
         screenPanel.add(option4, c);
         
         // Manual entry option (spans both columns)
         c.gridy = 3;
         c.gridx = 0;
         c.gridwidth = 2;
         JLabel option5 = new JLabel("5 - Type amount manually", JLabel.CENTER);
         option5.setForeground(new Color(255, 255, 255));
         option5.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option5.setPreferredSize(new Dimension(50, 50));
         option5.setBorder(lineborder);
         screenPanel.add(option5, c);

         // Manual entry option (spans both columns)
         c.gridy = 4;
         c.gridx = 0;
         c.gridwidth = 2;
         JLabel option6 = new JLabel("5 - Type amount manually", JLabel.CENTER);
         option6.setForeground(new Color(255, 255, 255));
         option6.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option6.setPreferredSize(new Dimension(50, 50));
         option6.setBorder(lineborder);
         screenPanel.add(option6, c);
         
         screenPanel.revalidate();
         screenPanel.repaint();
      }
   }
   // perform transaction
   public void execute()
   {
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
                  continue;
                  // will loop again for user to type another amount
               }
            }
            
            availableBalance = 
               bankDatabase.getAvailableBalance( getAccountNumber() );
               
            // check whether the user has enough money in the account 
            if ( amount <= availableBalance )
            {  
               // check whether the cash dispenser has enough money
               if ( cashDispenser.isSufficientCashAvailable( amount ) )
               {
                  int tmp = amount;
                  int cashCount[] = {0,0,0};
                  while (tmp > 0){
                  if (tmp >= 1000 && cashCount[0] < cashDispenser.getCashCount(0)){
                    cashCount[0] ++;
                    tmp -=1000;
                    }
                    else if (tmp >= 500 && cashCount[1] < cashDispenser.getCashCount(1)){
                    cashCount[1] ++;
                    tmp -= 500;
                    }
                    else if(tmp >=100 &&  cashCount[2] < cashDispenser.getCashCount(2)){
                    cashCount[2] ++;
                    tmp -= 100;
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
               } // end if
               else // cash dispenser does not have enough cash
                  screen.displayMessageLine( 
                     "\nInsufficient cash available in the ATM." +
                     "\n\nPlease choose a smaller amount." );
            } // end if
            else // not enough money available in user's account
            {
               screen.displayMessageLine( 
                  "\nInsufficient funds in your account." +
                  "\n\nPlease choose a smaller amount." );
            } // end else
         } // end if
         else // user chose cancel menu option 
         {
            screen.displayMessageLine( "\nCanceling transaction..." );
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
         // determine how to proceed based on the input value
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
               boolean times = true;
               while(times == true){
                   input = keypad.getIntInput();
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
               
                }
               
            }
            break;
            case 6: // the user chose to cancel
               userChoice = CANCELED; // save user's choice
               break;
            default: // the user did not enter a value from 1-6
               screen.displayMessageLine( 
                  "\nIvalid selection. Try again." );
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