package hellojpa.practice.class_instatnce;

public class Bank3 {
    public static void main(String[] args){
        Bank000 bank = new Bank000("xxx-xxxx-xxxx",1000);
        bank.deposit(1000);
        check(bank);
        bank.withdraw(500);
        check(bank);

    }
    public static void check(Bank000 bank){
        bank.checkBalance();
        bank.checkAccNumber();
    }

    static class Bank000 {
        
        int balance=0;
        String myAccNumber;

        public Bank000(String s, int i) {
            myAccNumber = s;
            balance=i;
        }

        private void checkBalance()
        {
            System.out.println("balance = " + balance);
        }
        private void checkAccNumber()
        {
            System.out.println("myAccNumber = " + myAccNumber);
        }

        private  void withdraw(int money) {
            balance-=money;
        }

        private void deposit(int money) {
            balance +=money;
        }
    }
}
