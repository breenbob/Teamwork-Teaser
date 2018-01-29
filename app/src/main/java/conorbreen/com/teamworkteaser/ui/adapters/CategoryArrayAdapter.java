package conorbreen.com.teamworkteaser.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import conorbreen.com.teamworkteaser.models.Category;

/**
 * Created by Conor Breen on 29/01/2018.
 */

public class CategoryArrayAdapter extends ArrayAdapter<Category> {
    public CategoryArrayAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        Category category = getItem(position);

        if (category != null) {
            TextView txtSpinnerItem = convertView.findViewById(android.R.id.text1);
            if (position == getCount()) {
                txtSpinnerItem.setText(null);
                txtSpinnerItem.setHint(category.getName());
            }
            else {
                txtSpinnerItem.setText(category.getName());
            }
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView text = convertView.findViewById(android.R.id.text1);
        text.setText(getItem(position).getName());

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount()-1; // you don't display last item. It is used as hint.
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }
}
