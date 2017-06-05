package app.maisagua;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by romario on 04/06/17.
 */

public class HistoryAdapter extends ArrayAdapter {

    Context context;

    int resource;

    public HistoryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Object[]> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = View.inflate(getContext(), resource, null);

        Object[] object = (Object[]) getItem(position);

        TextView date = (TextView) view.findViewById(R.id.textView_date);
        TextView sum = (TextView) view.findViewById(R.id.textView_sum);
        TextView percent = (TextView) view.findViewById(R.id.textView_percent);

        date.setText((CharSequence) object[0]);
        sum.setText(getContext().getString(R.string.quant_litro_label, (CharSequence) object[1]));
        percent.setText((CharSequence) object[2]+"%");

        return view;
    }
}
