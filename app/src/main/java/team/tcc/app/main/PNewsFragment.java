package team.tcc.app.main;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import team.tcc.app.R;
import team.tcc.app.adapter.NotificationAdapter;
import team.tcc.app.adapter.PlacementAdapter;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.PlacementModel;
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class PNewsFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "PNewsFragment:";


    private Context mContext;
    FragmentTransaction mFt ;
    Dialog dialog;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private ListView pnewsListView;
    private Button btnCreatePnews;
    private PlacementAdapter mPlacementAdapter;
    FragmentTransaction fragmentTransaction ;


    public PNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "###onAttach invoked");
        super.onAttach(context);
        mContext = context;
        mPreference = new AppSharedPreference(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "###onCreateView invoked");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_pnews, container, false);


        btnCreatePnews = (Button)rootView.findViewById(R.id.btnCreatePnews);
        btnCreatePnews.setTransformationMethod(null);
        btnCreatePnews.setOnClickListener(this);
        if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_ADMIN)){
            btnCreatePnews.setVisibility(View.VISIBLE);
        }
        pnewsListView = (ListView)rootView.findViewById(R.id.pnewsListView);
        pnewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final PlacementModel selectedItem =(PlacementModel) pnewsListView.getItemAtPosition(position);
                System.out.println("Selected Placement news-------"+selectedItem.getNews());

                if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_ADMIN)){
                   //ask for stop this news or cancel
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle("BITP");
                    alertDialog.setMessage("Remove this news ?");

                    alertDialog.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Log.d(TAG, "###Deleting the placement news...");
                                    if(AppUtils.checkInternet(mContext)) {
                                        new RemovePlacementTask().execute(selectedItem);
                                    }else{
                                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

                                    }




                                }
                            });

                    alertDialog.show();
                }

            }
        });

        //fetch notifications and render
        if(AppUtils.checkInternet(mContext)) {
            new PlacemetNewsViewTask().execute();
        }else{
            AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

        }

        return rootView;
    }//end onCreateView



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "###onActivityCreated invoked");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "###onViewCreated invoked");
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        Log.d(TAG, "###onStart...");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "###onResume...");
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        Log.d(TAG, "###onDestroyView...");
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "###onDestroy...");
        super.onDestroy();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.btnCreatePnews:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {//Remove the profile fragment from view
                    fm.popBackStackImmediate();
                }
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_container,new NewPlacementFragment());
                ft.addToBackStack("NewPlacementFragment");
                ft.commit();
                break;
        }
    }//end of onClick
    /******************************************************
     * This task is used to delete the placement news from
     * list
     *******************************************************/

    private class RemovePlacementTask extends AsyncTask<PlacementModel, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        RemovePlacementTask(){
            //constructor
            Log.w(TAG,"RemovePlacementTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("Deleting placement news");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(PlacementModel... placements) {
            PlacementModel pm = placements[0];
            try {
                int uid = mPreference.getUserId();
                JSONObject jObjRequest = new JSONObject();
                jObjRequest.put("user_id",uid);
                jObjRequest.put("placement_code",pm.getPlacement_code());
                respObj = HttpUtils.sendToServer(mContext, jObjRequest, AppContstants.DROP_PLACEMENT_NEWS);
                Log.d(TAG,"###Response--->"+respObj);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            if (pd!=null) {
                pd.dismiss();
            }
            try {
                System.out.println("Msg---------"+msg);
                if(null != respObj) {
                    if (respObj.getString("status").equalsIgnoreCase("SUCCESS")) {

                        Toast.makeText(mContext, "Placement news removed from main list successfully!!!", Toast.LENGTH_LONG).show();
                        AppUtils.showAlert(mContext, "Message", "Placement news removed.");

                    } else {
                        AppUtils.showAlert(mContext, "Error", respObj.getString("msg"));
                    }
                }else {
                    AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class NotificationTask
    /******************************************************
     * This task is used to fetch new placement news from server
     * and render in page
     *******************************************************/

    private class PlacemetNewsViewTask extends AsyncTask<Void, Void, Void> {

        JSONObject respObj = null ;
        String msg = null;
        PlacemetNewsViewTask(){
            //constructor
            Log.w(TAG,"PlacemetNewsViewTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("fetching placement news");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                JSONObject jObjRequest = new JSONObject();
                jObjRequest.put("status","Y");
                respObj = HttpUtils.sendToServer(mContext, jObjRequest, AppContstants.FETCH_PLACEMENT_NEWS);
                Log.d(TAG,"###Response--->"+respObj);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            if (pd!=null) {
                pd.dismiss();
            }
            try {
                System.out.println("Msg---------"+msg);
                if(null != respObj && respObj.getString("status").equalsIgnoreCase("SUCCESS")){
                    //Retrieve notifications and render
                    JSONArray jaPlacements = respObj.getJSONArray("placements");
                    if(jaPlacements != null && jaPlacements.length() > 0){
                        ArrayList<PlacementModel> alPlacemetns = new ArrayList<>();
                        JSONObject jObj = null ;
                        PlacementModel pm = null;
                        for(int i = 0 ; i < jaPlacements.length() ; i++){
                            jObj = jaPlacements.getJSONObject(i);
                            pm = new PlacementModel();

                            pm.setSlno(jObj.getInt("slno"));
                            pm.setComp_code(jObj.getString("comp_code"));
                            pm.setActive(jObj.getString("active"));
                            pm.setNews(jObj.getString("news"));
                            pm.setPlacement_code(jObj.getString("placement_code"));
                            pm.setCreated_by(jObj.getString("created_by"));
                            pm.setCreated_on(jObj.getString("created_on"));
                            alPlacemetns.add(pm);
                        }
                        mPlacementAdapter = new PlacementAdapter(mContext, R.layout.placement_listview_body, alPlacemetns);
                        pnewsListView.setAdapter(mPlacementAdapter);
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.MSG_NO_PLACEMENTS);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class NotificationTask

}//end class
