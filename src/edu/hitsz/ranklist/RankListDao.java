package edu.hitsz.ranklist;

import java.time.LocalDateTime;
import java.util.List;

public interface RankListDao {
    public void addRecord(String name, int score, LocalDateTime time);
    public void printRankList();
    public void saveRankList();
    public void deleteRecord(int id);
    public List<UserGameRecord> getAllRecords();
}
