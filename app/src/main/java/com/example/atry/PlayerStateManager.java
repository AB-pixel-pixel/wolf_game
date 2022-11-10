package com.example.atry;



import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStateManager {
    // TEST
    private final String TAG =  "player_msg";
    private final boolean TEST = true;

    // 用于转换数据的Map
    public final static Map<Integer,String> int2identity_map = new HashMap<>();
    public final static Map<Integer,String> int2state_map = new HashMap<>();
    // 存放各种数据的列表
    private static List<civilians> role_list_;
    private static List<Integer> night_states_;
    public static List<String> visual_player_data_list_;

    // 记录游戏状态以及游戏阶段的列表
    private int game_stage_now_;

    //游戏阶段，-1未开始游戏，0进入夜晚，1 狼人刀人，2 女巫使用能力，
    // 3 守卫使用能力，4 预言家验身份, 5 遗言，6发言， 7放逐， 8上警
    // 9 狼人自爆 10 猎人使用能力
    private List<Integer> game_stages_;

    private int[] date_; // 记录时间，首元素是天数，第二个元素是当天的时间（夜晚，白天）。


    // 记录公告栏内容的文本,与game_stages_搭配使用
    public static List<String> board_text_;// 各种阶段的文字提示以及说明，比如：遗言环节，发言环节，放逐环节，上警环节

    public PlayerStateManager(List<Integer> identity_list, List<String> player_str_list){


        // 构造数据转换的map
        init_translation_map();

        // 填充可视化数据并报告
        init_visual_player_data_list(player_str_list);

        // 初始化角色
        init_role_list(identity_list,player_str_list);
        // 初始化游戏阶段
        init_game_stage();
        // 初始化文本内容
        init_board_text();

        // 初始化开局情况
        // 初始化日期
        date_ = new int[2];
        date_[0] = 1;
        date_[1] = 1;

        // 初始化晚上的情况
        night_states_= new ArrayList<>();
        night_states_.add(-1);
        night_states_.add(-1);
        night_states_.add(-1);
        night_states_.add(-1);

    }// 结束构造函数

    public void start(){

    }

    public void next(){

    }




    private void init_translation_map() {
        // 方便数字转为可视化的字符(身份）
        int2identity_map.put(-2,"狼人");
        int2identity_map.put(-1,"狼人");
        int2identity_map.put(0,"平民");
        int2identity_map.put(1,"守卫");
        int2identity_map.put(2,"猎人");
        int2identity_map.put(3,"预言家");
        int2identity_map.put(4,"女巫");
        // 方便数字转为可视化的字符（生命状态）
        int2state_map.put(0,"普通出局");
        int2state_map.put(1,"活着");
        int2state_map.put(2,"毒死");
        int2state_map.put(3,"狼王自爆");

    }

    private void init_role_list(List<Integer> identity_list,List<String> player_str_list){
        // 设置角色身份
        role_list_ = new ArrayList<>();
        for (int i =0;i<player_str_list.size();i++)
        {
            int identity_num = identity_list.get(i);
            String player_name = player_str_list.get(i);
            switch (identity_num){
                case -2:
                {
                    civilians temp = new white_wolf_king(i,player_name,identity_num,1);
                    role_list_.add(temp);
                    break;
                }
                case -1:
                {
                    civilians temp = new wolf(i,player_name,identity_num, 1);
                    role_list_.add(temp);
                    break;
                }
                case 0:
                {
                    civilians temp = new civilians(i,player_name,identity_num, 1);
                    role_list_.add(temp);
                    break;
                }
                case 1:
                {
                    civilians temp = new guards(i,player_name,identity_num, 1);
                    role_list_.add(temp);
                    break;
                }
                case 2:
                {
                    civilians temp = new hunter(i,player_name,identity_num, 1);
                    role_list_.add(temp);
                    break;
                }
                case 3:
                {
                    civilians temp = new prophet(i,player_name,identity_num, 1);
                    role_list_.add(temp);
                    break;
                }
                case 4:
                {
                    civilians temp = new witch(i,player_name, identity_num, 1);
                    role_list_.add(temp);
                    break;
                }
            }
        }
    }

    private void init_visual_player_data_list(List<String> player_str_list){
        // 用于可视化玩家信息的列表
        visual_player_data_list_ = new ArrayList<>();
        for (int i =0;i<player_str_list.size();i++)
        {
            String temp_data = role_list_.get(i).toVisualText();
            visual_player_data_list_.add(temp_data);
        }
    }

    private void init_game_stage(){
        game_stages_ = new ArrayList<>();
        for (int i = -1;i<=8;i++)
        {
            game_stages_.add(i);
        }
        game_stage_now_ = -1;
    }


    private void init_board_text(){
        board_text_ =new ArrayList<>();
        String temp = "根据公告栏的游戏进程安排，通过点击列表中的玩家名称确定目标，“下一阶段”表示跳过当前环节。“发动能力”只有在相应的游戏阶段才能使用。\n点击确定开始游戏！";
        board_text_.add(temp);
        temp = "夜晚请闭眼";
        board_text_.add(temp);
        temp = "狼人请睁眼，指出你要刀的人，狼人请闭眼";
        board_text_.add(temp);
        temp = "女巫请睁眼，昨晚有人死了，你有一瓶解药，要救吗？你有一瓶毒药要用吗？毒谁？好的，女巫请闭眼";
        board_text_.add(temp);
        temp = "守卫请睁眼，今晚你守卫哪个人。好的，守卫请闭眼";
        board_text_.add(temp);
        temp ="预言家请睁眼，选择一位玩家查验身份，好人是向上，坏人是向下，他的身份是这个，预言家请闭眼。";
        board_text_.add(temp);
        temp = "昨晚死的是他，请发表遗言";
        board_text_.add(temp);
        temp = "请开始发言。发言完毕，进行公投。";
        board_text_.add(temp);
        temp = "放逐（点击玩家名称后确定则放逐他，若无人被放逐，则点击下一阶段）";
        board_text_.add(temp);
        temp = "竞选警徽的请举手，请发言，没有上警的玩家请投票。最后请警长选择发言顺序";
        board_text_.add(temp);
        temp = "白狼王自爆，打断了上警，直接进入黑夜";
        board_text_.add(temp);
        temp = "白狼王自爆，打断了发言，直接进入黑夜";
        board_text_.add(temp);
        temp = "猎人，你要带走谁";
        board_text_.add(temp);
    }


    public List<String> getVisual_player_data_list_()
    {
        return visual_player_data_list_;
    }
}

