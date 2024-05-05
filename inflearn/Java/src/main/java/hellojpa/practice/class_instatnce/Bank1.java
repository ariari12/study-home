package hellojpa.practice.class_instatnce;

public class Bank1 {
    public static void main(String[] args){
        Bank0 bank0 = new Bank0();
        bank0.deposit(1000);
        bank0.checkBalance();
        bank0.withdraw(500);
        bank0.checkBalance();

    }

    static class Bank0 {
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
