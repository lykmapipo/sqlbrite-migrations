package com.github.lykmapipo.sqlbrite.migrations;

import android.database.Cursor;
import rx.functions.Func1;

/**
 * Created by lally on 3/18/17.
 */
public class Brite {
    private String name;

    public Brite(String name) {
        this.name = name;
    }

    static final Func1<Cursor, Brite> MAPPER = new Func1<Cursor, Brite>() {
        @Override public Brite call(Cursor cursor) {
            return new Brite(cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
        }
    };
}
