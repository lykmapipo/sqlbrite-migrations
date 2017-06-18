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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Brite brite = (Brite) o;

        return name != null ? name.equals(brite.name) : brite.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    static final Func1<Cursor, Brite> MAPPER = new Func1<Cursor, Brite>() {
        @Override
        public Brite call(Cursor cursor) {
            return new Brite(cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
        }
    };
}
