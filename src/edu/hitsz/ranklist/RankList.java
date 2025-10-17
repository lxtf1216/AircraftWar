package edu.hitsz.ranklist;


import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class RankList {
    private static volatile RankList Instance;
    private PriorityQueue<UserGameRecord> recordHeap;
    private static final String SAVE_FILE = "ranklist.dat";
    private RankList() {
        this.recordHeap = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.getScore(), a.getScore()) // b - a 实现降序
        );
        loadRankList();
    }
    private void loadRankList() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("排行榜文件不存在，使用空排行榜。");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof java.util.List<?>) {
                java.util.List<UserGameRecord> list = (java.util.List<UserGameRecord>) obj;
                recordHeap.addAll(list);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载排行榜时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static RankList getInstance() {
        if(Instance == null) {
            synchronized (RankList.class) {
                if(Instance == null) {
                    Instance = new RankList();
                }
            }
        }
        return Instance;
    }
    public void storeRankList() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            // 由于 PriorityQueue 本身可序列化，但为了确保顺序一致，我们转为 List 保存
            List<UserGameRecord> list = new ArrayList<>(recordHeap);
            oos.writeObject(list);
        } catch (IOException e) {
            System.err.println("保存排行榜时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertRecord(UserGameRecord record) {
        recordHeap.add(record);
    }

    public void printAllRecords() {
        PriorityQueue<UserGameRecord> tmp = new PriorityQueue<>(recordHeap);
        System.out.println("**************************************");
        System.out.println("        得分排行榜");
        System.out.println("**************************************");
        if(tmp.isEmpty()) {
            System.out.println("没有记录");
            return ;
        }
        int rank = 0;
        while(!tmp.isEmpty()) {
            rank ++ ;
            UserGameRecord record = tmp.poll();
            String timeStr = record.getTime().toString().substring(5, 10) + " " +
                    record.getTime().toString().substring(11, 16);
            // 格式化为 MM-dd HH:mm
            System.out.printf("第%d名：%s,%d,%s%n", rank, record.getName(), record.getScore(), timeStr);
        }
     }

     public void deleteRecord(int id) {
         // 将PriorityQueue转换为有序列表
         List<UserGameRecord> sortedList = new ArrayList<>();
         PriorityQueue<UserGameRecord> tempHeap = new PriorityQueue<>(recordHeap);
         while (!tempHeap.isEmpty()) {
             sortedList.add(tempHeap.poll());
         }
         
         // 检查索引是否有效
         if (id >= 0 && id < sortedList.size()) {
             // 删除指定索引的记录
             sortedList.remove(id);
             
             // 重新构建PriorityQueue
             recordHeap.clear();
             recordHeap.addAll(sortedList);
         }
     }
     
     /**
      * 获取所有记录的有序列表（按分数降序）
      */
     public List<UserGameRecord> getAllRecords() {
         List<UserGameRecord> records = new ArrayList<>();
         PriorityQueue<UserGameRecord> tempHeap = new PriorityQueue<>(recordHeap);
         while (!tempHeap.isEmpty()) {
             records.add(tempHeap.poll());
         }
         return records;
     }
}
