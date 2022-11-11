package com.example.atry;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class GamePlayerListFragment extends Fragment {


    // 玩家的状态
    private RecyclerView player_state_list_RV_;
    private PlayerListAdapter adapter_;
    private Context context_;
    //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // TODO 美化一些变量的调用

        // 读取存储的数据
        View base_view = inflater.inflate(R.layout.fragment_player_state_list,container,false);
        context_ = base_view.getContext();

        //操作
        List<String>  visual_player_data_list= PlayerStateManager.visual_player_data_list_ ;

        player_state_list_RV_ = base_view.findViewById(R.id.player_state_list_recycler_view);
        // 初始化 Adapter
        adapter_ = new PlayerListAdapter(visual_player_data_list,this.getContext());
        player_state_list_RV_.setAdapter(adapter_);


        GridLayoutManager gridLayoutManager= new GridLayoutManager(context_,1);
        player_state_list_RV_.setLayoutManager(gridLayoutManager);


        adapter_.setRecyclerItemClickListener(position ->         player_state_manager_.getClick_process_player_button_().observe(getViewLifecycleOwner(), list -> {
            list.set(0,position);
            Log.i("List：",String.valueOf(list.get(0))+String.valueOf(list.get(1)));
        })
        );


        return base_view;
    }
    //---------------------------------------------------------------------------------------------
    //
    //      处理点击列表事件
    //
    //---------------------------------------------------------------------------------------------

    private PlayerStateManager player_state_manager_;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player_state_manager_ = new ViewModelProvider(requireActivity()).get(PlayerStateManager.class);

    }
}