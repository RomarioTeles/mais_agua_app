package app.maisagua.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.maisagua.HistoryAdapter;
import app.maisagua.R;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;

public class HistoryActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView) findViewById(R.id.listView_history);

        QueryTask task = new QueryTask();
        task.execute();
    }

    @Override
    public boolean useToolbar() {
        return true;
    }

    @Override
    public boolean useFabButton() {
        return false;
    }

    class QueryTask extends AsyncTask<String, String, List> {

        ProgressDialog dialog;

        public QueryTask() {
            this.dialog = new ProgressDialog(HistoryActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Aguarde");
            dialog.setMessage("Pesquisando o histórico");
            dialog.show();
        }

        @Override
        protected List doInBackground(String... params) {

            List<Object[]> resultQuery = new ArrayList<>();

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(HistoryActivity.this);

            String[] projection = new String[]{
                    DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL
            };

            Cursor cursorGoal = mDataSourceHelper.query(DataBaseContract.SettingsEntry.TABLE_NAME, projection, null, null, null);
            int rows = cursorGoal.getCount();
            if(rows > 0) {
                cursorGoal.moveToFirst();
                String query = "SELECT SUM(" + DataBaseContract.NoteEntry.COLUMN_NAME_POTION + "), " + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME + " FROM " +
                        DataBaseContract.NoteEntry.TABLE_NAME + " GROUP BY " + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME;

                SQLiteDatabase sqLiteDatabase = mDataSourceHelper.getReadableDatabase();

                Cursor cursor = sqLiteDatabase.rawQuery(query, null);

                rows = cursor.getCount();

                if (rows > 0) {
                    cursor.moveToFirst();
                    do {
                        try {
                            Double dSum = cursor.getDouble(0) / 1000;
                            String date = cursor.getString(1);
                            Double dGoal = cursorGoal.getDouble(0)/1000;

                            String sum = String.format("%.1f", dSum.floatValue());
                            String goal = String.format("%.1f", dGoal.floatValue());
                            Double dPercent = (dSum / dGoal) * 100;
                            String percent = String.format("%.1f", dPercent.floatValue());
                            Object[] item = new Object[]{date, sum, percent};

                            resultQuery.add(item);
                        }catch (Exception e){
                            Log.e("doInBackground", e.getMessage());
                        }

                    } while (cursor.moveToNext());

                }
            }

            return resultQuery;
        }

        @Override
        protected void onPostExecute(List result) {
            super.onPostExecute(result);
            setListView(result);
            dialog.dismiss();
        }
    }

    private void setListView(List result) {

        HistoryAdapter arrayAdapter = new HistoryAdapter(this, R.layout.item_history, result);
        listView.setAdapter(arrayAdapter);
    }


}
