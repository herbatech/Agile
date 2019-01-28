package com.ads.agile.room;

        import java.util.Date;

public class LogTime {

    public String getTime() {
        Date date = new Date();
        return String.valueOf(date.getTime());
    }
}