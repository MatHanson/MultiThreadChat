import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Application {
    // Text area for displaying contents
    private TextArea ta = new TextArea();
    CopyOnWriteArrayList<HandleAClient> ClientList = new CopyOnWriteArrayList<>();
    private int clientNo = 0;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {

        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("Chat Server"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() ->
                        ta.appendText("Server started at " + new Date() + '\n'));


                while (true) {
                    // Listen for a connection request
                    Socket socket = serverSocket.accept();

                    // Increment client number
                    clientNo++;

                    // Create and start a new thread for the connection
                    new Thread(new HandleAClient(socket, clientNo)).start();

                    Platform.runLater(() -> {
                        // Display client connection
                        ta.appendText("Client " + clientNo + " has joined the chat\n");
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        private int clientNo;

        public String getClientNo() {
            String clientNumber = String.valueOf(this.clientNo);
            return clientNumber;
        }

        /**
         * Construct a thread
         */
        public HandleAClient(Socket socket, int clientNo) {
            this.socket = socket;
            this.clientNo = clientNo;
        }

        /**
         * Run a thread
         */
        public void run() {
            try {
                // Create data input and output streams
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                // Continuously serve the client
                while (true) {
                    // Receive text from the client
                    String receivedText = inputFromClient.readUTF();

                    ClientList.add(this);

                    Platform.runLater(() -> {
                        ta.appendText(receivedText);
                    });

                    Platform.runLater(() -> {
                       for(int x = 0; x < ClientList.size(); x++) {
                           try {
                               outputToClient.writeUTF(receivedText);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}