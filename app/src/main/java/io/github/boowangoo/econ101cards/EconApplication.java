package io.github.boowangoo.econ101cards;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EconApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("econ.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
