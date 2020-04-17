package com.example.tranquoctrungcntt.uchat.OtherClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tranquoctrungcntt.uchat.Models.StickerToShow;
import com.example.tranquoctrungcntt.uchat.Objects.Message;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AppLocalDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppLocalDatabase";
    private static final String MESSAGE_TABLE_NAME = "TABLE_MESSAGE";
    private static final String MESSAGE_ID = "MESSAGE_ID";
    private static final String MESSAGE = "MESSAGE";
    private static final String STICKER_TABLE_NAME = "TABLE_STICKER";
    private static final String STICKER = "STICKER";
    private static final String STICKER_ID = "STICKER_ID";

    public AppLocalDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String stickerQuery = "CREATE TABLE IF NOT EXISTS " + STICKER_TABLE_NAME + "(" + STICKER_ID + " INTEGER PRIMARY KEY, " + STICKER + " TEXT)";
        String messageQuery = "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE_NAME + "(" + MESSAGE_ID + " TEXT PRIMARY KEY, " + MESSAGE + " TEXT)";
        db.execSQL(stickerQuery);
        db.execSQL(messageQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STICKER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
        onCreate(db);
    }

    public void deleteAllSticker() {
        final SQLiteDatabase database = getWritableDatabase();
        if (database != null) {
            database.execSQL(" DELETE FROM " + STICKER_TABLE_NAME);
            database.close();
        }
    }

    //Add new a student
    public void addSticker(StickerToShow stickerToShow) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        final Gson gson = new Gson();

        final String toStoreObject = gson.toJson(stickerToShow, StickerToShow.class);

        values.put(STICKER, toStoreObject);
        //Neu de null thi khi value bang null thi loi

        db.insert(STICKER_TABLE_NAME, null, values);

        db.close();
    }

    public ArrayList<StickerToShow> getAllSticker() {
        final ArrayList<StickerToShow> stickerList = new ArrayList<>();
        // Select All Query
        final String selectQuery = "SELECT  * FROM " + STICKER_TABLE_NAME;

        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(selectQuery, null);

        final Gson gson = new Gson();

        if (cursor.moveToFirst()) {
            do {
                final StickerToShow stickerToShow = gson.fromJson(cursor.getString(1), StickerToShow.class);
                stickerList.add(stickerToShow);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return stickerList;
    }

    public void addMessage(Message message) {

        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        final Gson gson = new Gson();

        final String messageToStorage = gson.toJson(message, Message.class);

        values.put(MESSAGE_ID, message.getMessageId());
        values.put(MESSAGE, messageToStorage);
        //Neu de null thi khi value bang null thi loi

        db.insert(MESSAGE_TABLE_NAME, null, values);

        db.close();

    }

    public ArrayList<Message> getAllMessage() {
        final ArrayList<Message> messageList = new ArrayList<>();
        // Select All Query
        final String selectQuery = "SELECT  * FROM " + MESSAGE_TABLE_NAME;

        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(selectQuery, null);

        final Gson gson = new Gson();

        if (cursor.moveToFirst()) {
            do {
                final Message message = gson.fromJson(cursor.getString(1), Message.class);
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messageList;
    }

    public void deleteMessage(String messageId) {
        final SQLiteDatabase database = getWritableDatabase();
        if (database != null) {
            database.execSQL(" DELETE FROM " + MESSAGE_TABLE_NAME + " WHERE " + MESSAGE_ID + " = " + "'" + messageId + "'");
            database.close();
        }
    }


}

