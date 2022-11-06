package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class identity_setting extends AppCompatActivity implements View.OnClickListener {
    // 用于存放配置的字典
    private Map<String, Integer> identity_map_param_;
    private List<String> identity_list_param_;
    private ArrayAdapter identity_adapter_;

    // 对识图进行注册
    private EditText identity_input_;
    private EditText identity_number_input_;
    private ListView identity_list_view_;
    private Button ensure_identity_button_;
    private final boolean test = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_setting);




        // 身份配置
        identity_map_param_ = new HashMap<>();
        // 读取默认配置
        if (test)
        {
            //  identity_map_param_.put
            identity_map_param_.put("狼人",3);
            identity_map_param_.put("平民",2);
            identity_map_param_.put("预言家",1);
            identity_map_param_.put("猎人",1);
            identity_map_param_.put("女巫",1);
        }


        // 注册editText
        identity_input_ = (EditText) findViewById(R.id.identity_input);
        identity_number_input_ = (EditText) findViewById(R.id.identity_number_input);


        // 注册“确定”按钮
        ensure_identity_button_ =(Button) findViewById(R.id.identity_ensure);
        ensure_identity_button_.setOnClickListener(this);

        // 将map配置转到list配置
        identity_list_param_ = new ArrayList<>();

        identity_map_param_.entrySet().forEach(entry->{
            identity_list_param_.add(entry.getKey());
            identity_list_param_.add(entry.getValue().toString());
        });

        identity_adapter_ = new ArrayAdapter<String>(identity_setting.this,android.R.layout.simple_list_item_1,
                identity_list_param_);


        // 注册ListView
        identity_list_view_ = (ListView) findViewById(R.id.identity_list_view);
        identity_list_view_.setAdapter(identity_adapter_);
        //identity_list_view_.setOnItemClickListener(new AdapterView.OnItemClickListener()


    }



    // 控制点击
    public void onClick(View v)
    {
        // 检查相关属性是否合理
        // 添加相关属性到map中

    }
    // 点击确定的事件
    private void identity_ensure_click()
    {
        // 获取元素
        String identity;
        int number;
        identity = identity_input_.getText().toString();
        number = Integer.parseInt(identity_number_input_.getText().toString());
        // 检查输入是否合法
        if (identity.equals("")||number<=0)
        {
            Toast.makeText(identity_setting.this,"配置添加错误，输入身份为空或者数量不符合", Toast.LENGTH_SHORT).show();
        }
        else
        {
            identity_map_param_.put(identity,number);

        }

    }
}