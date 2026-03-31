package edu.hitsz.dao;

import edu.hitsz.model.ScoreRecord;
import java.util.List;

public interface ScoreDao {
    long insert(ScoreRecord record);
    boolean deleteById(long id);
    int deleteAll();
    List<ScoreRecord> findAllOrderByScoreDesc();
}