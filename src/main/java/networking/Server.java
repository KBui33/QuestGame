package networking;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import logic.GameCommand;
import utils.Observer;
import utils.Subject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private static final int WORKER_POOL_SIZE = 10;
    private final int SERVER_PORT = 5000;
    private final int SERVER_BROADCAST_PORT = 5050;
    private static ExecutorService _workerPool;

    private final Selector _selector;
    private final ServerSocketChannel _serverSocketChannel;

    private final ServerSocket _serverBroadcastSocket;
    private final ArrayList<Socket> _broadcastClients;
    private int lastClientIndex = 0;
    //private final ArrayList<ObjectInputStream> _broadcastClientInputStreams;
    private final ArrayList<ObjectOutputStream> _broadcastClientOutputStreams;

    Server() throws IOException {
        _selector = Selector.open();
        _serverSocketChannel = ServerSocketChannel.open();
        _serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        _serverSocketChannel.configureBlocking(false); // Non-blocking

        SelectionKey selectionKey = _serverSocketChannel.register(_selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
        System.out.println("== Initialized server socket channel");

        _serverBroadcastSocket = new ServerSocket(SERVER_BROADCAST_PORT);
        _broadcastClients = new ArrayList<Socket>();
        //_broadcastClientInputStreams = new ArrayList<ObjectInputStream>();
        _broadcastClientOutputStreams = new ArrayList<ObjectOutputStream>();
        System.out.println("== Initialized server broadcast channel");
    }
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
                SocketChannel socketChannel = _serverSocketChannel.accept();
                Socket socket = _serverBroadcastSocket.accept();
                registerClient(socket);
                System.out.println("== New client connected");
                if(socketChannel != null) new Handler(Server.this, _selector, socketChannel);
                notifyClients(new GameCommand(GameCommand.Command.JOINED));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerClient(Socket socket) {
        int index = lastClientIndex++;
        _broadcastClients.add(index, socket);
        try {
            //_broadcastClientInputStreams.add(hs, new ObjectInputStream(socket.getInputStream()));
            _broadcastClientOutputStreams.add(index, new ObjectOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(Socket socket) {
        int hs = socket.hashCode();
        _broadcastClients.remove(hs);
        _broadcastClients.remove(hs);
        _broadcastClients.remove(hs);
    }

    public void notifyClients(GameCommand command) {
        try {
            for (ObjectOutputStream oos : _broadcastClientOutputStreams) {
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
            new Thread(new Server()).start();
            System.out.println("== Server started on port 5000");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
