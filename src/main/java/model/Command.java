package model;

public enum Command {
    JOIN,
    JOINED,
    PLAYER_JOINED,
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
    PLAYER_TAKE_STAGE_CARD,
    ACCEPT_QUEST_STAGE_CARD,
    ACCEPTED_QUEST_STAGE_CARD,
    DISCARD_QUEST_STAGE_CARD,
    DISCARDED_QUEST_STAGE_CARD,
    PLAYER_TAKE_QUEST_SHIELDS,
    ACCEPT_QUEST_SHIELDS,
    ACCEPTED_QUEST_SHIELDS,
    PLAYER_TAKE_SPONSOR_QUEST_CARDS,
    ACCEPT_SPONSOR_QUEST_CARDS,
    ACCEPTED_SPONSOR_QUEST_CARDS,
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
    PLAYER_END_QUEST,
    END_QUEST,
    ENDED_QUEST,
    QUEST_COMPLETED,
    EVENT_STARTED,
    EVENT_END,
    TAKE_QUEST_STAGE_CARD,
    TOOK_QUEST_STAGE_CARD
}
