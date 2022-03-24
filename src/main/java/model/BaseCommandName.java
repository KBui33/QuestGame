package model;

public enum BaseCommandName implements CommandName {
    JOIN,
    JOINED,
    HAS_JOINED,
    QUIT,
    QUITED,
    GET_LOBBY_STATE,
    RETURN_LOBBY_STATE,
}
