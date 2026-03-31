package edu.hitsz.application;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.hitsz.R;
import edu.hitsz.adapter.ScoreAdapter;
import edu.hitsz.dao.ScoreDao;
import edu.hitsz.dao.ScoreDaoImpl;
import edu.hitsz.model.ScoreRecord;

public class LeaderboardActivity extends AppCompatActivity {

    private ListView lvScores;
    private TextView tvEmpty;
    private Button btnClearAll;
    private Button btnBack;

    private ScoreDao scoreDao;
    private List<ScoreRecord> scoreList;
    private ScoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        lvScores = findViewById(R.id.lv_scores);
        tvEmpty = findViewById(R.id.tv_empty);
        btnClearAll = findViewById(R.id.btn_clear_all);
        btnBack = findViewById(R.id.btn_back);

        scoreDao = new ScoreDaoImpl(this);
        scoreList = new ArrayList<>(scoreDao.findAllOrderByScoreDesc());

        adapter = new ScoreAdapter(this, scoreList, scoreDao, this::updateEmptyView);
        lvScores.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnClearAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("清空排行榜")
                    .setMessage("确定清空全部排行榜数据吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        scoreDao.deleteAll();
                        scoreList.clear();
                        adapter.notifyDataSetChanged();
                        updateEmptyView();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        updateEmptyView();
    }

    private void updateEmptyView() {
        if (scoreList.isEmpty()) {
            tvEmpty.setText("暂无排行榜数据");
            lvScores.setEmptyView(tvEmpty);
        } else {
            tvEmpty.setText("");
        }
    }
}