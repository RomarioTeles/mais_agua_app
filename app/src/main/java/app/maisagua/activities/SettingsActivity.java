package app.maisagua.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.maisagua.R;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;
import app.maisagua.receivers.NotificationReceiver;
import app.maisagua.receivers.NotificationService;

/**
 * Created by Samsung on 03/05/2017.
 */

public class SettingsActivity extends BaseActivity {

    private static final long ONE_HOUR_IN_MILLISECONDS = 3600000;

    static final int ML_BY_KG = 35;

    EditText editTextWeight;

    TextView textViewGoal;

    Integer id;

    SharedPreferences sharedPreferences;

    Spinner spinnerInterval;

    private static final Integer[] INTERVALS = new Integer[]{ 1, 2, 3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_settings);

        editTextWeight = (EditText) findViewById(R.id.editText_weight);
        spinnerInterval = (Spinner) findViewById(R.id.spinner_interval);
        textViewGoal = (TextView) findViewById(R.id.textView_goal);

        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this, R.layout.support_simple_spinner_dropdown_item, INTERVALS);
        spinnerInterval.setAdapter(arrayAdapter);
        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        QueryTask queryTask = new QueryTask();
        queryTask.execute();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = editTextWeight.getText().toString();
                Integer interval = (Integer) spinnerInterval.getSelectedItem();
                SaveTask saveTask = new SaveTask();
                saveTask.execute(weight, String.valueOf(interval));
            }
        });
    }

    @Override
    public boolean useToolbar() {
        return true;
    }

    @Override
    public boolean useFabButton() {
        return true;
    }

    class SaveTask extends AsyncTask<String, Void, List<String>>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SettingsActivity.this);
            progressDialog.setTitle(getString(R.string.aguarde));
            progressDialog.setMessage(getString(R.string.calculando_meta_message));
            progressDialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {

            String weight = params[0];
            String interval = params[1];
            Double goal = Double.valueOf(weight) * ML_BY_KG;

            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBaseContract.SettingsEntry.COLUMN_NAME_WEIGHT, weight);
            contentValues.put(DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL, goal);
            contentValues.put(DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL, interval);

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(SettingsActivity.this);

            if(id == null) {
                mDataSourceHelper.insert(DataBaseContract.SettingsEntry.TABLE_NAME, contentValues);
            }else{
                mDataSourceHelper.update(DataBaseContract.SettingsEntry.TABLE_NAME, contentValues,"_ID = ?", new String[]{String.valueOf(id)});
            }

            List<String> paramsAsList = new ArrayList<String>();
            paramsAsList.add(weight);
            paramsAsList.add(interval);
            paramsAsList.add(String.valueOf(goal));

            return paramsAsList;
        }

        @Override
        protected void onPostExecute(List<String> params) {
            super.onPostExecute(params);
            progressDialog.dismiss();
            textViewGoal.setText(params.get(2) + " ml");

           startService(new Intent(SettingsActivity.this, NotificationService.class));

        }
    }

    class QueryTask extends AsyncTask<Void, Void, Cursor>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SettingsActivity.this);
            progressDialog.setMessage(getString(R.string.aguarde));
            progressDialog.show();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            DataSourceHelper mDataSourceHelper = new DataSourceHelper(SettingsActivity.this);
            String[] projection = new String[]{
                    DataBaseContract.SettingsEntry._ID,
                    DataBaseContract.SettingsEntry.COLUMN_NAME_WEIGHT,
                    DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL,
                    DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL,
            };

            Cursor c = mDataSourceHelper.query(DataBaseContract.SettingsEntry.TABLE_NAME, projection, null, null, null);

            return c;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            int rows = cursor.getCount();
            if(rows > 0) {
                cursor.moveToFirst();

                id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry._ID));

                String weight = cursor.getString(
                        cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry.COLUMN_NAME_WEIGHT)
                );
                String goal = cursor.getString(
                        cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL)
                );

                int interval = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL)
                );

                editTextWeight.setText(weight);

                int position = 0;

                for(int i = 0; i < INTERVALS.length; i++){
                    int pos = INTERVALS[i];
                    if(pos == interval){
                        position = i;
                        break;
                    }
                }
                spinnerInterval.setSelection(position);
                textViewGoal.setText(goal + " ml");
            }

            progressDialog.dismiss();
        }
    }


}
