package app.maisagua.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
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

    public void handleResult(String result) {
        if(result != null){
            textViewQuant.setText(result);
        }
    }

    class QueryTask extends AsyncTask<String, String, String>{

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
        protected String doInBackground(String... params) {

            String amount = null;

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(NoteActivity.this);

            String[] projection = new String[]{
                    DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL
            };

            Cursor cursorGoal = mDataSourceHelper.query(DataBaseContract.SettingsEntry.TABLE_NAME, projection, null, null, null);
            int rows = cursorGoal.getCount();
            if(rows > 0) {
                cursorGoal.moveToFirst();
                Double goal = cursorGoal.getDouble(cursorGoal.getColumnIndex(DataBaseContract.SettingsEntry.COLUMN_NAME_GOAL)) / 1000;
                String date = new SimpleDateFormat().format(new Date()).split("/")[0];
                Cursor cursorSum = mDataSourceHelper.getReadableDatabase().rawQuery("SELECT SUM("+ DataBaseContract.NoteEntry.COLUMN_NAME_POTION +")" + " FROM " +
                        DataBaseContract.NoteEntry.TABLE_NAME + " WHERE " + DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME + " = ?", new String[]{date} );

                if(cursorSum.getCount() > 0){
                    cursorSum.moveToFirst();
                    Double sum = cursorSum.getDouble(0) / 1000;
                    amount = sum + "l of " + goal + "l";
                }
            }

            return amount;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            handleResult(aVoid);
            dialog.dismiss();
        }
    }

}
