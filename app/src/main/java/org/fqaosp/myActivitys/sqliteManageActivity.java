package org.fqaosp.myActivitys;

import static org.fqaosp.utils.multiFunc.jump;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

import org.fqaosp.MainActivity;
import org.fqaosp.R;
import org.fqaosp.sql.DBHelp;
import org.fqaosp.sql.killAppDB;
import org.fqaosp.utils.fuckActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class sqliteManageActivity extends AppCompatActivity {

    private Activity me;
    private LinearLayout linearLayout1;
    private Button sqlitemab1,sqlitemab2;
    private HashMap<String, Integer> select;
    private ArrayList<Map.Entry<String, Integer> > list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlite_manage_activity);
        fuckActivity.getIns().add(this);
        me = this;
        linearLayout1 = findViewById(R.id.sqlitemalinearlayout1);
        sqlitemab1 = findViewById(R.id.sqlitemab1);
        sqlitemab2 = findViewById(R.id.sqlitemab2);
        TableLayout tableLayout = new TableLayout(me);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(me);
        ScrollView scrollView = new ScrollView(me);
        killAppDB killAppdb = new killAppDB(sqliteManageActivity.this, "killApp.db", null, 1);

        sqlitemab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatabase(killAppdb,tableLayout,horizontalScrollView,scrollView);
            }
        });


        sqlitemab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int childCount = tableLayout.getChildCount();
                for(int i =1 ;i<childCount;i++){
                   TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                   EditText editText = (EditText) tableRow.getChildAt(0);
                   EditText editText2 = (EditText) tableRow.getChildAt(1);

                    Map.Entry<String, Integer> entry = list.get(i-1);
                   if(editText.getText().toString().isEmpty() || editText2.getText().toString().isEmpty()){
                       killAppdb.delete(entry.getKey(),entry.getValue());
                   }
                    if(select.get(editText.getText().toString()) == null){
                        try {
                            killAppdb.update(entry.getKey(),entry.getValue(),editText.getText().toString(),Integer.valueOf(editText2.getText().toString()));
                        }catch (Exception e){
                            Toast.makeText(me, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                showDatabase(killAppdb,tableLayout,horizontalScrollView,scrollView);
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

    private void  addData(LinearLayout layout2,Map.Entry<String, Integer> entry){
        LinearLayout layout = initLayout(LinearLayout.HORIZONTAL);
        layout.addView(getEditText(entry.getKey()));
        layout.addView(getEditText(entry.getValue().toString()));
        layout2.addView(layout);
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
        menu.add(Menu.NONE,0,0,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case 0:
                fuckActivity.getIns().killall();
                ;
        }
        return super.onOptionsItemSelected(item);
    }


}
