package networking;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Handler implements Runnable {
    private final SocketChannel _socketChannel;
    private final SelectionKey _selectionKey;

    private static final int READ_BUFFER_SIZE = 1024;
    private static final int WRITE_BUFFER_SIZE = 1024;
    private ByteBuffer _readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private ByteBuffer _writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);

    public Handler(Selector selector, SocketChannel socketChannel) throws IOException {
        _socketChannel = socketChannel;
        _socketChannel.configureBlocking(false);

        _selectionKey = _socketChannel.register(selector, SelectionKey.OP_READ);
        _selectionKey.attach(this);
        selector.wakeup();
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

    synchronized void process() {
        _readBuffer.flip();
        byte[] bytes = new byte[_readBuffer.remaining()];
        _readBuffer.get(bytes, 0, bytes.length);
        System.out.println("== Processing: " + new String(bytes, Charset.forName("ISO-8859-1")));

        _writeBuffer = ByteBuffer.wrap(bytes);

        _selectionKey.interestOps(SelectionKey.OP_WRITE);
        _selectionKey.selector().wakeup();
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
