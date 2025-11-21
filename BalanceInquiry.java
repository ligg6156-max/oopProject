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
import javax.swing.BorderFactory;
import java.awt.GridLayout;

public class BalanceInquiry extends Transaction
{
   private Keypad keypad; // reference to keypad
   private ATM atm; // reference to ATM
   private JPanel screenPanel; // reference to screen panel
   private Thread TakeCardThread;

   protected JProgressBar progressBar; // reference to progress bar
   private int Count100;// constant corresponding to menu option to cancel
   
   // BalanceInquiry constructor
   public BalanceInquiry( int userAccountNumber, Screen atmScreen, 
      BankDatabase atmBankDatabase, Keypad atmKeypad, ATM atmInstance )
   {
      super( userAccountNumber, atmScreen, atmBankDatabase);
      this.keypad = atmKeypad;
      this.atm = atmInstance;
      this.screenPanel = atmInstance.screen_panel;

   } // end BalanceInquiry constructor

   public void execute()
   {
      balanceInquiryUI();
      keypad.waitAction();
      getScreen().clear();
   }
   
   public void balanceInquiryUI()
    { 
   if (screenPanel != null) {
        screenPanel.removeAll();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.setBackground(atm.SCREEN_PANEL_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 10.0;

        Border lineborder = javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5);

        JLabel title = new JLabel("Balance Information", JLabel.CENTER);
        title.setForeground(new Color(255, 255, 255));
        title.setPreferredSize(new Dimension(50, 50));
        title.setBorder(lineborder);
        title.setBackground(atm.GREEN_COLOR);
        title.setOpaque(true);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;  // Span both columns
        screenPanel.add(title, c);

        // Reset gridwidth for info rows
        c.gridwidth = 1;
        c.gridy = 1;

        // Get account information
        BankDatabase bankDatabase = getBankDatabase();
        Account account = bankDatabase.getAccount(getAccountNumber());

        if (account != null) {
            JLabel typeLabel = new JLabel("Account Type", JLabel.CENTER);
            typeLabel.setForeground(new Color(255, 255, 255));
            typeLabel.setPreferredSize(new Dimension(50, 50));
            typeLabel.setBorder(lineborder);
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            screenPanel.add(typeLabel, c);

            JLabel typeValue = new JLabel(getAccountType(account), JLabel.CENTER);
            typeValue.setForeground(new Color(255, 255, 255));
            typeValue.setPreferredSize(new Dimension(50, 50));
            typeValue.setBorder(lineborder);
            c.gridx = 1;
            c.gridy = 1;
            screenPanel.add(typeValue, c);

            // Account Details (differ between different types of account) row
            JLabel detailsLabel = new JLabel("Account Details", JLabel.CENTER);
            detailsLabel.setForeground(new Color(255, 255, 255));
            detailsLabel.setPreferredSize(new Dimension(50, 50));
            detailsLabel.setBorder(lineborder);
            c.gridx = 0;
            c.gridy = 2;
            screenPanel.add(detailsLabel, c);

            JLabel detailsValue = new JLabel(getAccountDetails(account), JLabel.CENTER);
            detailsValue.setForeground(new Color(255, 255, 255));
            detailsValue.setPreferredSize(new Dimension(50, 50));
            detailsValue.setBorder(lineborder);
            c.gridx = 1;
            c.gridy = 2;
            screenPanel.add(detailsValue, c);

            // Available Balance row
            JLabel availLabel = new JLabel("Available Balance", JLabel.CENTER);
            availLabel.setForeground(new Color(255, 255, 255));
            availLabel.setPreferredSize(new Dimension(50, 50));
            availLabel.setBorder(lineborder);
            c.gridx = 0;
            c.gridy = 3;
            screenPanel.add(availLabel, c);

            JLabel availValue = new JLabel("HK$" + String.format("%,.2f", account.getAvailableBalance()), JLabel.CENTER);
            availValue.setForeground(new Color(255, 255, 255));
            availValue.setPreferredSize(new Dimension(50, 50));
            availValue.setBorder(lineborder);
            c.gridx = 1;
            c.gridy = 3;
            screenPanel.add(availValue, c);

            // Total Balance row
            JLabel totalLabel = new JLabel("Total Balance", JLabel.CENTER);
            totalLabel.setForeground(new Color(255, 255, 255));
            totalLabel.setPreferredSize(new Dimension(50, 50));
            totalLabel.setBorder(lineborder);
            c.gridx = 0;
            c.gridy = 4;
            screenPanel.add(totalLabel, c);

            JLabel totalValue = new JLabel("HK$" + String.format("%,.2f", account.getTotalBalance()), JLabel.CENTER);
            totalValue.setForeground(new Color(255, 255, 255));
            totalValue.setPreferredSize(new Dimension(50, 50));
            totalValue.setBorder(lineborder);
            c.gridx = 1;
            c.gridy = 4;
            screenPanel.add(totalValue, c);
            
            JLabel continueLabel = new JLabel("Press ANY KEY to continue", JLabel.CENTER);
            continueLabel.setForeground(new Color(255, 255, 255));
            continueLabel.setPreferredSize(new Dimension(50, 50));
            continueLabel.setBorder(lineborder);
            c.gridx = 0;
            c.gridy = 5;
            c.gridwidth = 2;
            screenPanel.add(continueLabel, c);

        }
        screenPanel.revalidate();
        screenPanel.repaint();
        }
    }

   private String getAccountType(Account account) {
        if (account instanceof Saving_Account) {
            return "Saving Account";
        } else {
            return "Cheque Account";
       } 
   }

   //Return different data based on account type
   private String getAccountDetails(Account account) {
       if (account instanceof Saving_Account) {
           Saving_Account savingAccount = (Saving_Account) account;
           return "Interest Rate: " + String.format("%.2f%%", savingAccount.getInterest_rate() * 100);
        } else {
            Cheque_Account chequeAccount = (Cheque_Account) account;
            return "Cheque Limit: HK$" + (chequeAccount.getLimit_per_cheque());
        }
   }
}


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