package uw.lmanker.expenses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DbHandler extends SQLiteOpenHelper {
    //sets up the database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Expenses.db";

    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + "(" +
                    FeedEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_NAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_CATEGORY + " TEXT," +
                    FeedEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedEntry.COLUMN_NAME_AMOUNT + " DOUBLE," +
                    FeedEntry.COLUMN_NAME_NOTE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    //feed entry to reference database
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Expenses";
        public static final String COLUMN_NAME_NAME= "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DATE= "date";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_ID = "id";
    }
}
