package com.example.atry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class GameButtonBarFragment extends Fragment implements View.OnClickListener{


    public GameButtonBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


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


        return root_;
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.game_confirm_button:
            {
                // TODO 接受UI和manager的信息
                break;
            }
            case R.id.game_button_bar_fragment:
            {
                // TODO 接受UI和manager的信息
                break;
            }
        }
    }
}