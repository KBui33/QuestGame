package model;

public enum Command {
    JOIN,
    JOINED,
    READY,
    IS_READY,
    QUIT,
    QUITED,
    GET_LOBBY_STATE,
    RETURN_LOBBY_STATE,
    GET_ATTACHED_PLAYER,
    RETURN_ATTACHED_PLAYER,
    GAME_STARTED,
    PLAYER_TURN,
    TAKE_TURN,
    END_TURN,
    PLAYER_QUEST_TURN,
    TAKE_QUEST_TURN,
    TOOK_QUEST_TURN,
    END_QUEST_TURN,
    ENDED_QUEST_TURN,
    ENDED_TURN,
    TOOK_TURN,
    DISCARD_CARD,
    DISCARDED_CARD,
    QUEST_STARTED,
    DRAW_QUEST_CARD,
    DREW_QUEST_CARD,
    FIND_QUEST_SPONSOR,
    FOUND_QUEST_SPONSOR,
    SHOULD_SPONSOR_QUEST,
    WILL_SPONSOR_QUEST,
    WILL_NOT_SPONSOR_QUEST,
    SHOULD_JOIN_QUEST,
    WILL_JOIN_QUEST,
    JOINED_QUEST,
    WILL_NOT_JOIN_QUEST,
    DID_NOT_JOIN_QUEST,
    QUEST_STAGE_LOST,
    QUEST_STAGE_WON,
    QUEST_STAGE_STATE,
    ENDED_QUEST,
    EVENT_STARTED,
    EVENT_ENDED
}
