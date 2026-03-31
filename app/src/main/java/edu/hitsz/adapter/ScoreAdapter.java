package edu.hitsz.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import edu.hitsz.R;
import edu.hitsz.dao.ScoreDao;
import edu.hitsz.model.ScoreRecord;

public class ScoreAdapter extends BaseAdapter {

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private final Context context;
    private final List<ScoreRecord> data;
    private final ScoreDao scoreDao;
    private final OnDataChangedListener listener;

    public ScoreAdapter(Context context, List<ScoreRecord> data, ScoreDao scoreDao,
                        OnDataChangedListener listener) {
        this.context = context;
        this.data = data;
        this.scoreDao = scoreDao;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ScoreRecord getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
            holder = new ViewHolder();
            holder.tvRank = convertView.findViewById(R.id.tv_rank);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvScore = convertView.findViewById(R.id.tv_score);
            holder.tvDifficulty = convertView.findViewById(R.id.tv_difficulty);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.btnDelete = convertView.findViewById(R.id.btn_delete_one);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScoreRecord record = getItem(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(record.getPlayerName());
        holder.tvScore.setText(String.valueOf(record.getScore()));
        holder.tvDifficulty.setText(record.getDifficulty());
        holder.tvTime.setText(record.getPlayTime());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("删除记录")
                    .setMessage("确定删除这条成绩吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        scoreDao.deleteById(record.getId());
                        data.remove(position);
                        notifyDataSetChanged();
                        if (listener != null) {
                            listener.onDataChanged();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvRank;
        TextView tvName;
        TextView tvScore;
        TextView tvDifficulty;
        TextView tvTime;
        Button btnDelete;
    }
}