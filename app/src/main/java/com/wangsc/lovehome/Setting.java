package com.wangsc.lovehome;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public class Setting {

    private String key;
    private String value;

    public Setting(String key, String value){
        this.key=key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getString() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getBoolean(){
        return Boolean.parseBoolean(value);
    }
    public int getInt(){
        return Integer.parseInt(value);
    }
    public long getLong(){
        return Long.parseLong(value);
    }
    public DateTime getDateTime(){
        return new DateTime(getLong());
    }
    public float getFloat(){
        return Float.parseFloat(value);
    }
    public double getDouble(){
        return Double.parseDouble(value);
    }

    public enum KEYS{
        main_start_page_index,
        nianfo_intervalInMillis, nianfo_endInMillis, nianfo_palyId, nianfo_isReading,nianfo_screen_light,

        tally_dayTargetInMillis, tally_manualOverTimeInMillis,tally_sectionStartInMillis, tally_endInMillis, tally_intervalInMillis,
        tally_music_is_playing,tally_music_switch,tally_isShowDialog, tally_music_name,

        music_current_list_id,music_isplaying, is_keep_cpu_runing,

        weixin_curr_account_id, is_have_didi_order_running,headset_volume,

        is_print_state_else, is_print_content_else, is_print_other_all, is_print_ifClassName,

        map_httpTimeOut, map_interval, map_isSensorEnable, map_isOnceLocationLatest, map_isOnceLocation, map_isGpsFirst, map_isNeedAddress, map_isLocationCacheEnable, map_animateLong,
        map_is_default_toilet, map_location_isAutoChangeGear, map_location_gear, map_location_is_opened, map_search_radius_toilet, map_search_radius_gas, map_location_clock_alarm_is_open,
        map_mylocation_type, web_index, is_auto_record, is_mark_day_show_all, is_trade_alarm_open, is_allow_action_recents, mark_day_in_homepage, tally_record_item_text, bar_title,
        bank_bill_warning_days, is_widget_listview_item_allow_click, bank_card, is_keep_screen_wake, listener, is_rimet_week, phone, password;

    }


//    public static final String main_start_page_index = "main_start_page_index";
//    public static final String nianfo_intervalInMillis = "nianfo_intervalInMillis";
//    public static final String nianfo_endInMillis = "nianfo_endInMillis";
//    public static final String nianfo_palyId = "nianfo_palyId";
//    public static final String nianfo_isReading = "nianfo_isReading";
//    public static final String nianfo_screen_light = "nianfo_screen_light";
//    public static final String tally_dayTargetInMillis = "tally_dayTargetInMillis";
//    public static final String tally_manualOverTimeInMillis = "tally_manualOverTimeInMillis";
//    public static final String tally_sectionStartInMillis = "tally_sectionStartInMillis";
//    public static final String tally_intervalInMillis = "tally_intervalInMillis";
//    public static final String tally_endInMillis = "tally_endInMillis";
//    public static final String tally_music_is_playing = "tally_music_is_playing";
//    public static final String tally_music_switch = "tally_music_switch";
//    public static final String tally_isShowDialog = "tally_isShowDialog";
//    public static final String music_current_list_id = "music_current_list_id";
//    public static final String music_isplaying = "music_isplaying";
//    public static final String weixin_curr_account_id = "weixin_curr_account_id";
//    public static final String setting_autoSetVolume = "setting_autoSetVolume";
//    public static final String is_have_didi_order_running = "is_have_didi_order_running";
//    public static final String headset_volume = "headset_volume";
//    public static final String tally_music_name = "tally_music_name";
//    public static final String is_print_content_else = "is_print_content_else";
//    public static final String is_print_other_all = "is_print_other_all";
//    public static final String is_print_state_else = "is_print_state_else";
//    public static final String is_print_node = "is_print_node";
//    public static final String map_httpTimeOut = "map_httpTimeOut";
//    public static final String map_interval = "map_interval";
//    public static final String map_isSensorEnable = "map_isSensorEnable";
//    public static final String map_isOnceLocationLatest = "map_isOnceLocationLatest";
//    public static final String map_isOnceLocation = "map_isOnceLocation";
//    public static final String map_isGpsFirst = "map_isGpsFirst";
//    public static final String map_isNeedAddress = "map_isNeedAddress";
//    public static final String map_isLocationCacheEnable = "map_isLocationCacheEnable";
//    public static final String map_animateLong = "map_animateLong";
//    public static final String map_is_default_toilet = "map_is_default_toilet";
//    public static final String map_location_isAutoChangeGear = "map_location_isAutoChangeGear";
//    public static final String map_location_gear = "map_location_gear";
//    public static final String map_search_radius_gas = "map_search_radius_gas";
//    public static final String map_location_is_opened = "map_location_is_opened";
//    public static final String map_search_radius_toilet = "map_search_radius_toilet";
//
//    @StringDef({main_start_page_index, nianfo_intervalInMillis, nianfo_endInMillis, nianfo_palyId, nianfo_isReading, nianfo_screen_light, tally_dayTargetInMillis, tally_manualOverTimeInMillis
//            , tally_sectionStartInMillis, tally_intervalInMillis, tally_endInMillis, tally_music_is_playing, tally_music_switch, tally_isShowDialog, music_current_list_id, music_isplaying, weixin_curr_account_id
//            , setting_autoSetVolume, is_have_didi_order_running, headset_volume, tally_music_name, is_print_content_else, is_print_other_all, is_print_state_else, is_print_node, map_httpTimeOut
//            , map_interval, map_isSensorEnable, map_isOnceLocationLatest, map_isOnceLocation, map_isGpsFirst, map_isNeedAddress, map_isLocationCacheEnable, map_animateLong, map_is_default_toilet
//            , map_location_isAutoChangeGear, map_location_gear, map_search_radius_gas, map_location_is_opened, map_search_radius_toilet})
//    //region 注解静态类替换枚举类型
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface KEYS {
//    }

    //endregion
}
