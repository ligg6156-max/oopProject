public class Saving_Account extends Account
{
    private double AvailableBalance;
    private double TotalBalance;
    private double Interest_rate = 0.0025;
    public Saving_Account(int AccountNumber, int PIN, double AvailableBalance, double TotalBalance)
    {

        super(AccountNumber, PIN, AvailableBalance, TotalBalance);
    }
    public double getInterest_rate(){
        return Interest_rate;
    }
    public void setInterest_rate(double interest){
        Interest_rate = interest;
    }
}