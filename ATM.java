// ATM.java
// Represents an automated teller machine
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Color;
import java.awt.Dimension;


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

   protected static final Color BACKGROUND = new Color(30, 30, 40);
   protected static final Color SCREEN_PANEL_COLOR = new Color(20, 120, 180);
   protected static final Color BUTTON_COLOR = new Color(70,70,90);
   protected static final Color MODERN_TEXT = new Color(255, 255, 255);
   protected static final Color GREEN_COLOR = new Color(50, 150, 50);
   protected static final Font MODERN_FONT = new Font("Consolas", Font.PLAIN, 18);

   // no-argument ATM constructor initializes instance variables
   public ATM() 
   {
      userAuthenticated = false; // user is not authenticated to start
      currentAccountNumber = 0; // no current account number to start
      // Create single TextArea for display and input (4:3 aspect ratio)
      displayArea = new TextArea("", 15, 50, TextArea.SCROLLBARS_NONE);
      displayArea.setEditable(false); // Make it non-editable - use KeyListener for input
      displayArea.setBackground(Color.BLACK); // Black background for input
      displayArea.setForeground(Color.GREEN); // Green text for ATM style
      displayArea.setFont(new Font("Courier New", Font.BOLD, 16));
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
      UIManager.put("Button.font", new Font("Consolas", Font.PLAIN, 18));
      UIManager.put("Label.font", new Font("Consolas", Font.PLAIN, 18));
      Font customFont = new Font("Consolas", Font.PLAIN, 18);
      displayArea.setFont(customFont);
      UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 18));
      
      // Create keypad after frame and TextArea are created
      keypad = new Keypad(displayArea); // create keypad with TextArea for GUI input
      frame.addKeyListener(keypad); // register keypad as listener for frame
      displayArea.addKeyListener(keypad); // register keypad as listener for TextArea
      frame.setFocusable(true); // Make frame focusable to receive key events
      mainpanel.add(screen_panel, BorderLayout.CENTER);
      
      // Create side button panels
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
      leftPanel.setPreferredSize(new Dimension(180, 0));
      rightPanel.setPreferredSize(new Dimension(150, 0));
      rightPanel.setBackground(new Color(30, 30, 40));
      leftPanel.setBackground(new Color(30, 30, 40));
      
      for (int i = 0; i < 4; i++) {
         c.gridy = i + 1;
         Sidebuttons[i] = new JButton("►");
         Sidebuttons[i].setPreferredSize(new Dimension(100, 100));
         Sidebuttons[i].setBackground(BUTTON_COLOR);
         Sidebuttons[i].setForeground(Color.WHITE);
         Sidebuttons[i].setFocusable(false);
         final int buttonIndex = i + 1;
         Sidebuttons[i].addActionListener(e -> {
            keypad.setButtonInput(buttonIndex);
         });
         leftPanel.add(Sidebuttons[i], c);
      }
      
      c.gridy = 0;
      for (int i = 4; i < 8; i++) {
         c.gridy = i + 1;
         Sidebuttons[i] = new JButton("◄");
         Sidebuttons[i].setPreferredSize(new Dimension(100, 100));
         Sidebuttons[i].setBackground(BUTTON_COLOR);
         Sidebuttons[i].setForeground(Color.WHITE);
         Sidebuttons[i].setFocusable(false);
         final int buttonIndex = i + 1;
         Sidebuttons[i].addActionListener(e -> {
            keypad.setButtonInput(buttonIndex);
         });
         rightPanel.add(Sidebuttons[i], c);
      }

      mainpanel.add(leftPanel, BorderLayout.WEST);
      mainpanel.add(rightPanel, BorderLayout.EAST);
      frame.add(mainpanel);
      
      frame.setVisible(true);
      frame.requestFocus();
      
      System.out.println("DEBUG: ATM constructor completed");
      
      // Start welcome screen
      new Thread(() -> {
         try {
            Thread.sleep(500);
            wellcome();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }).start();
   } // end no-argument ATM constructor

   public void wellcome(){
      System.out.println("DEBUG: wellcome() with time and card started");
      
      screen_panel.removeAll();
      screen_panel.setLayout(new BorderLayout());
      screen_panel.setBackground(new Color(0, 92, 75));
      
      // Create main container
      JPanel mainContainer = new JPanel(new BorderLayout());
      mainContainer.setBackground(new Color(0, 92, 75));
      mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
      
      // Top panel with time and date
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.setBackground(new Color(0, 92, 75));
      
      // Current time and date
      SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String currentTime = timeFormat.format(new Date());
      String currentDate = dateFormat.format(new Date());
      
      JLabel timeLabel = new JLabel(currentTime, JLabel.RIGHT);
      timeLabel.setForeground(Color.WHITE);
      timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
      
      JLabel dateLabel = new JLabel(currentDate, JLabel.RIGHT);
      dateLabel.setForeground(Color.WHITE);
      dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
      
      JPanel timePanel = new JPanel(new BorderLayout());
      timePanel.setBackground(new Color(0, 92, 75));
      timePanel.add(timeLabel, BorderLayout.NORTH);
      timePanel.add(dateLabel, BorderLayout.SOUTH);
      
      topPanel.add(timePanel, BorderLayout.EAST);
      
      // Bank logo area
      JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      logoPanel.setBackground(new Color(0, 92, 75));
      
      JLabel bankLogo = new JLabel("YOUR BANK", JLabel.CENTER);
      bankLogo.setForeground(Color.WHITE);
      bankLogo.setFont(new Font("Arial", Font.BOLD, 36));
      bankLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
      logoPanel.add(bankLogo);
      
      topPanel.add(logoPanel, BorderLayout.CENTER);
      
      // Welcome message area
      JPanel welcomePanel = new JPanel(new GridLayout(4, 1, 10, 10));
      welcomePanel.setBackground(new Color(0, 92, 75));
      
      JLabel welcomeLabel1 = new JLabel("Welcome to", JLabel.CENTER);
      welcomeLabel1.setForeground(Color.WHITE);
      welcomeLabel1.setFont(new Font("Arial", Font.PLAIN, 28));
      
      JLabel welcomeLabel2 = new JLabel("ATM SERVICE", JLabel.CENTER);
      welcomeLabel2.setForeground(Color.YELLOW);
      welcomeLabel2.setFont(new Font("Arial", Font.BOLD, 32));
      
      JLabel cardLabel = new JLabel("Card Inserted Successfully", JLabel.CENTER);
      cardLabel.setForeground(Color.CYAN);
      cardLabel.setFont(new Font("Arial", Font.BOLD, 20));
      cardLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
      
      JLabel instructionLabel = new JLabel("Click CONTINUE button to begin", JLabel.CENTER);
      instructionLabel.setForeground(new Color(200, 200, 200));
      instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
      
      welcomePanel.add(welcomeLabel1);
      welcomePanel.add(welcomeLabel2);
      welcomePanel.add(cardLabel);
      welcomePanel.add(instructionLabel);
      
      // Continue button area
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      buttonPanel.setBackground(new Color(0, 92, 75));
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
      
      JButton continueButton = new JButton("CONTINUE");
      continueButton.setBackground(Color.GREEN);
      continueButton.setForeground(Color.BLACK);
      continueButton.setFont(new Font("Arial", Font.BOLD, 24));
      continueButton.setPreferredSize(new Dimension(200, 60));
      continueButton.addActionListener(e -> {
         System.out.println("DEBUG: Continue button clicked - starting authentication");
         loginScreen();
         
         // Start authentication directly
         new Thread(() -> {
            try {
               Thread.sleep(500); // Wait for login screen to load
               authenticateUser();
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
         }).start();
      });
      buttonPanel.add(continueButton);
      
      // Bottom information
      JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      bottomPanel.setBackground(new Color(0, 92, 75));
      
      JLabel securityLabel = new JLabel("Secure Banking • 24/7 Service", JLabel.CENTER);
      securityLabel.setForeground(new Color(180, 180, 180));
      securityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      bottomPanel.add(securityLabel);
      
      mainContainer.add(topPanel, BorderLayout.NORTH);
      mainContainer.add(welcomePanel, BorderLayout.CENTER);
      mainContainer.add(buttonPanel, BorderLayout.SOUTH);
      
      screen_panel.add(mainContainer, BorderLayout.CENTER);
      screen_panel.revalidate();
      screen_panel.repaint();
      
      System.out.println("DEBUG: Welcome screen with time and card displayed");
   }

   public void loginScreen(){
      screen_panel.removeAll();
      screen_panel.setLayout(new BorderLayout());
      screen_panel.add(displayArea, BorderLayout.CENTER);
      screen_panel.revalidate();
      screen_panel.repaint();
      
      // Setup display area for input
      displayArea.setBackground(Color.BLACK);
      displayArea.setForeground(Color.GREEN);
      displayArea.setFont(new Font("Courier New", Font.BOLD, 16));
      displayArea.setText("\n\n> Please enter your account number:\n> ");
      
      // Key setup
      keypad.setTextArea(displayArea);
      displayArea.requestFocus();
      frame.requestFocus();
      
      System.out.println("DEBUG: loginScreen completed - ready for input");
   }

   
   private void authenticateUser() 
   {
      System.out.println("DEBUG: ===== authenticateUser() STARTED =====");
      
      screen.clear();
      screen.displayMessage( "\nPlease enter your account number: " );
      System.out.println("DEBUG: Waiting for account number input...");
      
      int accountNumber = keypad.getIntInput(); // input account number
      System.out.println("DEBUG: Account number received: " + accountNumber);
      
      if (accountNumber == CANCELED){
         screen.clear();
         // Use dialog box for error message
         JOptionPane.showMessageDialog(frame, 
            "Invalid account number or PIN. Please try again.", 
            "Login Failed", 
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      screen.displayMessage( "\nEnter your PIN: " ); // prompt for PIN
      keypad.setPasswordMode(true); // Enable password masking
      System.out.println("DEBUG: Waiting for PIN input...");
      
      int pin = keypad.getIntInput(); // input PIN
      System.out.println("DEBUG: PIN received: " + pin);
      
      keypad.setPasswordMode(false); // Disable password masking
      
      if (pin == CANCELED){
         screen.clear();
         // Use dialog box for error message
         JOptionPane.showMessageDialog(frame, 
            "Invalid account number or PIN. Please try again.", 
            "Login Failed", 
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      // set userAuthenticated to boolean value returned by database
      userAuthenticated = bankDatabase.authenticateUser( accountNumber, pin );
      System.out.println("DEBUG: Authentication result: " + userAuthenticated);
      
      // check whether authentication succeeded
      if ( userAuthenticated )
      {
         currentAccountNumber = accountNumber; // save user's account #
         System.out.println("DEBUG: Login successful for account: " + currentAccountNumber);
         
         // After successful login, go directly to main menu
         performTransactions();
         
      } // end if
      else
      {
         screen.clear();
         // Use dialog box for error message
         JOptionPane.showMessageDialog(frame, 
            "Invalid account number or PIN. Please try again.", 
            "Login Failed", 
            JOptionPane.ERROR_MESSAGE);
         System.out.println("DEBUG: Login failed");
      }
   } // end method authenticateUser

   private Transaction createTransaction(int type) {
      Transaction temp = null;

      switch (type) {
         case BALANCE_INQUIRY:
            temp = new BalanceInquiry(currentAccountNumber, screen, bankDatabase);
            break;
         case WITHDRAWAL:
            temp = new Withdrawal(currentAccountNumber, screen, bankDatabase, keypad, cashDispenser, screen_panel, this);
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
      System.out.println("DEBUG: performTransactions() started");
      
      // local variable to store transaction currently being processed
      Transaction currentTransaction = null;
      
      boolean userExited = false; // user has not chosen to exit

      // loop while user has not chosen option to exit system
      while ( !userExited )
      {     
         // show main menu and get user selection
         int mainMenuSelection = displayMainMenu();
         System.out.println("DEBUG: Main menu selection: " + mainMenuSelection);

         // decide how to proceed based on user's menu selection
         switch ( mainMenuSelection )
         {
            // user chose to perform one of three transaction types
            case BALANCE_INQUIRY: 
            case WITHDRAWAL: 
            case TRANSFER:
               // initialize as new object of chosen type
               currentTransaction = createTransaction( mainMenuSelection );
               System.out.println("DEBUG: Transaction created: " + currentTransaction.getClass().getSimpleName());

               currentTransaction.execute(); // execute transaction
               break; 
            case EXIT: // user chose to terminate session
               JOptionPane.showMessageDialog(frame, "Exiting the system...", "EXIT", JOptionPane.INFORMATION_MESSAGE);
               userExited = true; // this ATM session should end
               break;
            case CANCELED:
               screen.displayMessageLine( "\nExiting the system..." );
               userExited = true; // this ATM session should end
               break;
            default: // user did not enter an integer from 1-4
               System.out.println("DEBUG: Invalid selection: " + mainMenuSelection);
               screen.displayMessageLine( "\nYou did not enter a valid selection. Try again." );
               break;
         } // end switch
      } // end while
      
      System.out.println("DEBUG: performTransactions() ended");
   } // end method performTransactions
   
   // display the main menu and return an input selection
   private int displayMainMenu()
   {
       System.out.println("DEBUG: displayMainMenu() called");
       int input;
       keypad.buttonPressState = true;
       keypad.ButtonPressed = 0;
       MainmenuUI();
       input = keypad.getIntInput(); // return user's selection
       System.out.println("DEBUG: Raw input received: " + input);
       
        if (input == 1 || input == 5)
            input = 1;
        else if (input == 2 || input == 6) 
            input = 2;
        else if (input == 3 || input == 7) 
            input = 3;
        else if (input == 4 || input == 8)
            input = 4;
        else {
            // No button pressed, keep input as is
         }
         
      System.out.println("DEBUG: Mapped menu selection: " + input);
      return input;
   } // end method displayMainMenu
   
   private void MainmenuUI(){
         System.out.println("DEBUG: MainmenuUI() called");
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
         System.out.println("DEBUG: MainmenuUI completed");
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
} // end class ATM