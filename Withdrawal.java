// Withdrawal.java
// Represents a withdrawal ATM transaction
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class Withdrawal extends Transaction
{
   private int amount; // amount to withdraw
   private Keypad keypad; // reference to keypad
   private CashDispenser cashDispenser; // reference to cash dispenser
   private JPanel screenPanel; // reference to screen_panel from ATM
   
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
         
         // Create a new panel for withdrawal UI
         JPanel withdrawalPanel = new JPanel(new BorderLayout(10,10));
         withdrawalPanel.setBackground(Color.BLUE);
         screenPanel.add(withdrawalPanel);
         // Add instruction
         JLabel Title = new JLabel("Withdraw\nPlease select withdrawal amount:", JLabel.CENTER);
         Title.setForeground(Color.WHITE);
         withdrawalPanel.add(Title, BorderLayout.NORTH);
         JPanel Table = new JPanel(new GridLayout(2,2,10,10));
         Table.setBackground(Color.BLUE);
         JLabel option1 = new JLabel("1 - HK$200", JLabel.CENTER);
         option1.setForeground(Color.WHITE);
         JLabel option2 = new JLabel("2 - HK$400", JLabel.CENTER);
         option2.setForeground(Color.WHITE);
         JLabel option3 = new JLabel("3 - HK$800", JLabel.CENTER);
         option3.setForeground(Color.WHITE);
         JLabel option4 = new JLabel("4 - HK$1,000", JLabel.CENTER);
         option4.setForeground(Color.WHITE);
         Table.add(option1);
         Table.add(option2);
         Table.add(option3);
         Table.add(option4);
         withdrawalPanel.add(Table, BorderLayout.CENTER);
         withdrawalPanel.add(Table, BorderLayout.CENTER);
         JLabel option5 = new JLabel("5 - Type out the amount of cash withdraw manually", JLabel.CENTER);
         option5.setForeground(Color.WHITE);
         withdrawalPanel.add(option5, BorderLayout.SOUTH);
         
         // Add the withdrawal panel to screen_panel
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