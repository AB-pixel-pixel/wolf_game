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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentitySettingActivity extends AppCompatActivity implements View.OnClickListener {
    // 用于存放配置的字典
    private SharedPreferences identity_saved_param_;
    private SharedPreferences.Editor identity_saved_param_editor_;

    // identity_map_ 作为存储preferences的中介，与数据增删改查直接接触, key 为身份（String）,value为身份的数量（int）
    private Map<String,Integer> identity_map_;
    // 用于存放ListView的文本内容，直接与Adapter交互，内容排序与identity_keys
    private List<String> identity_visual_list;
    // identity_keys_ identity_map_的 key_set 作为排序与identity_visual_list
    private List<String> identity_keys_;

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
        init();
    }

    protected void onDestroy() {
        super.onDestroy();
        save_identity_list();
    }

    // 注册各种组件、初始化变量
    private void init()
    {
        // 注册editText
        identity_input_ = (EditText) findViewById(R.id.identity_input);
        identity_number_input_ = (EditText) findViewById(R.id.identity_number_input);


        // 注册“确定”按钮
        ensure_identity_button_ =(Button) findViewById(R.id.identity_ensure);
        ensure_identity_button_.setOnClickListener(this);

        // 使用SharedPreferences存储身份配置
        identity_saved_param_ = getSharedPreferences("last_game_setting",MODE_PRIVATE);

        identity_saved_param_editor_ = identity_saved_param_.edit();
        // 测试用：标准12人局配置
        if (test)
        {
            //  12人局标准配置：预言家、女巫、猎人、守卫、4个平民、3个普通狼人以及白狼王
            // 参考：https://baijiahao.baidu.com/s?id=1738220641116983806
            identity_saved_param_editor_.putInt("狼人",3);
            identity_saved_param_editor_.putInt("平民",4);
            identity_saved_param_editor_.putInt("预言家",1);
            identity_saved_param_editor_.putInt("猎人",1);
            identity_saved_param_editor_.putInt("女巫",1);
            identity_saved_param_editor_.putInt("白狼王",1);
            identity_saved_param_editor_.putInt("守卫",1);
            identity_saved_param_editor_.commit();
        }

        // 初始化 辅助身份查重的列表
        identity_keys_ = new ArrayList<>();

        // 初始化为可视化准备的参数
        identity_visual_list = new ArrayList<>();
        identity_map_ = new HashMap<String,Integer>((Map<? extends String, ? extends Integer>) identity_saved_param_.getAll());

        identity_map_.entrySet().forEach(entry->{
            identity_visual_list.add(entry.getKey()+"    " + entry.getValue()+"位");
            identity_keys_.add(entry.getKey());
        });


        // 用列表初始化 adapter
        identity_adapter_ = new ArrayAdapter<String>(IdentitySettingActivity.this,android.R.layout.simple_list_item_1,
                identity_visual_list);


        // 注册ListView
        identity_list_view_ = (ListView) findViewById(R.id.identity_list_view);
        identity_list_view_.setAdapter(identity_adapter_);
        // 点击就删除
        identity_list_view_.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view, int position, long id)
            {

                // TODO： 使用缓存等机制优化程序，减少开销
                // TODO： 将删除的逻辑写入同一个函数
                // 删除元素
                String identity_key = identity_visual_list.get(position);
                Toast.makeText(IdentitySettingActivity.this,identity_key+"删除成功", Toast.LENGTH_SHORT).show();
                identity_visual_list.remove(position);
                identity_adapter_ = new ArrayAdapter(IdentitySettingActivity.this, android.R.layout.simple_list_item_1, identity_visual_list);


                // 刷新页面
                identity_adapter_.notifyDataSetChanged();
                identity_list_view_.invalidateViews();
                identity_list_view_.refreshDrawableState();

                // 配套删除keys 和 map的内容
                String temp_key = identity_keys_.get(position);
                identity_map_.remove(temp_key);
                identity_keys_.remove(position);
            }
        });
    }

    // 控制点击
    public void onClick(View v)
    {
        identity_ensure_click();
    }
    // 点击确定的事件
    private void identity_ensure_click()
    {
        // 获取元素
        String identity;
        String str_number;
        identity = identity_input_.getText().toString();
        str_number = identity_number_input_.getText().toString();
        // 检查输入是否合法
        if (identity.equals("")||str_number.equals(""))
        {
            // 不合法的元素
            Toast.makeText(IdentitySettingActivity.this,"配置添加错误，身份或者数量输入为空", Toast.LENGTH_SHORT).show();
        }
        else
        {
            int number = Integer.parseInt(str_number);
            // 添加相关属性到map中
            if (identity_map_.containsKey(identity))
            {
                // 关于身份重复的操作
                // 先删除
                int index = identity_keys_.indexOf(identity);
                identity_visual_list.remove(index);
                // 后插入
                identity_visual_list.add(index,identity+"   "+str_number+"位");

                // 刷新页面
                identity_adapter_ = new ArrayAdapter(IdentitySettingActivity.this, android.R.layout.simple_list_item_1, identity_visual_list);
                identity_adapter_.notifyDataSetChanged();
                identity_list_view_.invalidateViews();
                identity_list_view_.refreshDrawableState();
            }
            else
            {
                // 无身份重复的操作
                identity_visual_list.add(str_number+identity);
                identity_keys_.add(identity);
            }

            identity_map_.put(identity,number);
        }
        // 清空输入框
        identity_input_.setText("");
        identity_number_input_.setText("");
    }


    private void save_identity_list()
    {
        identity_saved_param_editor_.clear();
        identity_map_.entrySet().forEach(entry->{
            identity_saved_param_editor_.putInt(entry.getKey(),entry.getValue());
        });
        identity_saved_param_editor_.commit();
    }


}