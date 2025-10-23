// Keypad.java
// Represents the keypad of the ATM
import java.util.Scanner; // program uses Scanner to obtain user input

public class Keypad
{
   private Scanner input; // reads data from the command line
                         
   // no-argument constructor initializes the Scanner
   public Keypad()
   {
      input = new Scanner( System.in );    
   } // end no-argument Keypad constructor

   // return an integer value entered by user 
   public int getIntInput()
   {
    for(int i = 2; i > -1; i--){
    int num = 0;
    try{
        num = Integer.parseInt(input.nextLine());
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
    for(int i = 2; i > -1; i--){
    double num = 0;
    try{
        num = Double.parseDouble(input.nextLine());
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