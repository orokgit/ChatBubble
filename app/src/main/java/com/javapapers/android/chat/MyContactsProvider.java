package com.javapapers.android.chat;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

public class MyContactsProvider extends ContentProvider {
    final String LOG_TAG = "myLogs";

    // // Константы для БД
    // БД
    static public String DB_NAME = "mymessagesdb";
    static final int DB_VERSION = 22;


    // ======== table MESSAGES ============================

    static public String MESSAGE_TABLE = "messages";

    static public String MESSAGE_ID = "_id";
    static public String MESSAGE_FROM_USER = "from_user";
    static public String MESSAGE_TO_USER = "to_user";
    static public String MESSAGE_CONTENT = "content";
    static public String MESSAGE_DATE = "date";
    static public String MESSAGE_KEY_ID = "key_id";

    // Скрипт создания таблицы
    static final String DB_MESSAGE_CREATE = "create table " + MESSAGE_TABLE + "("
            + MESSAGE_ID + " integer primary key autoincrement, "
            + MESSAGE_FROM_USER + " text, "
            + MESSAGE_TO_USER + " text, "
            + MESSAGE_CONTENT + " text, "
            + MESSAGE_DATE + " integer default 0, "
            + MESSAGE_KEY_ID + " integer default 0 "
            + ");";


    // ======== table USERS ============================

    static public String USER_TABLE = "users";

    static public String USER_ID = "_id";
    static public String USER_UUID = "uuid";
    static public String USER_NAME = "name";
    static public String USER_EMAIL = "email";
    static public String USER_PASSWORD = "password";
    static public String USER_KEY_ID = "key_id";
    static public String USER_GCM_ID = "gcm_id";

    // Скрипт создания таблицы
    static final String DB_USER_CREATE = "create table " + USER_TABLE + "("
            + USER_ID + " integer, "
            + USER_UUID + " text, "
            + USER_NAME + " text, "
            + USER_EMAIL + " text, "
            + USER_PASSWORD + " text, "
            + USER_GCM_ID + " text, "
            + USER_KEY_ID + " integer default 0 "
            + ");";


    // // Uri
    // authority
    static public String MESSAGE_AUTHORITY = "ru.orok.chat.providers.Message";

    // path
    static public String MESSAGE_PATH = "messages";

    static public String USER_PATH = "users";

    // Общий Uri
    static public final Uri MESSAGE_URI = Uri.parse("content://"
            + MESSAGE_AUTHORITY + "/" + MESSAGE_PATH);
    static public final Uri MESSAGE_LASTDATE_URI = Uri.parse("content://"
            + MESSAGE_AUTHORITY + "/" + MESSAGE_PATH + "/lastdate");

    static public final Uri  USER_URI = Uri.parse("content://"
            + MESSAGE_AUTHORITY + "/" + USER_PATH);

    static public final Uri  USER_FIRST_URI = Uri.parse("content://"
            + MESSAGE_AUTHORITY + "/" + USER_PATH + "/firstuser");
    static public final Uri  USER_SECOND_URI = Uri.parse("content://"
            + MESSAGE_AUTHORITY + "/" + USER_PATH + "/seconduser");



    // Типы данных
    // набор строк
    static final String MESSAGE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + MESSAGE_AUTHORITY + "." + MESSAGE_PATH;

    // одна строка
    static final String MESSAGE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + MESSAGE_AUTHORITY + "." + MESSAGE_PATH;

    //// UriMatcher
    static final int URI_MESSAGES = 101;
    static final int URI_MESSAGES_ID = 102;
    static final int URI_MESSAGES_LASTDATE = 103;

    static final int URI_USERS = 201;
    static final int URI_USERS_ID = 202;
    static final int URI_USERS_FIRST = 203;
    static final int URI_USERS_SECOND = 204;

