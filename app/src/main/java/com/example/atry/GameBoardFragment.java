package com.example.atry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

    }


    private View view_;
    private TextView time_topic_;
    private TextView game_notice_board_;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view_ == null)
        {
            view_ = inflater.inflate(R.layout.fragment_game_notice_board, container,false);
        }

        game_notice_board_ = view_.findViewById(R.id.game_notice_board);
        time_topic_ = view_.findViewById(R.id.time_topic);
        return view_;
    }





    //---------------------------------------------------------------------------------------------
    //
    //      公告栏文本
    //
    //---------------------------------------------------------------------------------------------

    private final Map<Integer,Integer> game_state_2_string_text_ = new HashMap<>();

    private void init_game_state_2_string_text_(){
        game_state_2_string_text_.put(-1,R.string.text_rule);
        game_state_2_string_text_.put(0,R.string.text_night);
        game_state_2_string_text_.put(1,R.string.text_wolf);
        game_state_2_string_text_.put(2,R.string.text_guard);
        game_state_2_string_text_.put(3,R.string.text_witch);
        game_state_2_string_text_.put(4,R.string.text_prophet);
        game_state_2_string_text_.put(5,R.string.text_daytime);
        game_state_2_string_text_.put(6,R.string.text_exile);
        game_state_2_string_text_.put(7,R.string.text_white_wolf);
        game_state_2_string_text_.put(8,R.string.text_hunter);
    }


    public void update_text_content(List<Integer> game_board_fragment_data){
        String temp = "第" + game_board_fragment_data.get(0) + "天";
        if (game_board_fragment_data.get(1)==0)
        {
            temp += "夜晚";
        }
        else {
            temp += "白天";
        }
        time_topic_.setText(temp);
        game_notice_board_.setText(game_state_2_string_text_.get(game_board_fragment_data.get(3)));
    }

    //---------------------------------------------------------------------------------------------
    //
    //      LiveData的观察者
    //
    //---------------------------------------------------------------------------------------------
    private PlayerStateManager text_view_model_;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text_view_model_ = new ViewModelProvider(requireActivity()).get(PlayerStateManager.class);
        text_view_model_.get_game_board_fragment_data_().observe(getViewLifecycleOwner(), list -> {
            update_text_content(list);
        });
    }
}