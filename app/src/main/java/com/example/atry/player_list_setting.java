package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class player_list_setting extends AppCompatActivity implements View.OnClickListener {
    private EditText player_name_input_;
    private ArrayAdapter adapter_;
    ListView player_list_view_;

    private List<String> player_list_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list_setting);

        player_list_ = new ArrayList<>();
        // 注册editText
        player_name_input_ = (EditText) findViewById(R.id.player_name_input);
        adapter_ = new ArrayAdapter<String>(player_list_setting.this,
                android.R.layout.simple_list_item_1, player_list_);

        // 注册“确定”按钮
        Button save_player_button =(Button) findViewById(R.id.save_player_name);
        save_player_button.setOnClickListener(this);

        // 注册ListView
        player_list_view_ = (ListView) findViewById(R.id.player_list_view);
        player_list_view_.setAdapter(adapter_);
        player_list_view_.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view, int position, long id)
            {

                // TODO： 使用缓存等机制优化程序，减少开销

                // 删除元素
                String player_name_text = player_list_.get(position);
                Toast.makeText(player_list_setting.this,player_name_text+"删除成功", Toast.LENGTH_SHORT).show();
                player_list_.remove(position);
                adapter_ = new ArrayAdapter(player_list_setting.this, android.R.layout.simple_list_item_1, player_list_);

                // 刷新页面
                adapter_.notifyDataSetChanged();
                player_list_view_.invalidateViews();
                player_list_view_.refreshDrawableState();
            }
        });
    }

    protected void onDestroy(){
        super.onDestroy();
        save(player_list_);
    }

    private void save(List<String> player_list){
        FileOutputStream out =null;
        BufferedWriter writer = null;
        try{
            out = openFileOutput("player_list", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            for (String data:player_list)
            {
                writer.write(data);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
                }
        }

    }


    public void onClick(View v)
    {
        switch ((v.getId()))
        {
            case R.id.save_player_name:
            {
                add_player(player_name_input_);
                player_name_input_.setText("");
                break;
            }
            //TODO delete player name
        }
    }



    public void add_player(EditText player_name_input)
    {
        String player_name_text = player_name_input_.getText().toString();
        player_list_.add(player_name_text);
        Toast.makeText(player_list_setting.this,player_name_text+"添加成功", Toast.LENGTH_SHORT).show();
    }
}