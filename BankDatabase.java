// BankDatabase.java
// Represents the bank account information database 

public class BankDatabase
{
   private Account accounts[]; // array of Accounts
   private Cheque_Account cheque_accounts[];
   private Saving_Account saving_accounts[];
   // no-argument BankDatabase constructor initializes accounts
   public BankDatabase()
   {
      cheque_accounts = new Cheque_Account[2];
      saving_accounts = new Saving_Account[2];// just 2 accounts for testing
      cheque_accounts[ 0 ] = new Cheque_Account( 24001, 54321, 4500.0, 4500.0 );
      cheque_accounts[ 1 ] = new Cheque_Account( 24002, 12345, 60000.0, 60000.0 );
      saving_accounts[ 0 ] = new Saving_Account( 25001, 56789, 200.0, 200.0 );  
      saving_accounts[ 1 ] = new Saving_Account( 25002, 98765, 4000.5, 4000.5 ); 
   } // end no-argument BankDatabase constructor
   
   // retrieve Account object containing specified account number
   public Account getAccount( int accountNumber )
   {
      // loop through accounts searching for matching account number
      for ( Cheque_Account currentAccount : cheque_accounts )
      {
         // return current account if match found
         if ( currentAccount.getAccountNumber() == accountNumber )
            return currentAccount;
      } // end for
      for ( Saving_Account currentAccount : saving_accounts )
      {
         // return current account if match found
         if ( currentAccount.getAccountNumber() == accountNumber )
            return currentAccount;
      }
      return null; // if no matching account was found, return null
   } // end method getAccount

    
    public void transferFunds(int fromAccount, int toAccount, double amount) 
    {
        Account sender = getAccount(fromAccount);
        Account receiver = getAccount(toAccount);

        if (sender != null && receiver != null) {
            sender.debit(amount);
            receiver.credit(amount);
        }
    

    // Assume getAccount(), debit(), and credit() are defined
}
   // determine whether user-specified account number and PIN match
   // those of an account in the database
   public boolean authenticateUser( int userAccountNumber, int userPIN )
   {
      // attempt to retrieve the account with the account number
      Account userAccount = getAccount( userAccountNumber );

      // if account exists, return result of Account method validatePIN
      if ( userAccount != null )
         return userAccount.validatePIN( userPIN );
      else
         return false; // account number not found, so return false
   } // end method authenticateUser

   // return available balance of Account with specified account number
   public double getAvailableBalance( int userAccountNumber )
   {
      return getAccount( userAccountNumber ).getAvailableBalance();
   } // end method getAvailableBalance

   // return total balance of Account with specified account number
   public double getTotalBalance( int userAccountNumber )
   {
      return getAccount( userAccountNumber ).getTotalBalance();
   } // end method getTotalBalance

   // credit an amount to Account with specified account number
   public void credit( int userAccountNumber, double amount )
   {
      getAccount( userAccountNumber ).credit( amount );
   } // end method credit

   // debit an amount from of Account with specified account number
   public void debit( int userAccountNumber, double amount )
   {
      getAccount( userAccountNumber ).debit( amount );
   } // end method debit
   
}
 // end class BankDatabase



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