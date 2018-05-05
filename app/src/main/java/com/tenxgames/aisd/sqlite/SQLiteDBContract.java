package com.tenxgames.aisd.sqlite;

import android.provider.BaseColumns;


public class SQLiteDBContract {

    private SQLiteDBContract(){
    }

    public static final class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "logs";

        public final static String COLUMN_TIME = "time";
        public final static String COLUMN_SORTTIME = "sort_time";
        public final static String COLUMN_SEQ_START = "start_seq";
        public final static String COLUMN_SEQ_SORTED = "sorted_seq";
    }

}
