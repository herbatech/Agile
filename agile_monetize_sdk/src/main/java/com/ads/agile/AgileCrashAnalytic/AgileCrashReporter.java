package com.ads.agile.AgileCrashAnalytic;

import android.content.Context;

import com.ads.agile.AgileCrashAnalytic.utils.AgileCrashReporterExceptionHandler;
import com.ads.agile.AgileCrashAnalytic.utils.AgileCrashReporterNotInitializedException;


public class AgileCrashReporter {
    public  static  String TAG= AgileCrashReporter.class.getSimpleName();

    private static Context applicationContext;

    private static String crashReportPath;

    private static boolean isNotificationEnabled = true;

    private AgileCrashReporter() {
        // This class in not publicly instantiable
    }

    public static void initialize(Context context) {
        applicationContext = context;
        setUpExceptionHandler();
    }

    public static void initialize(Context context, String crashReportSavePath) {
        applicationContext = context;
        crashReportPath = crashReportSavePath;
        setUpExceptionHandler();
    }

    private static void setUpExceptionHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof AgileCrashReporterExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new AgileCrashReporterExceptionHandler(applicationContext));
        }
    }

    public static Context getContext() {
        if (applicationContext == null) {
            try {
                throw new AgileCrashReporterNotInitializedException("Initialize AgileCrashReporter : call AgileCrashReporter.initialize(context, crashReportPath)");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return applicationContext;
    }


}
