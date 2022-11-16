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




    @Override
    protected void onCleared()
    {
        super.onCleared();
        game_board_fragment_manager_data_ = null;
    }


    public void init(List<Integer> identity_list, List<String> player_str_list){
        // 构造数据转换的map
        init_translation_map();
        init_night_states();
        // GamePlayerListFragment ModelView
        player_list_fragment_ = new player_list_fragment(identity_list, player_str_list);
        player_list_fragment_data_.setValue(player_list_fragment_);
        // 初始化与游戏公告栏交流数据的容器
        init_game_board_fragment_manager();
        // 初始化点击事件
        init_click_button_();

        game_stage_manager_ = new game_stage_manager();

    }// 结束构造函数



    //---------------------------------------------------------------------------------------------
    //
    //      管理玩家列表的碎片UI
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<player_list_fragment> player_list_fragment_data_ =
            new MutableLiveData<>();

    private player_list_fragment player_list_fragment_;

    public player_list_fragment get_player_list_fragment(){return player_list_fragment_;}

    //---------------------------------------------------------------------------------------------
    //      用死亡名单管理玩家列表
    //---------------------------------------------------------------------------------------------



    MutableLiveData<Map<Integer,String>> death_list_data_ = new MutableLiveData<>();

    private void write_death_message_map(List<Integer> death_list){
        Map<Integer,String> death_map;
        death_map = player_list_fragment_.update_player_list(death_list);
        death_list_data_.setValue(death_map);
        player_list_fragment_data_.setValue(player_list_fragment_);
    }

    private void write_death_message_map(int death_id){
        Map<Integer,String> death_map;
        death_map = player_list_fragment_.update_player_list(death_id);
        death_list_data_.setValue(death_map);
        player_list_fragment_data_.setValue(player_list_fragment_);
    }

    public MutableLiveData<Map<Integer,String> > get_death_list_data_() {
        return death_list_data_;
    }


    static class player_list_fragment{

        public player_list_fragment(List<Integer> identity_list, List<String> player_str_list){
            init_role_list(identity_list,player_str_list);
            init_visual_player_data_list(player_str_list);
        }


        public Map<Integer,String> update_player_list(List<Integer> ids){
            Map<Integer,String> message = new HashMap<>();
            for (int i:ids){
                String text_msg = role_list_.get(i).toVisualText();
                message.put(i,text_msg);
            }
            return message;
        }

        public Map<Integer,String> update_player_list(int id){
            Map<Integer,String> message = new HashMap<>();
            String text_msg = role_list_.get(id).toVisualText(); // 获取当前的状态
            message.put(id,text_msg);
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


        public List<Role> role_list_;

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
    }


    //---------------------------------------------------------------------------------------------
    //
    //      管理玩家列表的点击
    //
    //---------------------------------------------------------------------------------------------

    private final MutableLiveData<Integer> picked_player_ = new MutableLiveData<>();

    public void init_click_button_(){
        picked_player_.setValue(-1);
    }
    public MutableLiveData<Integer> getPicked_player_(){
        return picked_player_;
    }


    //---------------------------------------------------------------------------------------------
    //
    //      游戏流程设置
    //
    //---------------------------------------------------------------------------------------------

    // 通过点击事件的处理管理游戏流程
    // target_id 为 -1时，输入的用户为空
    public void process_confirm_click_input(int target_id){
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
                    player_list_fragment_.role_list_.get(target_id).out();
                    write_death_message_map(target_id);
                    message_to_user(R.string.exit);
                    player_list_fragment_data_.setValue(player_list_fragment_);
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



    // 取消按钮
    public void process_cancel_click_input(int target_id){
        Log.i("cancel_click",String.valueOf(target_id));
        switch(game_stage_manager_.game_stage_now_){
            case -1:{
                game_next_stage();
                message_to_user(R.string.game_on);
                break;
            }
            case 0:{
                // TODO 返回白天
                boolean back = game_back_day_stage();
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
            player_list_fragment_.role_list_.get(out_ids.get(i)).out();
        }

        // 刷新夜晚的情况
        for (int i = 0; i < night_states_.size(); i++) {
            night_states_.set(i, -1);
        }

        write_death_message_map(out_ids);
    }

    //---------------------------------------------------------------------------------------------
    //
    //      管理游戏的时间（阶段）
    //
    //---------------------------------------------------------------------------------------------


    public void game_next_stage(){
        get_game_board_fragment_manager_data_().setValue(game_stage_manager_.update_stage_manager_local());
    }

    // 用于从黑夜返回白天
    public boolean game_back_day_stage(){
        if(get_game_board_fragment_manager_data_().getValue().game_stage_now_!=0)
        {
            game_stage_manager_ = get_game_board_fragment_manager_data_().getValue().update_stage_manager_local(-1);
            get_game_board_fragment_manager_data_().setValue(game_stage_manager_);
            return true;
        }
        else
            return false;
    }

    public void game_next_stage(int stage){
        Log.i("stage",String.valueOf(stage));
        game_stage_manager_ = get_game_board_fragment_manager_data_().getValue().update_stage_manager_local(stage);
        get_game_board_fragment_manager_data_().setValue(game_stage_manager_);
    }



    //---------------------------------------------------------------------------------------------
    //
    //      通过游戏时间（阶段），管理公告栏文本的数据
    //
    //---------------------------------------------------------------------------------------------

    private MutableLiveData<game_stage_manager> game_board_fragment_manager_data_ = new MutableLiveData<>();

    private void init_game_board_fragment_manager(){
        game_board_fragment_manager_data_.setValue(new game_stage_manager());
    }

    public MutableLiveData<game_stage_manager> get_game_board_fragment_manager_data_(){
        return game_board_fragment_manager_data_;
    }

    private game_stage_manager game_stage_manager_;

    private int get_date_(){
        return game_stage_manager_.get_date_().get(0);
    }

    static class game_stage_manager
    {

        game_stage_manager(){
            init_game_stage_now_();
            init_data();
        }

        // 管理游戏阶段
        public int game_stage_now_;
        public final int regular_round_num_ = 7;
        public game_stage_manager update_stage_manager_local(){
            // 让流程在0到6的区间里流动
            game_stage_now_ = (game_stage_now_ + 1 )%regular_round_num_;
            // 更新日期
            update_date();
            return this;
        }

        public game_stage_manager update_stage_manager_local(int stage){
            game_stage_now_ = stage;
            update_date();
            return this;
        }


        private void init_game_stage_now_(){game_stage_now_ = -1;}

        public int get_game_stage_now()
        {
            return game_stage_now_;
        }


        // 管理时间
        private List<Integer> date_; // 记录时间，首元素是天数，第二个元素是当天的时间（0:夜晚，1:白天）。
        private void init_data(){
            date_ = new ArrayList<>();
            date_.add(0);
            date_.add(0);
        }

        private void update_date(){
            if (game_stage_now_ == regular_round_num_-1)
            {
                date_.set(0,date_.get(0)+1);
            }
            // 控制日期
            if (game_stage_now_<regular_round_num_-1)
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
    //      游戏通知
    //
    //---------------------------------------------------------------------------------------------
    MutableLiveData<Integer> message_data_ = new MutableLiveData<>();

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
        int2state_map.put(2,"毒死");
        int2state_map.put(3,"狼王自爆");
    }
}

