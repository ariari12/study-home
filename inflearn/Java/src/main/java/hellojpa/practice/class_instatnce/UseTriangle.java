package hellojpa.practice.class_instatnce;

public class UseTriangle {
    public static void main(String[] args) {
        Triangle triangle = new Triangle(10,5);
        triangle.calcArea();
        triangle.setBottom(20.6);
        triangle.setHeight(8.4);
        triangle.calcArea();

    }
    static class Triangle {
        double bottom;
        double height;
        double Area;
        public Triangle(int bottom, int height) {
            this.bottom=bottom;
            this.height=height;
        }

        public void setBottom(double bottom) {
            this.bottom = bottom;
        }
        public void setHeight(double height){
            this.height=height;
        }
        public void calcArea(){
            System.out.printf("%.3f\n",(bottom*height)/2);

        }
    }


}

