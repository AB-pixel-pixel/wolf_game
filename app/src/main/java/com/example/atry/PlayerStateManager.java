package com.example.atry;



import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerStateManager extends ViewModel {

    // 用于转换数据的Map
    public final static Map<Integer,String> int2identity_map = new HashMap<>();
    public final static Map<Integer,String> int2state_map = new HashMap<>();




    @Override
    protected void onCleared()
    {
        super.onCleared();
        game_board_fragment_manager_ = null;
    }

    // 记录公告栏内容的文本,与game_stages_搭配使用
    // 各种阶段的文字提示以及说明，比如：遗言环节，发言环节，放逐环节，上警环节
    // public static List<String> board_text_;



    public void init(List<Integer> identity_list, List<String> player_str_list){
        // 构造数据转换的map
        init_translation_map();

        // GamePlayerListFragment ModelView
        player_list_fragment_ = new player_list_fragment(identity_list,player_str_list);
        player_list_fragment_data_.setValue(player_list_fragment_);
        // 初始化与游戏公告栏交流数据的容器
        init_game_board_fragment_manager();

        // 初始化点击事件
        init_click_process_player_button_();
    }// 结束构造函数




    //---------------------------------------------------------------------------------------------
    //
    //      管理按钮的点击事件
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<List<Integer>> click_process_player_button_= new MutableLiveData<List<Integer>>();

    public void init_click_process_player_button_(){
        List<Integer> click_process_player_button_list = new ArrayList<>();
        click_process_player_button_list.add(-1);
        click_process_player_button_list.add(-1);
        click_process_player_button_.setValue(click_process_player_button_list);
        }
    public LiveData<List<Integer>> getClick_process_player_button_(){
        return click_process_player_button_;
    }

    //---------------------------------------------------------------------------------------------
    //
    //      管理玩家列表的碎片UI， gplf
    //
    //---------------------------------------------------------------------------------------------

    private MutableLiveData<player_list_fragment> player_list_fragment_data_ =
            new MutableLiveData<player_list_fragment>();

    private player_list_fragment player_list_fragment_;

    public player_list_fragment get_player_list_fragment(){return player_list_fragment_;}
    public void update_player_list_fragment(){player_list_fragment_=player_list_fragment_data_.getValue();}

    class player_list_fragment{

        public player_list_fragment(List<Integer> identity_list, List<String> player_str_list){
            init_night_states();
            init_role_list(identity_list,player_str_list);
            init_visual_player_data_list(player_str_list);
        }




        // 晚上的情况
        private List<Integer> night_states_;

        private void init_night_states() {
            night_states_= new ArrayList<>();
            night_states_.add(-1);
            night_states_.add(-1);
            night_states_.add(-1);
            night_states_.add(-1);
        }

        public List<Integer> getNight_states_() {
            return night_states_;
        }

        public void setNight_states_(List<Integer> night_states_) {
            this.night_states_ = night_states_;
        }


        public void setVisual_player_data_list_(List<String> visual_player_data_list_) {
            this.visual_player_data_list_ = visual_player_data_list_;
        }

        // 玩家数据的文本
        public List<String> visual_player_data_list_;

        private void init_visual_player_data_list(List<String> player_str_list){
            // 用于可视化玩家信息的列表
            visual_player_data_list_ = new ArrayList<>();
            for (int i =0;i<player_str_list.size();i++)
            {
                String temp_data = role_list_.get(i).toVisualText();
                visual_player_data_list_.add(temp_data);
            }
        }

        public List<String> getVisual_player_data_list_()
        {
            return visual_player_data_list_;
        }

        // TODO: player_list_fragment.role_list_ 的写法是有错误的

        // 设置角色身份
        public List<civilians> role_list_;

        private void init_role_list(List<Integer> identity_list,List<String> player_str_list){
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
                        // TODO,完善一下这个switch架构，现在懒得改
                        civilians temp = new civilians(i,player_name,identity_num, 1);
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


    }




    //---------------------------------------------------------------------------------------------
    //
    //      管理公告栏文本的数据
    //
    //---------------------------------------------------------------------------------------------

    private MutableLiveData<game_board_fragment_manager> game_board_fragment_manager_ = new MutableLiveData<>();

    private void init_game_board_fragment_manager(){
        game_board_fragment_manager_.setValue(new game_board_fragment_manager());
    }

    public MutableLiveData<game_board_fragment_manager> get_game_board_fragment_manager_(){
        return game_board_fragment_manager_;
    }

    static class game_board_fragment_manager
    {

        game_board_fragment_manager(){
            init_game_stage_now_();
            init_data();
        }

        game_board_fragment_manager(List<Integer> date, int game_stage)
        {
            game_stage_now_ = game_stage;
            date_ = date;
        }

        public static game_board_fragment_manager get_instance_game_board_fragment_manager_(game_board_fragment_manager copy){
            return new game_board_fragment_manager(copy.get_date_(),copy.game_stage_now_);
        }

        // **
        // * 管理游戏阶段
        // *
        public int game_stage_now_;

        public game_board_fragment_manager update_stage(){
            // 让流程在0到5的区间里流动
            game_stage_now_ = (game_stage_now_ + 1 )%6;
            // 更新日期
            update_date();
            return this;
        }

        private void init_game_stage_now_(){game_stage_now_ = -1;}

        public void back_to_daytime_stage(){
            game_stage_now_ = 5;
        }

        public int getGame_stage_now_()
        {
            return game_stage_now_;
        }

        public void setGame_stage_now_(int stage)
        {
            game_stage_now_ = stage;
        }

        // **
        // * 管理时间
        // *
        private List<Integer> date_; // 记录时间，首元素是天数，第二个元素是当天的时间（0:夜晚，1:白天）。
        private void init_data(){
            date_ = new ArrayList<>();
            date_.add(0);
            date_.add(0);
        }

        private void update_date(){
            if (game_stage_now_ ==0)
            {
                int data_now = date_.get(0)+1;
                date_.set(0,data_now);
            }
            // 控制日期
            if (game_stage_now_<5)
            {
                date_.set(1,0);
            }
            else
            {
                date_.set(1,1);
            }
        }

        public List<Integer> get_date_(){return date_;}
    }



    //---------------------------------------------------------------------------------------------
    //
    //      初始化游戏参数
    //
    //---------------------------------------------------------------------------------------------

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





    public List<String> getVisual_player_data_list_()
    {
        return player_list_fragment_.getVisual_player_data_list_();
    }
}

    //---------------------------------------------------------------------------------------------
    //
    //      定义游戏角色
    //
    //---------------------------------------------------------------------------------------------
class civilians{
    // 玩家的序号
    protected int id_;
    // 玩家的名字
    protected String name_;

    // 以数字代指身份
    protected int identity_;
    // 存储同索引的玩家角色的生命状态，0 为普通出局， 1为活着， 2为特有能力杀害（毒杀或者白狼王自爆带走）
    protected int state_;

    public civilians(int id,String name,int identity,int state)
    {
        // 玩家从1号开始
        id_ = id+1;
        name_ = name;
        identity_ = identity;
        state_ = state;
    }

    // 定义出局的模式
    public void out(int mode){
        state_ = mode;
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


//---------------------------------------------------------------------------------------------
//
//      狼人
//
//---------------------------------------------------------------------------------------------

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

//---------------------------------------------------------------------------------------------
//
//      女巫
//
//---------------------------------------------------------------------------------------------

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
        cure_number_--;
    }
}


//---------------------------------------------------------------------------------------------
//
//      白狼王
//
//---------------------------------------------------------------------------------------------

interface perish_together_skill {
    // 自爆技能
    public void perish_together(int target_id);
    // 能否使用技能
    public boolean perish_together_condition(int game_states);
}

class white_wolf_king extends wolf implements perish_together_skill {
    public white_wolf_king(int id,String name,int identity,int state) {
        super(id, name, identity, state);
    }

    @Override
    public void perish_together(int target_id) {
    }

    // 检查调用的玩家是不是死了
    @Override
    public boolean perish_together_condition(int game_states) {
        return this.state_==1;
    }
}

//---------------------------------------------------------------------------------------------
//
//      猎人
//
//---------------------------------------------------------------------------------------------

class hunter extends civilians
{
    public hunter(int id,String name,int identity,int state) {
        super(id, name, identity, state);
    }

    public void perish_together(int target_id) {

    }

    public boolean perish_together_condition(int game_states) {
        return this.state_==1;
    }

    @Override
    public void out(int mode)
    {
        this.state_ = mode;
        // TODO finish game_state_condition
        if (perish_together_condition(1));
        {
            // TODO 看看UI能怎么和这个类结合再写
        //    perish_together_skill(target_id);
        }
    }
}

//---------------------------------------------------------------------------------------------
//
//      守卫
//
//---------------------------------------------------------------------------------------------

interface guards_ability{
    public boolean defend_condition(int target_id);
    public void defend(int target_id,List<Integer> night_states);
}

class guards extends civilians implements guards_ability{
    private int last_guard_id_;
    public guards(int id, String name, int identity,int state) {
        super(id, name, identity, state);
        last_guard_id_ = -1;
    }

    @Override
    public boolean defend_condition(int target_id) {
        return true;
    }

    @Override
    public void defend(int target_id, List<Integer> night_states) {
        night_states.set(3,target_id);
        last_guard_id_ = target_id;
    }
}

