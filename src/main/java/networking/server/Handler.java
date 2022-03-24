package networking.server;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import model.Command;
import networking.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable {
    private final SocketChannel _socketChannel;
    private final SelectionKey _selectionKey;

    private static final int READ_BUFFER_SIZE = 4096;
    private static final int WRITE_BUFFER_SIZE = 4096;

    private ByteBuffer _readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private ByteBuffer _writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);

    private CommandHandler commandHandler;

    public Handler(Server server, Selector selector, SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        _socketChannel = socketChannel;
        _socketChannel.configureBlocking(false);

        _selectionKey = _socketChannel.register(selector, SelectionKey.OP_READ);
        _selectionKey.attach(this);
        selector.wakeup();

        commandHandler =  new BaseCommandHandler();
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
            Command receivedCommand = Command.fromBytesArray(bytes);
            System.out.println("== Received command: " + receivedCommand);
            Command sentCommand = handleCommand(receivedCommand);
            System.out.println("== Send command: " + sentCommand);
            _writeBuffer = ByteBuffer.wrap(Command.toBytesArray(sentCommand));

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

    private Command handleCommand(Command command) throws IOException {
        switch (command.getCommandType()) {
            case GAME:
                commandHandler = new GameCommandHandler(commandHandler);
                break;
            case QUEST:
                commandHandler = new QuestCommandHandler(commandHandler);
                break;
            case EVENT:
                commandHandler = new EventCommandHandler(commandHandler);
                break;
            default:
                break;
        }

        return commandHandler.processGameCommand(command);
    }
}
