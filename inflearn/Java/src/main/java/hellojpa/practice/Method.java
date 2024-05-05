package hellojpa.practice;

public class Method {
    public static void main(String[] args) {
        System.out.println(calcSum(100,200));
        System.out.println(calcSquare(4.5,5.0));
        System.out.println("factorial = "+ factorial(5));
    }

    private static int factorial(int i) {
        if(i==0){
            return 1;
        }
        else {
            return factorial(i-1)*i;
        }

    }

    private static double calcSquare(double x, double y) {
        return x*y;
    }

    private static int calcSum(int x, int y) {
        return x+y;
    }
}
