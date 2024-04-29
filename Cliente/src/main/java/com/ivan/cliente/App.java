package com.ivan.cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import javafx.application.Platform;

public class App extends Application {

    private static Scene scene;
    private static BufferedWriter writer;
    private static Label connectionStatusLabel;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 400, 200);
        stage.setScene(scene);
        stage.show();
        connectionStatusLabel = (Label) scene.lookup("#connectionStatusLabel");
        conectarSocket();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static BufferedWriter getWriter() {
        return writer;
    }

    //public static void login(String username, String password) throws IOException {
       // if (writer != null) {
            //System.out.println("Se hizo la conexión con el servidor" + writer);
            //writer.write(username + "\n");
            //writer.flush();
        //} else {
            //System.out.println("Can't send message to server");
       // }
   // }

    public static void conectarSocket() {
        final String serverhost = "localhost";
        final int serverport = 31010; // Cambiado al puerto 31010

        try {
            Socket socket = new Socket(serverhost, serverport);
            System.out.println("Connected to server " + serverport);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            connectionStatusLabel.setText("Conectado al servidor"); // Actualiza el mensaje de estado
        

            Thread hiloEntrada = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        System.out.println("Recibí mensaje del servidor: " + inputLine);
                        imprimirMensaje(inputLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            hiloEntrada.start();

        } catch (IOException e) {
            e.printStackTrace();
            connectionStatusLabel.setText("No se pudo conectar al servidor"); // Actualiza el mensaje de estado
        }
    }

    // Método para imprimir un mensaje recibido en la consola
    public static void imprimirMensaje(String mensaje) {
        System.out.println(mensaje);
        Platform.runLater(()-> connectionStatusLabel.setText(mensaje));
    }
}
