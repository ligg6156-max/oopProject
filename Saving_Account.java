public class Saving_Account extends Account
{
    private double AvailableBalance;
    private double TotalBalance;
    private double Limit_per_cheque = 0.0025;
    public Saving_Account(int AccountNumber, int PIN, double AvailableBalance, double TotalBalance)
    {

        super(AccountNumber, PIN, AvailableBalance, TotalBalance);
    }
    public double getLimit_per_cheque(){
        return Limit_per_cheque;
    }
    public void setLimit_per_cheque(double Limit){
        Limit_per_cheque = Limit;
    }
}