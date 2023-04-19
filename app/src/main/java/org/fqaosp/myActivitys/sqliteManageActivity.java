package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.preventDismissDialog;
import static org.fqaosp.utils.multiFunc.showInfoMsg;
import static org.fqaosp.utils.multiFunc.showMyDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.fqaosp.R;
import org.fqaosp.sql.killAppDB;
import org.fqaosp.utils.fuckActivity;
import org.fqaosp.utils.multiFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class sqliteManageActivity extends AppCompatActivity {

    private Activity me;
    private LinearLayout linearLayout1;
    private Button sqlitemab1,sqlitemab2,sqlitemab3;
    private HashMap<String, Integer> select;

    private ArrayList<Map.Entry<String, Integer> > list = new ArrayList<>();

    private TableLayout tableLayout ;
    private HorizontalScrollView horizontalScrollView ;
    private ScrollView scrollView;
    private killAppDB killAppdb = new killAppDB(sqliteManageActivity.this, "killApp.db", null, 1);

    private int mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlite_manage_activity);
        fuckActivity.getIns().add(this);
        me = this;
        linearLayout1 = findViewById(R.id.sqlitemalinearlayout1);
        sqlitemab1 = findViewById(R.id.sqlitemab1);
        sqlitemab2 = findViewById(R.id.sqlitemab2);
        sqlitemab3 = findViewById(R.id.sqlitemab3);

        tableLayout = new TableLayout(me);
        horizontalScrollView = new HorizontalScrollView(me);
        scrollView = new ScrollView(me);
        mode =0;
        sqlitemab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == 0){
                    showDatabase(killAppdb,tableLayout,horizontalScrollView,scrollView);
                }
            }
        });


        sqlitemab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExecutorService cacheThreadPool = Executors.newFixedThreadPool(4);
                AlertDialog show = showMyDialog(sqliteManageActivity.this,"提示","正在更新数据库,请稍后(可能会出现无响应，请耐心等待)....");
                preventDismissDialog(show);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        int childCount = tableLayout.getChildCount();
                        Map.Entry<String, Integer> entry = null;
                        for(int i =1 ;i<childCount;i++){
                            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                            EditText editText = (EditText) tableRow.getChildAt(0);
                            EditText editText2 = (EditText) tableRow.getChildAt(1);
                            if(mode == 0){
                                entry = list.get(i-1);
                            }

                            Map.Entry<String, Integer> finalEntry = entry;
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if(mode ==0){
                                        if(editText.getText().toString().isEmpty() || editText2.getText().toString().isEmpty()){
                                            killAppdb.delete(finalEntry.getKey(), finalEntry.getValue());
                                        }
                                        if(select.get(editText.getText().toString()) == null){
                                            try {
                                                killAppdb.update(finalEntry.getKey(), finalEntry.getValue(),editText.getText().toString(),Integer.valueOf(editText2.getText().toString()));
                                            }catch (Exception e){
                                                Toast.makeText(me, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                }
                            };

                            cacheThreadPool.execute(runnable);

                        }

                        cacheThreadPool.shutdown();
                        while(true){
                            if(cacheThreadPool.isTerminated()){

                                if(mode ==0){
                                    showDatabase(killAppdb,tableLayout,horizontalScrollView,scrollView);
                                }

                                multiFunc.dismissDialog(show);
                                break;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });

            }
        });
        sqlitemab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == 0){
                    killAppdb.delete(null,null);
                    showDatabase(killAppdb,tableLayout,horizontalScrollView,scrollView);
                }
            }
        });


    }

    private void showDatabase(killAppDB killAppdb , TableLayout tableLayout,HorizontalScrollView horizontalScrollView,ScrollView scrollView){
        list.clear();
        linearLayout1.removeAllViews();
        horizontalScrollView.removeAllViews();
        scrollView.removeAllViews();
        tableLayout.removeAllViews();
        TableRow tableRow = initTableRow();
        String[] columNames = killAppdb.getColumNames();
        for (String columName : columNames) {
            addTableText(tableRow,columName);
        }
        tableLayout.addView(tableRow);
        select = killAppdb.select(null, null);
        for (Map.Entry<String, Integer> entry : select.entrySet()) {
            addTableData(tableLayout,entry);
            list.add(entry);
        }
        horizontalScrollView.addView(tableLayout);
        scrollView.addView(horizontalScrollView);
        linearLayout1.addView(scrollView);
    }

    private EditText getEditText(String text){
        EditText et = new EditText(me);
        et.setText(text);
        return et;
    }

    private TableRow initTableRow(){
        TableRow tableRow = new TableRow(me);
        return  tableRow;
    }


    private void  addTableData(TableLayout tableLayout,Map.Entry<String, Integer> entry){
        TableRow tableRow = initTableRow();
        tableRow.addView(getEditText(entry.getKey()));
        tableRow.addView(getEditText(entry.getValue().toString()));
        tableLayout.addView(tableRow);
    }

    private void addTableText(TableRow tableRow , String text){
        TextView tv = new TextView(me);
        tv.setText(text);
        tableRow.addView(tv);
    }

    private void addText(LinearLayout layout , String text){
        TextView tv = new TextView(me);
        tv.setText(text);
        layout.addView(tv);
    }

    private LinearLayout initLayout(int state){
        LinearLayout layout = new LinearLayout(me);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(state);
        return layout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"切换killapp数据库");
        menu.add(Menu.NONE,1,1,"帮助");
        menu.add(Menu.NONE,2,2,"退出");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                mode=0;
                break;
            case 1:
                showInfoMsg(this,"帮助信息","该页面是用于编辑该软件产生的数据库文件。\r\n" +
                        "1.killapp（后台管理），即保存的默认后台进程信息.\r\n" +
                        "2.应用分身，即保存的应用分身信息。\r\n"
                );
                break;
            case 2:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }


}
