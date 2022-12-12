package com.example.atry;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 游戏状态管理器
 * 处理UI传输过来的信息后，将相应的结果返回给UI
 * 目录：
 * A1 游戏时间、阶段 -> A2公告栏（事件）文本
 * B1 管理玩家列表内容 -> B2 更新淘汰玩家名单
 * C1 游戏逻辑 {@link #process_confirm_click_input}{@link #process_cancel_click_input}{@link #checking_point}
 * C2 监听点击玩家列表 {@link #getPicked_player_data_} C3 晚上处理的逻辑{@link #process_night_states}
 * D 游戏通知{@link #message_to_user}
 * E 游戏参数设置（硬编码）{@link #init_translation_map()}
 *
 * @author ab
 */
public class gameStateManager extends ViewModel {


    //---------------------------------------------------------------------------------------------
    //
    //     A1 管理游戏时间（阶段）
    //
    //---------------------------------------------------------------------------------------------

    /**
     * 游戏阶段管理
     *
     * @author ab
     */
    static class game_stage_manager
    {
        // 记录时间，首元素是天数，第二个元素是当天的时间（0:夜晚，1:白天）。
        private List<Integer> day_time_;

        // 当前的游戏阶段
        // -1未开始游戏，0进入夜晚，1 狼人刀人，2 守卫使用能力，3 女巫救人，4 女巫毒人，5 预言家验身份， 6 白天
        public int game_stage_now_;

        // 初始化，0天，夜晚
        game_stage_manager(){
            game_stage_now_ = -1;
            day_time_ = new ArrayList<>();
            day_time_.add(0);
            day_time_.add(0);
        }

        // 粗糙模拟循环队列，管理游戏阶段
        public final int regular_round_num_ = 7;

        public game_stage_manager updateStage(){
            game_stage_now_ = (game_stage_now_ + 1 )%regular_round_num_;
            // 更新日期
            update_day_time();
            return this;
        }

        public game_stage_manager updateStage(int stage){
            game_stage_now_ = stage;
            update_day_time();
            return this;
        }

        public int get_game_stage_now()
        {
            return game_stage_now_;
        }


        private void update_day_time(){
            // 白天就天数+1
            if (game_stage_now_ == regular_round_num_-1)
            {
                day_time_.set(0, day_time_.get(0)+1);
            }
            // 控制日期
            if (game_stage_now_<regular_round_num_-1)
            {
                day_time_.set(1,0);
            }
            else
            {
                day_time_.set(1,1);
            }
        }


        public List<Integer> get_day_time(){return day_time_;}
    } // end game_stage_manager


    public void game_next_stage(){
        get_board_data_().setValue(game_stage_manager_.updateStage());
    }


    /**
     * 在进入夜晚阶段，点击“取消”，则返回上一个阶段
     * @return boolean 然后按了没
     */
    public boolean stage_back_to_day_stage(){
        if(get_board_data_().getValue().game_stage_now_!=0)
        {
            game_stage_manager_ = get_board_data_().getValue().updateStage(-1);
            get_board_data_().setValue(game_stage_manager_);
            return true;
        }
        else
            return false;
    }

    public void game_next_stage(int stage){
        Log.i("stage",String.valueOf(stage));
        game_stage_manager_ = get_board_data_().getValue().updateStage(stage);
        get_board_data_().setValue(game_stage_manager_);
    }



    //---------------------------------------------------------------------------------------------
    //
    //      A2 公告栏文本
    //
    //---------------------------------------------------------------------------------------------

    private MutableLiveData<game_stage_manager> board_data_ = new MutableLiveData<>();

    private void init_game_board_fragment_manager(){
        board_data_.setValue(new game_stage_manager());
    }

    public MutableLiveData<game_stage_manager> get_board_data_(){
        return board_data_;
    }
    private game_stage_manager game_stage_manager_;

    private int get_date_(){
        return game_stage_manager_.get_day_time().get(0);
    }

    // TODO 此处好像缺少了本地数据同步到“云端”的步骤



    //---------------------------------------------------------------------------------------------
    //
    //      B1 管理玩家列表内容
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<player_list_manager> player_list_data_ =
            new MutableLiveData<>();

    private player_list_manager player_list_manager_;

    public player_list_manager get_player_list_manager_(){return player_list_manager_;}

    /**
     * 玩家列表管理器
     * role_list_ 存放玩家的列表
     * visual_player_data_list_ 玩家数据的文本
     * update_player_list 读取本地的role_list当中指定id的玩家，将变动的信息传递给UI{@link #update_player_list}
     * init_role_list 初始化角色，要拓展的话需要改动 {@link #init_role_list}
     * @author ab
     */
    static class player_list_manager {
        // 存放玩家的列表
        public List<Role> role_list_;
        // 玩家数据的文本
        public List<String> visual_player_data_list_;

        public player_list_manager(List<Integer> identity_list, List<String> player_str_list){
            init_role_list(identity_list,player_str_list);
            init_visual_player_data_list(player_str_list);
        }


        public void update_player_list(List<Integer> ids,MutableLiveData<Map<Integer, String>> changed_player_list_data){
            Map<Integer,String> message = new HashMap<>();
            for (int i:ids){
                String text_msg = role_list_.get(i).toVisualText();
                message.put(i,text_msg);
            }
            changed_player_list_data.setValue(message);
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

        public List<String> getVisual_player_data_list_()
        {
            return visual_player_data_list_;
        }


        private void init_role_list(List<Integer> identity_list,List<String> player_str_list){
            role_list_ = new ArrayList<>();
            for (int i =0;i<player_str_list.size();i++)
            {
                int identity_num = identity_list.get(i);
                String player_name = player_str_list.get(i);
                switch (identity_num){
                    case -2:
                    case -1: {
                        Role temp = new wolf(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 0:{
                        Role temp = new civilian(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 3: {
                        Role temp = new prophet(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 1:
                    {
                        Role temp = new guards(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 2:
                    {
                        Role temp = new hunter(i,player_name,identity_num);
                        role_list_.add(temp);
                        break;
                    }
                    case 4:
                    {
                        Role temp = new witch(i,player_name, identity_num);
                        role_list_.add(temp);
                        break;
                    }
                }
            }
        }
    } // end  player_list_manager



    //---------------------------------------------------------------------------------------------
    //
    //      B2 更新淘汰玩家名单
    //
    //---------------------------------------------------------------------------------------------

    MutableLiveData<Map<Integer,String>> death_list_data_ = new MutableLiveData<>();
    public MutableLiveData<Map<Integer,String> > get_death_list_data_() {
        return death_list_data_;
    }

    private void update_player_list_data(int death_id){
        List<Integer> death_list = new ArrayList<>();
        death_list.add(death_id);
        update_player_list_data(death_list);
    }

    private void update_player_list_data(List<Integer> death_ids){
        player_list_manager_.update_player_list(death_ids,death_list_data_);
        player_list_data_.setValue(player_list_manager_);
    }

    //---------------------------------------------------------------------------------------------
    //
    //      C1 游戏逻辑设定
    //      通过“确定”“取消”按键调用
    //
    //---------------------------------------------------------------------------------------------

    // 通过点击事件的处理管理游戏流程
    // target_id 为 -1时，输入的用户为空
    public void process_confirm_click_input(){
        int target_id =getPicked_player_id_();
        switch (game_stage_manager_.game_stage_now_){
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
                    if (guards.guard_all_dead()){
                        game_next_stage();
                    }
                    else{
                        message_to_user(R.string.no_picked_player);
                    }
                }else{
                    if(guards.guard_all_dead()){
                        message_to_user(R.string.guards_dead);
                        game_next_stage();
                    }else if(guards.defend_condition(target_id)){
                        guards.defend(target_id,night_states_);
                        game_next_stage();
                    }else{
                        message_to_user(R.string.no_picked_player);
                    }
                }
                break;
            }
            case 3:{
                int today = get_date_();
                if (witch.cure_condition())
                {
                    witch.cure(night_states_,today);
                    Log.i("witch","女巫救人了");
                }
                else{
                    if(target_id!=-1){
                        message_to_user(R.string.no_cure);
                    }
                }
                game_next_stage();
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
                        message_to_user(R.string.next_stage);
                    }
                    game_next_stage();
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
                    // update_player_list_fragment();
                    // 淘汰
                    player_list_manager_.role_list_.get(target_id).out();
                    update_player_list_data(target_id);
                    message_to_user(R.string.exit);
                    player_list_data_.setValue(player_list_manager_);
                    checking_point();    // 判断胜负
                }
                else {
                    game_next_stage();
                }
                break;
            }
            case 7:{
                message_to_user(R.string.good_win);
                break;
            }
            case 8:{
                message_to_user(R.string.bad_win);
                break;
            }
        }
    }



    public void process_cancel_click_input(){
        int target_id = getPicked_player_id_();
        Log.i("cancel_click",String.valueOf(target_id));
        switch(game_stage_manager_.game_stage_now_){
            case -1:{
                game_next_stage();
                message_to_user(R.string.game_on);
                break;
            }
            case 0:{
                // TODO 返回白天
                boolean back = stage_back_to_day_stage();
                if (back)
                {
                    message_to_user(R.string.back);
                }
                else{
                    game_next_stage();
                }
                break;
            }
            case 1:
            case 2: {
                if (target_id==-1){
                    message_to_user(R.string.no_picked_player);
                }
                break;
            }
            case 3:
            case 4:
            case 5:
            case 6:{
                game_next_stage();
                break;
            }
        }
    }

    // 判断胜负
    private void checking_point(){
        if(wolf.wolf_all_dead())
        {
            game_next_stage(7);
        } else if(prophet.prophet_all_dead()&&hunter.hunter_all_dead()&&witch.witch_all_dead()&&guards.guard_all_dead()){
            game_next_stage(8);
            message_to_user(R.string.good_god_dead);
        } else if(civilian.civilian_all_dead()){
            game_next_stage(8);
            message_to_user(R.string.good_people_dead);
        }
    }

    //---------------------------------------------------------------------------------------------
    //
    //      C2 监听点击玩家列表
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<Integer> picked_player_data_ = new MutableLiveData<>();

    public void init_click_button_(){
        picked_player_data_.setValue(-1);
    }
    public MutableLiveData<Integer> getPicked_player_data_(){
        return picked_player_data_;
    }
    public int getPicked_player_id_(){
        return picked_player_data_.getValue();
    }


    //---------------------------------------------------------------------------------------------
    //
    //      C3 晚上处理的逻辑
    //
    //---------------------------------------------------------------------------------------------

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

    public void process_night_states(){
        List<Integer> out_ids = new ArrayList<>();

        int wolf_kill = night_states_.get(0);
        int witch_save = night_states_.get(1);
        int witch_poison = night_states_.get(2);
        int guard = night_states_.get(3);

        if (witch_poison != -1) {
            // 女巫毒人
            out_ids.add(witch_poison);
            if (wolf_kill != guard) {
                // 守卫没对人
                out_ids.add(wolf_kill);
            }
        } else if (witch_save != -1) {
            // 女巫救人
            if (wolf_kill == guard) {
                out_ids.add(wolf_kill);
            }
        } else {
            // 女巫啥事没做
            if (wolf_kill != guard) {
                out_ids.add(wolf_kill);
            }
        }
        // 淘汰情况
        for (int i =0;i < out_ids.size();i++){
            player_list_manager_.role_list_.get(out_ids.get(i)).out();
        }

        // 刷新夜晚的情况
        for (int i = 0; i < night_states_.size(); i++) {
            night_states_.set(i, -1);
        }

        update_player_list_data(out_ids);
    }


    //---------------------------------------------------------------------------------------------
    //
    //      D. 游戏通知
    //
    //---------------------------------------------------------------------------------------------


    MutableLiveData<Integer> message_data_ = new MutableLiveData<>();

    public  LiveData<Integer> get_message_data(){
        return message_data_;
    }

    /**
     * 给用户传递文本（String）
     * 遵循推荐的实践方式，利用了R.string
     * @param message 消息
     */
    public void message_to_user(int message) {
        message_data_.setValue(message);
    }


    //---------------------------------------------------------------------------------------------
    //
    //      E. 初始化游戏参数
    //
    //---------------------------------------------------------------------------------------------
    public void init(List<Integer> identity_list, List<String> player_str_list){
        // 构造数据转换的map
        init_translation_map();
        init_night_states();
        // GamePlayerListFragment ModelView
        player_list_manager_ = new player_list_manager(identity_list, player_str_list);
        player_list_data_.setValue(player_list_manager_);
        // 初始化与游戏公告栏交流数据的容器
        init_game_board_fragment_manager();
        // 初始化点击事件
        init_click_button_();

        game_stage_manager_ = new game_stage_manager();

    }// 结束构造函数


    public final static Map<Integer,String> int2state_map = new HashMap<>();
    public final static Map<Integer,String> int2identity_map = new HashMap<>();

    private void init_translation_map() {
        // 方便数字转为可视化的字符(身份）
        int2identity_map.put(-2,"白狼王");
        int2identity_map.put(-1,"狼人");
        int2identity_map.put(0,"平民");
        int2identity_map.put(1,"守卫");
        int2identity_map.put(2,"猎人");
        int2identity_map.put(3,"预言家");
        int2identity_map.put(4,"女巫");
        // 方便数字转为可视化的字符（生命状态）
        int2state_map.put(0,"出局");
        int2state_map.put(1,"活着");
        // 保留待用
        //  int2state_map.put(2,"毒死");
        //  int2state_map.put(3,"狼王自爆");
    }

    @Override
    protected void onCleared()
    {
        super.onCleared();
        board_data_ = null;
    }

} // 结束 gameStateManager类定义


/**
 * 定义游戏角色
 *
 * @author ab
 * @date 2022/11/23
 */
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
        String visual_identity = gameStateManager.int2identity_map.get(identity_);
        String visual_state = gameStateManager.int2state_map.get(state_);
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

