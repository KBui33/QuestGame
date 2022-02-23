package networking;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private final int PORT = 80;
    private Socket _socket;
    private ObjectInputStream _inStream;
    private ObjectOutputStream _outStream;
    private Scanner _scanner;

    public Client() throws IOException {
        System.out.println("== init");
        //_socket = new Socket("quest-3004.herokuapp.com", PORT);
        _socket = new Socket("localhost", 5000);
        System.out.println("== Connected");
        _outStream = new ObjectOutputStream(_socket.getOutputStream());
        System.out.println("== Out");
        _inStream = new ObjectInputStream(_socket.getInputStream());
        _scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("== Send a message to server. Type (q) to exit");

                System.out.print("== Client: ");
                String userInput = _scanner.nextLine();

                if (userInput.equals("q")) break;

                _outStream.writeObject(userInput);
                _outStream.flush();
                //_outStream.reset();

                String serverInput = (String) _inStream.readObject();
                System.out.println("== Server says: " + serverInput);
            }

            // Close connection
            _inStream.close();
            _outStream.close();
            _scanner.close();
            _socket.close();

            System.out.println("== Client exiting");
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new Thread(new Client()).start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
