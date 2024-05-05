package hellojpa.practice.class_instatnce;

public class Bank {
    static int balance=0;
    public static void main(String[] args){
        deposit(1000); //입금
        checkBalance(); //출력
        withdraw(500); // 출금
        checkBalance();
    }

    private static void checkBalance() {
        System.out.println("balance = " + balance);
    }

    private static void withdraw(int money) {
        balance-=money;
    }

    private static void deposit(int money) {
        balance +=money;
    }
}
