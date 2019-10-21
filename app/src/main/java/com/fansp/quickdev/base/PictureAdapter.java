package com.fansp.quickdev.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fansp.quickdev.R;
import com.fansp.quickdev.listener.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.MyViewHolder> {
    private List<String> list;
    private Context context;
    private String where;
    private Boolean isEdit = true;
    public PictureAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public PictureAdapter(List<String> list, Context context, Boolean isEdit) {
        this.list = list;
        this.context = context;
        this.isEdit = isEdit;
    }
    onItemDeleteListener onItemDeleteListener;
    OnItemClickListener onItemClickListener;
    onDeleteVisibleListener onDeleteVisibleListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemDeleteListener(onItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }
    public void setonDeleteVisibleListener(onDeleteVisibleListener onItemDeleteListener) {
        this.onDeleteVisibleListener = onItemDeleteListener;
    }
    public interface onItemDeleteListener {
        void onDelete(View view, int position);
    }
    public interface onDeleteVisibleListener {
        void onDeleteVisible(View view);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_publish, parent, false));
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (list.get(position).contains("/storage")||list.get(position).contains("/system")){
            Glide.with(context).load(list.get(position)).into(holder.ivItemPublish);
        }else {
            Glide.with(context).load(list.get(position)).skipMemoryCache(true).into(holder.ivItemPublish);
        }

        if (onDeleteVisibleListener != null) {
            onDeleteVisibleListener.onDeleteVisible(holder.ivItemPublishDelete);
        }

        if (onItemDeleteListener != null) {
            holder.ivItemPublishDelete.setOnClickListener(v -> onItemDeleteListener.onDelete(holder.ivItemPublishDelete, position));
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition()));
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item_publish)
        ImageView ivItemPublish;
        @BindView(R.id.iv_item_publish_delete)
        ImageView ivItemPublishDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
