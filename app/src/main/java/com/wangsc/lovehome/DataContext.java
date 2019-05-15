package com.wangsc.lovehome;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wangsc.lovehome.model.DateTime;
import com.wangsc.lovehome.model.RimetClock;
import com.wangsc.lovehome.model.RunLog;
import com.wangsc.lovehome.model.Setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;
    private Context context;

    public DataContext(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    //region RimetClock
    public List<RimetClock> getRimetClocks() {
        List<RimetClock> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("rimetClock", null, null, null, null, null, " hour, minite");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RimetClock model = new RimetClock(UUID.fromString(cursor.getString(0)));
                model.setHour(cursor.getInt(1));
                model.setMinite(cursor.getInt(2));
                model.setSummery(cursor.getString(3));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }


    public void addRimetClock(RimetClock rimetClock) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", rimetClock.getId().toString());
            values.put("hour", rimetClock.getHour());
            values.put("minite", rimetClock.getMinite());
            values.put("summery", rimetClock.getSummery());
            //调用方法插入数据
            db.insert("rimetClock", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void addRimetClocks(List<RimetClock> rimetClocks) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (RimetClock rimetClock:rimetClocks) {
                //使用insert方法向表中插入数据
                ContentValues values = new ContentValues();
                values.put("id", rimetClock.getId().toString());
                values.put("hour", rimetClock.getHour());
                values.put("minite", rimetClock.getMinite());
                values.put("summery", rimetClock.getSummery());
                //调用方法插入数据
                db.insert("rimetClock", "id", values);
            }
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    public void updateRimetClock(RimetClock runLog) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("hour", runLog.getHour());
            values.put("minite", runLog.getMinite());
            values.put("summery", runLog.getSummery());

            db.update("rimetClock", values, "id=?", new String[]{runLog.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRimetClock(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("rimetClock", "id = ?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    //endregion

    //region RunLog
    public List<RunLog> getRunLogs() {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, null, null, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<RunLog> getRunLogsByEquals(String item) {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, "item like ?", new String[]{item}, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<RunLog> getRunLogsByLike(String[] itemLike) {
        List<RunLog> result = new ArrayList<>();
        String where = "";
        for (int i = 0; i < itemLike.length; i++) {
            where += " item like  ? ";
            if (i < itemLike.length - 1) {
                where += "OR";
            }
        }
        String[] whereArg = new String[itemLike.length];
        for (int i = 0; i < itemLike.length; i++) {
            whereArg[i] = "%" + itemLike[i] + "%";
        }

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, where, whereArg, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public void addRunLog(RunLog runLog) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", runLog.getId().toString());
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void addRunLog(String item, String message) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", UUID.randomUUID().toString());
            values.put("runTime", System.currentTimeMillis());
            values.put("tag", new DateTime(System.currentTimeMillis()).toLongDateTimeString());
            values.put("item", item);
            values.put("message", message);
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void updateRunLog(RunLog runLog) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());

            db.update("runLog", values, "id=?", new String[]{runLog.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void clearRunLog() {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", null, null);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLogByEquals(String item) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "item like ?", new String[]{"%" + item + "%"});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLogByLike(String itemLike) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "item like ?", new String[]{"%" + itemLike + "%"});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLog(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "id = ?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    //endregion

    //region Setting
    public Setting getSetting(Object key) {

        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, "key=?", new String[]{key.toString()}, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(key.toString(), cursor.getString(1));
            cursor.close();
            db.close();
            return setting;
        }
        return null;
    }

    public Setting getSetting(Object key, Object defaultValue) {
        Setting setting = getSetting(key);
        if (setting == null) {
            this.addSetting(key, defaultValue);
            setting = new Setting(key.toString(), defaultValue.toString());
            return setting;
        }
        return setting;
    }

    /**
     * 修改制定key配置，如果不存在则创建。
     *
     * @param key
     * @param value
     */
    public void editSetting(Object key, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("value", value.toString());
        if (db.update("setting", values, "key=?", new String[]{key.toString()}) == 0) {
            this.addSetting(key, value.toString());
        }
        db.close();
    }

    public void deleteSetting(Object key) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "key=?", new String[]{key.toString()});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void deleteSetting(String key) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "key=?", new String[]{key});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void addSetting(Object key, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("key", key.toString());
        values.put("value", value.toString());
        //调用方法插入数据
        db.insert("setting", "key", values);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public List<Setting> getSettings() {
        List<Setting> result = new ArrayList<>();
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, null, null, null, null, "key ASC");
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(cursor.getString(0), cursor.getString(1));
            result.add(setting);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearSetting() {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", null, null);
        //        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }
    //endregion
}
