package org.fqaosp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.fqaosp.entity.workProfileDBEntity;

import java.util.ArrayList;

public class workProfileDB  extends SQLiteOpenHelper {

    public workProfileDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTest = "create table workProfile (uid int not null,pkgname varchar(128))";
        sqLiteDatabase.execSQL(createTest);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insert(String pkgname , Integer uid){
//        Log.d("wpdb :: ", " insert :::: pkgname : " + pkgname + " -- uid : " + uid);
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "insert into workProfile (pkgname,uid) values ('"+pkgname+"',"+uid+")";
        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public int count(){
        SQLiteDatabase writableDatabase = this.getReadableDatabase();
        int count  =0;
        String sql = "select count(*) from workProfile";
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



    public void delete(String pkgname,Integer uid){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "delete from workProfile where pkgname='"+pkgname+"' and uid="+uid;

        if(pkgname == null && uid != null){
            sql = "delete from workProfile where uid="+uid;
        }
        if(pkgname == null && uid == null){
            sql = "delete from workProfile";
        }


        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public void update(String pkgname,Integer uid , String newpkgname , Integer newuid){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        String sql = "update workProfile set uid="+newuid +",pkgname='"+newpkgname+"' where pkgname='"+pkgname+"' and uid="+uid;
        writableDatabase.execSQL(sql);
        writableDatabase.close();
    }

    public ArrayList<workProfileDBEntity> select(String pkgname, Integer uid){
        SQLiteDatabase writableDatabase = this.getReadableDatabase();
        ArrayList<workProfileDBEntity> workProfileDBEntities = new ArrayList<>();
        String sql = "select * from workProfile where pkgname='"+pkgname+"' and uid="+uid;

        if(pkgname == null && uid != null){
            sql = "select * from workProfile where uid="+uid;
        }

        if(uid == null && pkgname != null){
            sql = "select * from workProfile where pkgname='"+pkgname+"'";
        }

        if(pkgname == null && uid == null){
            sql = "select * from workProfile ";
        }

        Cursor cursor = writableDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            workProfileDBEntities.add(new workProfileDBEntity(cursor.getString(1),cursor.getInt(0)));
            cursor.moveToNext();
        }
        writableDatabase.close();
        return workProfileDBEntities;
    }

    public String[] getColumNames(){
        SQLiteDatabase writableDatabase = this.getReadableDatabase();
        String sql = "select * from workProfile where 0";
        Cursor cursor = writableDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        writableDatabase.close();
        return columnNames;
    }


}
