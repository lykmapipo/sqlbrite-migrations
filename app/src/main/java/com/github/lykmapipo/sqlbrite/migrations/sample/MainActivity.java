package com.github.lykmapipo.sqlbrite.migrations.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper;
import com.squareup.sqlbrite2.BriteDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BriteDatabase database;
    private BriteAdapter adapter;
    private Disposable disposable;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new BriteAdapter(getApplicationContext());
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        database = SQLBriteOpenHelper.get(getApplicationContext(), "brite", 1);

        disposable = database.createQuery("brites", "SELECT * FROM brites")
                .mapToList(Brite.MAPPER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.dispose();
    }
}
