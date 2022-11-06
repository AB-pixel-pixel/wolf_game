package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
//implements View.OnClickListener
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 游戏开局
        Button play_button = (Button)findViewById(R.id.play);
        play_button.setOnClickListener(this);
        // 身份配置
        Button identity_button = (Button)findViewById(R.id.identity_setting);
        identity_button.setOnClickListener(this);
        // 玩家配置
        Button player_list_button = (Button)findViewById(R.id.players_list);
        player_list_button.setOnClickListener(this);



    }

    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.players_list:
            {
                Intent intent = new Intent(MainActivity.this, player_list_setting.class);
                startActivity(intent);
                break;
            }
            case R.id.play:
            {
                Toast.makeText(MainActivity.this,"身份配置与玩家数量不符，请重新配置",Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.identity_setting:
            {
                Intent intent = new Intent(MainActivity.this, identity_setting.class);
                startActivity(intent);
                break;
            }
        }
    }
}