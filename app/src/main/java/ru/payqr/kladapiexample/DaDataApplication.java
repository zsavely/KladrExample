/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Savelii Zagurskii
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.payqr.kladapiexample;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.payqr.kladapiexample.realm.modules.KladrRealmModule;

/**
 * @author Savelii Zagurskii
 */
public class DaDataApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize a Realm configuration
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .setModules(new KladrRealmModule())
                .name("kladr.realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        // Set the default Realm configuration
        // for getting Realm instance everywhere without a Context
        Realm.setDefaultConfiguration(config);
    }
}