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

    private static final int DATABASE_VERSION = 2;
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

    /*@Override
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        return contact;
    }*/

    public List<User> getAllUsers() {
        List<User> contactList = new ArrayList<User>();
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
                contactList.add(user);
            } while (cursor.moveToNext());
        }
        return contactList;
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
