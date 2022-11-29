import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader inMessage;
    private BufferedWriter outMessage;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.outMessage = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.inMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = inMessage.readLine();
            clientHandlers.add(this);
            broadcastMessage("Hello from server :) - " + clientUsername + " has entered the chat!");
    } catch (IOException e) {
            close(socket, inMessage, outMessage);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected())  {
            try {
                messageFromClient = inMessage.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                close(socket, inMessage, outMessage);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.outMessage.write(messageToSend);
                    clientHandler.outMessage.newLine();
                    clientHandler.outMessage.flush();
                }
            } catch (IOException e) {
                close(socket, inMessage, outMessage);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left from the chat!");
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
}
