package com.coded.chatApp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coded.chatApp.Models.Chat;
import com.coded.chatApp.Models.User;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "application.db";

    /**
     * Table Name-
     * @CHAT
     * @USER
     */

    private static final String TABLE_CHAT = "Table_Chats";
    private static final String TABLE_USER = "Table_Users";
    private static final String TABLE_CONTACTS = "Table_Contacts";

    /**
     * COLUMN names in the the tables
     * CHAT_ for the Chats Table
     * USER_ for the User Table
     * uid for the fireBase Id
     */

    private static final String CHAT_ID = "ID";
    private static final String CHAT_SENDER = "SENDER";
    private static final String CHAT_RECEIVER = "RECEIVER";
    private static final String CHAT_MESSAGE = "MESSAGE";
    private static final String CHAT_ISSEEN = "IS_SEEN";
    private static final String CHAT_STATUS = "IS_DELIVERED";

    private static final String USER_ID = "ID";
    private static final String USER_USERID = "USER_ID";
    private static final String USER_IMAGE = "IMAGE_URL";
    private static final String USER_PHONE_NUMBER = "PHONE_NUMBER";
    private static final String USER_NAME = "USERNAME";
    private static final String USER_STATUS = "STATUS";

    private static final String CONTACT_ID = "ID";
    private static final String CONTACT_USERID = "CONTACT_USER_ID";
    private static final String CONTACT_IMAGE = "CONTACT_IMAGE_URL";
    private static final String CONTACT_PHONE_NUMBER = "CONTACT_NUMBER";
    private static final String CONTACT_NAME = "CONTACT_NAME";
    private static final String CONTACT_STATUS = "CONTACT_STATUS";

    /**
     * Constructor for the database #application.dp
     * @param context activity context
     * @param name name of the database
     * @param factory null
     * @param version version of the database
     */
    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String chatquery = "CREATE TABLE " + TABLE_CHAT + " (" +
                CHAT_ID + " INTEGER PRIMARY KEY, " +
                CHAT_SENDER + " TEXT, " +
                CHAT_RECEIVER + " TEXT, " +
                CHAT_MESSAGE + " TEXT, " +
                CHAT_ISSEEN + " TEXT, " +
                CHAT_STATUS + " TEXT"+
                ");";

        String userquery = "CREATE TABLE " + TABLE_USER + " (" +
                USER_ID + " INTEGER PRIMARY KEY, " +
                USER_USERID + " TEXT, " +
                USER_NAME + " TEXT," +
                USER_IMAGE + " TEXT, " +
                USER_STATUS + " TEXT," +
                USER_PHONE_NUMBER + " TEXT" +
                ");";

        String contactquery = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                CONTACT_ID + " INTEGER PRIMARY KEY, " +
                CONTACT_USERID + " TEXT, " +
                CONTACT_NAME + " TEXT, " +
                CONTACT_IMAGE + " TEXT, " +
                CONTACT_STATUS + " TEXT," +
                CONTACT_PHONE_NUMBER + " TEXT" +
                ");";

        db.execSQL(chatquery);
        db.execSQL(userquery);
        db.execSQL(contactquery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_CONTACTS);
        onCreate(db);
    }

    /**
     * For adding the user in the database
     * @param user User class object
     */
    public void addUsers(User user){
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_USERID,user.getId());
        contentValues.put(USER_NAME,user.getUsername());
        contentValues.put(USER_IMAGE,user.getImageurl());
        contentValues.put(USER_STATUS,user.getStatus());
        contentValues.put(USER_PHONE_NUMBER,user.getPhonenumber());

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_USER,null,contentValues);
        sqLiteDatabase.close();
    }

    /**
     * For adding the chats in the database
     * @param chat Chat class object
     */
    public void addChats(Chat chat,String status){
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAT_SENDER,chat.getSender());
        contentValues.put(CHAT_RECEIVER,chat.getReceiver());
        contentValues.put(CHAT_MESSAGE,chat.getMessage());
        contentValues.put(CHAT_ISSEEN,String.valueOf(chat.isIsseen()));
        contentValues.put(CHAT_STATUS,status);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_CHAT,null,contentValues);
        sqLiteDatabase.close();
    }

    /**
     * @param user User Class object
     */
    public void addContactsUser(User user)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CONTACT_USERID,user.getId());
        contentValues.put(CONTACT_NAME,user.getUsername());
        contentValues.put(CONTACT_IMAGE,user.getImageurl());
        contentValues.put(CONTACT_STATUS,user.getStatus());
        contentValues.put(CONTACT_PHONE_NUMBER,user.getPhonenumber());

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_CONTACTS,null,contentValues);
        sqLiteDatabase.close();
    }

    /**
     * @see #viewUser()
     * @see #addUsers(User)
     */
    public void clearUserTable(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_USER,null,null);
    }

    public Cursor viewUser()
    {
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        String query = "Select * from " + TABLE_USER;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    /**
     * @see #viewChatUser()
     * @see #addChats(Chat, String)
     */
    public void clearChatTable(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CHAT,null,null);
    }

    public Cursor viewChatUser()
    {
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        String query = "Select * from " + TABLE_CHAT;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    public Cursor viewQueue(String status)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "Select * from " + TABLE_CHAT + " WHERE "+ CHAT_STATUS + " = '" + status + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    /**
     * @param id for
     * @param status "Delivered", "send" or "seen"
     */

    /**
     * @see #viewContactUser()
     * @see #addContactsUser(User)
     */
    public void clearContactUsertable(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CONTACTS,null,null);
    }

    public Cursor viewContactUser()
    {
        SQLiteDatabase sqLiteDatabase= getWritableDatabase();
        String query = "Select * from " + TABLE_CONTACTS;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }


}
