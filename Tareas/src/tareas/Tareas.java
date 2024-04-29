package tareas;
import java.util.Scanner;

public class Tareas {
    public static double AreaTriangulo(double base, double altura){
        double area = (base*altura)/2;
        return area;
    }
    
    public static void SumaNaturales(){
        int suma = 0;
        for(int i = 0; i<11; i++ ){
            suma += i;    
            System.out.println((suma - i) + " + " + i + " = " + suma);
        }
    }
    
    public static String bisisesto(int year){
        String es = "True";
        if (year%4 != 0){
            es = "False";
        }
        
        return es;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int option;
        int flag = 0;
        //System.out.print("Nombre: ");
        //String nombre = scan.nextLine();
        System.out.println();
        do{
            System.out.print("1) Saludo 2) AREA Triangulo 3) Suma numeros naturales 4) Anio bisiesto 5) Exit \n");
            option = scan.nextInt();
            System.out.println();

            switch(option){
                case 1 -> {
                    scan.nextLine(); 
                    System.out.print("Nombre: ");
                    String nombre = scan.nextLine();
                    System.out.println("Hola "+ nombre + ", bienvenido!");
                    System.out.println();
                    break;
                }
                case 2 -> {
                    System.out.print("Base: ");
                    double base = scan.nextDouble();

                    System.out.print("Altura: ");
                    double altura = scan.nextDouble();

                    double area = AreaTriangulo(base, altura);

                    System.out.print("El area del triangulo es: " + area);
                    System.out.println();
                    break;
                }
                case 3 -> {
                    SumaNaturales();
                    System.out.println();
                    break;
                }
                case 4 -> {
                    System.out.println("Year: ");
                    int year = scan.nextInt();
                    System.out.println(bisisesto(year));
                    System.out.println();
                    break;
                }
                case 5 -> {
                    flag = 1;
                    System.out.println();
                    break;
                }
                default -> {
                    System.out.println("Esa opcion no existe");
                    System.out.println();
                    break;
                }
                    
            }
        }while(flag == 0);
    } 
}