    static final int FIRST_USER_ID = 1;
    static final int SECOND_USER_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MESSAGE_AUTHORITY, MESSAGE_PATH, URI_MESSAGES);
        uriMatcher.addURI(MESSAGE_AUTHORITY, MESSAGE_PATH + "/lastdate", URI_MESSAGES_LASTDATE);
        uriMatcher.addURI(MESSAGE_AUTHORITY, MESSAGE_PATH + "/#", URI_MESSAGES_ID);

        uriMatcher.addURI(MESSAGE_AUTHORITY, USER_PATH, URI_USERS);
        uriMatcher.addURI(MESSAGE_AUTHORITY, USER_PATH + "/#", URI_USERS_ID);
        uriMatcher.addURI(MESSAGE_AUTHORITY, USER_PATH + "/firstuser", URI_USERS_FIRST);
        uriMatcher.addURI(MESSAGE_AUTHORITY, USER_PATH + "/seconduser", URI_USERS_SECOND);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // чтение
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());

        Cursor cursor;
        String id;

        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_MESSAGES_LASTDATE:
                db = dbHelper.getWritableDatabase();
                cursor = db.query(MESSAGE_TABLE, new String[] {"MAX(date) as lastdate" }, MESSAGE_FROM_USER + " = ?", new String[]{UsersData.getInstance().getUserUuid(2)}, null, null, null, "1");
                return cursor;

            case URI_USERS: // общий Uri
                Log.d(LOG_TAG, "URI_USERS");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = USER_ID + " ASC";
                }
                db = dbHelper.getWritableDatabase();
                cursor = db.query(USER_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                // просим ContentResolver уведомлять этот курсор
                // об изменениях данных в CONTACT_CONTENT_URI
                cursor.setNotificationUri(getContext().getContentResolver(),
                        USER_URI);
                return cursor;

            case URI_MESSAGES: // общий Uri
                Log.d(LOG_TAG, "URI_MESSAGES");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MESSAGE_DATE + " ASC";
                }
                db = dbHelper.getWritableDatabase();
                cursor = db.query(MESSAGE_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                // просим ContentResolver уведомлять этот курсор
                // об изменениях данных в CONTACT_CONTENT_URI
                cursor.setNotificationUri(getContext().getContentResolver(),
                        MESSAGE_URI);
                return cursor;

            case URI_MESSAGES_ID: // Uri с ID
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = MESSAGE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + MESSAGE_ID + " = " + id;
                }
                db = dbHelper.getWritableDatabase();
                cursor = db.query(MESSAGE_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                // просим ContentResolver уведомлять этот курсор
                // об изменениях данных в CONTACT_CONTENT_URI
                cursor.setNotificationUri(getContext().getContentResolver(),
                        MESSAGE_URI);
                return cursor;

            case URI_USERS_FIRST:
                id = "1";
                Log.d(LOG_TAG, "URI_USERS_FIRST");
                selection = USER_ID + " = " + id;
                db = dbHelper.getWritableDatabase();
                cursor = db.query(USER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), USER_FIRST_URI);
                return cursor;

            case URI_USERS_SECOND:
                id = "2";
                Log.d(LOG_TAG, "URI_USERS_SECOND");
                selection = USER_ID + " = " + id;
                db = dbHelper.getWritableDatabase();
                cursor = db.query(USER_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), USER_SECOND_URI);
                return cursor;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());

        long rowID;
        Uri resultUri;

        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                db = dbHelper.getWritableDatabase();
                rowID = db.insert(USER_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(USER_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            case URI_MESSAGES:
                db = dbHelper.getWritableDatabase();
                rowID = db.insert(MESSAGE_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(MESSAGE_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());

        String id;
        int cnt;

        switch (uriMatcher.match(uri)) {
            case URI_MESSAGES:
                Log.d(LOG_TAG, "URI_MESSAGES");
                db = dbHelper.getWritableDatabase();
                cnt = db.delete(MESSAGE_TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            case URI_MESSAGES_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_MESSAGES_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = MESSAGE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + MESSAGE_ID + " = " + id;
                }

                db = dbHelper.getWritableDatabase();
                cnt = db.delete(MESSAGE_TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());

        String id;
        int cnt;

        switch (uriMatcher.match(uri)) {
            case URI_MESSAGES:
                Log.d(LOG_TAG, "URI_MESSAGES");
                db = dbHelper.getWritableDatabase();
                cnt = db.update(MESSAGE_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            case URI_MESSAGES_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_MESSAGES_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = MESSAGE_ID + " = " + id;
                } else {
                    selection = selection + " AND " + MESSAGE_ID + " = " + id;
                }

                db = dbHelper.getWritableDatabase();
                cnt = db.update(MESSAGE_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;

            case URI_USERS_FIRST:
                id = "1";
                Log.d(LOG_TAG, "URI_USERS_FIRST, " + id);
                selection = USER_ID + " = " + id;
                db = dbHelper.getWritableDatabase();
                cnt = db.update(USER_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;

            case URI_USERS_SECOND:
                id = "2";
                Log.d(LOG_TAG, "URI_USERS_FIRST, " + id);
                selection = USER_ID + " = " + id;
                db = dbHelper.getWritableDatabase();
                cnt = db.update(USER_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_MESSAGES:
                return MESSAGE_CONTENT_TYPE;
            case URI_MESSAGES_ID:
                return MESSAGE_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_MESSAGE_CREATE);
            db.execSQL(DB_USER_CREATE);

            ContentValues cv;
//            cv = new ContentValues();
//            for (int i = 1; i <= 3; i++) {
//                cv.put(MESSAGE_FROM_USER, "from_user " + i);
//                cv.put(MESSAGE_TO_USER, "to_user " + i);
//                cv.put(MESSAGE_CONTENT, "content " + i);
//                db.insert(MESSAGE_TABLE, null, cv);
//            }

            cv = new ContentValues();
            cv.put(USER_ID, 1);
            cv.put(USER_NAME, "");
            cv.put(USER_EMAIL, "");
            cv.put(USER_PASSWORD, "");
            cv.put(USER_UUID, "");
            cv.put(USER_GCM_ID, "");
            db.insert(USER_TABLE, null, cv);

            cv = new ContentValues();
            cv.put(USER_ID, 2);
            cv.put(USER_NAME, "");
            cv.put(USER_EMAIL, "");
            cv.put(USER_PASSWORD, "");
            cv.put(USER_UUID, "");
            cv.put(USER_GCM_ID, "");
            db.insert(USER_TABLE, null, cv);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            onCreate(db);
        }
    }
}
