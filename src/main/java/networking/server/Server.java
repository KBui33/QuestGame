package networking.server;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import model.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
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
    private final HashMap<Integer, Socket> _broadcastClients;
    private final HashMap<Integer, Socket> _gameStateUpdateClients;

    private final HashMap<Integer, ObjectOutputStream> _broadcastClientOutputStreams;
    private final HashMap<Integer, ObjectOutputStream> _gameStateUpdateOutputStreams;

    private InternalGameState internalGameState;
    private ExternalGameState externalGameState;

    private HashMap<Integer, Integer> clientPlayerIds;
    private HashMap<CommandType, HashSet<Integer>> numResponded; // Keeps track of clients who responded to a given command

    Server() throws IOException {
        // Initialize game state
        internalGameState = new InternalGameState();
        externalGameState = new ExternalGameState(internalGameState);

        clientPlayerIds = new HashMap<>();
        numResponded = new HashMap<>();

        _serverBroadcastSocket = new ServerSocket(SERVER_BROADCAST_PORT);
        _serverGameStateUpdateSocket = new ServerSocket(SERVER_GAME_STATE_UPDATE_PORT);

        _broadcastClients = new HashMap<>();
        _broadcastClientOutputStreams = new HashMap<>();
        System.out.println("== Initialized server broadcast channel");

        _gameStateUpdateClients = new HashMap<>();
        _gameStateUpdateOutputStreams = new HashMap<>();
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
        if (instance == null) instance = new Server();
        return instance;
    }

    public InternalGameState getGameState() {
        return internalGameState;
    }

    public ExternalGameState getExternalGameState() {
        return externalGameState;
    }

    public int getNumClients() {
        return _broadcastClients.size();
    }

    public int getClientPlayerId(int clientIndex) {
        return clientPlayerIds.get(clientIndex);
    }

    public void setClientPlayerId(int clientIndex, int playerId) {
        clientPlayerIds.put(clientIndex, playerId);
    }

    public Integer removeClientPlayerId(int clientIndex) { return clientPlayerIds.remove(clientIndex); }

    public int incrementNumResponded(CommandType commandType, int index) {
        if(!numResponded.containsKey(commandType)) numResponded.put(commandType, new HashSet<>());
        numResponded.get(commandType).add(index);
        return numResponded.get(commandType).size();
    }

    public void resetNumResponded(CommandType commandType) {
        if(numResponded.containsKey(commandType)) numResponded.put(commandType, new HashSet<>());
    }

    public int getNumResponded(CommandType commandType) {
        if(numResponded.containsKey(commandType)) return numResponded.get(commandType).size();
        return 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                _selector.select();
                Iterator it = _selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey sk = (SelectionKey) it.next();
                    it.remove();
                    Runnable r = (Runnable) sk.attachment();
                    if (r != null) r.run();
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
                SocketChannel socketChannel = _serverSocketChannel.accept();
                ByteBuffer byteLength = ByteBuffer.allocate(4);
                ByteBuffer byteBuffer;
                byte[] outMessage;

                if(socketChannel != null) {
                 if (_broadcastClients.size() >= MAX_CLIENTS) {
                        System.out.println("== Server says:  Client limit reached");
                        outMessage = Command.toBytesArray(new BaseCommand(BaseCommandName.MAX_CLIENTS_REACHED));
                     byteLength.putInt(outMessage.length).rewind();
                     byteBuffer = ByteBuffer.allocate(outMessage.length);
                     byteBuffer.put(outMessage).rewind();
                     socketChannel.write(new ByteBuffer[] {byteLength, byteBuffer});
                    } else if (internalGameState.getGameStatus() == null || !internalGameState.getGameStatus().equals(GameStatus.READY)) {
                        System.out.println("== Server says:  Game has already started. No longer accepting players");
                     outMessage = Command.toBytesArray(new BaseCommand(BaseCommandName.GAME_ALREADY_STARTED));
                     byteLength.putInt(outMessage.length).rewind();
                     byteBuffer = ByteBuffer.allocate(outMessage.length);
                     byteBuffer.put(outMessage).rewind();
                     socketChannel.write(new ByteBuffer[] {byteLength, byteBuffer});
                    } else {
                     outMessage = Command.toBytesArray(new BaseCommand(BaseCommandName.CONNECT_SUCCESSFULL));
                     byteLength.putInt(outMessage.length).rewind();
                     byteBuffer = ByteBuffer.allocate(outMessage.length);
                     byteBuffer.put(outMessage).rewind();
                     socketChannel.write(new ByteBuffer[] {byteLength, byteBuffer});
                        Socket broadcastSocket = _serverBroadcastSocket.accept();
                        registerClientForBroadcasts(broadcastSocket);

                        Socket gameStateUpdateSocket = _serverGameStateUpdateSocket.accept();
                        registerClientForGameStateUpdates(gameStateUpdateSocket);

                        new Handler(Server.this, _selector, socketChannel);

                        clientPlayerIds.put(lastClientIndex, 0);
                        BaseCommand joinedCommand = new BaseCommand(BaseCommandName.JOINED);
                        joinedCommand.setClientIndex(lastClientIndex);

                        notifyClient(lastClientIndex, joinedCommand);

                        lastClientIndex++;
                        notifyClients(new BaseCommand(BaseCommandName.HAS_JOINED));

                        System.out.println("== Server Says: New client connected");
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Integer, ObjectOutputStream> getBroadcastClientOutputStreams() {
        return _gameStateUpdateOutputStreams;
    }

    public HashMap<Integer, ObjectOutputStream> getGameStateUpdateOutputStreams() {
        return _gameStateUpdateOutputStreams;
    }

    public void registerClientForBroadcasts(Socket broadcastSocket) throws IOException {
        _broadcastClients.put(lastClientIndex, broadcastSocket);
        _broadcastClientOutputStreams.put(lastClientIndex, new ObjectOutputStream(broadcastSocket.getOutputStream()));
    }

    public void registerClientForGameStateUpdates(Socket gameStateUpdateSocket) throws IOException {
        _gameStateUpdateClients.put(lastClientIndex, gameStateUpdateSocket);
        _gameStateUpdateOutputStreams.put(lastClientIndex, new ObjectOutputStream(gameStateUpdateSocket.getOutputStream()));
    }

    public void removeClient(int clientIndex) {
        _broadcastClientOutputStreams.remove(clientIndex);
        _gameStateUpdateOutputStreams.remove(clientIndex);
        _broadcastClients.remove(clientIndex);
        _gameStateUpdateClients.remove(clientIndex);
        clientPlayerIds.remove(clientIndex);
    }

    public void resetClientPlayerIds() {
        clientPlayerIds.clear();
    }

    public synchronized void notifyClient(int clientIndex, BaseCommand command) {
        try {
            ObjectOutputStream oos = _broadcastClientOutputStreams.get(clientIndex);
            oos.writeObject(command);
            oos.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void notifyClientByPlayerId(int playerId, BaseCommand command) {
        int clientIndex = -1;

        for (HashMap.Entry entry : clientPlayerIds.entrySet()) {
            if((int) entry.getValue() == playerId) {
                clientIndex = (int)entry.getKey();
                break;
            }
        }

        if(clientIndex >= 0) notifyClient(clientIndex, command);
    }

    public synchronized void notifyClients(Command command) {
        System.out.println("== Server notifier says: " + command);
        if(command.getCommandType().equals(CommandType.BASE)) ((BaseCommand) command).setNumJoined(_broadcastClients.size());

        try {
            for (ObjectOutputStream oos : _gameStateUpdateOutputStreams.values()) {
                oos.writeObject(externalGameState);
                oos.reset();
            }

            for (ObjectOutputStream oos : _broadcastClientOutputStreams.values()) {
                oos.writeObject(command);
                oos.reset();
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
            new Thread(Server.getInstance()).start();
            System.out.println("== Server started on port " + SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
