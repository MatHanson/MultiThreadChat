import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Application {
    // IO streams
    DataOutputStream toServer = null;
    DataInputStream fromServer = null;
    Socket socket;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) throws IOException {
        // Panel to hold the message text field
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: green");
        TextField tf = new TextField();
        tf.setAlignment(Pos.BOTTOM_LEFT);
        paneForTextField.setCenter(tf);

        BorderPane mainPane = new BorderPane();
        // TextArea for main chat window
        TextArea chatWindow = new TextArea();
        ScrollPane chatPane = new ScrollPane(chatWindow);
        chatPane.setFitToHeight(true);
        chatPane.setFitToWidth(true);
        mainPane.setCenter(chatPane);
        mainPane.setBottom(paneForTextField);

        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 400);
        primaryStage.setTitle("Chat Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        // Connect to the server
        try {
            // Create a socket to connect to the server
            socket = new Socket("localhost", 8000);

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());

        } catch (UnknownHostException ex) {
            chatWindow.appendText(ex.toString() + '\n');
        } catch (IOException ex) {
            chatWindow.appendText(ex.toString() + '\n');
        }

        // Handle "enter" action
        tf.setOnAction(e -> {
            try {
                // Get strings from the text fields
                String message = tf.getText().trim();

                // Clear tf
                tf.clear();

                // Send message to the server
                toServer.writeUTF(message + '\n');
                toServer.flush();

            } catch (IOException e1) {
                System.err.println(e1);
            }
        });

        // Create thread to receive messages
        new Thread(() -> {
            try {
                while(true) {
                    // Create an input stream to receive data from the server
                    fromServer = new DataInputStream(socket.getInputStream());

                    String text = fromServer.readUTF();
                    chatWindow.appendText(text);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();



    }
}