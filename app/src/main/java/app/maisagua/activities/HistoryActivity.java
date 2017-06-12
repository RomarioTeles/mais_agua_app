package app.maisagua.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import app.maisagua.HistoryAdapter;
import app.maisagua.R;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;

public class HistoryActivity extends BaseActivity {

    ListView listView;

    RadioButton day, month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView) findViewById(R.id.listView_history);

        day = (RadioButton) findViewById(R.id.radio_day);
        month = (RadioButton) findViewById(R.id.radio_month);

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryTask task = new QueryTask();
                task.execute(QueryTask.DAY);
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryTask task = new QueryTask();
                task.execute(QueryTask.MONTH);
            }
        });

        day.performClick();
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

        public static final String DAY = "DAY";
        public static final String MONTH = "MONTH";

        ProgressDialog dialog;

        public QueryTask() {
            this.dialog = new ProgressDialog(HistoryActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle(getString(R.string.aguarde));
            dialog.setMessage(getString(R.string.pesquisar_historico_message));
            dialog.show();
        }

        @Override
        protected List doInBackground(String... params) {

            String filter = params[0];

            List<Object[]> resultQuery = new ArrayList<>();

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(HistoryActivity.this);

            String[] projection = new String[]{
                    DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL
            };

            Cursor cursorGoal = mDataSourceHelper.query(DataBaseContract.SettingsEntry.TABLE_NAME, projection, null, null, null);
            int rows = cursorGoal.getCount();
            if(rows > 0) {
                String query = null;

                switch (filter){
                    case DAY:
                        query = "SELECT SUM(" + DataBaseContract.NoteEntry.COLUMN_NAME_POTION + "), date(" + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME + ") FROM " +
                                DataBaseContract.NoteEntry.TABLE_NAME + " GROUP BY date("+ DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME+") ORDER BY " +
                                "date("+ DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME+") desc";
                        break;
                    case MONTH:
                        query = "SELECT SUM(" + DataBaseContract.NoteEntry.COLUMN_NAME_POTION + "), strftime('%Y-%m'," + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME + ") FROM " +
                                DataBaseContract.NoteEntry.TABLE_NAME + " GROUP BY strftime('%Y-%m', " + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME +") ORDER BY " +
                                "strftime('%Y-%m', " + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME +") desc";
                        break;
                }

                cursorGoal.moveToFirst();


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
