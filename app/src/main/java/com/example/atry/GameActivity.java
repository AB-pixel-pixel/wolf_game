package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//---------------------------------------------------------------------------------------------
//
//      GameActivity的内容
//
//---------------------------------------------------------------------------------------------



public class GameActivity extends AppCompatActivity {
    private static final boolean TEST = false;
    // 关于身份和玩家的列表，以索引判断身份和玩家
    private List<String> player_str_list_;
    private List<Integer> identity_int_list_;
    private List<String> identity_str_list_;

    // 玩家状态（数据库）
    private PlayerStateManager player_state_manager_;
    private GamePlayerListFragment plyaer_state_list_fragment_;

    // 图标关联输入
    private int click_id_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
        plyaer_state_list_fragment_ = new GamePlayerListFragment(); // 实例化Fragment
        getSupportFragmentManager().beginTransaction().add(R.id.player_state_list_fragment, plyaer_state_list_fragment_,"").commitAllowingStateLoss(); // 将fragment添加到activity

    }

    private void init()
    {

        // 使用SharedPreferences存储身份配置
        SharedPreferences identity_saved_param_ = getSharedPreferences("last_game_setting",MODE_PRIVATE);
        SharedPreferences player_list_param_ = getSharedPreferences("player_list",MODE_PRIVATE);

        // player_name存玩家名字，identity_str_list存放身份
        player_str_list_ = new ArrayList<String>(player_list_param_.getAll().keySet());
        identity_str_list_ = new ArrayList<>();
        identity_int_list_ = new ArrayList<>();
        Map<String,Integer> identity_number_map = new HashMap<String,Integer>((Map<? extends String, ? extends Integer>) identity_saved_param_.getAll());
        identity_number_map.entrySet().forEach(entry->{
            for (int num = entry.getValue();num >0;num--)
            {
                identity_str_list_.add(entry.getKey());
            }
        });

        // 将STRING 转为int
        // TODO: 如果要拓展程序，需要在这里修改硬编码
        Map<String,Integer> identity2int_map = new HashMap<>();
        identity2int_map.put("白狼王",-2);
        identity2int_map.put("狼人",-1);
        identity2int_map.put("平民",0);
        identity2int_map.put("守卫",1);
        identity2int_map.put("猎人",2);
        identity2int_map.put("预言家",3);
        identity2int_map.put("女巫",4);

        // 打乱两个list
        Collections.shuffle(player_str_list_);
        Collections.shuffle(identity_str_list_);

        // 转换
        identity_int_list_ = identity_str2int(identity2int_map,identity_str_list_);
        // TEST
        if (TEST)
        {
            for (String i : player_str_list_) {
                Log.i("检查player", i);
            }
        }


        player_state_manager_ = new ViewModelProvider(this).get(PlayerStateManager.class);
        // 使用整理后的数据初始化玩家信息（序号，身份等）
        player_state_manager_.init(identity_int_list_,player_str_list_);

    }


    // 将identity: str->int，方便后续的比较和存储
    private List<Integer> identity_str2int(Map<String, Integer> identity2int, List<String> identity_str_list)
    {
        List<Integer> identity_int_list = new ArrayList<>();
        for(int i =0;i < identity_str_list.size();i++)
        {
            String temp_str = identity_str_list.get(i);
            int temp_int = identity2int.get(temp_str);
            identity_int_list.add(temp_int);
        }
        return identity_int_list;
    }


    public List<String> getPlayerStateList(){
        return player_str_list_;
    }

    public void set_click_id_(int id)
    {
        click_id_ = id;
    }
}
