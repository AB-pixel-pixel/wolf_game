package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 与Fragment通信
class PlayerStateViewModel extends ViewModel{
    private final MutableLiveData<PlayerState> saved_player_state_ = new MutableLiveData<PlayerState>();

    public void selectItem(PlayerState player_state){
        saved_player_state_.setValue(player_state);
    }

    public LiveData<PlayerState> getSelectedItem(){
        return saved_player_state_;
    }
}



public class game extends AppCompatActivity {
    // 关于身份和玩家的列表，以索引判断身份和玩家
    private List<String> player_str_list_;
    private List<Integer> identity_int_list_;
    private List<String> identity_str_list_;

    // 玩家状态（数据库）
    private PlayerState player_state_;

    // 关联
    private PlayerStateViewModel saved_player_state_;

    // 图标关联输入
    private int click_id_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
    }

    private void init()
    {
        // 使用SharedPreferences存储身份配置
        SharedPreferences identity_saved_param_ = getSharedPreferences("last_game_setting",MODE_PRIVATE);
        SharedPreferences player_list_param_ = getSharedPreferences("player_list",MODE_PRIVATE);

        // player_name存玩家名字，identity_str_list存放身份
        player_str_list_ = new ArrayList<String>(player_list_param_.getAll().keySet());
        System.out.println(player_str_list_);
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

        // 使用整理后的数据初始化玩家信息（序号，身份等）
        player_state_ = new PlayerState(identity_int_list_,player_str_list_);

        // 将玩家信息放入内存，方便和fragment交流
        saved_player_state_ = new ViewModelProvider(this).get(PlayerStateViewModel.class);
        saved_player_state_.selectItem(player_state_);
//        saved_player_state_.getSelectedItem().observe(this,item->{
//            // Perform an action with the latest item data
//
//        });
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
















//@Entity
//class player{
//    @PrimaryKey
//    public int id_;
//
//    public String name_;
//    public String identity_;
//    public String status_;
//    public String
//
//}