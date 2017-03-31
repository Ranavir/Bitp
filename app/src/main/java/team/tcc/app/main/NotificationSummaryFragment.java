package team.tcc.app.main;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.TrainingModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class NotificationSummaryFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "NSummaryFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerNotification;
    private Button btnProcess;
    private TextView tvNotificationDesc,tvApplied,tvSelected,tvRejected;
    private String mNotification;
    private NotificationModel mNotificationModel;
    private String mExamCode;
    private String mStudentId;
    private JSONObject mJObjExamProfile;

    private JSONObject mJObjReq;
    public NotificationSummaryFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_notification_summary, container, false);


        spinnerNotification = (Spinner)rootView.findViewById(R.id.spinnerNotification);
        tvNotificationDesc = (TextView)rootView.findViewById(R.id.tvNotificationDesc);
        tvApplied = (TextView)rootView.findViewById(R.id.tvApplied);
        tvSelected = (TextView)rootView.findViewById(R.id.tvSelected);
        tvRejected = (TextView)rootView.findViewById(R.id.tvRejected);


        btnProcess = (Button) rootView.findViewById(R.id.btnProcess);
        btnProcess.setTransformationMethod(null);
        btnProcess.setOnClickListener(this);



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
        spinnerNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNotificationModel = (NotificationModel) parent.getSelectedItem();
                mNotification = mNotificationModel.getNotification_id();
                Log.d("TAG", "###Notification Selected--->" + mNotification);


                StringBuffer sb = new StringBuffer();
                sb.append(mNotificationModel.getNotification());
                if(null != mNotificationModel.getHsc_percentage() && mNotificationModel.getHsc_percentage() > 0){
                    sb.append("\nHSC " + mNotificationModel.getHsc_percentage() + " %age");
                }
                if(null != mNotificationModel.getIntrm_percentage() && mNotificationModel.getIntrm_percentage() > 0){
                    sb.append("\n12th " + mNotificationModel.getIntrm_percentage() + " %age");
                }
                if(null != mNotificationModel.getGrad_percentage() && mNotificationModel.getGrad_percentage() > 0){
                    sb.append("\nGraduation " + mNotificationModel.getGrad_percentage() + " %age");
                }
                if(null != mNotificationModel.getPg_percentage() && mNotificationModel.getPg_percentage() > 0){
                    sb.append("\nPG " + mNotificationModel.getPg_percentage() + " %age");
                }
                tvNotificationDesc.setText(sb.toString());

                //Go for the task fetching result of notification
                new NotificationResultTask().execute(mNotification);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Get Notifications
        if(AppUtils.checkInternet(mContext)) {
            new NotificationTask().execute();
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
            case R.id.btnProcess:

                if(validEntry()){
                    mStudentId = "";
                    //Ask for student id and Verify the student after getting student id
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Enter Student Id");

                    // Set up the input
                    final EditText input = new EditText(mContext);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER );// | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    builder.setView(input);

                    // Set up the buttons
                    builder.setNegativeButton("Verify", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mStudentId = input.getText().toString();
                            //Start process to bring the Student exam profile
                            if (!TextUtils.isEmpty(mStudentId)) {
                                dialog.dismiss();
                                if(AppUtils.checkInternet(mContext)) {
                                    new StudentExamProfileTask().execute();
                                }else{
                                    AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

                                }

                            } else {
                                Toast.makeText(mContext, "Please provide student id.", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
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

        if(TextUtils.isEmpty(mNotification)){
            AppUtils.showAlert(mContext, "Alert", "Please select notification.");
            flag = false ;
        }else if(TextUtils.isEmpty(mExamCode)){
            AppUtils.showAlert(mContext, "Alert", "Exam not conducted yet.");
            flag = false ;
        }
        return  flag;
    }//validEntry

    /**
     * This task used to fetch student exam profile details related
     * to a specific notification and specific exam code by its user
     * id
     */
    private class StudentExamProfileTask extends AsyncTask<Void, Void, Void> {
        JSONObject respObj = null ;
        String msg = null;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("fetching student exam profile");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                mJObjReq = new JSONObject();
                try {
                    mJObjReq.put("notification_id", mNotification);
                    mJObjReq.put("exam_code", mExamCode);
                    mJObjReq.put("user_id", mStudentId);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(TAG+"Before sending to server------------------>>>" + mJObjReq.toString());

                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.FETCH_EXAM_PROFILE);
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
                    mJObjExamProfile = respObj.getJSONObject("exam_profile");
                    if(null != mJObjExamProfile && mJObjExamProfile.length()>0){
                        //tvStudName.setText(mJObjExamProfile.getString("name"));
                        //tvTotalMark.setText(mJObjExamProfile.getString("marks_interview_total"));

                        //Toast.makeText(mContext,"Success!!! Go next...",Toast.LENGTH_LONG).show();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            fm.popBackStackImmediate();
                        }
                        mFt = fm.beginTransaction();
                        MatchMakingFragment mmf = new MatchMakingFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("notification_id",mNotificationModel.getNotification_id());
                        bundle.putString("notification",mNotificationModel.getNotification());
                        bundle.putString("exam_profile",mJObjExamProfile.toString());
                        mmf.setArguments(bundle);
                        mFt.replace(R.id.frame_container,mmf );
                        mFt.addToBackStack("MatchMakingFragment");
                        mFt.commit();
                    }else{
                        AppUtils.showAlert(mContext, "Error", "Student profile not found.");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }
        }
    };
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
    /******************************************************
     * This task is used to fetch new notification specific
     * result
     *******************************************************/

    private class NotificationResultTask extends AsyncTask<String, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        NotificationResultTask(){
            //constructor
            Log.w(TAG,"NotificationResultTask started...");
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
        protected Void doInBackground(String... arg0) {
            try {

                JSONObject jObjRequest = new JSONObject();
                jObjRequest.put("notification_id",arg0[0]);
                respObj = HttpUtils.sendToServer(mContext, jObjRequest, AppContstants.NOTIFICATION_RESULT);
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
                    JSONObject jObj = respObj.getJSONObject("notification_results");
                    if(jObj != null && jObj.length() > 0){
                        tvApplied.setText(jObj.getString("applied"));
                        tvSelected.setText(jObj.getString("selected"));
                        tvRejected.setText(jObj.getString("rejected"));
                        mExamCode = jObj.getString("exam_code");
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
