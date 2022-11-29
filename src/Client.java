import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader inMessage;
    private BufferedWriter outMessage;
    private String name;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            this.outMessage = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = name;
        } catch (IOException e) {
            close(socket, inMessage, outMessage);
        }
    }

    public void sendMessage() {
        try {
            outMessage.write(name);
            outMessage.newLine();
            outMessage.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                outMessage.write(name + ": " + messageToSend);
                outMessage.newLine();
                outMessage.flush();
            }
        } catch (IOException e) {
            close(socket, inMessage, outMessage);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = inMessage.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        close(socket, inMessage, outMessage);
                    }
                }
            }
        }).start();
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name for the chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 9999);
        Client client = new Client(socket, username);
        System.out.println("You entered the chat! Say Hello: ");
        client.listenForMessage();
        client.sendMessage();
    }
}
