package networking;

public abstract class CommandHandlerDecorator implements CommandHandler {
    CommandHandler commandHandler;

    public CommandHandlerDecorator(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }
}
