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
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.model.ExamModel;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class ProcessApplFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ProcessApplFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerExam;
    private TextView tvNotificationDetails, tvAppliedCount;
    private Button btnProcApplication;
    private String mNotificationId,mExamCode;
    private NotificationModel mNotificationModel;
    /*This will hold the request details */
    private JSONObject mJSONReqObj;
    public ProcessApplFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_process_application, container, false);

        /*private Spinner spinnerExam;
        private TextView tvNotificationDetails, tvAppliedCount;
        private Button btnProcApplication;
        private String mNotificationId,mTvExamCode;*/
        spinnerExam = (Spinner)rootView.findViewById(R.id.spinnerExam);
        tvNotificationDetails = (TextView)rootView.findViewById(R.id.tvNotificationDetails);
        tvAppliedCount = (TextView)rootView.findViewById(R.id.tvAppliedCount);

        btnProcApplication = (Button) rootView.findViewById(R.id.btnProcApplication);
        btnProcApplication.setTransformationMethod(null);
        btnProcApplication.setOnClickListener(this);

        //Retrieve the bundle element
        if(null != getArguments()) {
            mNotificationModel = (NotificationModel)getArguments().getSerializable("notification");
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
            mNotificationId = mNotificationModel.getNotification_id();
            tvNotificationDetails.setText(sb.toString());
        }


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
        spinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ExamModel em = (ExamModel)parent.getSelectedItem();
                mExamCode =  em.getExam_code();
                Log.d("TAG","###Exam Selected--->"+mExamCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Get companies names
        if(AppUtils.checkInternet(mContext)) {
            preProcessApplication();
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
            case R.id.btnProcApplication:

                if(validEntry()){
                    //process applications
                    if(AppUtils.checkInternet(mContext)) {
                        new ProcessApplicationTask().execute();
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
        //mComp,mNotifyTxt,mHscPer,mIntrimPer,mGradPer,mPgPer;

        //mExamCode = spinnerExam.getSelectedItem().toString();

        if(TextUtils.isEmpty(mExamCode)) {
            AppUtils.showAlert(mContext, "Alert", "Please select exam and proceed.");
            flag = false ;
        }else{
            mJSONReqObj = new JSONObject();
            try {

                mJSONReqObj.put("exam_code", mExamCode);
                mJSONReqObj.put("notification_id", mNotificationId);
                mJSONReqObj.put("admin_user_id", ProfileModel.user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task is used to STOP the notification by changing
     * status of this notification to 'processed' and also makes
     * the status of all the applications under this to 'Accepted'
     * Then maps this exam_code to this notification
     *******************************************************/

    private class ProcessApplicationTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        ProcessApplicationTask(){
            //constructor
            Log.w(TAG, "ProcessApplicationTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("Processing applications and linking exam");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                respObj = HttpUtils.sendToServer(mContext, mJSONReqObj, AppContstants.APPLICATIONS_PROCESSING);
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
                }else {
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_SERVER_ERROR);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class SyncResponseTask
    /**********************************************************
     * This method used to fetch valid exam lists along with that
     * the total applied count for this notification
     **********************************************************/
    private void preProcessApplication() {
        System.out.println(TAG + " ENTRY----> preProcessApplication");


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            JSONObject respObj = null ;
            String msg = null;
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(mContext);
                pd.setTitle("fetching details");
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {

                try {
                    JSONObject reqObj = new JSONObject();
                    reqObj.put("notification_id",mNotificationId);
                    System.out.println(TAG+"Before sending to server------------------>>>" + reqObj.toString());

                    respObj = HttpUtils.sendToServer(mContext, reqObj, AppContstants.APPLICATIONS_PRE_PROCESSING);
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
                        tvAppliedCount.setText(respObj.getString("applied"));
                        JSONArray jaComp = respObj.getJSONArray("exams");
                        ArrayList<ExamModel> alExams = new ArrayList<>();
                        if(jaComp.length() > 0){
                            ExamModel em = null ;
                            for(int i = 0 ; i < jaComp.length(); i++){
                                em = new ExamModel();
                                em.setExam_code(jaComp.getJSONObject(i).getString("exam_code"));
                                em.setExam_title(jaComp.getJSONObject(i).getString("exam_title"));
                                alExams.add(em);
                            }
                            //Setting adapter to show the items in the spinner
                            //spinnerCompany.setAdapter(new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, mVehicles));

                        }
                        ArrayAdapter<ExamModel> adapter =
                                new ArrayAdapter<ExamModel>(
                                        mContext,
                                        android.R.layout.simple_spinner_item,
                                        alExams);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerExam.setAdapter(adapter);
                        System.out.println(TAG + "spinner length----->" + spinnerExam.getChildCount());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
                }
            }
        };
        task.execute();
    }//end of populateCompanies
}//end class
