package edu.hitsz.model;

public class ScoreRecord {
    private long id;
    private String playerName;
    private int score;
    private String difficulty;
    private String playTime;

    public ScoreRecord() {
    }

    public ScoreRecord(long id, String playerName, int score, String difficulty, String playTime) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.playTime = playTime;
    }

    public ScoreRecord(String playerName, int score, String difficulty, String playTime) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.playTime = playTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }
}