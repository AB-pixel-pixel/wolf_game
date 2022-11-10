package com.example.atry;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder>{


    private List<String> player_state_list_;
    private Context context_;
    private PlayerListViewHolder view_holder_;


    public PlayerListAdapter(List<String> player_states,Context context) {
        this.player_state_list_ = player_states;
        context_ = context;
    }


    @NonNull
    @Override
    public PlayerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context_,R.layout.player_state_list_item,null);
        return new PlayerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListViewHolder holder, int position) {
        holder.tv.setText(player_state_list_.get(position));
    }

    @Override
    public int getItemCount() {
        return  player_state_list_ ==null ? 0 : player_state_list_.size();
    }



    public class PlayerListViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;

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

    private OnRecyclerItemClickListener entry_OnRecyclerItemClickListener_;

    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        entry_OnRecyclerItemClickListener_ = listener;
    }

    public interface OnRecyclerItemClickListener{
        void onRecyclerItemClick(int position);
    }
}



