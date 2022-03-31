package model;

import java.io.Serializable;

public class TournamentCommand extends GameCommand implements Serializable {
    private Tournament tournament;

    public TournamentCommand() {
        super();
        commandType = CommandType.TOURNAMENT;
    }

    public TournamentCommand(TournamentCommandName commandName) {
        this();
        this.commandName = commandName;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        if(tournament != null) cmd += "Tournament: " + tournament.getTitle() + ", ";
        return cmd;
    }
}
