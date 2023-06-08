package org.fqaosp.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class iptablesDB extends SQLiteOpenHelper {

    //0是禁用联网

    private String table = "iptable";

    public iptablesDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public iptablesDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTest = "create table "+table+" (pkgname varchar(128) ,status int)";
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
        long killApp = writableDatabase.insert(table, null, contentValues);
        writableDatabase.close();
    }

    public int count(){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        int count  =0;
        String sql = "select count(*) from "+table;
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


    public void delete(String pkgname,Integer status){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "delete from "+table+" where pkgname='"+pkgname+"' and status="+status;

        if(pkgname == null && status != null){
            sql = "delete from "+table+" where status="+status;
        }

        if(pkgname == null && status == null){
            sql = "delete from "+table;
        }

        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public void update(String pkgname,int status , String newpkgname , int newstatus){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "update "+table+" set status="+newstatus +",pkgname='"+newpkgname+"' where pkgname='"+pkgname+"' and status="+status;
        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public HashMap<String,Integer> select(String pkgname, Integer status){
        SQLiteDatabase writableDatabase = this.getReadableDatabase();
        HashMap<String, Integer> map = new HashMap<>();
        String sql = "select * from "+table+" where pkgname='"+pkgname+"' and status="+status;

        if(pkgname == null && status != null){
            sql = "select * from "+table+" where status="+status;
        }

        if(status == null && pkgname != null){
            sql = "select * from "+table+" where pkgname='"+pkgname+"'";
        }

        if(pkgname == null && status == null){
            sql = "select * from  "+table;
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
        String sql = "select * from "+table+" where 0";
        Cursor cursor = writableDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        writableDatabase.close();
        return columnNames;
    }



}
