package networking.server;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import model.ExternalGameState;
import model.GameCommand;
import model.GameState;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private static Server instance;

    public static final int MAX_CLIENTS = 4;

    private static final int WORKER_POOL_SIZE = 10;
    private static final int SERVER_PORT = 5700;
    private static final int SERVER_BROADCAST_PORT = 5710;
    private static final int SERVER_GAME_STATE_UPDATE_PORT = 5720;
    private static ExecutorService _workerPool;

    private final Selector _selector;
    private final ServerSocketChannel _serverSocketChannel;

    private int lastClientIndex = 0;

    private final ServerSocket _serverBroadcastSocket;
    private final ServerSocket _serverGameStateUpdateSocket;
    private final ArrayList<Socket> _broadcastClients;
    private final ArrayList<Socket> _gameStateUpdateClients;

    private final ArrayList<ObjectOutputStream> _broadcastClientOutputStreams;
    private final ArrayList<ObjectOutputStream> _gameStateUpdateOutputStreams;

    private GameState gameState;
    private ExternalGameState externalGameState;

    Server() throws IOException {
        // Initialize game state
        gameState = new GameState();
        externalGameState = new ExternalGameState(gameState);

        _serverBroadcastSocket = new ServerSocket(SERVER_BROADCAST_PORT);
        _serverGameStateUpdateSocket = new ServerSocket(SERVER_GAME_STATE_UPDATE_PORT);

        _broadcastClients = new ArrayList<Socket>();
        _broadcastClientOutputStreams = new ArrayList<ObjectOutputStream>();
        System.out.println("== Initialized server broadcast channel");

        _gameStateUpdateClients = new ArrayList<Socket>();
        _gameStateUpdateOutputStreams = new ArrayList<ObjectOutputStream>();
        System.out.println("== Initialized server game state update channel");

        _selector = Selector.open();
        _serverSocketChannel = ServerSocketChannel.open();
        _serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        _serverSocketChannel.configureBlocking(false); // Non-blocking

        SelectionKey selectionKey = _serverSocketChannel.register(_selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
        System.out.println("== Initialized server socket channel");
    }

    public static Server getInstance() throws IOException {
        if(instance == null) instance = new Server();
        return instance;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getNumClients() {return lastClientIndex;}

    @Override
    public void run() {
        try {
            while(true) {
                _selector.select();
                Iterator it = _selector.selectedKeys().iterator();

                while(it.hasNext()) {
                    SelectionKey sk = (SelectionKey) it.next();
                    it.remove();
                    Runnable r = (Runnable) sk.attachment();
                    if(r != null) r.run();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Accept requests and sends them to handler
    private class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                    if (lastClientIndex >= MAX_CLIENTS) {
                        System.out.println("== Server says:  Client limit reached");
                        return;
                    }

                    if (gameState.getGameStatus().equals(GameState.GameStatus.STARTED)) {
                        System.out.println("== Server says:  Game has already started. No longer accepting players");
                        return;
                    }

                    SocketChannel socketChannel = _serverSocketChannel.accept();
                    Socket broadcastSocket = _serverBroadcastSocket.accept();
                    registerClientForBroadcasts(broadcastSocket);
                    Socket gameStateUpdateSocket = _serverGameStateUpdateSocket.accept();
                    registerClientForGameStateUpdates(gameStateUpdateSocket);
                    if (socketChannel != null) new Handler(Server.this, _selector, socketChannel);
                    lastClientIndex++;
                    notifyClients(new GameCommand(GameCommand.Command.JOINED));
                    System.out.println("== Server Says: New client connected");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerClientForBroadcasts(Socket broadcastSocket)  throws IOException{
        _broadcastClients.add(lastClientIndex, broadcastSocket);
        _broadcastClientOutputStreams.add(lastClientIndex, new ObjectOutputStream(broadcastSocket.getOutputStream()));
    }

    public void registerClientForGameStateUpdates(Socket gameStateUpdateSocket)  throws IOException{
        _gameStateUpdateClients.add(lastClientIndex, gameStateUpdateSocket);
        _gameStateUpdateOutputStreams.add(lastClientIndex, new ObjectOutputStream(gameStateUpdateSocket.getOutputStream()));
    }

    public void removeClient(int index) {
        _broadcastClientOutputStreams.remove(index);
        _gameStateUpdateOutputStreams.remove(index);
        _broadcastClients.remove(index);
    }

    public void notifyClients(GameCommand command) {
        command.setJoinedPlayers(lastClientIndex);
        try {
            for (ObjectOutputStream oos : _gameStateUpdateOutputStreams) {
                oos.reset();
                oos.writeObject(externalGameState);
            }

            for (ObjectOutputStream oos : _broadcastClientOutputStreams) {
                oos.reset();
                oos.writeObject(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExecutorService getWorkerPool() {
        return _workerPool;
    }


    public static void main(String[] args) {
        _workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);

        try {
            //Integer port = Integer.parseInt(System.getenv("PORT"));
            //int port = 5000;
            new Thread(Server.getInstance()).start();
            System.out.println("== Server started on port 5000");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
