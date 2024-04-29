package tareas;

import java.util.Scanner;
import java.util.ArrayList;


public class listas {
     public static ArrayList AgregarTarea(ArrayList<String> lista){
         Scanner scan = new Scanner(System.in);
         System.out.print("Cuantas tareas vas a agregar? ");
         int num = scan.nextInt();
         scan.nextLine(); 
         
         for (int i =0; i < num; i++){
             System.out.print("Escribe la tarea " + (i+1));
             String tarea = scan.nextLine();
             
             lista.add(tarea);
         }
         
         
        return lista;
     }
    
     public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        
         ArrayList<String> nombres = new ArrayList<>();
         for(int i =0;i<5;i++){
             System.out.print("Escribe la tarea " + i + ": ");
             String name = scan.nextLine();
             System.out.println();
             nombres.add(name);
         }
         
         for (String n : nombres){
             System.out.println(n);
         }
         
         System.out.println();
         nombres = AgregarTarea(nombres);
         
     }
}
