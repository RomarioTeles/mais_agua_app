package app.maisagua.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import app.maisagua.Interval;
import app.maisagua.IntervalAdapter;
import app.maisagua.R;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;
import app.maisagua.receivers.NotificationService;

/**
 * Created by Samsung on 03/05/2017.
 */

public class SettingsActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener{

    private static final long ONE_HOUR_IN_MILLISECONDS = 3600000;

    static final int ML_BY_KG = 35;

    EditText editTextWeight;

    TextView textViewGoal;

    Integer id;

    SharedPreferences sharedPreferences;

    Spinner spinnerInterval;

    private List<Interval> intervals;

    IntervalAdapter arrayAdapter;

    AppCompatSeekBar seekBar;

    boolean seekBarHasMoved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_settings);

        editTextWeight = (EditText) findViewById(R.id.editText_weight);
        spinnerInterval = (Spinner) findViewById(R.id.spinner_interval);
        textViewGoal = (TextView) findViewById(R.id.textView_goal);
        textViewGoal.setText(2000+"ml");
        seekBar = (AppCompatSeekBar) findViewById(R.id.seekBar_goal);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(2000);
        seekBar.setSelected(true);
        seekBar.setEnabled(false);

        intervals = new ArrayList<>();
        intervals.add(new Interval(1D, "1 H"));
        intervals.add(new Interval(2D, "2 H"));
        intervals.add(new Interval(3D, "3 H"));

        arrayAdapter = new IntervalAdapter(this, R.layout.item_spinner, intervals);
        spinnerInterval.setAdapter(arrayAdapter);
        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        QueryTask queryTask = new QueryTask();
        queryTask.execute();

        editTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Double weight = Double.valueOf(s.toString());
                Double goal = (weight * ML_BY_KG);
                BigDecimal bdgoal = BigDecimal.valueOf(goal);
                bdgoal.setScale(0, RoundingMode.HALF_UP);
                textViewGoal.setText(bdgoal.intValue() + " ml");
                seekBar.setMax(bdgoal.intValue()+1000);
                seekBar.setProgress(bdgoal.intValue());
            }

            @Override
            public void afterTextChanged(Editable s) {
                seekBarHasMoved = false;
            }
        });

        spinnerInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seekBarHasMoved = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double weight = Double.valueOf(editTextWeight.getText().toString());
                Double interval = ((Interval) arrayAdapter.getItem(spinnerInterval.getSelectedItemPosition())).getTime();
                Double goal = 2000D;
                SaveTask saveTask = new SaveTask();
                if(seekBarHasMoved){
                    goal = Double.valueOf(seekBar.getProgress());
                    saveTask.execute(weight, interval, goal, 0D);
                }else{
                    goal = weight * ML_BY_KG;
                    saveTask.execute(weight, interval, goal, 1D);
                }

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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textViewGoal.setText(progress + "ml");
        seekBarHasMoved = true;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    class SaveTask extends AsyncTask<Double, Void, List<Double>>{

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
        protected List<Double> doInBackground(Double... params) {

            Double weight = params[0];
            Double interval = params[1];
            Double goal = params[2];
            Double hasMoved = params[3];

            ContentValues contentValues = new ContentValues();

            if(hasMoved > 0D) {
                contentValues.put(DataBaseContract.SettingsEntry.COLUMN_NAME_WEIGHT, weight);
                contentValues.put(DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL, interval);
            }

            contentValues.put(DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL, goal);

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(SettingsActivity.this);

            if(id == null) {
                mDataSourceHelper.insert(DataBaseContract.SettingsEntry.TABLE_NAME, contentValues);
            }else{
                mDataSourceHelper.update(DataBaseContract.SettingsEntry.TABLE_NAME, contentValues,"_ID = ?", new String[]{String.valueOf(id)});
            }

            List<Double> paramsAsList = new ArrayList<Double>();
            paramsAsList.add(weight);
            paramsAsList.add(interval);
            paramsAsList.add(goal);

            return paramsAsList;
        }

        @Override
        protected void onPostExecute(List<Double> params) {
            super.onPostExecute(params);
            progressDialog.dismiss();
            BigDecimal goal = BigDecimal.valueOf(params.get(2));
            goal.setScale(0, RoundingMode.HALF_UP);
            textViewGoal.setText(goal.intValue() + " ml");
            int max = Double.valueOf(params.get(0) * ML_BY_KG).intValue();
            seekBar.setMax(max + 1000);
            seekBar.setProgress(params.get(2).intValue());
            seekBar.setEnabled(true);

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

                Double weight = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry.COLUMN_NAME_WEIGHT)
                );
                Double goal = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL)
                );

                int interval = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DataBaseContract.SettingsEntry.COLUMN_NAME_NOTIFICATION_INTERVAL)
                );

                editTextWeight.setText(weight.toString());

                int position = 0;

                for(int i = 0; i < intervals.size(); i++){
                    Interval pos = intervals.get(i);
                    if(pos.getTime() == interval){
                        position = i;
                        break;
                    }
                }

                spinnerInterval.setSelection(position);
                BigDecimal bdgoal = BigDecimal.valueOf(goal);
                bdgoal.setScale(0, RoundingMode.HALF_UP);
                textViewGoal.setText(bdgoal.intValue() + " ml");
                int max = Double.valueOf(weight * ML_BY_KG).intValue();
                seekBar.setMax(max + 1000);
                seekBar.setProgress(goal.intValue());
                seekBar.setEnabled(true);
            }

            progressDialog.dismiss();
        }
    }


}
