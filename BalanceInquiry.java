// BalanceInquiry.java
// Represents a balance inquiry ATM transaction
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

public class BalanceInquiry extends Transaction
{
   private Keypad keypad; // reference to keypad
   private ATM atm; // reference to ATM
   private JPanel screenPanel; // reference to screen panel
   private Thread TakeCardThread;

   protected JProgressBar progressBar; // reference to progress bar
   private int Count100;
   // constant corresponding to menu option to cancel
   
   // BalanceInquiry constructor
   public BalanceInquiry( int userAccountNumber, Screen atmScreen, 
      BankDatabase atmBankDatabase, Keypad atmKeypad, ATM atmInstance )
   {
      super( userAccountNumber, atmScreen, atmBankDatabase);
      this.keypad = atmKeypad;
      this.atm = atmInstance;
      this.screenPanel = atmInstance.screen_panel;

   } // end BalanceInquiry constructor

   // performs the transaction
   public void execute()
   {
      // get references to bank database and screen
      BankDatabase bankDatabase = getBankDatabase();
      Screen screen = getScreen();
      screen.clear();
      Account account = bankDatabase.getAccount(getAccountNumber());
      
      if (account == null) {
         screen.MessagePopup("Error: Account not found. Please contact support for help.");
         return;
      }
      

      // get the available balance for the account involved
      double availableBalance = 
         bankDatabase.getAvailableBalance( getAccountNumber() );

      // get the total balance for the account involved
      double totalBalance = 
         bankDatabase.getTotalBalance( getAccountNumber() );

      // show interest rate if account is saving_acount
      if (account instanceof Saving_Account) {
         Saving_Account savingAccount = (Saving_Account) account;
         screen.displayMessageLine( " - Account Type: Saving Account" );
         screen.displayMessage( " - Interest Rate: " );
         screen.displayMessageLine( String.valueOf(savingAccount.getInterest_rate() * 100) + "%" );
      } else if (account instanceof Cheque_Account) {
         Cheque_Account chequeAccount = (Cheque_Account) account;
         screen.displayMessageLine( " - Account Type: Cheque Account" );
         screen.displayMessage( " - Limit per cheque: " );
         screen.displayDollarAmount( chequeAccount.getLimit_per_cheque() );
         screen.displayMessageLine( "" );
      }// show transfer limit if account is cheque_acount
        
      // display the balance information on the screen
      screen.displayMessageLine( "\nBalance Information:" );
      screen.displayMessage( " - Available balance: " ); 
      screen.displayDollarAmount( availableBalance );
      screen.displayMessage( "\n - Total balance:     " );
      screen.displayDollarAmount( totalBalance );
      screen.displayMessageLine( "" );
      keypad.waitAction();
   } // end method execute

} // end class BalanceInquiry



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