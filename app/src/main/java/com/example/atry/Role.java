package com.example.atry;

import android.util.Log;

import java.util.List;

//---------------------------------------------------------------------------------------------
//
//      定义游戏角色
//
//---------------------------------------------------------------------------------------------
class Role {
    // 玩家的序号
    protected int id_;
    // 玩家的名字
    protected String name_;

    // 以数字代指身份
    protected int identity_;
    // 存储同索引的玩家角色的生命状态，0 为普通出局， 1为活着
    protected int state_;

    public Role(int id, String name, int identity)
    {
        // 玩家从1号开始
        id_ = id+1;
        name_ = name;
        identity_ = identity;
        state_ = 1;
    }

    // 定义出局的模式
    public Role out(){
        Log.w("role","role_out");
        state_ = 0;
        return this;
    }

    // 返回可视化的文本数据
    public String toVisualText()
    {
        String visual_identity = PlayerStateManager.int2identity_map.get(identity_);
        String visual_state = PlayerStateManager.int2state_map.get(state_);
        return id_ + "号位   "+name_+"   "+visual_identity+"   "+visual_state;
    }
}

class civilian extends Role {
    private static int civilians_number_ = 0;

    public civilian(int id, String name, int identity) {
        super(id, name, identity);
        Log.i("civilians_number_",String.valueOf(civilians_number_));
        civilians_number_++;
    }

    public Role out(){
        Log.i("civilians_number",String.valueOf(civilians_number_));
        civilians_number_--;
        state_=0;
        return  this;
    }

    public static boolean civilian_all_dead(){
        Log.i("civilians_number_",String.valueOf(civilians_number_));
        return (civilians_number_==0);
    }
}

// night_states 用于记录夜晚的情况，分别是，狼人指杀，女巫救药，女巫毒杀，守卫的守护


//---------------------------------------------------------------------------------------------
//
//      狼人
//
//---------------------------------------------------------------------------------------------

class wolf extends Role {
    private static int wolf_number_ = 0;
    wolf(int id,String name,int identity){
        super(id,name,identity);
        wolf_number_++;
    }

    public static void Kill(int target_id, List<Integer> night_states)
    {
        night_states.set(0,target_id);
    }


    public Role out(){
        state_ = 0;
        wolf_number_--;
        Log.i("wolf_number",String.valueOf(wolf_number_));
        return this;
    }

    public static boolean wolf_all_dead(){
        return (wolf_number_==0);
    }
}

//---------------------------------------------------------------------------------------------
//
//      女巫
//
//---------------------------------------------------------------------------------------------
class witch extends Role {
    private static int poison_number_;
    private static int cure_day_;
    private static int cure_number_;
    private static int witch_num_ = 0;
    witch(int id,String name,int identity)
    {
        super(id,name,identity);
        poison_number_  = 1;
        cure_number_ = 1;
        witch_num_++;
        cure_day_ = -2;
    }


    public static boolean poison_condition(int today) {
        return (poison_number_ > 0)&(cure_day_ !=today);
    }


    public static void poison(int target_id, List<Integer> night_states) {
        night_states.set(2,target_id);
        poison_number_--;
    }


    public static boolean cure_condition() {
        return (cure_number_>0);
    }

    public static void cure(List<Integer> night_states,int cure_day) {
        night_states.set(1,1);
        cure_number_--;
        cure_day_ =  cure_day;
    }

    public static boolean witch_all_dead(){
        return (witch_num_ == 0);
    }

    public Role out(){
        witch_num_--;
        Log.i("witch",String.valueOf(witch_num_));
        state_=0;
        return this;
    }
}
//---------------------------------------------------------------------------------------------
//
//      预言家
//
//---------------------------------------------------------------------------------------------

class prophet extends Role {
    private static int prophet_number_ = 0;

    public prophet(int id, String name, int identity) {
        super(id, name, identity);
        prophet_number_++;
    }

    public Role out(){
        prophet_number_--;
        Log.i("prophet",String.valueOf(prophet_number_));
        state_ =0;
        return this;
    }

    public static boolean prophet_all_dead(){
        return (prophet_number_==0);
    }


}




//---------------------------------------------------------------------------------------------
//
//      白狼王(该角色并入狼人)
//
//---------------------------------------------------------------------------------------------

//interface perish_together_skill {
//    // 自爆技能
//    public void perish_together(int target_id);
//    // 能否使用技能
//    public boolean perish_together_condition(int game_states);
//}
//
//class white_wolf_king extends wolf implements perish_together_skill {
//    public white_wolf_king(int id,String name,int identity,int state) {
//        super(id, name, identity, state);
//    }
//
//    @Override
//    public void perish_together(int target_id) {
//    }
//
//    // 检查调用的玩家是不是死了
//    @Override
//    public boolean perish_together_condition(int game_states) {
//        return this.state_==1;
//    }
//}

//---------------------------------------------------------------------------------------------
//
//      猎人
//
//---------------------------------------------------------------------------------------------

class hunter extends Role
{

    public hunter(int id,String name,int identity) {
        super(id, name, identity);
        hunter_number_++;
    }
    private static int hunter_number_=0;
    public Role out(){
        hunter_number_--;
        Log.i("hunter",String.valueOf(hunter_number_));
        state_ = 0;
        return this;
    }

    public static boolean hunter_all_dead(){
        return (hunter_number_==0);
    }
}

//---------------------------------------------------------------------------------------------
//
//      守卫
//
//---------------------------------------------------------------------------------------------

class guards extends Role {
    private static int last_guard_id_ = -2;
    public guards(int id, String name, int identity) {
        super(id, name, identity);
        guard_number_++;
    }

    private static int guard_number_ =0;
    public Role out(){
        guard_number_--;
        state_ = 0;
        return this;
    }
    public static boolean guard_all_dead(){
        return (guard_number_==0);
    }

    public static boolean defend_condition(int target_id) {
        return (target_id!=last_guard_id_);
    }

    public static void defend(int target_id, List<Integer> night_states) {
        night_states.set(3,target_id);
        last_guard_id_ = target_id;
    }
}

