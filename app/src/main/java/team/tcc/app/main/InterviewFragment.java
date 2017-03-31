package team.tcc.app.main;


import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.model.ExamModel;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class InterviewFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "InterviewFragment:";


    private Context mContext;
    FragmentTransaction mFt ;
    private Spinner spinnerNotification;
    private Button btnInterviewView, btnInterviewSubmit;
    private EditText etStudentId,etMarkSecure;
    private TextView tvExamCode,tvStudName,tvTotalMark;
    private String mNotification,mExamCode,mStudentId;

    Dialog dialog;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;
    /*Used for Database operations*/
    private DatabaseHelper mDbHelper;
    /*This will hold the profile details availed from database*/
    private JSONObject mJObjReq;
    private JSONObject mJObjExamProfile;


    public InterviewFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_interview, container, false);

        spinnerNotification = (Spinner)rootView.findViewById(R.id.spinnerNotification);
        btnInterviewView = (Button)rootView.findViewById(R.id.btnInterviewView);
        btnInterviewSubmit = (Button)rootView.findViewById(R.id.btnInterviewSubmit);
        btnInterviewView.setOnClickListener(this);
        btnInterviewSubmit.setOnClickListener(this);
        btnInterviewView.setTransformationMethod(null);
        btnInterviewSubmit.setTransformationMethod(null);

        etStudentId = (EditText)rootView.findViewById(R.id.etStudentId);
        tvExamCode = (TextView)rootView.findViewById(R.id.tvExamCode);
        tvStudName = (TextView)rootView.findViewById(R.id.tvStudName);
        tvTotalMark = (TextView)rootView.findViewById(R.id.tvTotalMark);
        etMarkSecure = (EditText)rootView.findViewById(R.id.etMarkSecure);

        if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_STUDENT)){//Student

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
        spinnerNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NotificationModel nm = (NotificationModel) parent.getSelectedItem();
                mNotification = nm.getNotification_id();
                Log.d("TAG", "###Notification Selected--->" + mNotification);
                tvExamCode.setText(nm.getExam_code());
                Log.d("TAG", "###Exam code found--->" + nm.getExam_code());
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
            case R.id.btnInterviewSubmit:
                if(mJObjExamProfile != null && validUpdateEntry()){
                    //view Exam profile of a student
                    if(AppUtils.checkInternet(mContext)) {
                        new UpdateInterviewTask().execute();
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

                    }
                }
                break;

            case R.id.btnInterviewView:
                if(validViewEntry()){
                    //view Exam profile of a student
                    if(AppUtils.checkInternet(mContext)) {
                        new ViewExamProfileTask().execute();
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

                    }
                }

                /*FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {//Remove the profile fragment from view
                    fm.popBackStackImmediate();
                }
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_container,new ProfileEditFragment());
                ft.addToBackStack("ProfileEditFragment");
                ft.commit();
                break;*/
        }
    }//end of onClick
    private boolean validUpdateEntry() {
        boolean flag = true ;
        /*private String mExamCode,mStudentId,mStudentName;
        private Double mTotalMark,mMarkSecure;*/
        mExamCode = tvExamCode.getText().toString();
        mStudentId = etStudentId.getText().toString();
        String totalM = !TextUtils.isEmpty(tvTotalMark.getText().toString()) ? tvTotalMark.getText().toString() : "0";
        String enteredM = etMarkSecure.getText().toString();
        Log.d(TAG,"totalM-->"+totalM+" enteredM-->"+enteredM);
        try {
            if (TextUtils.isEmpty(mNotification)) {
                AppUtils.showAlert(mContext, "Alert", "Please select notification.");
                flag = false;
            }else if (TextUtils.isEmpty(mExamCode)) {
                AppUtils.showAlert(mContext, "Alert", "Invalid exam code.");
                flag = false;
            } else if (!(mStudentId.length() > 0)) {
                AppUtils.showAlert(mContext, "Alert", "Invalid student id.");
                flag = false;
            } else if (!(totalM.length() > 0)) {
                AppUtils.showAlert(mContext, "Alert", "Invalid Interview Details.");
                flag = false;
            } else if (!(enteredM.length() > 0)) {
                AppUtils.showAlert(mContext, "Alert", "Please enter valid secure mark.");
                flag = false;
            } else if (Double.parseDouble(enteredM) > Double.parseDouble(totalM)) {
                AppUtils.showAlert(mContext, "Alert", "Please enter valid secure mark.");
                flag = false;
            } else {
                mJObjReq = new JSONObject();
                try {
                    mJObjReq.put("notification_id", mNotification);
                    mJObjReq.put("exam_code", mExamCode);
                    mJObjReq.put("user_id", mStudentId);
                    mJObjReq.put("marks_interview_acquired", enteredM);
                    mJObjReq.put("updated_by", mPreference.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
            flag = false;
        }
        return  flag;
    }//end of validUpdateEntry
    private class UpdateInterviewTask extends AsyncTask<Void, Void, Void> {
        JSONObject respObj = null ;
        String msg = null;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("updating student exam profile");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {

                System.out.println(TAG+"Before sending to server------------------>>>" + mJObjReq.toString());

                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.UPDATE_EXAM_INTERVEIW);
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
                    //AppUtils.showAlert(mContext, "Message", "Interview Mark updated successfully.");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            mContext);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage(respObj.getString("msg"));

                    alertDialog.setNeutralButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //pop this fragment
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                            });

                    alertDialog.show();
                }else{
                    AppUtils.showAlert(mContext, "Error", "Failed to update interview mark.");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }
        }
    };
    private boolean validViewEntry() {
        boolean flag = true ;
        /*private String mExamCode,mStudentId,mStudentName;
        private Double mTotalMark,mMarkSecure;*/
        mExamCode = tvExamCode.getText().toString();
        mStudentId = etStudentId.getText().toString();

        if (TextUtils.isEmpty(mNotification)) {
            AppUtils.showAlert(mContext, "Alert", "Please select notification.");
            flag = false;
        }else if(TextUtils.isEmpty(mExamCode)) {
            AppUtils.showAlert(mContext, "Alert", "Invalid exam code.");
            flag = false ;
        }else if(!(mStudentId.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Invalid student id.");
            flag = false ;
        }else{
            mJObjReq = new JSONObject();
            try {
                mJObjReq.put("notification_id", mNotification);
                mJObjReq.put("exam_code", mExamCode);
                mJObjReq.put("user_id", mStudentId);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//end of validViewEntry

    private class ViewExamProfileTask extends AsyncTask<Void, Void, Void> {
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
                        tvStudName.setText(mJObjExamProfile.getString("name"));
                        tvTotalMark.setText(mJObjExamProfile.getString("marks_interview_total"));
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
                jObjRequest.put("status", "processed");
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
                            nm.setExam_code(jObj.getString("exam_code"));
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
