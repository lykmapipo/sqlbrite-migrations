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

## Migration File Format
```yaml
up: # sql scripts to run during database upgrade or create
  - CREATE TABLE tests (name VARCHAR(45))
seeds: # sql scripts to be run during seeding
  - INSERT INTO tests (name) values("Test")
down: # sql scripts to run during database downgrade
  - DROP TABLE tests
```

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