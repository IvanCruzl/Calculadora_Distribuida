/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tareas;
import java.util.HashSet;
import java.util.Scanner;

public class Hashset {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashSet<Integer> primos = new HashSet<>();

        while (true) {
            System.out.print("Ingrese un número entero (o escriba 'salir' para terminar): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("salir")) {
                break;
            }

            int num = Integer.parseInt(input);
            if (esPrimo(num)) {
                primos.add(num);
                System.out.println(num + " es un número primo.");
            } else {
                System.out.println(num + " no es un número primo.");
            }
        }

        System.out.println("Los números primos ingresados son: " + primos);
    }

    public static boolean esPrimo(int num) {
        if (num < 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
