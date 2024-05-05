package hellojpa.practice.class_instatnce;

public class Bank2 {
    public static void main(String[] args){
        Bank00 bank00 = new Bank00();
        bank00.deposit(1000);
        check(bank00);
        bank00.withdraw(500);
        check(bank00);

    }
    public static void check(Bank00 bank00){
        bank00.checkBalance();
    }

    static class Bank00 {
        int balance=0;

        private void checkBalance() {
            System.out.println("balance = " + balance);
        }

        private  void withdraw(int money) {
            balance-=money;
        }

        private void deposit(int money) {
            balance +=money;
        }
    }
}
