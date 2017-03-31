package team.tcc.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import team.tcc.app.R;

/**
 * Created on 19-03-2017
 */
public class CustomDropdownAdapter extends ArrayAdapter<String> {

        public CustomDropdownAdapter(Context context, List<String> objects) {
            super(context, R.layout.spinner_item, objects);
        }


        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            View v = null;

            // If this is the initial dummy entry, make it hidden
            if (position == 0) {
                TextView tv = new TextView(getContext());
                tv.setHeight(0);
                tv.setVisibility(View.GONE);
                v = tv;
            }
            else {
                // Pass convertView as null to prevent reuse of special case views
                v = super.getDropDownView(position, null, parent);
            }

            return v;
        }


}
