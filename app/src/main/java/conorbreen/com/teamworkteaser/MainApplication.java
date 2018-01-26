package conorbreen.com.teamworkteaser;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Conor Breen on 23/01/2018.
 */

/**
 * If we were having problems with the 65k limit, and we couldn't remove any more 3rd party libraries or ProGuard shrink out any more methods etc,
 * then (as a last resort) the Multidex support library could be referenced, and this could be changed to extend MultiDexApplication to allow us to exceed that 65k limit
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(getString(R.string.app_name))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
