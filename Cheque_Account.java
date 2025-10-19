public class Cheque_Account extends Account
{
    private double AvailableBalance;
    private double TotalBalance;
    private double Interest_rate;
    public Cheque_Account(int AccountNumber, int PIN, double AvailableBalance, double TotalBalance)
    {

        super(AccountNumber, PIN, AvailableBalance, TotalBalance);
    }
    public double Interest_rate(){
        return Interest_rate;
    }
    public void setLimit_per_cheque(double rate){
        Interest_rate = rate;
    }
}