class civilians{
    // 玩家的序号
    protected int id_;
    // 玩家的名字
    protected String name_;

    // 以数字代指身份
    // "白狼王"->-2 || "狼人"->-1 ||  "平民"->0
    // "守卫"->1  ||  "猎人"->2  ||  "预言家"->3
    // "女巫"->4
    protected int identity_;
    // 存储同索引的玩家角色的生命状态，0 为普通出局， 1位或者， 2为特有能力杀害（毒杀或者白狼王自爆带走）
    protected int state_;

    public civilians(int id,String name,int identity,int state)
    {
        // 玩家从1号开始
        id_ = id+1;
        name_ = name;
        identity_ = identity;
        state_ = state;
    }

    // 返回可视化的文本数据
    protected String toVisualText()
    {
        String visual_identity = PlayerStateManager.int2identity_map.get(identity_);
        String visual_state = PlayerStateManager.int2state_map.get(state_);
        return id_ + "号位   "+name_+"   "+visual_identity+"   "+visual_state;
    }
}

// night_states 用于记录夜晚的情况，分别是，狼人指杀，女巫救药，女巫毒杀，守卫的守护

interface wolf_ability{
    public void Kill(int target_id,int[] night_states);
}

class wolf extends civilians implements wolf_ability{
    wolf(int id,String name,int identity,int state){
        super(id,name,identity,state);
    }
    @Override
    public void Kill(int target_id, int[] night_states)
    {
        night_states[0] = target_id;
    }
}

