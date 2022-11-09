package com.example.atry;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerState{

    public final static Map<Integer,String> int2identity_map = new HashMap<>();
    public final static Map<Integer,String> int2state_map = new HashMap<>();
    //
    private List<civilians> role_list_;
    private static List<Integer> night_states_;
    private static List<String> visual_player_data_list_;

    public PlayerState(List<Integer> identity_list,List<String> player_str_list){
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

        // 用于可视化玩家信息的列表
        visual_player_data_list_ = new ArrayList<>();
        //
        night_states_= new ArrayList<>();
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
        // 填充可视化数据
        for (int i =0;i<player_str_list.size();i++)
        {
            visual_player_data_list_.add(role_list_.get(i).toVisualText());
        }
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
    // "白狼王"->-2 || "狼人"->-1 ||  "平民"->0
    // "守卫"->1  ||  "猎人"->2  ||  "预言家"->3
    // "女巫"->4
    protected int identity_;
    // 存储同索引的玩家角色的生命状态，0 为普通出局， 1位或者， 2为特有能力杀害（毒杀或者白狼王自爆带走）
    protected int state_;

    public civilians(int id,String name,int identity,int state)
    {
        id_ = id;
        name_ = name;
        identity_ = identity;
        state_ = state;
    }

    // 返回可视化的文本数据
    protected String toVisualText()
    {
        String visual_identity = PlayerState.int2identity_map.get(identity_);
        String visual_state = PlayerState.int2identity_map.get(state_);
        String visual_text = id_ + "   "+name_+"   "+visual_identity+"   "+visual_state;
        return visual_text;
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

