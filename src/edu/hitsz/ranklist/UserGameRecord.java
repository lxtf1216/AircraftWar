package edu.hitsz.ranklist;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserGameRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int score;
    private final LocalDateTime time ;
    public  UserGameRecord(String name,int score,LocalDateTime time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }
    public  String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
    public LocalDateTime getTime() {
        return time;
    }
}
