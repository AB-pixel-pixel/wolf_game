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
        game_board_fragment_manager_data_ = null;
    }

    // 记录公告栏内容的文本,与game_stages_搭配使用
    // 各种阶段的文字提示以及说明，比如：遗言环节，发言环节，放逐环节，上警环节



    public void init(List<Integer> identity_list, List<String> player_str_list){
        // 构造数据转换的map
        init_translation_map();
        init_night_states();
        // GamePlayerListFragment ModelView
        player_list_fragment_ = new player_list_fragment(identity_list,player_str_list);
        player_list_fragment_data_.setValue(player_list_fragment_);
        // 初始化与游戏公告栏交流数据的容器
        init_game_board_fragment_manager();
        // 初始化点击事件
        init_click_process_player_button_();

        game_board_fragment_manager_ = new game_board_fragment_manager();

    }// 结束构造函数



    //---------------------------------------------------------------------------------------------
    //
    //      管理玩家列表的碎片UI
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<player_list_fragment> player_list_fragment_data_ =
            new MutableLiveData<player_list_fragment>();

    private player_list_fragment player_list_fragment_;

    public player_list_fragment get_player_list_fragment(){return player_list_fragment_;}
    public void update_player_list_fragment(){player_list_fragment_=player_list_fragment_data_.getValue();}

    //---------------------------------------------------------------------------------------------
    //      用死亡名单管理玩家列表
    //---------------------------------------------------------------------------------------------



    MutableLiveData<Map<Integer,String>> death_list_data_ = new MutableLiveData<>();

    private void write_death_message_map(List<Integer> death_list){
        Map<Integer,String> death_map = new HashMap<>();
        death_map = player_list_fragment_.player_out_message(death_list);
        death_list_data_.setValue(death_map);
    }

    public MutableLiveData<Map<Integer,String> > get_death_list_data_() {
        return death_list_data_;
    }


    class player_list_fragment{

        public player_list_fragment(List<Integer> identity_list, List<String> player_str_list){
            init_role_list(identity_list,player_str_list);
            init_visual_player_data_list(player_str_list);
        }

        public void set_visual_player_data_list_item_(List<String> visual_player_data_list_) {
            this.visual_player_data_list_ = visual_player_data_list_;
        }

        public Map<Integer,String>  player_out_message(List<Integer> ids){
            Map<Integer,String> message = new HashMap<>();
            for (int i:ids){
                String text_msg = role_list_.get(i).out().toVisualText();
                message.put(i,text_msg);
            }
            return message;
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


        public List<role> role_list_;

        private void init_role_list(List<Integer> identity_list,List<String> player_str_list){
            role_list_ = new ArrayList<>();
            for (int i =0;i<player_str_list.size();i++)
            {
                int identity_num = identity_list.get(i);
                String player_name = player_str_list.get(i);
                switch (identity_num){
                    case -2:
                    case -1: {
                        role temp = new wolf(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 0:{
                        role temp = new role(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 3: {
                        role temp = new prophet(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 1:
                    {
                        role temp = new guards(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 2:
                    {
                        role temp = new hunter(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 4:
                    {
                        role temp = new witch(i,player_name, identity_num);
                        role_list_.add(temp);
                        break;
                    }
                }
            }
        }
    }


    //---------------------------------------------------------------------------------------------
    //
    //      管理玩家列表的点击
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<Integer> picked_player_ = new MutableLiveData<Integer>();

    public void init_click_process_player_button_(){
        picked_player_.setValue(-1);
    }
    public MutableLiveData<Integer> getPicked_player_(){
        return picked_player_;
    }

    public void game_next_stage(){
        game_board_fragment_manager_ = get_game_board_fragment_manager_().getValue().update_stage_counter();
        get_game_board_fragment_manager_().setValue(game_board_fragment_manager_);
    }

    public void game_next_stage(int stage){
        game_board_fragment_manager_ = get_game_board_fragment_manager_().getValue().update_stage_counter(stage);
        get_game_board_fragment_manager_().setValue(game_board_fragment_manager_);
    }

    //---------------------------------------------------------------------------------------------
    //
    //      游戏流程设置
    //
    //---------------------------------------------------------------------------------------------

    // 通过点击事件的处理管理游戏流程
    // target_id 为 -1时，输入的用户为空
    public void process_confirm_click_input(int target_id){
        Log.i("target_id",String.valueOf(target_id));
        switch (game_board_fragment_manager_.game_stage_now_){
            case -1:{
                game_next_stage();
                message_to_user(R.string.game_on);
                break;
            }
            case 0:{
                if (target_id!= -1)
                {
                    game_next_stage();
                    message_to_user(R.string.no_picked_player);
                }
                else
                {
                    game_next_stage();
                }
                break;
            }
            case 1:{
                // 狼人
                if(wolf.wolf_all_dead())
                {
                    message_to_user(R.string.wolf_lose);
                }else if (target_id!= -1)
                {
                    wolf.Kill(target_id, night_states_);
                    game_next_stage();
                }
                else
                {
                    message_to_user(R.string.no_picked_player);
                }
                break;
            }
            case 2:{
                if (target_id == -1) {
                    if (!guards.defend_condition(target_id)){
                        game_next_stage();
                    }
                    else{
                        message_to_user(R.string.no_picked_player);
                    }
                }else{
                    if(guards.defend_condition(target_id)){
                        guards.defend(target_id,night_states_);
                        game_next_stage();
                    }else{
                        message_to_user(R.string.guards_dead);
                        game_next_stage();
                    }
                }
                break;
            }
            case 3:{
                int today = get_date_();
                if (witch.cure_condition())
                {
                    witch.cure(target_id,night_states_,today);
                    game_next_stage();
                }
                else{
                    if(target_id!=-1){
                        message_to_user(R.string.no_cure);
                        game_next_stage();
                    }
                }
                break;
            }
            case 4:{
                int today = get_date_();
                if(witch.poison_condition(today))
                {
                    if (target_id != -1)
                    {
                        witch.poison(target_id,night_states_);
                        game_next_stage();
                    }
                }
                else{
                    if (target_id != -1)
                    {
                        message_to_user(R.string.no_poison);
                    }
                    else{
                        game_next_stage();
                    }
                }
                break;
            }
            case 5:{
                // 在预言家（最终）环节点击之后，将结果输出
                game_next_stage();
                process_night_states();
                break;
            }
            case 6:{
                if (target_id != -1)
                {
                    update_player_list_fragment();
                    player_list_fragment_.role_list_.get(target_id).out();
                    message_to_user(R.string.exit);
                }
                else {
                    // 判断胜负
                    if(wolf.wolf_all_dead())
                    {
                        game_next_stage(7);
                    } else if(prophet.prophet_all_dead()&&hunter.hunter_all_dead()&&witch.witch_all_dead()){
                        game_next_stage(8);
                    } else if(civilian.civilian_all_dead()){
                        game_next_stage(8);
                    }
                }

                game_next_stage();
                break;
            }
            case 7:{
                message_to_user(R.string.good_win);
            }
            case 8:{
                message_to_user(R.string.bad_win);
            }
        }
    }

    // 夜间事件
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

    public void process_night_states(){
        List<Integer> night_out_id = new ArrayList<>();

        int wolf_kill = night_states_.get(0);
        int witch_save = night_states_.get(1);
        int witch_poison = night_states_.get(2);
        int guard = night_states_.get(3);

        if (witch_poison != -1) {
            // 女巫毒人
            night_out_id.add(witch_poison);
            if (wolf_kill != guard) {
                // 守卫没对人
                night_states_.add(wolf_kill);
            }
        } else if (witch_save != -1) {
            // 女巫救人
            if (wolf_kill == guard) {
                night_out_id.add(wolf_kill);
            }
        } else {
            // 女巫啥事没做
            if (wolf_kill != guard) {
                night_out_id.add(wolf_kill);
            }
        }
        // 淘汰情况
        for (int i =0;i < night_out_id.size();i++){
            player_list_fragment_.role_list_.get(night_out_id.get(i)).out();
        }

        // 刷新夜晚的情况
        for (int i = 0; i < night_states_.size(); i++) {
            night_states_.set(i, -1);
        }

        write_death_message_map(night_out_id);
    }


    //---------------------------------------------------------------------------------------------
    //
    //      管理公告栏文本的数据
    //
    //---------------------------------------------------------------------------------------------

    private MutableLiveData<game_board_fragment_manager> game_board_fragment_manager_data_ = new MutableLiveData<>();

    private void init_game_board_fragment_manager(){
        game_board_fragment_manager_data_.setValue(new game_board_fragment_manager());
    }

    public MutableLiveData<game_board_fragment_manager> get_game_board_fragment_manager_(){
        return game_board_fragment_manager_data_;
    }
    private game_board_fragment_manager game_board_fragment_manager_;

    private int get_date_(){
        return game_board_fragment_manager_data_.getValue().get_date_().get(0);
    }

    class game_board_fragment_manager
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

        public  game_board_fragment_manager get_instance_game_board_fragment_manager_(game_board_fragment_manager copy){
            return new game_board_fragment_manager(copy.get_date_(),copy.game_stage_now_);
        }

        // **
        // * 管理游戏阶段
        // *
        public int game_stage_now_;
        public final int regular_round_num_ = 7;
        public game_board_fragment_manager update_stage_counter(){
            // 让流程在0到5的区间里流动
            game_stage_now_ = (game_stage_now_ + 1 )%regular_round_num_;
            // 更新日期
            update_date();
            return this;
        }

        public game_board_fragment_manager update_stage_counter(int stage){
            setGame_stage_now_(stage);
            update_date();
            return this;
        }

        private void init_game_stage_now_(){game_stage_now_ = -1;}

        public int get_game_stage_now()
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
    //      管理游戏通知
    //
    //---------------------------------------------------------------------------------------------
    MutableLiveData<Integer> message_data_ = new MutableLiveData<Integer>();

    public  LiveData<Integer> get_message_data(){
        return message_data_;
    }

    public void message_to_user(int message) {
        message_data_.setValue(message);
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
class role {
    // 玩家的序号
    protected int id_;
    // 玩家的名字
    protected String name_;

    // 以数字代指身份
    protected int identity_;
    // 存储同索引的玩家角色的生命状态，0 为普通出局， 1为活着
    protected int state_;

    public role(int id, String name, int identity)
    {
        // 玩家从1号开始
        id_ = id+1;
        name_ = name;
        identity_ = identity;
        state_ = 1;

    }

    // 定义出局的模式
    public role out(){
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

class civilian extends role{
    private static int civilians_number_ = 0;

    public civilian(int id, String name, int identity) {
        super(id, name, identity);
        civilians_number_++;
    }

    public role out(){
        civilians_number_--;
        state_=0;
        return  this;
    }

    public static boolean civilian_all_dead(){
        return (civilians_number_<1);
    }
}

// night_states 用于记录夜晚的情况，分别是，狼人指杀，女巫救药，女巫毒杀，守卫的守护


//---------------------------------------------------------------------------------------------
//
//      狼人
//
//---------------------------------------------------------------------------------------------

class wolf extends role {
    private static int wolf_number_ = 0;
    wolf(int id,String name,int identity){
        super(id,name,identity);
        wolf_number_++;
    }

    public static void Kill(int target_id, List<Integer> night_states)
    {
        night_states.set(0,target_id);
    }


    public role out(){
        state_ = 0;
        wolf_number_--;
        return this;
    }

    public static boolean wolf_all_dead(){
        return (wolf_number_<1);
    }
}

//---------------------------------------------------------------------------------------------
//
//      女巫
//
//---------------------------------------------------------------------------------------------
class witch extends role {
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

    public static void cure(int target_id, List<Integer> night_states,int cure_day) {
        night_states.set(1,target_id);
        cure_number_--;
        cure_day_ =  cure_day;
    }

    public static boolean witch_all_dead(){
        return (witch_num_ == 0);
    }

    public role out(){
        witch_num_--;
        state_=0;
        return this;
    }
}
//---------------------------------------------------------------------------------------------
//
//      预言家
//
//---------------------------------------------------------------------------------------------

class prophet extends role {
    private static int prophet_number_ = 0;

    public prophet(int id, String name, int identity) {
        super(id, name, identity);
        prophet_number_++;
    }

    public role out(){
        prophet_number_--;
        state_ =0;
        return this;
    }

    public static boolean prophet_all_dead(){
        return (prophet_number_<0);
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

class hunter extends role
{

    public hunter(int id,String name,int identity) {
        super(id, name, identity);
        hunter_number_++;
    }
    private static int hunter_number_=0;
    public role out(){
        hunter_number_--;
        state_ = 0;
        return this;
    }

    public static boolean hunter_all_dead(){
        return (hunter_number_<1);
    }
}

//---------------------------------------------------------------------------------------------
//
//      守卫
//
//---------------------------------------------------------------------------------------------

class guards extends role {
    private static int last_guard_id_ = -2;
    public guards(int id, String name, int identity) {
        super(id, name, identity);
        guard_number_++;
    }

    private static int guard_number_ =0;
    public role out(){
        guard_number_--;
        state_ = 0;
        return this;
    }
    public static boolean hunter_all_dead(){
        return (guard_number_<1);
    }

    public static boolean defend_condition(int target_id) {
        return (target_id!=last_guard_id_);
    }

    public static void defend(int target_id, List<Integer> night_states) {
        night_states.set(3,target_id);
        last_guard_id_ = target_id;
    }
}

