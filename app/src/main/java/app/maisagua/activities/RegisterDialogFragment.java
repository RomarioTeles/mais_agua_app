package app.maisagua.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.maisagua.R;
import app.maisagua.dataSource.DataBaseContract;
import app.maisagua.helpers.DataSourceHelper;

public class RegisterDialogFragment extends DialogFragment {

    String[] types;

    String[] medidas = new String[]{"ml"};

    ArrayAdapter typesAdapter, medidasAdapter;

    Spinner typesSpinner, medidasSpinner;

    EditText editTextPotion;

    Button buttonCancel, buttonOk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonCancel = (Button) getView().findViewById(R.id.button_cancel);

        buttonOk = (Button) getView().findViewById(R.id.button_ok);

        types = getResources().getStringArray(R.array.types_of_drinks);

        typesSpinner = (Spinner) getView().findViewById(R.id.spinner_types);

        medidasSpinner = (Spinner) getView().findViewById(R.id.spinner_medidas);

        editTextPotion = (EditText) getView().findViewById(R.id.editText_potion);

        typesAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, types);

        medidasAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, medidas);

        medidasSpinner.setAdapter(medidasAdapter);

        typesSpinner.setAdapter(typesAdapter);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTask saveTask = new SaveTask(getActivity());
                String type = (String) typesSpinner.getSelectedItem();
                String potion = editTextPotion.getText().toString();
                String medida = (String) medidasSpinner.getSelectedItem();
                saveTask.execute(type, potion, medida);
            }
        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    class SaveTask extends AsyncTask<String, String, Void >{

        ProgressDialog dialog;

        Context context;

        public SaveTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(this.context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.aguarde));
            dialog.setMessage(getString(R.string.registrando_message));
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            DataSourceHelper mDataSourceHelper = new DataSourceHelper(context);

            String type = params[0];
            String potion = params[1];
            String medida = params[2];
            String date = new SimpleDateFormat().format(new Date()).split(" ")[0];

            ContentValues values = new ContentValues();
            values.put(DataBaseContract.NoteEntry.COLUMN_NAME_DATETIME, date);
            values.put(DataBaseContract.NoteEntry.COLUMN_NAME_TYPE, type);
            values.put(DataBaseContract.NoteEntry.COLUMN_NAME_POTION, potion);
            values.put(DataBaseContract.NoteEntry.COLUMN_NAME_MEDIDA, medida);

            mDataSourceHelper.insert(DataBaseContract.NoteEntry.TABLE_NAME, values);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            Intent intent = new Intent(getActivity(), NoteActivity.class);
            startActivity(intent);
            getActivity().finish();
            dismiss();
        }
    }
}
