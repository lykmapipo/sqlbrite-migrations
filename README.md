sqlbrite-migrations
===================

[![](https://jitpack.io/v/lykmapipo/sqlbrite-migrations.svg)](https://jitpack.io/#lykmapipo/sqlbrite-migrations)

SQLBrite helper class to manage database creation and version management using an application's raw asset files.

*Note!: Current all migrations must be placed on `migrations` folder inside `assets` folder*


## Installation
Add [https://jitpack.io](https://jitpack.io) to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
add `sqlbite-migrations` dependency into your project

```gradle
dependencies {
    compile 'com.github.lykmapipo:sqlbrite-migrations:v0.1.0'
}
```

## Usage
```java
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
```

## Migrations
All migrations must be named after database version. So if you start a new project your first migration will be `1.yaml`.
When upgrading to new version the the migration file must have the name corresponding to the new database version i.e
if you upgrading from `version 1` to `version 2` then you migration script name will be `2.yaml`

All migration have the below format:

```yaml
up: # sql scripts to run during database upgrade or create
  - CREATE TABLE tests (name VARCHAR(45))
seeds: # sql scripts to be run during seeding
  - INSERT INTO tests (name) values("Test")
down: # sql scripts to run during database downgrade
  - DROP TABLE tests
```

Where:

- `up` - List of SQL DDL to be applied to a database. Mainly for creating or altering a table.
- `seeds` - List of SQL DML to be applied to a database. All DML will be applied after success `up DDL`.
- `down` - List of DML and DDL to be applied to a database when downgrading. `Currently not implemented`.

Whole of migration occur with a single database transaction. So up and seeding may happen or fail as whole.

## Contribute
It will be nice, if you open an issue first so that we can know what is going on, then, fork this repo and push in your ideas. 
Do not forget to add a bit of test(s) of what value you adding.

## License 

(The MIT License)

Copyright (c) 2017 lykmapipo && Contributors

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.