package com.ivan.server;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private List<Integer> availablePorts;
    private List<Socket> middlewareSockets;
    private List<DataOutputStream> middlewareOutputs;
    private ServerSocket serverSocket;
    private Map<Integer, Socket> connectedNodes = new HashMap<>();
    private Map<Integer, Socket> connectedClient = new HashMap<>();
    private final String portRegistryFile = "D:/Usuarios/Ivan/Desktop/Computo Distribuido/Puertos.txt";
    private final int initialPort = 31010;

    public Server() {
        availablePorts = new ArrayList<>();
        middlewareSockets = new ArrayList<>();
        middlewareOutputs = new ArrayList<>();
        readPortsFromFile();
    }

    private void readPortsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(portRegistryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                availablePorts.add(Integer.parseInt(line.trim()));
            }
            System.out.println("Ports read from file: " + availablePorts);
        } catch (IOException e) {
            System.err.println("Error reading port registry file: " + e);
        }
    }

    private void writePortsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(portRegistryFile))) {
            for (int port : availablePorts) {
                bw.write(String.valueOf(port));
                bw.newLine();
            }
            System.out.println("Ports written to file: " + availablePorts);
        } catch (IOException e) {
            System.err.println("Error writing to port registry file: " + e);
        }
    }

    public void startServer() {
        while (true) {
            int port = getNextAvailablePort();
            try {
                //sendMessageToNode();
                serverSocket = new ServerSocket(port);
                System.out.println("Server running on port " + port);
                availablePorts.add(port); // Add the new port to the list
                System.out.println("Available ports: " + availablePorts);
                startServerInstance(serverSocket);
                establishConnections();
                writePortsToFile();
                return;
            } catch (IOException e) {
                System.out.println("Error starting server on port " + port + ": " + e);
            }
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

    private void startServerInstance(ServerSocket serverSocket) {
        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("New connection accepted on port " + serverSocket.getLocalPort());
                    System.out.println("Connected servers: " + connectedNodes);
                    handleConnection(socket);

                    System.out.println("Connection processed on port " + serverSocket.getLocalPort());
                }
            } catch (IOException e) {
                System.out.println("Error accepting connection: " + e);
            }
        }).start();
    }
    
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
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while (true) {
                    message = in.readLine();
                    System.out.println("Recibido del cliente " + id + ": " + message);
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
    


    private void handleConnection(Socket socket) {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            String message = inputStream.readUTF();
            System.out.println("Message received from client on port " + serverSocket.getLocalPort() + ": " + message);
            //System.out.println("Socket: " + socket);
            String[] parts = message.split(":");
            int port = Integer.parseInt(parts[1]);
            String h = parts[0];
            System.out.println("Del puerto: " + port);
            if(h.equals("NN")){
                System.out.println("Se conecto un Nodo con el puerto: " + port);
                if (connectedNodes.containsKey(port)) {
                System.out.println("El mapa contiene " + port);
                } else {
                    connectedNodes.put(port, socket);
                    System.out.println("Se agrego la llave con exito");
                    System.out.println(connectedNodes);
                    ConnectionHandler hiloHandler = new ConnectionHandler(String.valueOf(port), socket, h);
                    hiloHandler.start();
                    System.out.println("Se inicio el hilo para el cliente en el puerto: " + port + "\n" );
                }
            } else if (h.equals("Servidor")){
                System.out.println("Se conecto una Célula-Servidor con el puerto: " + port);
            }else if(h.equals("Cliente")){
                System.out.println("Se conecto un cliente");
                connectedClient.put(port, socket);
                System.out.println("Se agrego la llave con exito");
                System.out.println(connectedClient);
                ConnectionHandler hiloHandler = new ConnectionHandler(String.valueOf(port), socket, h);
                hiloHandler.start();
                System.out.println("Se inicio el hilo para el cliente en el puerto: " + port + "\n" );
            }else{
                System.out.println("Se recibió otro tipo de mensaje");
            }
               System.out.print("-----------------------------------------------------------------\n");
        } catch (IOException e) {
            System.err.println("I/O error handling connection with client: " + e);
        }
    }

    private void sendWelcomeMessage(Socket socket, int connectedPort) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("NN:" + serverSocket.getLocalPort());
            System.out.println("Se mando aviso de conectividad " + connectedPort );
        } catch (IOException e) {
            System.err.println("Error sending welcome message to client on port " + connectedPort + ": " + e);
        }
    }
    
    private void sendMessageToNode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("¿Quieres enviar un mensaje a un nodo? (Sí/No): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("sí") || response.equals("si")) {
            System.out.print("Por favor, introduce el puerto del nodo al que deseas enviar el mensaje: ");
            int port = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea después del número

            if (connectedNodes.containsKey(port)) {
                System.out.print("Por favor, introduce el mensaje que deseas enviar: ");
                String message = scanner.nextLine().trim();
                try {
                    Socket socket = connectedNodes.get(port);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF("Mensaje desde el servidor: " + message);
                    System.out.println("Mensaje enviado al nodo en el puerto " + port + ": " + message);
                } catch (IOException e) {
                    System.err.println("Error enviando el mensaje al nodo en el puerto " + port + ": " + e);
                }
            } else {
                System.out.println("No hay un nodo activo en el puerto " + port);
            }
        }
    }

    private void establishConnections() {
        for (int port : availablePorts) {
            if (port != serverSocket.getLocalPort()) {
                try {
                    Socket socket = new Socket("127.0.0.1", port);
                    middlewareSockets.add(socket);
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    middlewareOutputs.add(output);
                    System.out.println("Connection established with client on port " + port);
                    connectedNodes.put(port, socket);
                    sendWelcomeMessage(socket, port);
                    System.out.println("Connected servers: " + connectedNodes);
                } catch (IOException e) {
                    System.out.println("Error trying to connect to client on port " + port + ": " + e);
                }
            }
        }
    }

    public void cleanShutdown() {
        // Close resources and handle disconnections
    }

    public static void main(String[] args) {
        Server server = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.cleanShutdown()));
        server.startServer();
    }
}
