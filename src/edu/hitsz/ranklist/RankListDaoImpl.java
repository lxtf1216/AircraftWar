package edu.hitsz.ranklist;

import javax.print.attribute.standard.RequestingUserName;
import java.time.LocalDateTime;

public class RankListDaoImpl implements RankListDao{
    private RankList rankList;
    @Override
    public void addRecord(String name, int score, LocalDateTime time) {
        rankList = RankList.getInstance();
        rankList.insertRecord(new UserGameRecord(name,score,time));
    }

    @Override
    public void printRankList() {
        rankList = RankList.getInstance();
        rankList.printAllRecords();
    }

    @Override
    public void saveRankList() {
        rankList = RankList.getInstance();
        rankList.storeRankList();
    }

    @Override
    public void deleteRecord(int id) {
        rankList = RankList.getInstance();
        rankList.deleteRecord(id) ;
    }
}
