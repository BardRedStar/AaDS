package com.tenxgames.aisd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.tenxgames.aisd.sqlite.SQLiteHelper;
import com.tenxgames.aisd.sqlite.SortRecord;

import java.util.ArrayList;

public class SortsLogActivity extends AppCompatActivity {

    private SQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorts_log);

        /// Получаем подключение к БД
        db = new SQLiteHelper(getApplicationContext());

        /// Получаем данные из базы
        ArrayList<SortRecord> listRecords = new ArrayList<>();
        db.getAllSortRecords(listRecords);

        /// Записываем их в лист через адаптер
        ExpandableListView expListView = findViewById(R.id.expListView);
        expListView.setAdapter(new ExpListAdapter(this, listRecords));
    }
}
