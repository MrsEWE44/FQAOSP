package org.fqaosp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class killAppDB extends SQLiteOpenHelper {
    public killAppDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTest = "create table killApp (pkgname varchar(128) ,status int)";
        sqLiteDatabase.execSQL(createTest);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String pkgname , int status){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgname",pkgname);
        contentValues.put("status",status+"");
        long killApp = writableDatabase.insert("killApp", null, contentValues);
        writableDatabase.close();
    }

    public int count(){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        int count  =0;
        String sql = "select count(*) from killApp";
        Cursor cursor = writableDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            count = cursor.getInt(0);
            cursor.moveToNext();
        }
        writableDatabase.close();
        writableDatabase.close();
        return count;
    }


    public void delete(String pkgname,int status){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "delete from killApp where pkgname='"+pkgname+"' and status="+status;
        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public void update(String pkgname,int status , String newpkgname , int newstatus){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "update killApp set status="+newstatus +",pkgname='"+newpkgname+"' where pkgname='"+pkgname+"' and status="+status;
        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public HashMap<String,Integer> select(String pkgname, Integer status){
        SQLiteDatabase writableDatabase = this.getReadableDatabase();
        HashMap<String, Integer> map = new HashMap<>();
        String sql = "select * from killApp where pkgname='"+pkgname+"' and status="+status;

        if(pkgname == null && status != null){
            sql = "select * from killApp where status="+status;
        }

        if(status == null && pkgname != null){
            sql = "select * from killApp where pkgname='"+pkgname+"'";
        }

        if(pkgname == null && status == null){
            sql = "select * from killApp ";
        }

        Cursor cursor = writableDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            map.put(cursor.getString(0),cursor.getInt(1));
            cursor.moveToNext();
        }
        writableDatabase.close();
        return map;
    }

    public String[] getColumNames(){
        SQLiteDatabase writableDatabase = this.getReadableDatabase();
        String sql = "select * from killApp where 0";
        Cursor cursor = writableDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        writableDatabase.close();
        return columnNames;
    }


}
