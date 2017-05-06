package app.maisagua.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.maisagua.dataSource.DataBaseContract;

/**
 * Created by Samsung on 03/05/2017.
 */

public class DataSourceHelper extends SQLiteOpenHelper {

    private Context context;

    public DataSourceHelper(Context context) {
        super(context, DataBaseContract.DATABASE_NAME, null, DataBaseContract.DATABASE_VERSION);
    }

    public DataSourceHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataSourceHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseContract.SettingsEntry.SQL_CREATE_ENTRY);
        db.execSQL(DataBaseContract.NoteEntry.SQL_CREATE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DataBaseContract.SettingsEntry.SQL_DELETE_ENTRY);
        db.execSQL(DataBaseContract.NoteEntry.SQL_DELETE_ENTRY);
        onCreate(db);
    }

    public long insert(String table, ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(table, null, values);
        return id;
    }

    public int update(String table, ContentValues values, String whereClause, String[] selectionArgs){
        SQLiteDatabase db = getWritableDatabase();

        int count = db.update(
                table,
                values,
                whereClause,
                selectionArgs);
        return count;
    }

    public int delete(String table, String whereClause, String[] selectionArgs){
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(table, whereClause, selectionArgs);
        return rows;
    }

    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                table,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }
}