interface witch_ability{
    public boolean hasPoison();
    public void poison(int target_id, int[] night_states);
    public boolean hasCure();
    public void cure(int target_id,int[] night_states);
}

class witch extends  civilians implements witch_ability{
    private int poison_number_;
    private int cure_number_;
    witch(int id,String name,int identity,int state)
    {
        super(id,name,identity, state);
        poison_number_  = 1;
        cure_number_ = 1;
    }

    @Override
    public boolean hasPoison() {
        return poison_number_ > 0;
    }

    @Override
    public void poison(int target_id, int[] night_states) {
        night_states[1] = -1;
        night_states[2] = target_id;
        poison_number_--;
    }

    @Override
    public boolean hasCure() {
        return cure_number_>0;
    }

    @Override
    public void cure(int target_id, int[] night_states) {
        night_states[1] = target_id;
        night_states[2] = -1;
        cure_number_--;
    }
}


interface prophet_ability{
    public int isWolf(int id,List<Integer> id_identity_list, List<Integer> id_states_list);
}

class prophet extends civilians implements  prophet_ability{
    prophet (int id,String name,int identity,int state) {
        super(id, name, identity, state);
    }

    @Override
    public int isWolf(int id,List<Integer> id_identity_list, List<Integer> id_states_list) {
        if (id_states_list.get(id)==1)
        {
            if (id_identity_list.get(id)>0)
            {
                return 0;
            }
            else
                return 1;
        }
        // 人都死了，验不了，要重新验
        return -1;
    }
}

interface perish_together_skill {
    // 自爆技能
    public void perish_together(int target_id,List<Integer> id_states_list);
    // 能否使用技能
    public boolean perish_together_condition(int game_states);
}

class white_wolf_king extends wolf implements perish_together_skill {
    public white_wolf_king(int id,String name,int identity,int state) {
        super(id, name, identity, state);
    }

    @Override
    public void perish_together(int target_id,List<Integer> id_states_list) {
        id_states_list.set(target_id,3);
        id_states_list.set(this.id_,3);
    }

    @Override
    public boolean perish_together_condition(int game_states) {
        // game_states 0 为天黑，1为遗言，2为发言，3为放逐，4为上警
        return game_states == 4 || game_states == 2;
    }
}

class hunter extends civilians
{
    public hunter(int id,String name,int identity,int state) {
        super(id, name, identity, state);
    }

    public void perish_together(int target_id, List<Integer> id_states_list) {
        id_states_list.set(target_id,0);
    }


    public boolean perish_together_condition(int game_states,List<Integer> id_states_list) {
        if (id_states_list.get(this.id_)==0) // id_state_list 0为普通出局， 1 活着，2 毒死， 3 狼王自爆带走
        {
            if(game_states == 1 || game_states == 3)        // game_states 0 为天黑，1为遗言，2为发言，3为放逐，4为上警
            {
                return true;
            }
        }
        return false;
    }
}

interface guards_ability{
    public boolean defend_condition(int target_id,List<Integer> id_states_list);
    public void defend(int target_id,List<Integer> night_states);
}

class guards extends civilians implements guards_ability{
    private int last_guard_id;
    public guards(int id, String name, int identity,int state) {
        super(id, name, identity, state);
        last_guard_id = -1;
    }

    @Override
    public boolean defend_condition(int target_id, List<Integer> id_states_list) {
        if(last_guard_id!=target_id)
        {
            int states =id_states_list.get(target_id);
            if (states== 1)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void defend(int target_id, List<Integer> night_states) {
        night_states.set(3,target_id);
    }
}

