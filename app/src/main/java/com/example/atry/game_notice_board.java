package com.example.atry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link game_notice_board#newInstance} factory method to
 * create an instance of this fragment.
 */
public class game_notice_board extends Fragment {

    private View root_;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root_ == null)
        {
            root_ = inflater.inflate(R.layout.fragment_game_notice_board, container,false);
        }

        return root_;

    }
}