package networking;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private final Selector _selector;
    private final ServerSocketChannel _serverSocketChannel;
    private static final int WORKER_POOL_SIZE = 10;
    private static ExecutorService _workerPool;

    Server(int port) throws IOException {
        _selector = Selector.open();
        _serverSocketChannel = ServerSocketChannel.open();
        _serverSocketChannel.socket().bind(new InetSocketAddress(port));
        _serverSocketChannel.configureBlocking(false); // Non-blocking

        SelectionKey selectionKey = _serverSocketChannel.register(_selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
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

    public static ExecutorService getWorkerPool() {
        return _workerPool;
    }

    // Accept requests and sends them to handler
    private class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel socketChannel = _serverSocketChannel.accept();
                if(socketChannel != null) new Handler(_selector, socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        _workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);

        try {
            new Thread(new Server(9090)).start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
