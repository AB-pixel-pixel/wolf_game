package com.example.atry;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.PlayerListViewHolder>{


    private final List<String> player_state_list_;
    private final Context context_;


    public ListAdapter(List<String> player_states, Context context) {
        this.player_state_list_ = player_states;
        context_ = context;
    }


    @NonNull
    @Override
    public PlayerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context_,R.layout.player_state_list_item,null);
        return new PlayerListViewHolder(view);
    }

    // 将UI和数据绑定

    @Override
    public void onBindViewHolder(@NonNull PlayerListViewHolder holder, int position) {
        holder.tv.setText(player_state_list_.get(position));
    }

    @Override
    public int getItemCount() {
        return  player_state_list_ ==null ? 0 : player_state_list_.size();
    }


    // *********************************************************************************************
    //
    //      View Holder
    //
    // *********************************************************************************************
    public class PlayerListViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv;

        public PlayerListViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.player_state_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(entry_OnRecyclerItemClickListener_!=null){
                        entry_OnRecyclerItemClickListener_.onRecyclerItemClick(getAbsoluteAdapterPosition());
                    }
                }
            });

        }
    }

    // *********************************************************************************************
    //
    //      点击事件
    //
    // *********************************************************************************************

    private OnRecyclerItemClickListener entry_OnRecyclerItemClickListener_;

    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        entry_OnRecyclerItemClickListener_ = listener;
    }

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(int position);
    }
}



