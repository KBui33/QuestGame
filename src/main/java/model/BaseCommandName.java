package model;

public enum BaseCommandName implements CommandName {
    JOIN,
    JOINED,
    HAS_JOINED,
    QUIT,
    QUITED,
    GET_LOBBY_STATE,
    RETURN_LOBBY_STATE,
    DISCONNECT,
    DISCONNECTED,
    MAX_CLIENTS_REACHED,
    GAME_ALREADY_STARTED,
    CONNECT_SUCCESSFULL
}
