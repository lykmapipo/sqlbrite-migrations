package com.github.lykmapipo.sqlbrite.migrations;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import org.yaml.snakeyaml.Yaml;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * A helper class to manage database migrations and seeding using
 * an application's raw asset files.
 * <p>
 * This class provides developers with a simple way to ship their Android app
 * with migrations files which manage database creation and any upgrades required with subsequent
 * version releases.
 * <p>
 * <p>For examples see <a href="https://github.com/lykmapipo/sqlbrite-migrations">
 * https://github.com/lykmapipo/sqlbrite-migrations</a>
 * <p>
 * <p class="note"><strong>Note:</strong> this class assumes
 * monotonically increasing version numbers for upgrades.</p>
 */
public class SQLBriteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLBriteOpenHelper.class.getSimpleName();
    private String migrationDir = "migrations";
    private Context context;
    private boolean isOnTestMode = false;


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     */
    public SQLBriteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     */
    public SQLBriteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context = context;
    }


    /**
     * Create a helper object to create, open, and/or manage a testing database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     */
    public SQLBriteOpenHelper(Context context, String name, int version, boolean testing) {
        super(context, name, null, version);
        this.context = context;
        this.isOnTestMode = testing;
    }


    /**
     * Create a {@link BriteDatabase} instance.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     * @see BriteDatabase
     */
    public synchronized static BriteDatabase get(Context context, String name, int version) {
        SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, null, version);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        return briteDatabase;
    }


    /**
     * Create a {@link BriteDatabase} instance.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     * @see BriteDatabase
     */
    public synchronized static BriteDatabase get(Context context, String name,
                                                 SQLiteDatabase.CursorFactory factory, int version) {
        SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, factory, version);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        return briteDatabase;
    }


    /**
     * Create a {@link BriteDatabase} instance.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     * @see BriteDatabase
     */
    public synchronized static BriteDatabase get(Context context, String name,
                                                 SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, factory, version, errorHandler);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        return briteDatabase;
    }

    /**
     * Create a {@link BriteDatabase} instance for testing.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @see SQLBriteOpenHelper
     * @see BriteDatabase
     */
    public synchronized static BriteDatabase get(Context context, String name, int version, boolean testing) {
        SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, version, testing);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.immediate());
        return briteDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Map<String, List<String>> parsed = parse();
            up(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Map<String, List<String>> parsed = parse(newVersion);
            up(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        //TODO implement down migrations
    }

    /**
     * Load and parse initial database migrations.
     * <p>
     * It used it creating initial database
     * </p>
     *
     * @return
     * @throws IOException
     */
    public synchronized Map<String, List<String>> parse() throws IOException {
        return this.parse(1);
    }


    /**
     * Parse migration file
     * <p>
     * It used to upgrade database to newer version
     * </p>
     *
     * @param newVersion newer database version
     * @return
     * @throws IOException
     */
    public synchronized Map<String, List<String>> parse(int newVersion) throws IOException {

        //obtain migration path
        InputStream inputStream = null;
        String migrationPath = migrationDir + "/" + newVersion + ".yaml";

        //handle test mode
        if (isOnTestMode) {
            migrationPath = newVersion + ".yaml";
            inputStream = this.getClass().getClassLoader().getResourceAsStream(migrationPath);
        } else {
            inputStream = this.context.getAssets().open(migrationPath);
        }

        //parse migration content
        Yaml yaml = new Yaml();
        Map<String, List<String>> parsed = (Map) yaml.load(inputStream);

        return parsed;

    }


    /**
     * Apply up migration scripts and seed database with provided seeds
     *
     * @param database
     * @param scripts
     */
    private void up(SQLiteDatabase database, Map<String, List<String>> scripts) {
        database.beginTransaction();
        try {
            //obtain up migrations
            List<String> ups = scripts.get("up");

            //apply up migrations
            if (ups != null) {
                for (String script : ups) {
                    database.execSQL(script);
                }
            }

            //obtain seeds
            List<String> seeds = scripts.get("seeds");

            //apply seed migrations
            if (ups != null) {
                for (String script : seeds) {
                    database.execSQL(script);
                }
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}
