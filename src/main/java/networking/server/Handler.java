package networking.server;
// CODE FROM::https://tianpan.co/blog/2015-01-13-understanding-reactor-pattern-for-highly-scalable-i-o-bound-web-server

import model.Command;
import networking.*;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable {
    private final SocketChannel _socketChannel;
    private final SelectionKey _selectionKey;

    private static final int READ_BUFFER_SIZE = 16384;
    private static final int WRITE_BUFFER_SIZE = 16384;

    private ByteBuffer _readBuffer; // = ByteBuffer.allocate(READ_BUFFER_SIZE);
    private ByteBuffer _readLength;
    private ByteBuffer _writeBuffer; // = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
    private ByteBuffer _writeLength;

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
            _readBuffer.rewind();
            byte[] bytes = new byte[_readBuffer.remaining()];
            _readBuffer.get(bytes, 0, bytes.length);

            // Convert input to game command and send for processing
            Command receivedCommand = Command.fromBytesArray(bytes);
            System.out.println("== Received command: " + receivedCommand);
            Command sentCommand = handleCommand(receivedCommand);
            System.out.println("== Send command: " + sentCommand);

            byte[] outMessage = Command.toBytesArray(sentCommand);
            _writeLength = ByteBuffer.allocate(4);
            _writeLength.putInt(outMessage.length);
            _writeLength.rewind();
            _writeBuffer = ByteBuffer.allocate(outMessage.length);
            _writeBuffer.put(outMessage);

            //_writeBuffer = ByteBuffer.wrap(Command.toBytesArray(sentCommand));

            _selectionKey.interestOps(SelectionKey.OP_WRITE);
            _selectionKey.selector().wakeup();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void read() throws IOException {
        try {
            _readLength = ByteBuffer.allocate(4);
            _socketChannel.read(_readLength);
            _readLength.rewind();
            int numBytes = _readLength.getInt();

            System.out.println("== Reading: " + numBytes + " byte(s)");

            if(numBytes <= 0) {
                _selectionKey.cancel();
                _socketChannel.close();
                System.out.println("== Reading: Connection dropped with client");
            } else {
                _readBuffer = ByteBuffer.allocate(numBytes);
                _socketChannel.read(_readBuffer);
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
        long numBytes = 0;

        try {
            _writeBuffer.rewind();
            numBytes = _socketChannel.write(new ByteBuffer[]{_writeLength, _writeBuffer});
            System.out.println("== Writing: " + numBytes + " byte(s)");

//            if(numBytes > 0) {
                _readBuffer.clear();
                _readLength.clear();

                _writeBuffer.clear();
                _writeLength.clear();

                _selectionKey.interestOps(SelectionKey.OP_READ);
                _selectionKey.selector().wakeup();
//            }
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
            case TOURNAMENT:
                commandHandler = new TournamentCommandHandler(commandHandler);
                break;
            default:
                commandHandler = new BaseCommandHandler();
                break;
        }

        return commandHandler.processGameCommand(command);
    }
}
