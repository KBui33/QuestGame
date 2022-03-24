package networking;

import model.BaseCommand;
import model.Command;

import java.io.IOException;

public interface CommandHandler  {

    public Command processGameCommand(Command command) throws IOException;
}
