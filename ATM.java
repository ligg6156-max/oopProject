// ATM.java
// Represents an automated teller machine
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.JOptionPane;
public class ATM 
{
   private boolean userAuthenticated; // whether user is authenticated
   private int currentAccountNumber; // current user's account number
   private Screen screen; // ATM's screen
   private Keypad keypad; // ATM's keypad
   private CashDispenser cashDispenser; // ATM's cash dispenser
   private BankDatabase bankDatabase; // account information database

   // constants corresponding to main menu options
   private static final int BALANCE_INQUIRY = 1;
   private static final int WITHDRAWAL = 2;
   private static final int TRANSFER = 3;
   private static final int EXIT = 4;
   private static final int CANCELED = -1;
   protected JFrame frame;
   protected JButton[] Sidebuttons;
   protected JLabel label;
   protected JPanel screen_panel;
   private TextArea displayArea; // For both displaying messages and input
   // no-argument ATM constructor initializes instance variables
   public ATM() 
   {
      userAuthenticated = false; // user is not authenticated to start
      currentAccountNumber = 0; // no current account number to start
      // Create single TextArea for display and input (4:3 aspect ratio)
      displayArea = new TextArea("HK$", 30, 80, TextArea.SCROLLBARS_NONE); // Approximately 4:3 ratio for inner screen, no scrollbars
      displayArea.setEditable(false); // Make it non-editable - use KeyListener for input
      displayArea.setBackground(new Color(0, 0, 255)); // Modern dark background
      displayArea.setForeground(new Color(255, 255, 255)); // Modern cyan text
      screen = new Screen(displayArea); // create screen with display TextArea
      cashDispenser = new CashDispenser(); // create cash dispenser
      bankDatabase = new BankDatabase(); // create acct info database
      JPanel mainpanel = new JPanel(new BorderLayout(15,15));
      mainpanel.setBackground(new Color(30, 30, 40)); // Modern dark gray
      screen_panel = new JPanel(new BorderLayout());
      screen_panel.setPreferredSize(new Dimension(800, 600));
      screen_panel.setBackground(new Color(20, 20, 30)); // Match display area
      screen_panel.add(new JLabel("Loading"));
      frame = new JFrame("ATM Machine");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(1120, 720); // 5:3 aspect ratio (1000 width, 600 height)
      frame.setLayout(new BorderLayout(10,10));
      frame.setBackground(Color.BLACK); // Set frame background to black
      UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 18));
      UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 22));
      Font customFont = new Font("Consolas", Font.PLAIN, 18);
      displayArea.setFont(customFont);
      UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 18));
      // Create keypad after frame and TextArea are created
      keypad = new Keypad(displayArea); // create keypad with TextArea for GUI input
      frame.addKeyListener(keypad); // register keypad as listener for frame
      displayArea.addKeyListener(keypad); // register keypad as listener for TextArea
      frame.setFocusable(true); // Make frame focusable to receive key events
      mainpanel.add(screen_panel, BorderLayout.CENTER);
            // Create side button panels. BorderLayout only accepts one component
      // per region, so use a panel with GridLayout for multiple buttons.
      Sidebuttons = new JButton[8];
      JPanel leftPanel = new JPanel(new GridBagLayout());
      JPanel rightPanel = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.CENTER;
      c.weightx = 50;
      c.weighty = 50;
      c.gridx = 0;
      c.gridy= 0;
      leftPanel.add(Box.createVerticalStrut(100), c);
      leftPanel.add(Box.createVerticalStrut(100), c);
      rightPanel.add(Box.createVerticalStrut(100), c);
      rightPanel.add(Box.createVerticalStrut(100), c);
      leftPanel.setPreferredSize(new Dimension(180, 0)); // Set width to 150 pixels for reasonable button size
      rightPanel.setPreferredSize(new Dimension(150, 0)); // Set width to 150 pixels for reasonable button size
      rightPanel.setBackground(new Color(30, 30, 40));
      leftPanel.setBackground(new Color(30, 30, 40));
      for (int i = 0; i < 4; i++) {
         c.gridy = i + 1;
         Sidebuttons[i] = new JButton("►");
         Sidebuttons[i].setPreferredSize(new Dimension(100, 100));
         Sidebuttons[i].setBackground(new Color(0, 0, 0)); // Modern cyan
         Sidebuttons[i].setForeground(Color.WHITE);
         Sidebuttons[i].setFocusable(false); // Prevent button from taking focus
         final int buttonIndex = i + 1; // Button 1-4 for left side
         Sidebuttons[i].addActionListener(e -> {
            keypad.setButtonInput(buttonIndex); // Set the button value
         });
         leftPanel.add(Sidebuttons[i], c);
      }
      c.gridy = 0;
      for (int i = 4; i < 8; i++) {
         c.gridy = i + 1;
         Sidebuttons[i] = new JButton("◄");
         Sidebuttons[i].setPreferredSize(new Dimension(100, 100));
         Sidebuttons[i].setBackground(new Color(0, 0, 0)); // Modern cyan
         Sidebuttons[i].setForeground(Color.WHITE);
         Sidebuttons[i].setFocusable(false); // Prevent button from taking focus
         final int buttonIndex = i + 1; // Button 5-8 for right side
         Sidebuttons[i].addActionListener(e -> {
            keypad.setButtonInput(buttonIndex); // Set the button value
         });
         rightPanel.add(Sidebuttons[i], c);
      }

      mainpanel.add(leftPanel, BorderLayout.WEST);
      mainpanel.add(rightPanel, BorderLayout.EAST);
      frame.add(mainpanel);
      frame.setVisible(true);
      frame.requestFocus(); // Request focus so key events are received
   } // end no-argument ATM constructor
   
   // start ATM 
   public void wellcome(){
      screen_panel.removeAll();
      screen_panel.setLayout(new FlowLayout());
      screen_panel.setBackground(new Color(0, 0, 255)); // Keep modern dark background
      JLabel welcomeLabel = new JLabel("Welcome to the ATM - Press Enter to begin");
      welcomeLabel.setForeground(new Color(255, 255, 255)); // Modern cyan text to match
      screen_panel.add(welcomeLabel);
      screen_panel.revalidate();
      screen_panel.repaint();
      
      // Wait for Enter key press (blocking)
      keypad.waitAction();
      // Enter was pressed, show login screen
      loginScreen();
   }
   
   public void loginScreen(){
      screen_panel.removeAll();
      screen_panel.setLayout(new BorderLayout()); // Reset to BorderLayout for proper stretching
      screen_panel.add(displayArea, BorderLayout.CENTER); // Add to CENTER for full coverage
      screen_panel.revalidate();
      screen_panel.repaint();
      displayArea.requestFocus(); // Focus on TextArea
      // Restore keypad to main displayArea
      keypad.setTextArea(displayArea);
   }
   public void run()
   {
      keypad.buttonPressState = false;
      // Start wellcome in a separate thread so GUI doesn't freeze
      Thread atmThread = new Thread(() -> {
         wellcome();
         // welcome and authenticate user; perform transactions
         while ( true )
         {
            // loop while user is not yet authenticated
            while ( !userAuthenticated ) 
            {
               screen.displayMessageLine( "\nWelcome!" );       
               authenticateUser(); // authenticate user
            } // end while
            
            performTransactions(); // user is now authenticated 
            userAuthenticated = false; // reset before next ATM session
            currentAccountNumber = 0; // reset before next ATM session 
            screen.displayMessageLine( "\nThank you! Goodbye!" );
         } // end while
      });
      atmThread.start();
   } // end method run

   // attempts to authenticate user against database
   private void authenticateUser() 
   {
      screen.clear();
      screen.displayMessage( "\nPlease enter your account number: " );
      int accountNumber = keypad.getIntInput(); // input account number
      if (accountNumber == CANCELED){
        screen.clear();
        screen.displayMessageLine( "Invalid account number or PIN. Please try again." );
        return;
        }
      screen.displayMessage( "\nEnter your PIN: " ); // prompt for PIN
      keypad.setPasswordMode(true); // Enable password masking
      int pin = keypad.getIntInput(); // input PIN
      keypad.setPasswordMode(false); // Disable password masking
      if (accountNumber == CANCELED){
        screen.clear();
        screen.displayMessageLine( "Invalid account number or PIN. Please try again." );
        return;
      }
      
      // set userAuthenticated to boolean value returned by database
      userAuthenticated = 
         bankDatabase.authenticateUser( accountNumber, pin );
      
      // check whether authentication succeeded
      if ( userAuthenticated )
      {
         currentAccountNumber = accountNumber; // save user's account #
      } // end if
      else
      {
         screen.clear();
         screen.displayMessageLine( 
             "Invalid account number or PIN. Please try again." );
      }
   } // end method authenticateUser
