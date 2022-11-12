package com.example.atry;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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
    private View root_;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root_ = inflater.inflate(R.layout.fragment_game_button_bar, container, false);

        confirm_button_ = (Button) root_.findViewById(R.id.game_confirm_button);
        confirm_button_.setOnClickListener(this);
        cancel_button_ = (Button) root_.findViewById(R.id.game_cancel_button);
        cancel_button_.setOnClickListener(this);

        player_state_manager_ = new ViewModelProvider(getActivity()).get(PlayerStateManager.class);

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
    }


    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.game_confirm_button:
            {
                PlayerStateManager.game_board_fragment_manager temp = player_state_manager_.get_game_board_fragment_manager_().getValue().update_stage();
                player_state_manager_.get_game_board_fragment_manager_().setValue(PlayerStateManager.game_board_fragment_manager.get_instance_game_board_fragment_manager_(temp));
            }
            case R.id.game_cancel_button:
            {
                break;
            }
        }
    }


}