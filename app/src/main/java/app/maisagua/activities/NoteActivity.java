package app.maisagua.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.maisagua.R;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;

public class NoteActivity extends BaseActivity{

    TextView textViewPercent, textViewQuant;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        textViewPercent = (TextView) findViewById(R.id.textView_percent);
        textViewQuant = (TextView) findViewById(R.id.textView_quant);
        buttonAdd = (Button) findViewById(R.id.button_add);

    }

    @Override
    protected void onResume() {
        super.onResume();

        QueryTask task = new QueryTask();
        task.execute();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
        registerDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public boolean useToolbar() {
        return true;
    }

    @Override
    public boolean useFabButton() {
        return false;
    }

    public void handleResult(Double[] result) {
        if(result != null){

            if(result[1] == 0.0){
                Intent intent = new Intent(NoteActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }else{
                Double dSum = result[0] / 1000;
                Double dGoal = result[1] / 1000;

                String sum = String.format("%.1f", dSum.floatValue());
                String goal = String.format("%.1f", dGoal.floatValue());

                try {
                    Double percentTotal = (dSum / dGoal) * 100;

                    textViewQuant.setText(getString(R.string.amount_label, sum, goal));

                    textViewPercent.setText(String.format("%.0f", percentTotal) + "%");

                } catch (Exception e) {
                    Log.e("handleResult", e.getMessage());
                }
            }
        }
    }

    class QueryTask extends AsyncTask<String, String, Double[]>{

        ProgressDialog dialog;

        public QueryTask() {
            this.dialog = new ProgressDialog(NoteActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Aguarde");
            dialog.setMessage("Calculando Percentual");
            dialog.show();
        }

        @Override
        protected Double[] doInBackground(String... params) {

            Double sum = 0.0, goal = 0.0;

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(NoteActivity.this);

            String[] projection = new String[]{
                    DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL
            };

            Cursor cursorGoal = mDataSourceHelper.query(DataBaseContract.SettingsEntry.TABLE_NAME, projection, null, null, null);
            int rows = cursorGoal.getCount();
            if(rows > 0) {
                cursorGoal.moveToFirst();
                goal = cursorGoal.getDouble(cursorGoal.getColumnIndex(DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL));
                String date = new SimpleDateFormat().format(new Date()).split(" ")[0];
                Cursor cursorSum = mDataSourceHelper.getReadableDatabase().rawQuery("SELECT SUM("+ DataBaseContract.NoteEntry.COLUMN_NAME_POTION +")" + " FROM " +
                        DataBaseContract.NoteEntry.TABLE_NAME + " WHERE " + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME + " = ?", new String[]{date} );

                if(cursorSum.getCount() > 0){
                    cursorSum.moveToFirst();
                    sum = cursorSum.getDouble(0);
                }
            }

            return new Double[]{sum, goal};
        }

        @Override
        protected void onPostExecute(Double[] result) {
            super.onPostExecute(result);
            handleResult(result);
            dialog.dismiss();
        }
    }

}
