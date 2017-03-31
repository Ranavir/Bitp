package team.tcc.app.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team.tcc.app.R;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.PlacementModel;

/**
 * Created by Amod on 19-03-2017.
 */
public class PlacementAdapter extends ArrayAdapter<PlacementModel> {

    private ArrayList<PlacementModel> placementList;
    Context context;
    int ViewResourceId;


    public PlacementAdapter(Context context, int ViewResourceId, ArrayList<PlacementModel> placementList) {
        super(context, ViewResourceId, placementList);
        this.placementList = new ArrayList<PlacementModel>();
        this.placementList.addAll(placementList);
        this.context = context;
        this.ViewResourceId = ViewResourceId;
    }

    private class ViewHolder {
        TextView tv_placementTxt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(ViewResourceId, null);

            holder = new ViewHolder();
            holder.tv_placementTxt = (TextView) convertView.findViewById(R.id.tv_placementTxt);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        PlacementModel pm = placementList.get(position);

        StringBuffer sb = new StringBuffer();
        if(!TextUtils.isEmpty(pm.getCreated_on())){
            sb.append("Posted on : " + pm.getCreated_on() + "\n");
        }
        sb.append(pm.getNews());

        holder.tv_placementTxt.setText(sb.toString());


        return convertView;

    }

}
