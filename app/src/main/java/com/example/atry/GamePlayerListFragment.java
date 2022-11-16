package com.example.atry;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class GamePlayerListFragment extends Fragment {


    // 玩家的状态
    private RecyclerView player_state_list_RV_;
    private ListAdapter adapter_;
    private Context context_;
    private TextView picked_player_text_view_;
    //
    List<String> visual_player_data_list_;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private MutableLiveData<Integer> player_pick_manager_;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // TODO 美化一些变量的调用

        View base_view = inflater.inflate(R.layout.fragment_player_state_list,container,false);

        // 读取存储的数据
        player_state_manager_vm_ = new ViewModelProvider(getActivity()).get(PlayerStateManager.class);
        visual_player_data_list_= player_state_manager_vm_.get_player_list_fragment().getVisual_player_data_list_();


        player_state_list_RV_ = base_view.findViewById(R.id.player_state_list_recycler_view);

        // 注册TextView
        picked_player_text_view_ = (TextView) base_view.findViewById(R.id.choice_player);

        // 初始化 Adapter
        adapter_ = new ListAdapter(visual_player_data_list_,this.getContext());
        player_state_list_RV_.setAdapter(adapter_);


        // 监听玩家死亡名单
        final MutableLiveData<Map<Integer,String>> death_map = player_state_manager_vm_.get_death_list_data_();
        final Observer<Map<Integer,String> > death_list_observer  = new Observer<Map<Integer, String>>() {
            @Override
            public void onChanged(Map<Integer, String> integerStringMap) {
                integerStringMap.entrySet().forEach(integerStringEntry -> {
                    int id = integerStringEntry.getKey();
                    String content = integerStringEntry.getValue();
                    visual_player_data_list_.set(id,content);
                    adapter_.notifyItemChanged(id);
                });
            }
        };

        death_map.observe(getActivity(),death_list_observer);

        // RecyclerView的布局
        GridLayoutManager gridLayoutManager= new GridLayoutManager(base_view.getContext(),1);
        player_state_list_RV_.setLayoutManager(gridLayoutManager);

        player_pick_manager_ = player_state_manager_vm_.getPicked_player_();
        adapter_.setRecyclerItemClickListener(position ->{
                player_pick_manager_.setValue(position);
                }
        );

        final Observer<Integer> picked_player_observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer==-1)
                {
                    picked_player_text_view_.setText(R.string.candidate);
                }
                else
                {
                    integer++;
                    picked_player_text_view_.setText("当前选中"+integer+"号位玩家");
                }
            }
        };

        player_pick_manager_.observe(getActivity(),picked_player_observer);

        return base_view;
    }
    //---------------------------------------------------------------------------------------------
    //
    //      处理点击列表事件
    //
    //---------------------------------------------------------------------------------------------

    private PlayerStateManager player_state_manager_vm_;


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player_state_manager_vm_ = new ViewModelProvider(requireActivity()).get(PlayerStateManager.class);

    }
}