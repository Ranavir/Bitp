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

/**
 * Created by Amod on 19-03-2017.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

    private ArrayList<NotificationModel> notificationList;
    Context context;
    int ViewResourceId;


    public NotificationAdapter(Context context, int ViewResourceId, ArrayList<NotificationModel> notificationList) {
        super(context, ViewResourceId, notificationList);
        this.notificationList = new ArrayList<NotificationModel>();
        this.notificationList.addAll(notificationList);
        this.context = context;
        this.ViewResourceId = ViewResourceId;
    }

    private class ViewHolder {
        TextView tv_notificationTxt;
        TextView tv_eligibility;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(ViewResourceId, null);

            holder = new ViewHolder();
            holder.tv_notificationTxt = (TextView) convertView.findViewById(R.id.tv_notificationTxt);
            holder.tv_eligibility = (TextView) convertView.findViewById(R.id.tv_eligibility);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        NotificationModel nm = notificationList.get(position);

        holder.tv_notificationTxt.setText(nm.getNotification());
        StringBuffer sb = new StringBuffer();
        if(null != nm.getHsc_percentage() && nm.getHsc_percentage() > 0){
            sb.append("HSC " + nm.getHsc_percentage() + " %age");
        }
        if(null != nm.getIntrm_percentage() && nm.getIntrm_percentage() > 0){
            sb.append("\n12th " + nm.getIntrm_percentage() + " %age");
        }
        if(null != nm.getGrad_percentage() && nm.getGrad_percentage() > 0){
            sb.append("\nGraduation " + nm.getGrad_percentage() + " %age");
        }
        if(null != nm.getPg_percentage() && nm.getPg_percentage() > 0){
            sb.append("\nPG " + nm.getPg_percentage() + " %age");
        }

        holder.tv_eligibility.setText(sb.toString());


        return convertView;

    }

}
