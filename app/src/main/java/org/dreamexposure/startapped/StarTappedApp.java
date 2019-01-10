package org.dreamexposure.startapped;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * @author NovaFox161
 * Date Created: 12/16/2018
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class StarTappedApp extends Application {
    private static Application sApplication;
    private static StarTappedApp instance;

    private ClipboardManager clipboard;

    public static final String TAG = StarTappedApp.class.getSimpleName();

    public static Application getApplication() {
        return sApplication;
    }

    public static StarTappedApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        instance = this;
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    public ClipboardManager getClipboard() {
        return clipboard;
    }
}
