package com.github.lykmapipo.sqlbrite.migrations.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper;
import com.squareup.sqlbrite.BriteDatabase;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BriteDatabase database;
    private BriteAdapter adapter;
    private Subscription subscription;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = SQLBriteOpenHelper.get(getApplicationContext(), "brite", 1);
        adapter = new BriteAdapter(getApplicationContext());
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscription = database.createQuery("brites", "SELECT * FROM brites")
                .mapToList(Brite.MAPPER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }
}
