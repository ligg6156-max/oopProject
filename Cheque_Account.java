public class Cheque_Account extends Account
{
    private double availableBalance;
    private double totalBalance;
    private double interest_rate;
    public Cheque_Account(int AccountNumber, int PIN, double AvailableBalance, double TotalBalance)
    {

        super(AccountNumber, PIN, AvailableBalance, TotalBalance);
    }
        
}