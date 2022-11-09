package com.example.atry;

import java.util.List;

public class stateController{
    // 存储同索引的玩家角色的生命状态，0 为普通出局， 1位或者， 2为特有能力杀害（毒杀或者白狼王自爆带走）
    private int[] life_state_;
    //
    private List<civilians> role_list_;
    //
    public static List<Integer> id_states_list_;
    public static List<Integer> night_states_;
    public static List<Integer> id_identity_list_;

    public stateController(){

    }

    // 读取存储的内容来初始化游戏的状态控制
    private void init(){

    }

}

class civilians{
    protected int id_;
    protected String name_;
    protected String identity_;

    public civilians(int id,String name,String identity)
    {
        id_ = id;
        name_ = name;
        identity_ = identity;
    }
}

// night_states 用于记录夜晚的情况，分别是，狼人指杀，女巫救药，女巫毒杀，守卫的守护

interface wolf_ability{
    public void Kill(int target_id,int[] night_states);
}

class wolf extends civilians implements wolf_ability{
    wolf(int id,String name,String identity){
        super(id,name,identity);
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
    witch(int id,String name,String identity)
    {
        super(id,name,identity);
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
    prophet (int id,String name,String identity) {
        super(id, name, identity);
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
    public white_wolf_king(int id,String name,String identity) {
        super(id, name, identity);
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
    public hunter(int id,String name,String identity) {
        super(id, name, identity);
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
    public guards(int id, String name, String identity) {
        super(id, name, identity);
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

