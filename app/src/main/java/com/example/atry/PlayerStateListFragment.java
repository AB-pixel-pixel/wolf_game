package com.example.atry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class PlayerStateListFragment extends Fragment {

    // 存储用的ViewModel
    private PlayerStateViewModel player_state_view_model_;

    // 玩家的状态
    private PlayerState player_state_;

    private View root_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // TODO 美化一些变量的调用

        // 读取存储的数据
        player_state_view_model_ = new ViewModelProvider(requireActivity()).get(PlayerStateViewModel.class);
        player_state_ = player_state_view_model_.getSelectedItem().getValue();


        RecyclerView player_state_list_RV = root_.findViewById(R.id.player_state_list);


        // 初始化 Adapter
        PlayerListAdapter adapter = new PlayerListAdapter(player_state_,this.getContext());
        player_state_list_RV.setAdapter(adapter);

        GridLayoutManager gridLayoutManager= new GridLayoutManager(this.getContext(),2);
        player_state_list_RV.setLayoutManager(gridLayoutManager);

        adapter.setRecyclerItemClickListener(new PlayerListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                System.out.println(position);
            }
        });


        if (root_ == null)
        {
            root_ = inflater.inflate(R.layout.fragment_player_state_list,container,false);
        }

        return root_;
    }

}