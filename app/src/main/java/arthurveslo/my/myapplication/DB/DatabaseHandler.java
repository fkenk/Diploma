package arthurveslo.my.myapplication.DB;

/**
 * Created by User on 13.04.2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "mainDB";
    /// TABLE USERS
    private static final String TABLE_USERS = "users";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_AGE = "age";
    private static final String KEY_SEX = "sex";
    private static final String KEY_BMR = "BMR";
    /// TABLE ACTIVITIES
    private static final String TABLE_ACTIVITIES = "activities";
    private static final String KEY_ACTIVITIES_ID = "activities_id";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CALORIES = "calories";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time" ;
    private static final String KEY_AVR_SPEED = "avr_speed";
    private static final String KEY_DISTANCE = "distance";
    private static final String CURRENT_TIME = "cur_time";
    private static final String ADDRESS = "address";
    private static final String MAP = "map" ;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_WEIGHT + " REAL,"
                + KEY_HEIGHT + " REAL,"
                + KEY_AGE + " INTEGER,"
                + KEY_SEX + " INTEGER,"
                + KEY_BMR + " REAL" + ");";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES + "("
                + KEY_ACTIVITIES_ID + " integer primary key autoincrement, "
                + KEY_ACTIVITY + " TEXT,"
                + KEY_USER_ID + " TEXT,"
                + KEY_CALORIES + " REAL,"
                + KEY_STEPS + " INTEGER,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_AVR_SPEED + " REAL,"
                + KEY_DISTANCE + " REAL,"
                + CURRENT_TIME + " STRING,"
                + ADDRESS + " STRING,"
                + MAP + " STRING,"
                + " FOREIGN KEY ("+KEY_USER_ID+") REFERENCES "+TABLE_USERS+"("+KEY_ID+"));";
        db.execSQL(CREATE_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        onCreate(db);
    }


    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.get_id());
        values.put(KEY_NAME, user.get_name());
        values.put(KEY_WEIGHT, user.get_weight());
        values.put(KEY_HEIGHT, user.get_height());
        values.put(KEY_AGE, user.get_age());
        values.put(KEY_SEX, user.get_sex());
        values.put(KEY_BMR, user.get_BMR());
        try {
            db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        db.close();
    }

    public void addActivityDB(ActivityDB activityDB){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ACTIVITY,activityDB.get_activity());
        values.put(KEY_USER_ID, activityDB.get_user_id());
        values.put(KEY_CALORIES,activityDB.get_calories());
        values.put(KEY_STEPS, activityDB.get_steps());
        values.put(KEY_DATE,activityDB.get_date());
        values.put(KEY_TIME,activityDB.get_time());
        values.put(KEY_AVR_SPEED,activityDB.get_avr_speed());
        values.put(KEY_DISTANCE,activityDB.get_distance());
        values.put(CURRENT_TIME, activityDB.get_cur_time());
        values.put(ADDRESS, activityDB.get_address());
        values.put(MAP, activityDB.get_map());
        try {
            db.insertOrThrow(TABLE_ACTIVITIES, null, values);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        db.close();
    }

    public int updateUserParameters(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WEIGHT, user.get_weight());
        values.put(KEY_HEIGHT, user.get_height());
        values.put(KEY_SEX, user.get_sex());
        values.put(KEY_BMR, user.get_BMR());

        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.get_id()) });
    }

    public User getUser(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
                        KEY_NAME,KEY_WEIGHT, KEY_HEIGHT, KEY_AGE, KEY_SEX, KEY_BMR}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        User user = new User(cursor.getString(0), cursor.getString(1), Double.parseDouble(cursor.getString(2)),
                Double.parseDouble(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(5)), Double.parseDouble(cursor.getString(6)));

        return user;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.set_id(cursor.getString(0));
                user.set_name(cursor.getString(1));
                user.set_weight(Double.parseDouble(cursor.getString(2)));
                user.set_height(Double.parseDouble(cursor.getString(3)));
                user.set_age(Integer.parseInt(cursor.getString(4)));
                user.set_sex(Integer.parseInt(cursor.getString(5)));
                user.set_BMR(Double.parseDouble(cursor.getString(6)));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        return userList;
    }

    public List<ActivityDB> getAllActivityDB() {
        List<ActivityDB> activitiesList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ActivityDB activityDB = new ActivityDB();
                activityDB.set_num(Integer.parseInt(cursor.getString(0)));
                activityDB.set_activity(cursor.getString(1));
                activityDB.set_user_id(cursor.getString(2));
                activityDB.set_calories(Double.parseDouble(cursor.getString(3)));
                activityDB.set_steps(Integer.parseInt(cursor.getString(4)));
                activityDB.set_date(cursor.getString(5));
                activityDB.set_time(cursor.getString(6));
                activityDB.set_avr_speed(Double.parseDouble(cursor.getString(7)));
                activityDB.set_distance(Double.parseDouble(cursor.getString(8)));
                activityDB.set_cur_time(cursor.getString(9));
                activityDB.set_address(cursor.getString(10));
                activityDB.set_map(cursor.getString(11));
                activitiesList.add(activityDB);
            } while (cursor.moveToNext());
        }
        return activitiesList;
    }
    public List<Foo> getUniqueDateActivityDB() {
        List<Foo> foos = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT "+KEY_DATE+" FROM " + TABLE_ACTIVITIES;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Foo foo = new Foo();
                foo.setTitle(cursor.getString(0));
                foos.add(foo);
            } while (cursor.moveToNext());
        }
        return foos;
    }

    public List<Foo> getUniqueMonthActivityDB() {
        List<Foo> foos = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT "+KEY_DATE+" FROM " + TABLE_ACTIVITIES;

        ArrayList<String> months = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if(!months.contains(cursor.getString(0).split("\\.")[1])) {
                    months.add(cursor.getString(0).split("\\.")[1]);
                    Foo foo = new Foo();
                    foo.setTitle(cursor.getString(0));
                    foos.add(foo);
                }
            } while (cursor.moveToNext());
        }
        return foos;
    }



    public List<Foo> getUniqueYearActivityDB() {
        List<Foo> foos = new ArrayList<>();
        String selectQuery = "SELECT "+KEY_DATE+" FROM " + TABLE_ACTIVITIES;
        ArrayList<String> months = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if(months.contains(cursor.getString(0).split("\\.")[2])) {
                    months.add(cursor.getString(0).split("\\.")[2]);
                    Foo foo = new Foo();
                    foo.setTitle(cursor.getString(0));
                    foos.add(foo);
                }
            } while (cursor.moveToNext());
        }
        return foos;
    }

    public List<Foo> fillFoo(List<Foo> foos) {
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITIES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<ActivityDB> children = new ArrayList<>();
        if (cursor.moveToFirst()) {
            for (Foo item: foos) {
                // search proper child for current item
                do {
                    // if current row date value equals with current item datetime
                    if (item.getTitle().equals(cursor.getString(5))) {

                        ActivityDB activityDB = new ActivityDB();
                        activityDB.set_num(Integer.parseInt(cursor.getString(0)));
                        activityDB.set_activity(cursor.getString(1));
                        activityDB.set_user_id(cursor.getString(2));
                        activityDB.set_calories(Double.parseDouble(cursor.getString(3)));
                        activityDB.set_steps(Integer.parseInt(cursor.getString(4)));
                        activityDB.set_date(cursor.getString(5));
                        activityDB.set_time(cursor.getString(6));
                        activityDB.set_avr_speed(Double.parseDouble(cursor.getString(7)));
                        activityDB.set_distance(Double.parseDouble(cursor.getString(8)));// fetch data from columns
                        activityDB.set_cur_time(cursor.getString(9));
                        activityDB.set_address(cursor.getString(10));
                        activityDB.set_map(cursor.getString(11));
                        children.add(activityDB);
                    }
                } while (cursor.moveToNext());

                // assign created children into current item
                item.setChildren(children);

                // reset List that will be used for next item
                children = null;
                children = new ArrayList<ActivityDB>();

                // reset Cursor and move it to first row again
                cursor.moveToFirst();
            }
        }
        return foos;
    }

    public void deleteActivityDB(ActivityDB activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, KEY_ACTIVITIES_ID + " = ?", new String[] { String.valueOf(activity.get_num()) });
        db.close();
    }

    public double getMonthActivityDB(int month, String selector){
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        double value = 0.0;
        if (cursor.moveToFirst()) {
            do {
                if(Integer.parseInt(cursor.getString(5).split("\\.")[1]) == month) {
                    ActivityDB activityDB = new ActivityDB();
                    activityDB.set_num(Integer.parseInt(cursor.getString(0)));
                    activityDB.set_activity(cursor.getString(1));
                    activityDB.set_user_id(cursor.getString(2));
                    activityDB.set_calories(Double.parseDouble(cursor.getString(3)));
                    activityDB.set_steps(Integer.parseInt(cursor.getString(4)));
                    activityDB.set_date(cursor.getString(5));
                    activityDB.set_time(cursor.getString(6));
                    activityDB.set_avr_speed(Double.parseDouble(cursor.getString(7)));
                    activityDB.set_distance(Double.parseDouble(cursor.getString(8)));
                    activityDB.set_cur_time(cursor.getString(9));
                    activityDB.set_address(cursor.getString(10));
                    activityDB.set_map(cursor.getString(11));
                    if(selector.equals("Distance")) {
                        value += Double.parseDouble(cursor.getString(8));
                    }
                    if(selector.equals("Steps")) {
                        value += Integer.parseInt(cursor.getString(4));
                    }
                    if(selector.equals("Calories")) {
                        value += Double.parseDouble(cursor.getString(3));
                    }
                    if(selector.equals("Avr.Speed")) {
                        value += Double.parseDouble(cursor.getString(7));
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }

    public double getDayActivityDB(int day, String selector) {
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVITIES;
        ArrayList<ActivityDB> activitiesList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        double value = 0.0;
        if (cursor.moveToFirst()) {
            do {
                if(Integer.parseInt(cursor.getString(5).split("\\.")[0]) == day) {
                    ActivityDB activityDB = new ActivityDB();
                    activityDB.set_num(Integer.parseInt(cursor.getString(0)));
                    activityDB.set_activity(cursor.getString(1));
                    activityDB.set_user_id(cursor.getString(2));
                    activityDB.set_calories(Double.parseDouble(cursor.getString(3)));
                    activityDB.set_steps(Integer.parseInt(cursor.getString(4)));
                    activityDB.set_date(cursor.getString(5));
                    activityDB.set_time(cursor.getString(6));
                    activityDB.set_avr_speed(Double.parseDouble(cursor.getString(7)));
                    activityDB.set_distance(Double.parseDouble(cursor.getString(8)));
                    activityDB.set_cur_time(cursor.getString(9));
                    activityDB.set_address(cursor.getString(10));
                    activityDB.set_map(cursor.getString(11));
                    activitiesList.add(activityDB);
                    if(selector.equals("Distance")) {
                        value += Double.parseDouble(cursor.getString(8));
                    }
                    if(selector.equals("Steps")) {
                        value += Integer.parseInt(cursor.getString(4));
                    }
                    if(selector.equals("Calories")) {
                        value += Double.parseDouble(cursor.getString(3));
                    }
                    if(selector.equals("Avr.Speed")) {
                        value += Double.parseDouble(cursor.getString(7));
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }
/*
    @Override
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    @Override
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null, null);
        db.close();
    }

    @Override
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }*/
}
