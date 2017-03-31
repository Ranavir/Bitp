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
import java.util.Calendar;

import team.tcc.app.R;
import team.tcc.app.adapter.CustomDropdownAdapter;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.model.CompanyModel;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class StudentFeedbackFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "StudentFeedbackFragment";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerMonth;
    private EditText etFeedbackTxt;
    private Button btnPostFeedback;
    private String mFeedback,mMth;

    private JSONObject mJOBjReq;
    private JSONObject mJObjTraingProfile;
    public StudentFeedbackFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_student_feedback, container, false);

        spinnerMonth = (Spinner)rootView.findViewById(R.id.spinnerMonth);
        etFeedbackTxt = (EditText)rootView.findViewById(R.id.etFeedbackTxt);

        btnPostFeedback = (Button) rootView.findViewById(R.id.btnPostFeedback);
        btnPostFeedback.setTransformationMethod(null);
        btnPostFeedback.setOnClickListener(this);

        if(null != getArguments()) {
            try {
                mJObjTraingProfile = new JSONObject(getArguments().getString("training_profile"));
                populateMonths();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            getActivity().getSupportFragmentManager().popBackStackImmediate();
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
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMth = parent.getSelectedItem().toString();
                Log.d("TAG", "###Month Selected--->" + mMth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
            case R.id.btnPostFeedback:

                if(validEntry()){
                    //add new feedback
                    if(AppUtils.checkInternet(mContext)) {
                        new FeedbackTask().execute();
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

        mFeedback = etFeedbackTxt.getText().toString();


        if(TextUtils.isEmpty(mMth) || TextUtils.equals(mMth,"--Month--")) {
            AppUtils.showAlert(mContext, "Alert", "Please choose a valid month.");
            flag = false ;
        }else if(!(mFeedback.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Feedback cannot be blank.");
            flag = false ;
        }else{
            mJOBjReq = new JSONObject();
            try {
                if(TextUtils.equals(mMth, "First Month")){
                    mJOBjReq.put("mth1_feedback", mFeedback);
                }
                if(TextUtils.equals(mMth, "Second Month")){
                    mJOBjReq.put("mth2_feedback", mFeedback);
                }
                if(TextUtils.equals(mMth, "Third Month")){
                    mJOBjReq.put("mth3_feedback", mFeedback);
                }
                if(TextUtils.equals(mMth, "Fourth Month")){
                    mJOBjReq.put("mth4_feedback", mFeedback);
                }
                if(TextUtils.equals(mMth, "Fifth Month")){
                    mJOBjReq.put("mth5_feedback", mFeedback);
                }
                if(TextUtils.equals(mMth, "Sixth Month")){
                    mJOBjReq.put("mth6_feedback", mFeedback);
                }
                mJOBjReq.put("training_code", mJObjTraingProfile.getString("training_code"));
                mJOBjReq.put("user_id", mPreference.getUserId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task is used to push a training feedback
     *******************************************************/

    private class FeedbackTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        FeedbackTask(){
            //constructor
            Log.w(TAG, "FeedbackTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("Submitting your feedback");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                respObj = HttpUtils.sendToServer(mContext, mJOBjReq, AppContstants.PUSH_FEEDBACK);
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
                    //AppUtils.showAlert(mContext, "Message", respObj.getString("msg"));//success/failure
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
     * This method used to fetch months
     **********************************************************/
    private void populateMonths() {
        ArrayList<String> months = new ArrayList<String>();
        months.add("--Month--");
        try {
            if(TextUtils.isEmpty(mJObjTraingProfile.getString("mth1_feedback"))){
                months.add("First Month");
            }
            if(TextUtils.isEmpty(mJObjTraingProfile.getString("mth2_feedback"))){
                months.add("Second Month");
            }
            if(TextUtils.isEmpty(mJObjTraingProfile.getString("mth3_feedback"))){
                months.add("Third Month");
            }
            if(TextUtils.isEmpty(mJObjTraingProfile.getString("mth4_feedback"))){
                months.add("Fourth Month");
            }
            if(TextUtils.isEmpty(mJObjTraingProfile.getString("mth5_feedback"))){
                months.add("Fifth Month");
            }
            if(TextUtils.isEmpty(mJObjTraingProfile.getString("mth5_feedback"))){
                months.add("Sixth Month");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomDropdownAdapter adapterMonths = new CustomDropdownAdapter(mContext,months);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonths);


    }//end of populateMonths
}//end class
