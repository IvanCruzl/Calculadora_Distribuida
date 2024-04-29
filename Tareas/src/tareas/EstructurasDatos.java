package tareas;

import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;


public class EstructurasDatos {
     public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int[] numeros = new int[5];
        for(int i =0;i<5;i++){
            System.out.print("Escribe un numero: ");
            int num = scan.nextInt();
            System.out.println();
             
            numeros[i] = num;
        }
        
        System.out.println("Lista de numeros:");
         for(int i =0;i<5;i++){
             System.out.print(numeros[i] + ", ");
         }
         
         ArrayList<String> nombres = new ArrayList<>();
         for(int i =0;i<5;i++){
             System.out.print("Nombre de la persona " + i);
             String name = scan.nextLine();
             System.out.println();
             nombres.add(name);
         }
         
         for (String n : nombres){
             System.out.println(n);
         }
         
         
         
     }
}
