
package com.ivan.clietneservidor;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ClietneServidor {
    private static DataOutputStream dataOutputStream;
    private static Scanner scanner = new Scanner(System.in);

    public static void start(){
        int serverport = leerPuertoDesdeArchivo();//Busca el primer puerto desde los registros
        conectarSocket(serverport);
        //while(true);
    }
    
    public static void main(String[] args) {
        start();
    }
    public static int leerPuertoDesdeArchivo() {
        int puerto = -1;
        try {
            File archivo = new File("D:/Usuarios/Ivan/Desktop/Computo Distribuido/Puertos.txt");
            Scanner lector = new Scanner(archivo);

            // Obtener el primer puerto disponible del archivo
            if (lector.hasNextLine()) {
                puerto = Integer.parseInt(lector.nextLine());
            }

            // No cerrar el Scanner aquí, necesitas seguir usándolo

            // Borrar el primer puerto del archivo
            if (puerto != -1) {
                PrintWriter escritor = new PrintWriter("D:/Usuarios/Ivan/Desktop/Computo Distribuido/Puertos.txt");
                while (lector.hasNextLine()) {
                    escritor.println(lector.nextLine());
                }
                escritor.close();
            }

            lector.close(); // Cerrar el Scanner aquí después de haber terminado de usarlo completamente

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return puerto;
    }

    public static void conectarSocket(int serverport) {
        final String serverhost = "localhost";
        //final int serverport = 31013; // Cambiado al puerto 31010

        try {
            Socket socket = new Socket(serverhost, serverport);
            System.out.println("Connected to server " + serverport);
            // Utilizar DataOutputStream directamente
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Enviar un mensaje al servidor
            enviarMensaje("Servidor:1");

            //Thread hiloEntrada = new Thread(() -> {
                try {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String inputLine;
                    while ((inputLine = in.readUTF()) != null) {
                        System.out.println("Recibí mensaje del servidor: " + inputLine);
                        String[] parts = inputLine.split("#");
                        int num1 = Integer.parseInt(parts[0]);
                        int num2 = Integer.parseInt(parts[1]);;
                        
                        int tot = num1 + num2;
                        String mensaje = "respuesta:" + String.valueOf(tot);
                        enviarMensaje(mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //});

            //hiloEntrada.start();

        } catch (IOException e) {
            e.printStackTrace();
            //connectionStatusLabel.setText("No se pudo conectar al servidor"); // Actualiza el mensaje de estado
        }
    }
    private static void enviarMensaje(String mensaje) {
        try {
            dataOutputStream.writeUTF(mensaje);
            dataOutputStream.flush(); // Para asegurar que los datos se envíen inmediatamente
            System.out.println("Se mandó con éxito la operación");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
