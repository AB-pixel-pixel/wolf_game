package com.example.atry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameBoardFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_game_stage_2_string_text_();
    }

    private View view_;
    private TextView time_topic_;
    private TextView game_notice_board_;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view_ == null)
        {
            view_ = inflater.inflate(R.layout.fragment_game_notice_board, container,false);
        }


        game_notice_board_ = view_.findViewById(R.id.game_notice_board);
        time_topic_ = view_.findViewById(R.id.time_topic);

        // 将数据库的东西和UI绑定
        player_state_manager_ = new ViewModelProvider(getActivity()).get(PlayerStateManager.class);

        final MutableLiveData<PlayerStateManager.game_board_fragment_manager>  player_state_manager__game_board_fragment_manager_ = player_state_manager_.get_game_board_fragment_manager_();

        // Create the observer which updates the UI.
        final Observer<PlayerStateManager.game_board_fragment_manager> board_fragment_observer = new Observer<PlayerStateManager.game_board_fragment_manager>() {
            @Override
            public void onChanged(PlayerStateManager.game_board_fragment_manager game_board_fragment_manager) {
                int temp = game_board_fragment_manager.get_game_stage_now();
                game_stage_2_game_notice_board(temp);
                time_topic_.setText(date_2_time_topic(game_board_fragment_manager.get_date_()));
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        player_state_manager_.get_game_board_fragment_manager_().observe(getActivity(), board_fragment_observer);

        return view_;
    }





    //---------------------------------------------------------------------------------------------
    //
    //      公告栏文本
    //
    //---------------------------------------------------------------------------------------------

    private final Map<Integer,Integer> game_stage_2_string_text_ = new HashMap<>();
    // TODO 规范化阶段
    private void game_stage_2_game_notice_board(int stage){
        if (stage ==3)
        {
            int killed_id = player_state_manager_.getNight_states_().get(0) +1;
            String temp = "女巫请睁眼。今晚他（"+String.valueOf(killed_id)+"号位玩家）死了，你要救他吗？(点击取消，表示不救，点击确定表示救)";
            game_notice_board_.setText(temp);
        }
        else
        {
            game_notice_board_.setText(game_stage_2_string_text_.get(stage));
        }
    }

    private void init_game_stage_2_string_text_(){
        game_stage_2_string_text_.put(-1,R.string.text_rule);
        game_stage_2_string_text_.put(0,R.string.text_night);
        game_stage_2_string_text_.put(1,R.string.text_wolf);
        game_stage_2_string_text_.put(2,R.string.text_guard);
        game_stage_2_string_text_.put(4,R.string.text_witch);
        game_stage_2_string_text_.put(5,R.string.text_prophet);
        game_stage_2_string_text_.put(6,R.string.text_daytime);
        game_stage_2_string_text_.put(7,R.string.good_win);
        game_stage_2_string_text_.put(8,R.string.bad_win);
    }


    public String date_2_time_topic(List<Integer> game_board_fragment_data){
        int time = game_board_fragment_data.get(0);
        String temp= "";
        if (time != -1)
        {
            temp = "第" + time + "天";
            if (game_board_fragment_data.get(1) == 0) {
                temp += "夜晚";
            } else {
                temp += "白天";
            }
        }
        else
        {
            temp = "游戏准备阶段";
        }
        return(temp);
    }

    //---------------------------------------------------------------------------------------------
    //
    //      LiveData的观察者
    //
    //---------------------------------------------------------------------------------------------
    private PlayerStateManager player_state_manager_;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}