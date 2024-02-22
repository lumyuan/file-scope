package io.lumyuan.filescope;

import android.content.Context;

public class FileApplication {

    private static Context application;

    public static void init(Context context){
        application = context;
    }

    public static Context getApplication(){
        if (application == null){
            throw new NullPointerException("Please call FileApplication.init(Context).");
        }
        return application;
    }

}