private Transaction createTransaction(int type) {
    Transaction temp = null;

    switch (type) {
        case BALANCE_INQUIRY:
            temp = new BalanceInquiry(currentAccountNumber, screen, bankDatabase);
            break;
        case WITHDRAWAL:
            temp = new Withdrawal(currentAccountNumber, screen, bankDatabase, keypad, cashDispenser, screen_panel);
            break;
      
        case TRANSFER:
            temp = new Transfer(currentAccountNumber, screen, bankDatabase, keypad);
            break;
    }

    return temp;
}
   // display the main menu and perform transactions
   private void performTransactions() 
   {
      // local variable to store transaction currently being processed
      Transaction currentTransaction = null;
      
      boolean userExited = false; // user has not chosen to exit

      // loop while user has not chosen option to exit system
      while ( !userExited )
      {     
         // show main menu and get user selection
         int mainMenuSelection = displayMainMenu();

         // decide how to proceed based on user's menu selection
         switch ( mainMenuSelection )
         {
            // user chose to perform one of three transaction types
            case BALANCE_INQUIRY: 
            case WITHDRAWAL: 
            case TRANSFER:
               // initialize as new object of chosen type
               currentTransaction = 
                  createTransaction( mainMenuSelection );

               currentTransaction.execute(); // execute transaction
               break; 
            case EXIT: // user chose to terminate session
               JOptionPane.showMessageDialog(null, "Exiting the system...", "EXIT",JOptionPane.INFORMATION_MESSAGE);
               userExited = true; // this ATM session should end
               break;
            case CANCELED:
               screen.displayMessageLine( "\nExiting the system..." );
               userExited = true; // this ATM session should end
               break;
            default: // user did not enter an integer from 1-4
            System.out.printf("%s",mainMenuSelection);
               screen.displayMessageLine( 
                  "\nYou did not enter a valid selection. Try again." );
               break;
         } // end switch
      } // end while
   } // end method performTransactions
   
   // display the main menu and return an input selection
   private int displayMainMenu()
   {
       int input;
       keypad.buttonPressState = true;
       keypad.ButtonPressed = 0;
       MainmenuUI();
       input = keypad.getIntInput(); // return user's selection
        if (input ==1 || input ==5)
            input = 1;
        else if (input ==2 || input ==6) 
            input = 2;
        else if (input ==3 || input ==7) 
            input = 3;
        else if (input ==4 || input ==8)
            input = 4;
        else {
            // No button pressed, keep input as is
         }
      return input;
   } // end method displayMainMenu
   
   private void MainmenuUI(){
         screen_panel.removeAll();
         screen_panel.setLayout(new GridBagLayout());
         screen_panel.setBackground(new Color(0, 0, 255)); // Match modern theme
         GridBagConstraints c = new GridBagConstraints();
         c.fill = GridBagConstraints.BOTH;
         c.insets = new java.awt.Insets(10, 10, 10, 10);
         c.weightx = 1.0;
         c.weighty = 10.0;
         // Add title
         Border lineborder = javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5);
         JLabel title = new JLabel("Main menu", JLabel.CENTER);
         title.setForeground(new Color(255, 255, 255)); // Modern cyan
         title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
         title.setPreferredSize(new Dimension(50, 50));
         title.setBorder(lineborder);
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 2;
         screen_panel.add(title, c);
         
         // Reset gridwidth for options
         c.gridwidth = 1;
         c.gridy = 1;
         
         // options
         // 111111test for test
         c.gridy = 1;
         c.gridx = 0;
         c.gridwidth = 1;
         JLabel option1 = new JLabel("View my balance", JLabel.CENTER);
         option1.setForeground(new Color(255, 255, 255));
         option1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option1.setPreferredSize(new Dimension(50, 50));
         option1.setBorder(lineborder);
         screen_panel.add(option1, c);
         
         c.gridy = 2;
         c.gridx = 0;
         c.gridwidth = 1;
         JLabel option2 = new JLabel("Withdraw cash", JLabel.CENTER);
         option2.setForeground(new Color(255, 255, 255));
         option2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option2.setPreferredSize(new Dimension(50, 50));
         option2.setBorder(lineborder);
         screen_panel.add(option2, c);
         
         c.gridy = 3;
         c.gridx = 0;
         c.gridwidth = 1;
         JLabel option3 = new JLabel("Transfer funds", JLabel.CENTER);
         option3.setForeground(new Color(255, 255, 255));
         option3.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option3.setPreferredSize(new Dimension(50, 50));
         option3.setBorder(lineborder);
         screen_panel.add(option3, c);
         
         c.gridy = 4;
         c.gridx = 0;
         c.gridwidth = 1;
         JLabel option4 = new JLabel("Exit", JLabel.CENTER);
         option4.setForeground(new Color(255, 255, 255));
         option4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 20));
         option4.setPreferredSize(new Dimension(50, 50));
         option4.setBorder(lineborder);
         screen_panel.add(option4, c);
         
         screen_panel.revalidate();
         screen_panel.repaint();
      }
   

} // end class ATM


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