package com.example.atry;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder>{

    private PlayerState player_states_;
    private List<String> player_state_list_;
    private Context context_;

    public PlayerListAdapter(PlayerState player_states,Context context) {
        this.player_states_ = player_states;
        context_ = context;
    }


    @NonNull
    @Override
    public PlayerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context_,R.layout.fragment_player_state_list,null);
        return new PlayerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerListViewHolder holder, int position) {
        holder.tv.setText(player_states_.getVisual_player_data_list_().get(position));
    }

    @Override
    public int getItemCount() {
        return  player_states_ ==null ? 0 : player_states_.getVisual_player_data_list_().size();
    }



    public class PlayerListViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;

        public PlayerListViewHolder(@NonNull View itemView) {

            super(itemView);
            tv = itemView.findViewById(R.id.player_state_list);

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



