package com.ivan.nodo2;

//lIBRERIAS
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Nodo2 {
    private static final Logger LOGGER = LogManager.getLogger(Nodo2.class);
    private List<Integer> availablePorts; //Lista de los puertos
    private final int initialPort = 31010; //Variable con el pueto inicial
    private ServerSocket serverSocket;
    private static Map<Integer, Socket> connectedNodes = new HashMap<>();//lista que almacena a los nodos con su socket
    private static Socket connectedClient = null;//lista que almacena a los clientes con su socket
    private static Socket connectedServer = null;//lista que almacena a los servidores con su socket
    private final String portRegistryFile = "D:/Usuarios/Ivan/Desktop/Computo Distribuido/Puertos.txt"; //guarda donde esta el txt
    private int myport=0;
    private int clientid=0;
    private static String estado = "";
    
    public Nodo2() { //constructor
        availablePorts = new ArrayList<>();
        readPortsFromFile();
    }

    public static void main(String[] args) throws InterruptedException {
        Nodo2 nodo = new Nodo2();
        nodo.startListening(); // Inicia la escucha de conexiones entrantes
        Thread.sleep(1000);//Para que entre al otro hilo antes de que se inicie
        nodo.startScanning(); // Inicia la búsqueda de otros nodos disponibles
        System.out.println("Nodo iniciado, escuchando conexiones entrantes y buscando otros nodos...");
        while(true);
    }
    
    
    //-----------------------------Funciones para el txt--------------------------------------------------------------------------------

    private void readPortsFromFile() { //Esta funcion lee todos los puertos que existan en el txt
        try (BufferedReader br = new BufferedReader(new FileReader(portRegistryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                availablePorts.add(Integer.parseInt(line.trim()));
            }
            System.out.println("Ports read from file: " + availablePorts);
        } catch (IOException e) {
            System.err.println("Error reading port registry file: " + e);
            LOGGER.error("Hubo un error al leer el archivo", e);
        }
    }

    private void writePortToFile(int port) { // Esta funcion escribe el puerto actual en el txt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(portRegistryFile, true))) { // Usar 'true' para indicar que se agregará al final del archivo
            bw.write(String.valueOf(port));
            bw.newLine();
            //System.out.println("Port written to file: " + port);
        } catch (IOException e) {
            System.err.println("Error writing to port registry file: " + e);
        }
    }

    
    private int getNextAvailablePort() {
        if (availablePorts.isEmpty()) {
            return initialPort;
        } else {
            int lastPort = availablePorts.get(availablePorts.size() - 1);
            return lastPort + 1;
        }
    }
//-------------------------------------Conexiones--------------------------------------------------------------------------
    
    private void startListening() {
        new Thread(() -> {
            int port = initialPort;
            while (true) {
                try {
                    serverSocket = new ServerSocket(port); // Crear el ServerSocket en el puerto actual
                    writePortToFile(port);
                    myport = port;
                    System.out.println("Nodo escuchando en el puerto " + port + "...");
                    break; // Salir del bucle si se creó el ServerSocket correctamente
                } catch (IOException e) {
                    System.err.println("Error al intentar escuchar en el puerto " + port + ": " + e);
                    port++; // Intentar con el siguiente puerto
                }
            }

            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept(); // Esperar por una conexión entrante
                    // Manejar la conexión entrante
                    //System.out.println("Conexión entrante desde: " + clientSocket.getInetAddress().getHostAddress());

                    // Leer el mensaje entrante
                    DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                    String mensaje = inputStream.readUTF();
                    System.out.println("Mensaje recibido: " + mensaje);
                    String[] parts = mensaje.split(":");
                    int portNode = Integer.parseInt(parts[1]);
                    String type = parts[0];
                    switch(type){//Sirve parar identificar si es nodo, cliente o servidor
                        case "Nodo":
                            System.out.println("Se conecto un Nodo");
                            System.out.println("-----------------------------");
                            connectedNodes.put(portNode, clientSocket);
                            ConnectionHandler hiloNodo = new ConnectionHandler(String.valueOf(port), clientSocket,"Nodo");
                            //System.out.println("Nodos en el mapa: "+connectedNodes);
                            hiloNodo.start();
                            break;
                        case "Cliente":
                            System.out.println("Se conecto un Cliente");
                            System.out.println("-----------------------------");
                            clientid++;
                            connectedClient = clientSocket;
                            ConnectionHandler hiloCliente = new ConnectionHandler(String.valueOf(port), clientSocket,"Cliente");
                            hiloCliente.start();
                            break;
                        case "Servidor":
                            System.out.println("Se conecto un Servidor");
                            System.out.println("-----------------------------");
                            connectedServer = clientSocket;
                            ConnectionHandler hiloServer = new ConnectionHandler(String.valueOf(port), clientSocket,"Servidor");
                            hiloServer.start();
                            break;
                        default:
                            System.out.println() ;
                            break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error mientras se escuchan las conexiones: " + e);
            } finally {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Error cerrando el socket del servidor: " + e);
                }
            }
        }).start();
    }

    
    private void startScanning() { // Funcion para encontrar otros nodos a donde conectarse
        new Thread(() -> {
            System.out.println("Buscando otros nodos...");
            for (int port : availablePorts) {
                if (port != myport) {
                    try {
                        Socket socket = new Socket("127.0.0.1", port);
                        connectedNodes.put(port, socket);
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.writeUTF("Nodo:"+ myport); // Envía el mensaje "Soy un nodo"
                        //System.out.println("Nodo encontrado con el puerto: " + port);
                        //System.out.println(connectedNodes);
                        ConnectionHandler hiloNodo = new ConnectionHandler(String.valueOf(port), socket,"Nodo");
                        hiloNodo.start();
                    } catch (IOException e) {
                        // Si ocurre un error al intentar conectar, simplemente continuamos con el siguiente puerto
                        System.out.println("Error trying to connect to node on port " + port + ": " + e);
                    }
                }
                System.out.println("Tried port "+port);
            }
        }).start();
    }


