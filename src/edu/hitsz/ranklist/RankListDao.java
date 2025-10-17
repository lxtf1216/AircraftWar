package edu.hitsz.ranklist;

import java.time.LocalDateTime;

public interface RankListDao {
    public void addRecord(String name, int score, LocalDateTime time);
    public void printRankList();
    public void saveRankList();
    public void deleteRecord(int id);
}
