package app.maisagua;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by romario on 04/06/17.
 */

public class IntervalAdapter extends ArrayAdapter {

    Context context;

    int resource;

    public IntervalAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Interval> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = View.inflate(getContext(), resource, null);

        Interval object = (Interval) getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.textView);

        textView.setText(object.getDescricao());

        return textView;
    }
}
