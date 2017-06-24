package app.maisagua.dataSource;

import android.provider.BaseColumns;

/**
 * Created by Samsung on 03/05/2017.
 */
public class DataBaseContract {
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "maisagua.db";

    public static class NoteEntry implements BaseColumns{
        public static final String TABLE_NAME = "C_NOTE";
        public static final String COLUMN_NAME_DATETIME = "C_DATE";
        public static final String COLUMN_NAME_POTION = "C_POTION";
        public static final String COLUMN_NAME_MEDIDA = "C_MEDIDA";
        public static final String COLUMN_NAME_TYPE = "C_TYPE";

        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE IF NOT EXISTS " + NoteEntry.TABLE_NAME + " (" +
                        NoteEntry._ID + DataBaseType.INTEGER_TYPE +" PRIMARY KEY AUTOINCREMENT " + DataBaseType.COMMA_SEP +
                        NoteEntry.COLUMN_NAME_DATETIME + DataBaseType.DATETIME_TYPE + DataBaseType.COMMA_SEP +
                        NoteEntry.COLUMN_NAME_TYPE + DataBaseType.TEXT_TYPE + DataBaseType.COMMA_SEP +
                        NoteEntry.COLUMN_NAME_MEDIDA + DataBaseType.TEXT_TYPE + DataBaseType.COMMA_SEP +
                        NoteEntry.COLUMN_NAME_POTION + DataBaseType.REAL_TYPE + " )";

        public static final String SQL_DELETE_ENTRY =
                "DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;

    }

    public static class SettingsEntry implements BaseColumns{
        public static final String TABLE_NAME = "SETTINGS";
        public static final String COLUMN_NAME_WEIGHT = "C_WEIGHT";
        public static final String COLUMN_NAME_GOAL = "C_GOAL";
        public static final String COLUMN_NAME_NOTIFICATION_INTERVAL = "C_NOTIFICATION_INTERVAL";

        public static final String SQL_CREATE_ENTRY =
                "CREATE TABLE IF NOT EXISTS " + SettingsEntry.TABLE_NAME + " (" +
                        SettingsEntry._ID + DataBaseType.INTEGER_TYPE +" PRIMARY KEY AUTOINCREMENT " + DataBaseType.COMMA_SEP +
                        SettingsEntry.COLUMN_NAME_WEIGHT + DataBaseType.REAL_TYPE + DataBaseType.COMMA_SEP +
                        SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL + DataBaseType.REAL_TYPE + DataBaseType.COMMA_SEP +
                        SettingsEntry.COLUMN_NAME_GOAL + DataBaseType.REAL_TYPE +" )";

        public static final String SQL_DELETE_ENTRY =
                "DROP TABLE IF EXISTS " + SettingsEntry.TABLE_NAME;

    }
    
}
