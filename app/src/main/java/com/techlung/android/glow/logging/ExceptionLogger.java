package com.techlung.android.glow.logging;


import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class ExceptionLogger implements Thread.UncaughtExceptionHandler {

    private static final String EXCEPTION_LOG_NAME = "glow_exception";

    Activity activity;

    public ExceptionLogger(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {


        File trgDir = activity.getExternalFilesDir(null);
        if (!trgDir.exists()) {
            trgDir.mkdirs();
        }

        SimpleDateFormat s = new SimpleDateFormat("yy.MM.dd-hh.mm.ss", Locale.US);
        String time = s.format(new Date());

        File logFile = new File(trgDir.getAbsolutePath() + "/" + time + "_"+EXCEPTION_LOG_NAME+".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String model = android.os.Build.MODEL;
        String version = android.os.Build.VERSION.RELEASE;
        PrintWriter out;
        try {
            out = new PrintWriter(logFile.getAbsolutePath());
            out.println("model=" + model);
            out.println("version=" + version);

            ex.printStackTrace(out);

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ex.printStackTrace();

        final String errorLogMessage = "Error log written to: " + logFile.getAbsolutePath();
        Log.e("GLOW_ANDROID", errorLogMessage);

        System.exit(0);
    }

    public static void createNullpointerExeption () {
        String name = "";
        name = null;
        Log.d("TAG", "" + name.length());
    }

    @Nullable
    public String getExceptionLogs() {
        String result = null;

        File trgDir = activity.getExternalFilesDir(null);
        if (trgDir.exists()) {
            for (File file : trgDir.listFiles()) {
                if (file.isFile() && file.canRead() && file.getName().contains(EXCEPTION_LOG_NAME)) {
                    if (result == null) {
                        result = "";
                    }

                    try {
                        result += readFile(file.getAbsolutePath());
                        result += "\n\n";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    public void deleteExceptionLogs() {
        File trgDir = activity.getExternalFilesDir(null);
        if (trgDir.exists()) {
            for (File file : trgDir.listFiles()) {
                if (file.isFile() && file.getName().contains(EXCEPTION_LOG_NAME)) {
                    file.delete();
                }
            }
        }
    }

    private String readFile(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

}
