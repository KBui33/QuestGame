package model;

public enum GameStatus {
    READY,
    STARTED,
    RUNNING,
    TAKING_TURN,

    FINDING_QUEST_SPONSOR,
    PROMPTING_QUEST_SPONSOR,
    FINDING_QUEST_PARTICIPANTS,
    PROMPTING_QUEST_PARTICIPANT,
    RUNNING_QUEST,
    TAKING_QUEST_STAGE_CARD,
    TAKING_QUEST_TURN,
    ENDING_QUEST_TURN,
    TAKING_QUEST_SPONSOR_CARDS,
    TAKING_QUEST_SHIELDS,
    ENDING_QUEST,
    RUNNING_EVENT,

    FINDING_TOURNAMENT_PARTICIPANTS,
    PROMPTING_TOURNAMENT_PARTICIPANT,
    RUNNING_TOURNAMENT,
    TAKING_TOURNAMENT_CARD,
    TAKING_TOURNAMENT_TURN,
    ENDING_TOURNAMENT_TURN,
    DISTRIBUTING_TOURNAMENT_SHIELDS,
    ENDING_TOURNAMENT,

    ENDING_EVENT,
    FINDING_EVENT_CARD,
    TAKING_EVENT_ADVENTURE_CARD,
    GAME_OVER
}
