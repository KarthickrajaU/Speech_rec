package com.example.speech;

import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Databasehelper extends SQLiteOpenHelper {
    Context context;
    private String DB_PATH;
    private static final String DB_NAME = "sent.db";

    private SQLiteDatabase sqLiteDatabase;

    public Databasehelper(Context context) {
        super(context, DB_NAME, null, 1);

        this.context = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {

            this.getWritableDatabase();
            context.deleteDatabase(DB_NAME);
            copyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyDatabase() throws IOException {
        this.getWritableDatabase();
        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer)) > 0) {

            myOutput.write(buffer, 0, length);

        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void createDatabase() throws IOException {

        try {
            copyDatabase();
        } catch (IOException e) {
            throw new Error("Error Copying Database");
        }
        openDatabase();

    }


    public boolean checkDatabase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, OPEN_READONLY);


        } catch (SQLiteException e) {
            Log.e("error", String.valueOf(e));

        }

        if (checkDB != null) {
            checkDB.close();

        }

        return checkDB != null ? true : false;


    }

    public void openDatabase() {

        String path = DB_PATH + DB_NAME;
        sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, OPEN_READONLY);

    }

    @SuppressLint("Range")
    public ArrayList<String> get_data() {
        int min=1;
        int max=1756;
        int[] random_sentence_ID=new int[10];
        for(int i=0;i<10;i++) {
            Random randomNum = new Random();
            int showMe = min + randomNum.nextInt(max);
            random_sentence_ID[i]=showMe;
        }

        ArrayList<String> k = new ArrayList<>(10);
        String tmp;
        Cursor cursor;
        for (int i = 0; i < 10; i++) {
            cursor = sqLiteDatabase.rawQuery("select * from k where rowid='" + random_sentence_ID[i] + "'", null);
            if (cursor.moveToFirst()) {
                tmp = cursor.getString(cursor.getColumnIndex("word"));
                k.add(tmp);
            }
        }
        return k;
    }

}
