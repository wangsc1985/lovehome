package com.wangsc.lovehome.model;

import com.wangsc.lovehome.model.DateTime;

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
        is_rimet_clock_running, is_rimet_week, phone, password, rimet_alarm_time, is_clock_random;

    }


    //endregion
}
