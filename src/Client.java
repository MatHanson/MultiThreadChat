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

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) throws IOException {
        // Panel to hold the username text field
        BorderPane paneForUsername = new BorderPane();
        paneForUsername.setPadding(new Insets(5, 5, 5, 5));
        paneForUsername.setStyle("-fx-border-color: green");
        paneForUsername.setLeft(new Label("Username: "));
        TextField un = new TextField();
        Button btConnect = new Button("Connect");
        paneForUsername.setRight(btConnect);

        paneForUsername.setCenter(un);
        un.setAlignment(Pos.BOTTOM_LEFT);


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
        mainPane.setTop(paneForUsername);
        mainPane.setCenter(new ScrollPane(chatWindow));
        mainPane.setBottom(paneForTextField);

        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 200);
        primaryStage.setTitle("Chat Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage


        tf.setOnAction(e -> {
            try {
                // Get strings from the text fields
                String message = tf.getText().trim();
                String username = un.getText().trim();

                // Clear tf
                tf.clear();

                // Send message to the server
                toServer.writeUTF(username + ": " + message + '\n');
                toServer.flush();

            } catch (IOException e1) {
                System.err.println(e1);
            }
        });

        // This was my attempt but not sure if this was the issue, or the server end
        new Thread(() -> {
            try {
                while(!fromServer.readUTF().equals("")) {
                    String text = fromServer.readUTF();
                    chatWindow.appendText(text);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btConnect.setOnAction(e -> {
            try {
                // Create a socket to connect to the server
                Socket socket = new Socket("localhost", 8000);

                // Create an input stream to receive data from the server
                fromServer = new DataInputStream(socket.getInputStream());

                // Create an output stream to send data to the server
                toServer = new DataOutputStream(socket.getOutputStream());

                // Send client username to the server
                String username = un.getText().trim();
                toServer.writeUTF(username);
                toServer.flush();

            } catch (UnknownHostException ex) {
                chatWindow.appendText(ex.toString() + '\n');
            } catch (IOException ex) {
                chatWindow.appendText(ex.toString() + '\n');
            }
        });
    }
}