//---------------------------------Manjeadores---------------------------------------------------------------------------
    private static class ConnectionHandler extends Thread {
        private String id;
        private Socket socket;
        private String type;

        public ConnectionHandler(String clientId, Socket clientSocket, String type) {
            this.id = clientId;
            this.socket = clientSocket;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Agregado para enviar mensajes al cliente

                // Envía el mensaje de bienvenida al cliente
                //out.writeUTF("Bienvenido, cliente " + id + "!"); // Utiliza writeUTF para enviar una cadena UTF-8 al cliente

                //DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                //String mensaje = in.readUTF();
                //System.out.println("Mensaje recibido: " + mensaje);    
                
                String message;
                while (true) {
                    message = in.readUTF();
                    System.out.println("Se recibió: " +  message);
                    String[] parts = message.split(":");
                    String typeOp = parts[0];
                    String mensaje = parts[1];
                    //switch para cliente nodo servidor
                    switch(type){
                        case "Nodo":
                            //Si se recibe mensaje hacer un broadcast
                            switch(typeOp){
                                case "operacion":
                                    if(connectedServer != null){
                                        System.out.println("Aqui se debe mandar algo...");
                                        System.out.println(connectedServer);
                                        DataOutputStream outputStream =  new DataOutputStream(connectedServer.getOutputStream()); 
                                        outputStream.writeUTF(mensaje); // Escribir el mensaje en el OutputStream del socket
                                        outputStream.flush(); // Limpiar el buffer y enviar el mensaje
                                    }
                                    break;
                                case "respuesta":
                                    if(connectedClient != null && estado.equals("espera")){
                                        System.out.println(connectedClient);
                                        DataOutputStream outputStream =  new DataOutputStream(connectedClient.getOutputStream()); 
                                        outputStream.writeUTF(mensaje); // Escribir el mensaje en el OutputStream del socket
                                        outputStream.flush(); // Limpiar el buffer y enviar el mensaje
                                        estado = "";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            
                            break;
                        case "Cliente":
                            //Manda a su nodo solicitud y variable de que el hizo la solicitud
                            estado = "espera";
                            //out.writeUTF("recibi tu msj");
                            if(!connectedNodes.isEmpty()){
                                for (Map.Entry<Integer, Socket> entry : connectedNodes.entrySet()) {
                                    try {
                                       // System.out.println("Se le esta enviando el mensaje a: "+connectedNodes);
                                        //System.out.println(connectedNodes);
                                        DataOutputStream outputStream =  new DataOutputStream(entry.getValue().getOutputStream()); 
                                        outputStream.writeUTF(message); // Escribir el mensaje en el OutputStream del socket
                                        outputStream.flush(); // Limpiar el buffer y enviar el mensaje
                                    } catch (IOException e) {
                                        // Manejo de errores en caso de problemas con el socket
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //if (connectedNodes != null){
                              //      DataOutputStream outputStream =  new DataOutputStream(connectedServer.getOutputStream()); 
                            //        outputStream.writeUTF(message);
                            //        outputStream.flush();
                            //}else{
                            //    DataOutputStream outputStream =  new DataOutputStream(connectedClient.getOutputStream()); 
                            //    outputStream.writeUTF(message);
                            //    outputStream.flush();
                            //}
                            
                            break;
                        case "Servidor":
                            //Resuelve la solicitud y se la manda al nodo
                            if(!connectedNodes.isEmpty()){
                                for (Map.Entry<Integer, Socket> entry : connectedNodes.entrySet()) {
                                    
                                    try {
                                        System.out.println("Se le esta enviando el mensaje a: "+connectedNodes);
                                        DataOutputStream outputStream =  new DataOutputStream(entry.getValue().getOutputStream()); 
                                        outputStream.writeUTF(message); // Escribir el mensaje en el OutputStream del socket
                                        outputStream.flush(); // Limpiar el buffer y enviar el mensaje
                                    } catch (IOException e) {
                                        // Manejo de errores en caso de problemas con el socket
                                        e.printStackTrace();
                                    }
                                }
                            }
                            
                            break;
                    }
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
