package networking.server;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import model.GameCommand;
import networking.GameCommandHandler;
import networking.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable {
    private final SocketChannel _socketChannel;
    private final SelectionKey _selectionKey;

    private static final int READ_BUFFER_SIZE = 2048;
    private static final int WRITE_BUFFER_SIZE = 2048;

    private ByteBuffer _readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private ByteBuffer _writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);

    private GameCommandHandler _gameCommandHandler;

    public Handler(Server server, Selector selector, SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        _socketChannel = socketChannel;
        _socketChannel.configureBlocking(false);

        _selectionKey = _socketChannel.register(selector, SelectionKey.OP_READ);
        _selectionKey.attach(this);
        selector.wakeup();

        _gameCommandHandler =  new GameCommandHandler(server);
    }

    @Override
    public void run() {
        try {
            if(_selectionKey.isReadable()) read();
            else if(_selectionKey.isWritable()) write();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void process()  {
        try {
            _readBuffer.flip();
            byte[] bytes = new byte[_readBuffer.limit()];
            _readBuffer.get(bytes);

            // Convert input to game command and send for processing
            GameCommand receivedCommand = GameCommand.fromBytesArray(bytes);
            System.out.println("== Received command: " + receivedCommand);
            GameCommand sentCommand = _gameCommandHandler.processGameCommand(receivedCommand);
            System.out.println("== Send command: " + sentCommand);
            _writeBuffer = ByteBuffer.wrap(GameCommand.toBytesArray(sentCommand));

            _selectionKey.interestOps(SelectionKey.OP_WRITE);
            _selectionKey.selector().wakeup();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void read() throws IOException {
        try {
            int numBytes = _socketChannel.read(_readBuffer);
            System.out.println("== Reading: " + numBytes + " byte(s)");

            if(numBytes == -1) {
                _selectionKey.cancel();
                _socketChannel.close();
                System.out.println("== Reading: Connection dropped with client");
            } else {
                Server.getWorkerPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        process();
                    }
                });
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    void write() throws  IOException {
        int numBytes = 0;

        try {
            numBytes = _socketChannel.write(_writeBuffer);
            System.out.println("== Writing: " + numBytes + " byte(s)");

            if(numBytes > 0) {
                _readBuffer.clear();
                _writeBuffer.clear();

                _selectionKey.interestOps(SelectionKey.OP_READ);
                _selectionKey.selector().wakeup();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


}
