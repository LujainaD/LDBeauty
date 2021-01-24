package com.lujaina.ldbeauty;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.util.Locale;

public class YourGlobalClass extends Application {

    @Override
    public void onCreate() {
        updateLanguage(this, null);
        super.onCreate();
    }

    public static void updateLanguage(Context ctx, String lang) {

        Configuration cfg = new Configuration();
        LocalSharedManager manager = new LocalSharedManager(ctx);
        String language = manager.GetValueFromSharedPrefs("App_Locale");

        if (TextUtils.isEmpty(language) && lang == null) {
            cfg.locale = Locale.getDefault();
            String tmp_locale = "";
            tmp_locale = Locale.getDefault().toString().substring(0, 2);
            manager.SaveValueToSharedPrefs("App_Locale", tmp_locale);

        } else if (lang != null) {
            cfg.locale = new Locale(lang);
            manager.SaveValueToSharedPrefs("App_Locale", lang);

        } else if (!TextUtils.isEmpty(language)) {
            cfg.locale = new Locale(language);
        }
        ctx.getResources().updateConfiguration(cfg, null);
    }

}