package com.github.lykmapipo.sqlbrite.migrations;

import android.content.Context;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by lally on 3/18/17.
 */
@Config(sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class SQLBriteMigrationTest {
    private Context context;

    @Before
    public void setup() {
        context = ShadowApplication.getInstance().getApplicationContext();
    }

    @Test
    public void shouldBeAbleToLoadAndParseDefaultMigrationsFromInputStream() throws IOException {
        SQLBriteOpenHelper briteOpenHelper = new SQLBriteOpenHelper(context, "brite", 1, true);
        Map parsed = briteOpenHelper.parse();

        assertThat(parsed.get("up"), is(not(equalTo(null))));
        assertThat(parsed.get("down"), is(not(equalTo(null))));
        assertThat(parsed.get("seeds"), is(not(equalTo(null))));
    }

    @Test
    public void shouldBeAbleToLoadAndParseSpecificMigrationsFromInputStream() throws IOException {
        SQLBriteOpenHelper briteOpenHelper = new SQLBriteOpenHelper(context, "brite", 1, true);
        Map parsed = briteOpenHelper.parse(2);

        assertThat(parsed.get("up"), is(not(equalTo(null))));
        assertThat(parsed.get("down"), is(not(equalTo(null))));
        assertThat(parsed.get("seeds"), is(not(equalTo(null))));
    }

    @Test
    public void shouldBeAbleToLoadAndParseAllMigrationsFromInputStream() throws IOException {
        SQLBriteOpenHelper briteOpenHelper = new SQLBriteOpenHelper(context, "brite", 1, true);
        List<Map<String, List<String>>> parsed = briteOpenHelper.parse(1, 2, true);

        assertThat(parsed, is(not(equalTo(null))));
        assertThat(parsed.size(), is((equalTo(1))));
    }

    @Test
    public void shouldBeAbleToLoadAndParseAllMigrationsFromInputStreamV2() throws IOException {
        SQLBriteOpenHelper briteOpenHelper = new SQLBriteOpenHelper(context, "brite", 1, true);
        List<Map<String, List<String>>> parsed = briteOpenHelper.parse(1, 3, true);

        assertThat(parsed, is(not(equalTo(null))));
        assertThat(parsed.size(), is((equalTo(2))));
    }

    @Test
    public void shouldBeAbleToGetSQLBriteDatabase() {
        BriteDatabase database = SQLBriteOpenHelper.get(context, "brite", 1, true);
        assertThat(database, is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToCreateAndSeedInitialDatabase() {
        BriteDatabase database = SQLBriteOpenHelper.get(context, "brite", 1, true);
        TestSubscriber<Brite> subscriber = new TestSubscriber<>();

        database.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber);

        //asserts
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
    }

    @Test
    public void shouldBeAbleToCreateAndSeedInitialDatabaseV2() {
        BriteDatabase database = SQLBriteOpenHelper.get(context, "brite", 3, true);
        TestSubscriber<Brite> subscriber = new TestSubscriber<>();

        database.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 3.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber);

        //asserts
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertValue(new Brite("Test Debug 3.2"));
    }

    @Test
    public void shouldBeAbleToUpgradeAndSeedDatabase() {
        BriteDatabase database1 = SQLBriteOpenHelper.get(context, "brite", 1, true);
        BriteDatabase database2 = SQLBriteOpenHelper.get(context, "brite", 2, true);

        TestSubscriber<Brite> subscriber1 = new TestSubscriber<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber1);

        //asserts
        subscriber1.assertNoErrors();
        subscriber1.assertValueCount(1);

        TestSubscriber<Brite> subscriber2 = new TestSubscriber<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber2);

        //asserts
        subscriber2.assertNoErrors();
        subscriber2.assertValueCount(1);
    }

    @Test
    public void shouldBeAbleToUpgradeAndSeedDatabaseV2() {
        BriteDatabase database1 = SQLBriteOpenHelper.get(context, "brite", 1, true);
        BriteDatabase database2 = SQLBriteOpenHelper.get(context, "brite", 3, true);

        TestSubscriber<Brite> subscriber1 = new TestSubscriber<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber1);

        //asserts
        subscriber1.assertNoErrors();
        subscriber1.assertValueCount(1);

        TestSubscriber<Brite> subscriber2 = new TestSubscriber<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 3.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber2);

        //asserts
        subscriber2.assertNoErrors();
        subscriber2.assertValueCount(1);
    }

    @Test
    public void shouldBeAbleToDowngradeDatabase() {
        BriteDatabase database1 = SQLBriteOpenHelper.get(context, "brite", 1, true);
        BriteDatabase database2 = SQLBriteOpenHelper.get(context, "brite", 2, true);

        TestSubscriber<Brite> subscriber1 = new TestSubscriber<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber1);

        //asserts
        subscriber1.assertNoErrors();
        subscriber1.assertValueCount(1);

        TestSubscriber<Brite> subscriber2 = new TestSubscriber<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber2);

        //asserts
        subscriber2.assertNoErrors();
        subscriber2.assertValueCount(1);

        //downgrade
        BriteDatabase database3 = SQLBriteOpenHelper.get(context, "brite", 1, true);

        TestSubscriber<Brite> subscriber3 = new TestSubscriber<>();

        database3.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber3);

        //asserts
        subscriber3.assertNoErrors();
        subscriber3.assertValueCount(0);

    }

    @Test
    public void shouldBeAbleToDowngradeDatabaseV2() {
        BriteDatabase database1 = SQLBriteOpenHelper.get(context, "brite", 1, true);
        BriteDatabase database2 = SQLBriteOpenHelper.get(context, "brite", 3, true);

        TestSubscriber<Brite> subscriber1 = new TestSubscriber<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber1);

        //asserts
        subscriber1.assertNoErrors();
        subscriber1.assertValueCount(1);

        TestSubscriber<Brite> subscriber2 = new TestSubscriber<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber2);

        //asserts
        subscriber2.assertNoErrors();
        subscriber2.assertValueCount(1);

        //downgrade
        BriteDatabase database3 = SQLBriteOpenHelper.get(context, "brite", 1, true);

        TestSubscriber<Brite> subscriber3 = new TestSubscriber<>();

        database3.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(subscriber3);

        //asserts
        subscriber3.assertNoErrors();
        subscriber3.assertValueCount(0);

    }
}
