public class Saving_Account extends Account
{
    private double availableBalance;
    private double totalBalance;
    private double imit_per_cheque;
    public Saving_Account(int AccountNumber, int PIN, double AvailableBalance, double TotalBalance)
    {

        super(AccountNumber, PIN, AvailableBalance, TotalBalance);
    }
}