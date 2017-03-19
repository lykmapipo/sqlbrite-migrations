package com.github.lykmapipo.sqlbrite.migrations.sample;

import android.database.Cursor;
import rx.functions.Func1;

/**
 * Created by lally on 3/18/17.
 */
public class Brite {

    static final Func1<Cursor, Brite> MAPPER = new Func1<Cursor, Brite>() {
        @Override public Brite call(Cursor cursor) {
            return new Brite(
                    cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
        }
    };

    private Long id;
    private String name;

    public Brite(String name) {
        this.name = name;
    }

    public Brite(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Brite brite = (Brite) o;

        if (id != null ? !id.equals(brite.id) : brite.id != null) return false;
        return name != null ? name.equals(brite.name) : brite.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Brite{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
