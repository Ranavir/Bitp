package team.tcc.app.main;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.tcc.app.R;
import team.tcc.app.adapter.NotificationAdapter;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.model.CompanyModel;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.TrainingModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class AddTraineesFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "AddTraineesFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerTraining,spinnerNotification;
    private Button btnAddTrainees;
    private String mTraining,mNotification;
    private TrainingModel mTrainingModel;
    private NotificationModel mNotificationModel;

    private JSONObject mJObjReq;
    public AddTraineesFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_add_trainees, container, false);



        spinnerTraining = (Spinner)rootView.findViewById(R.id.spinnerTraining);
        spinnerNotification = (Spinner)rootView.findViewById(R.id.spinnerNotification);


        btnAddTrainees = (Button) rootView.findViewById(R.id.btnAddTrainees);
        btnAddTrainees.setTransformationMethod(null);
        btnAddTrainees.setOnClickListener(this);



        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "###onActivityCreated invoked");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "###onViewCreated invoked");
        super.onViewCreated(view, savedInstanceState);
        spinnerTraining.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTrainingModel = (TrainingModel) parent.getSelectedItem();
                mTraining = mTrainingModel.getTraining_code();
                Log.d("TAG", "###Training Selected--->" + mTraining);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNotificationModel = (NotificationModel)parent.getSelectedItem();
                mNotification =  mNotificationModel.getNotification_id();
                Log.d("TAG","###Notification Selected--->"+mNotification);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Get Trainings names
        if(AppUtils.checkInternet(mContext)) {
            populateTrainings();
        }else{
            AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

        }

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
            case R.id.btnAddTrainees:

                if(validEntry()){
                    //add new training
                    if(AppUtils.checkInternet(mContext)) {
                        new AddTraineesTask().execute();
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

                    }
                }
                break;
            default:
                break;
        }
    }//end of onClick

    /**
     * This method checks whether Details entered are valid by Syntax or not
     * and wraps it in a json obj
     * @author Ranavir Dash
     * @date 13Oct2016
     */
    private boolean validEntry() {
        boolean flag = true ;
        /*private Spinner spinnerTraining,spinnerNotification;
        private Button btnAddTrainees;
        private String mTraining,mNotification;*/



        if(TextUtils.isEmpty(mTraining)) {
            AppUtils.showAlert(mContext, "Alert", "Please select training.");
            flag = false ;
        }else if(TextUtils.isEmpty(mNotification)){
            AppUtils.showAlert(mContext, "Alert", "Please select notification.");
            flag = false ;
        }else{
            mJObjReq = new JSONObject();
            try {

                mJObjReq.put("training_code", mTraining);
                mJObjReq.put("notification_id", mNotification);
                mJObjReq.put("created_by",mPreference.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task does the followings:
     * - It takes the notification id with (processed status)
     * - It takes the exam code of that notification and get
     * all the selected students list(Generated after
     * match-making process)
     * - Gets all the students list under selected training
     * (active)
     * - Checks the list of students which are new for this
     * training
     * - Insert those new students list in training as created by
     * this admin user
     *******************************************************/

    private class AddTraineesTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        AddTraineesTask(){
            //constructor
            Log.w(TAG, "AddTraineesTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("Adding students to training");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {


                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.ADD_TRAINEES);
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
                if(null != respObj){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Message");
                    builder.setMessage(respObj.getString("msg"));

                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                            });
                    builder.show();
                    //AppUtils.showAlert(mContext, "Message", respObj.getString("msg"));//success/failure
                }else {
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_SERVER_ERROR);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class AddTraineesTask
    /**********************************************************
     * This method used to fetch training details
     **********************************************************/
    private void populateTrainings() {
        System.out.println(TAG + " ENTRY----> populateTrainings");


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            JSONObject respObj = null ;
            String msg = null;
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(mContext);
                pd.setTitle("fetching trainings");
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {

                try {
                    int uid = mPreference.getUserId();
                    JSONObject jObjRequest = new JSONObject();
                    jObjRequest.put("user_id",uid);
                    System.out.println(TAG+"Before sending to server------------------>>>" + jObjRequest.toString());

                    respObj = HttpUtils.sendToServer(mContext, jObjRequest, AppContstants.FETCH_TRAININGS);
                    Log.d(TAG, "###Response--->" + respObj);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (pd != null) {
                    pd.dismiss();
                }
                //if ack is not null then save the ack to local db as status
                try {
                    if (null != respObj && respObj.getString("status").equalsIgnoreCase("SUCCESS")) {
                        JSONArray jaTrainings = respObj.getJSONArray("trainings");
                        ArrayList<TrainingModel> alTrainings = new ArrayList<>();
                        if(jaTrainings.length() > 0){
                            TrainingModel tm = null ;
                            for(int i = 0 ; i < jaTrainings.length(); i++){
                                tm = new TrainingModel();
                                tm.setTraining_code(jaTrainings.getJSONObject(i).getString("training_code"));
                                tm.setTraining_desc(jaTrainings.getJSONObject(i).getString("training_desc"));
                                tm.setComp_code(jaTrainings.getJSONObject(i).getString("comp_code"));
                                tm.setActive(jaTrainings.getJSONObject(i).getString("active"));
                                tm.setMth1_stipen_amt(Double.parseDouble(jaTrainings.getJSONObject(i).getString("mth1_stipen_amt")));
                                tm.setMth2_stipen_amt(Double.parseDouble(jaTrainings.getJSONObject(i).getString("mth2_stipen_amt")));
                                tm.setMth3_stipen_amt(Double.parseDouble(jaTrainings.getJSONObject(i).getString("mth3_stipen_amt")));
                                tm.setMth4_stipen_amt(Double.parseDouble(jaTrainings.getJSONObject(i).getString("mth4_stipen_amt")));
                                tm.setMth5_stipen_amt(Double.parseDouble(jaTrainings.getJSONObject(i).getString("mth5_stipen_amt")));
                                tm.setMth6_stipen_amt(Double.parseDouble(jaTrainings.getJSONObject(i).getString("mth6_stipen_amt")));
                                tm.setCreated_by(jaTrainings.getJSONObject(i).getString("created_by"));

                                alTrainings.add(tm);
                            }
                            //Fetch notifications
                            new NotificationTask().execute();
                        }
                        ArrayAdapter<TrainingModel> adapter =
                                new ArrayAdapter<TrainingModel>(
                                        mContext,
                                        android.R.layout.simple_spinner_item,
                                        alTrainings);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTraining.setAdapter(adapter);
                        System.out.println(TAG + "spinner length----->" + spinnerTraining.getChildCount());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
                }
            }
        };
        task.execute();
    }//end of populateTrainings
    /******************************************************
     * This task is used to fetch new notifications from server
     * and render in dropdown
     *******************************************************/

    private class NotificationTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        NotificationTask(){
            //constructor
            Log.w(TAG,"NotificationTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("fetching notifications");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                int uid = mPreference.getUserId();
                JSONObject jObjRequest = new JSONObject();
                jObjRequest.put("user_id",uid);
                jObjRequest.put("status","processed");
                respObj = HttpUtils.sendToServer(mContext, jObjRequest, AppContstants.FETCH_NOTIFIATIONS);
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
                    JSONArray jaNotifications = respObj.getJSONArray("notifications");
                    if(jaNotifications != null && jaNotifications.length() > 0){
                        ArrayList<NotificationModel> alNotifications = new ArrayList<>();
                        JSONObject jObj = null ;
                        NotificationModel nm = null;
                        for(int i = 0 ; i < jaNotifications.length() ; i++){
                            jObj = jaNotifications.getJSONObject(i);
                            nm = new NotificationModel();

                            nm.setNotification_id(jObj.getString("notification_id"));
                            nm.setComp_code(jObj.getString("comp_code"));
                            nm.setNotification(jObj.getString("notification"));
                            nm.setHsc_percentage(jObj.getDouble("hsc_percentage"));
                            nm.setIntrm_percentage(jObj.getDouble("intrm_percentage"));
                            nm.setGrad_percentage(jObj.getDouble("grad_percentage"));
                            nm.setPg_percentage(jObj.getDouble("pg_percentage"));
                            nm.setStatus(jObj.getString("status"));
                            alNotifications.add(nm);
                        }
                        ArrayAdapter<NotificationModel> adapter =
                                new ArrayAdapter<NotificationModel>(
                                        mContext,
                                        android.R.layout.simple_spinner_item,
                                        alNotifications);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerNotification.setAdapter(adapter);
                        System.out.println(TAG + "spinner length----->" + spinnerNotification.getChildCount());


                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.MSG_NO_NOTIFICATIONS);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class NotificationTask
}//end class
