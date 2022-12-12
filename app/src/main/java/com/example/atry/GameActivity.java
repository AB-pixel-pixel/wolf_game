package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

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
    private List<String> identity_str_list_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        assignRoles();
    }

    /**
     * 1. 读取保存的配置
     * 2. 分配角色,打乱身份和姓名的列表。将索引相同的属性绑定在一起。
     * 3. 初始化gameStateManager
     * 4. 建立监听机制
     */
    private void assignRoles()
    {

        // 读取存入的信息
        SharedPreferences identity_saved_param_ = getSharedPreferences("last_game_setting",MODE_PRIVATE);
        SharedPreferences player_list_param_ = getSharedPreferences("player_list",MODE_PRIVATE);


        // 存玩家名字
        List<String> player_name_list_ = new ArrayList<>(player_list_param_.getAll().keySet());
        // identity_str_list存放身份
        identity_str_list_ = new ArrayList<>();
        List<Integer> identity_int_list_;
        Map<String,Integer> identity_number_map = new HashMap<String,Integer>((Map<? extends String, ? extends Integer>) identity_saved_param_.getAll());
        identity_number_map.entrySet().forEach(entry->{
            for (int num = entry.getValue();num >0;num--)
            {
                identity_str_list_.add(entry.getKey());
            }
        });

        // 打乱两个list
        Collections.shuffle(player_name_list_);
        Collections.shuffle(identity_str_list_);

        // 角色的编码转换
        identity_int_list_ = identity_str2int(identity_str_list_);


        // 初始化管理器，并且
        gameStateManager game_state_manager = new ViewModelProvider(this).get(gameStateManager.class);
        // 使用整理后的数据初始化玩家信息（序号，身份等）
        game_state_manager.init(identity_int_list_, player_name_list_);
        // 通过监听机制建立的，游戏通知的发送器
        final Observer<Integer> message_to_user_observer = s -> Snackbar.make(findViewById(R.id.player_state_list_fragment), s,
                Snackbar.LENGTH_SHORT)
                .show();

        game_state_manager.get_message_data().observe(this,message_to_user_observer);

    }


    // 将identity: str->int，方便后续的比较和存储
    private List<Integer> identity_str2int(List<String> identity_str_list)
    {
        // TODO: 如果要拓展可用角色，需要在这里修改硬编码
        Map<String,Integer> identity2int_map = new HashMap<>();
        identity2int_map.put("白狼王",-2);
        identity2int_map.put("狼人",-1);
        identity2int_map.put("平民",0);
        identity2int_map.put("守卫",1);
        identity2int_map.put("猎人",2);
        identity2int_map.put("预言家",3);
        identity2int_map.put("女巫",4);

        List<Integer> identity_int_list = new ArrayList<>();
        for(int i =0;i < identity_str_list.size();i++)
        {
            String temp_str = identity_str_list.get(i);
            int temp_int = identity2int_map.getOrDefault(temp_str,-100);
            if (temp_int==-100){

            }
            identity_int_list.add(temp_int);
        }
        return identity_int_list;
    }

}
