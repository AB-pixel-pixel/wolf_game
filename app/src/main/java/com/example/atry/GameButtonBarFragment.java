package com.example.atry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class GameButtonBarFragment extends Fragment implements View.OnClickListener{


    public GameButtonBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //---------------------------------------------------------------------------------------------
    //
    //      注册UI
    //
    //---------------------------------------------------------------------------------------------

    private Button confirm_button_;
    private Button cancel_button_;
    private TextView candidate_;
    private View root_;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root_ = inflater.inflate(R.layout.fragment_game_button_bar, container, false);

        confirm_button_ = (Button) root_.findViewById(R.id.game_confirm_button);
        confirm_button_.setOnClickListener(this);
        cancel_button_ = (Button) root_.findViewById(R.id.game_cancel_button);
        cancel_button_.setOnClickListener(this);

        candidate_ = (TextView) root_.findViewById(R.id.choice_player);

        return root_;
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


    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.game_confirm_button:
            {
                player_state_manager_.getClick_process_player_button_().observe(getViewLifecycleOwner(), list -> {
                    int target_id =list.get(0);
                    String temp = "当前选中玩家"+ String.valueOf(target_id+1);
                    candidate_.setText(temp);
                });
                break;
            }
            case R.id.game_cancel_button:
            {
                // TODO 接受UI和manager的信息
                String temp = "当前未选中玩家";
                candidate_.setText(temp);
                break;
            }
        }
    }


}