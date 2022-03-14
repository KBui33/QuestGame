package networking.client;

import model.ExternalGameState;
import model.GameCommand;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client  {
    private final int PORT = 80;
    private static final int READ_BUFFER_SIZE = 4096;
    private static final int WRITE_BUFFER_SIZE = 4096;

    // Various game commands
    public static enum ClientEvent {
        EXTERNAL_GAME_STATE_UPDATED,
        GAME_COMMAND_RECEIVED
    };

    private static Client instance = null;

    private final SocketChannel _socketChannel;
    private final ObjectInputStream _subscribeInputStream;
    private final ObjectInputStream _gameStateInputStream;

    private Socket _subscribeSocket;
    private Socket _gameStateSocket;

    private Scanner _scanner;
    private String serverHost;
    private ByteBuffer _readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private ByteBuffer _writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);

    private int playerId;

    public ClientEventManager clientEvents;

    public Client(String serverHost) throws IOException {
        this.serverHost = serverHost;
        InetSocketAddress address = new InetSocketAddress(serverHost, 5700);
        _socketChannel = SocketChannel.open(address);
        System.out.println("== Client connected to server socket");

        clientEvents = new ClientEventManager(new ClientEvent[] {ClientEvent.EXTERNAL_GAME_STATE_UPDATED, ClientEvent.GAME_COMMAND_RECEIVED});

        _subscribeSocket = new Socket(serverHost, 5710);
        _subscribeInputStream = new ObjectInputStream(_subscribeSocket.getInputStream());
        new Thread(new SocketSubscriptionThread()).start(); // Listen for server broadcasts
        System.out.println("== Client subscribed to server broadcast channel");

        _gameStateSocket = new Socket(serverHost, 5720);
        _gameStateInputStream = new ObjectInputStream(_gameStateSocket.getInputStream());
        new Thread(new GameStateUpdateThread()).start(); // Listen for game state updates
        System.out.println("== Client subscribed to game state update channel");

        _scanner = new Scanner(System.in);

    }

    public Client() throws IOException {
        this("192.168.122.1");
    }

    public static Client initialize(String serverHost) throws IOException {
        if(instance == null) instance = new Client(serverHost);
        return instance;
    }

    public static Client getInstance() throws IOException {
        return instance;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getServerHost() {
        return serverHost;
    }

    public synchronized GameCommand sendCommand(GameCommand command) {
        GameCommand receivedCommand = null;
        try {
            byte[] outMessage = GameCommand.toBytesArray(command);
            _writeBuffer = ByteBuffer.wrap(outMessage);
            _socketChannel.write(_writeBuffer);
            _writeBuffer.clear();

            _socketChannel.read(_readBuffer);
            _readBuffer.flip();
            byte[] inMessage = new byte[_readBuffer.limit()];
            _readBuffer.get(inMessage);

            receivedCommand = GameCommand.fromBytesArray(inMessage);
            System.out.println("== Server says: " + receivedCommand);

            _readBuffer.clear();
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return receivedCommand;
    }

    private class SocketSubscriptionThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    GameCommand command = (GameCommand) _subscribeInputStream.readObject();
                    clientEvents.notify(ClientEvent.GAME_COMMAND_RECEIVED, command);
                    System.out.println("== Subscription thread: " + command);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GameStateUpdateThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    ExternalGameState externalGameState = (ExternalGameState) _gameStateInputStream.readObject();
                    clientEvents.notify(ClientEvent.EXTERNAL_GAME_STATE_UPDATED, externalGameState);
                    System.out.println("== Game state update thread: Updating game state");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
