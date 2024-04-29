package com.ivan.cliente2;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente2 {
    private static DataOutputStream dataOutputStream;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean respuesta = true;

    public static void start(){
        int serverport = leerPuertoDesdeArchivo();//Busca el primer puerto desde los registros
        conectarSocket(serverport);
        while(true){

            System.out.print("Escribe un número: ");
            String num1 = scanner.nextLine();
            System.out.print("Escribe otro número: ");
            String num2 = scanner.nextLine();
            
            String mensaje = "operacion:"+num1+"#"+num2;
            enviarOperacion(mensaje);
            try {
                // Esperar 1 segundo
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                }
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
        //final int serverport = 31012; // Cambiado al puerto 31010

        try {
            Socket socket = new Socket(serverhost, serverport);
            System.out.println("Connected to server " + serverport);
            // Utilizar DataOutputStream directamente
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Enviar un mensaje al servidor
            enviarMensaje("Cliente:1");

            Thread hiloEntrada = new Thread(() -> {
                try {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String inputLine;
                    while ((inputLine = in.readUTF()) != null) {
                        if (respuesta == false){
                            System.out.println("Resultado: " + inputLine);
                            respuesta = true;
                        } 
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            hiloEntrada.start();

        } catch (IOException e) {
            e.printStackTrace();
            //connectionStatusLabel.setText("No se pudo conectar al servidor"); // Actualiza el mensaje de estado
        }
    }
    private static void enviarMensaje(String mensaje) {
        try {
            dataOutputStream.writeUTF(mensaje);
            dataOutputStream.flush(); // Para asegurar que los datos se envíen inmediatamente
            System.out.println("Se mandó con éxito el mensaje");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void enviarOperacion(String mensaje) {
        try {
            dataOutputStream.writeUTF(mensaje);
            dataOutputStream.flush(); // Para asegurar que los datos se envíen inmediatamente
            //System.out.println("Se mandó con éxito la operacion");
            respuesta = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}