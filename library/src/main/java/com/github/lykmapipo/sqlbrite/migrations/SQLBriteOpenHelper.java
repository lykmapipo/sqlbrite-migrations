package com.github.lykmapipo.sqlbrite.migrations;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import io.reactivex.schedulers.Schedulers;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    private String migrationDir = "migrations";
    private Context context;
    private int version = 1; //current database version to support seed up to requested database version
    private boolean isOnTestMode = false;
    private static BriteDatabase briteDatabase;


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
        this.version = version;
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
        this.version = version;
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
        this.version = version;
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
        if (briteDatabase == null) {
            SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, null, version);
            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        }
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
        if (briteDatabase == null) {
            SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, factory, version);
            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        }
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
                                                 SQLiteDatabase.CursorFactory factory, int version,
                                                 DatabaseErrorHandler errorHandler) {
        if (briteDatabase == null) {
            SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, factory, version, errorHandler);
            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        }
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
        //always return new instance on test mode
        SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, version, testing);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.trampoline());
        return briteDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //run all migrations from begin to current requested database version
            int version = db.getVersion();
            version = version > this.version ? version : this.version; //ensure we start at requested database version
            List<Map<String, List<String>>> parsed = parse(0, version, true);
            up(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    /**
     * Upgrade database to latest new version
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            //load all migrations from old version to current version
            List<Map<String, List<String>>> parsed = parse(oldVersion, newVersion, true);

            //upgrade database
            up(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    /**
     * Downgrade database to previous old version
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            //load all migrations from old version to current version
            List<Map<String, List<String>>> parsed = parse(oldVersion, newVersion, false);

            //upgrade database
            down(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }


    /**
     * Load and parse all required database migrations.
     *
     * @return
     * @throws IOException
     */
    public synchronized List<Map<String, List<String>>> parse(int oldVersion, int newVersion, boolean up) throws IOException {

        //collect all require migrations
        List<Map<String, List<String>>> scripts = new ArrayList<Map<String, List<String>>>();

        //iterate over all required migrations
        if (up) {
            //parse up migrations
            int startVersion = oldVersion + 1;
            for (int i = startVersion; i <= newVersion; i++) {
                Map<String, List<String>> script = this.parse(i);
                scripts.add(script);
            }
        } else {
            //parse down migrations
            for (int i = oldVersion; i > newVersion; i--) {
                Map<String, List<String>> script = this.parse(i);
                scripts.add(script);
            }
        }

        return scripts;
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
    private synchronized void up(SQLiteDatabase database, Map<String, List<String>> scripts) {
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

    /**
     * Apply up migration scripts and seed database with provided seeds
     *
     * @param database
     * @param scripts
     */
    private synchronized void up(SQLiteDatabase database, List<Map<String, List<String>>> scripts) {
        //ensure we apply or fail as whole
        database.beginTransaction();
        try {
            //start apply all migrations
            for (Map<String, List<String>> script : scripts) {
                up(database, script);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }


    /**
     * Apply down migration scripts
     *
     * @param database
     * @param scripts
     */
    private synchronized void down(SQLiteDatabase database, Map<String, List<String>> scripts) {
        database.beginTransaction();
        try {
            //obtain down migrations
            List<String> downs = scripts.get("down");

            //apply down migrations
            if (downs != null) {
                for (String script : downs) {
                    database.execSQL(script);
                }
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Apply down migration scripts
     *
     * @param database
     * @param scripts
     */
    private synchronized void down(SQLiteDatabase database, List<Map<String, List<String>>> scripts) {
        //ensure we apply or fail as whole
        database.beginTransaction();
        try {
            //start apply all migrations
            for (Map<String, List<String>> script : scripts) {
                down(database, script);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}
