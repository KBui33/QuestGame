package networking;

import logic.GameCommand;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client implements Runnable {
    private final int PORT = 80;
    private Socket _socket;
    private SocketChannel _socketChannel;
    private Scanner _scanner;
    private static final int READ_BUFFER_SIZE = 2048;
    private static final int WRITE_BUFFER_SIZE = 2048;
    private ByteBuffer _readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private ByteBuffer _writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);

    public Client() throws IOException, ClassNotFoundException {
        InetSocketAddress address = new InetSocketAddress("localhost", 5000);
        _socketChannel = SocketChannel.open(address);
        _scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("== Send a message to server.");
                System.out.println("\tj -> Join");
                System.out.println("\tr -> Ready");
                System.out.println("\tq -> Quit");

                Boolean willQuit = false;

                GameCommand sentCommand = new GameCommand();

                System.out.print("== Client: ");
                String userInput = _scanner.nextLine();

                if (userInput.equals("j")) {
                    sentCommand.setCommand(GameCommand.Command.JOIN);
                } else if(userInput.equals("r")) {
                    sentCommand.setCommand(GameCommand.Command.READY);
                } else if(userInput.equals("q")) {
                    sentCommand.setCommand(GameCommand.Command.QUIT);
                    willQuit = true;
                } else {
                    System.out.println("== Invalid command");
                    System.out.println("\tj -> Join");
                    System.out.println("\tr -> Ready");
                    System.out.println("\tq -> Quit");
                    continue;
                }

                byte[] outMessage = GameCommand.toBytesArray(sentCommand);
                _writeBuffer = ByteBuffer.wrap(outMessage);
                _socketChannel.write(_writeBuffer);
                _writeBuffer.clear();

                int numBytes = _socketChannel.read(_readBuffer);
                _readBuffer.flip();
                byte[] inMessage = new byte[_readBuffer.limit()];
                _readBuffer.get(inMessage);

                GameCommand receivedCommand = GameCommand.fromBytesArray(inMessage);
                System.out.println("== Server says: " + receivedCommand);

                _readBuffer.clear();


                if(willQuit) break;
            }

            // Close connection
            _scanner.close();
            _socketChannel.close();

            System.out.println("== Client exiting");
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new Thread(new Client()).start();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
