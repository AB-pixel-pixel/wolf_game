package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PlayerSettingActivity extends AppCompatActivity implements View.OnClickListener {
    // 存储姓名信息
    private SharedPreferences player_list_param_;
    private SharedPreferences.Editor player_list_param_editor_;
    // UI的组件
    private EditText player_name_input_;
    private ArrayAdapter adapter_;
    ListView player_list_view_;
    // 放置玩家名单
    private List<String> player_list_;

    // 填充测试用的数据
    private boolean TEST = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list_setting);
        init_();

    }

    protected void onDestroy(){
        super.onDestroy();
        // 保存数据
        save_player_list();
    }


    // 初始化成员和注册组件
    private void init_()
    {
        // 使用SharedPreferences存储配置
        player_list_param_ = getSharedPreferences("player_list",MODE_PRIVATE);
        player_list_param_editor_ = player_list_param_.edit();


        // 用12人局的数据作测试，此处进行数据填充
        if(TEST)
        {
            player_list_param_editor_.putInt("玩家1",1);
            player_list_param_editor_.putInt("玩家2",1);
            player_list_param_editor_.putInt("玩家3",1);
            player_list_param_editor_.putInt("玩家4",1);
            player_list_param_editor_.putInt("玩家5",1);
            player_list_param_editor_.putInt("玩家6",1);
            player_list_param_editor_.putInt("玩家7",1);
            player_list_param_editor_.putInt("玩家8",1);
            player_list_param_editor_.putInt("玩家9",1);
            player_list_param_editor_.putInt("玩家10",1);
            player_list_param_editor_.putInt("玩家11",1);
            player_list_param_editor_.putInt("玩家12",1);
            player_list_param_editor_.commit();
        }

        // 注册editText
        player_name_input_ = (EditText) findViewById(R.id.player_name_input);

        // 注册“确定”按钮
        Button save_player_button =(Button) findViewById(R.id.save_player_name);
        save_player_button.setOnClickListener(this);

        // 初始化玩家名单及其adapter,为ListView准备
        player_list_ =new ArrayList<String>(player_list_param_.getAll().keySet());

        adapter_ = new ArrayAdapter<String>(PlayerSettingActivity.this,
                android.R.layout.simple_list_item_1, player_list_);

        // 注册ListView
        player_list_view_ = (ListView) findViewById(R.id.player_list_view);
        player_list_view_.setAdapter(adapter_);

        // 点击就删除
        player_list_view_.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view, int position, long id)
            {

                // TODO： 使用缓存等机制优化程序，减少开销
                // 删除元素
                String player_name_text = player_list_.get(position);
                Toast.makeText(PlayerSettingActivity.this,player_name_text+"删除成功", Toast.LENGTH_SHORT).show();
                player_list_.remove(position);
                adapter_ = new ArrayAdapter(PlayerSettingActivity.this, android.R.layout.simple_list_item_1, player_list_);

                // 刷新页面
                adapter_.notifyDataSetChanged();
                player_list_view_.invalidateViews();
                player_list_view_.refreshDrawableState();
            }
        });
    }

    public void onClick(View v)
    {
        save_player_name_click();
    }

    public void save_player_name_click()
    {
        String player_name_text = player_name_input_.getText().toString();
        // 判断空输入
        if (player_name_text.equals(""))
        {
            // 不合法的元素
            Toast.makeText(PlayerSettingActivity.this,"配置添加错误，输入为空", Toast.LENGTH_SHORT).show();
        }
        else if(player_list_.contains(player_name_text))
        {
            Toast.makeText(PlayerSettingActivity.this,"该名字已经输入过了", Toast.LENGTH_SHORT).show();
        }
        else
        {
            player_list_.add(player_name_text);
            Toast.makeText(PlayerSettingActivity.this,player_name_text+"添加成功", Toast.LENGTH_SHORT).show();
        }
        player_name_input_.setText("");
    }

    public void save_player_list()
    {
        // 清空之前的设置
        player_list_param_editor_.clear();
        for(String key:player_list_)
        {
            player_list_param_editor_.putInt(key,1);
        }
        player_list_param_editor_.commit();
    }
}

