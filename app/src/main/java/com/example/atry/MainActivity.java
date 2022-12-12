package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                Intent intent = new Intent(MainActivity.this, PlayerSettingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.play:
            {
                if(check_condition()) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.identity_setting:
            {
                Intent intent = new Intent(MainActivity.this, IdentitySettingActivity.class);
                startActivity(intent);
                break;
            }
        }
    }


    // 判断人数和身份的数量是否匹配
    private boolean check_condition()
    {
        SharedPreferences player_list_param_ = getSharedPreferences("player_list",MODE_PRIVATE);
        SharedPreferences identity_saved_param_ = getSharedPreferences("last_game_setting",MODE_PRIVATE);
        int player_num = player_list_param_.getAll().values().stream().collect(Collectors.summingInt(i-> (int) i));
        int identity_num = identity_saved_param_.getAll().values().stream().collect(Collectors.summingInt(i-> (int) i));
        // 确定身份是够用的
        Set<String> identity_list = identity_saved_param_.getAll().keySet();
        System.out.println(identity_list);
        Set<String> standard_identity_list = new HashSet<>();
        standard_identity_list.add("白狼王");
        standard_identity_list.add("狼人");
        standard_identity_list.add("平民");
        standard_identity_list.add("守卫");
        standard_identity_list.add("猎人");
        standard_identity_list.add("预言家");
        standard_identity_list.add("女巫");

        boolean condition_size = (player_num==identity_num);
        // 不满足条件
        if (!condition_size)
        {
            Toast.makeText(MainActivity.this, "身份配置与玩家数量不符，请重新配置", Toast.LENGTH_LONG).show();
        }

        boolean condition_identity = standard_identity_list.equals(identity_list);
        if (condition_identity){
            Toast.makeText(MainActivity.this, "开始游戏", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "角色种类暂不支持修改", Toast.LENGTH_LONG).show();
        }
        return condition_identity&&condition_size;//player_list_param_ = getSharedPreferences("")
    }
}

