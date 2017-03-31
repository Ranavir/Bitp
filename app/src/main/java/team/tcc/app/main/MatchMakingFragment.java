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
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class MatchMakingFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "MatchMakingFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Button btnSelect,btnReject;
    private TextView tvNotificationDesc,tvOlExamTotal,tvInterviewTotal,tvCutoff;
    private TextView tvOlExamMark,tvInterviewMark,tvSelectionStatus;
    private String mExamCode;
    private String mStudentId;
    private JSONObject mJObjExamProfile;

    private JSONObject mJObjReq;
    public MatchMakingFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_match_making, container, false);



        tvNotificationDesc = (TextView)rootView.findViewById(R.id.tvNotificationDesc);
        tvOlExamTotal = (TextView)rootView.findViewById(R.id.tvOlExamTotal);
        tvInterviewTotal = (TextView)rootView.findViewById(R.id.tvInterviewTotal);
        tvCutoff = (TextView)rootView.findViewById(R.id.tvCutoff);
        tvOlExamMark = (TextView)rootView.findViewById(R.id.tvOlExamMark);
        tvInterviewMark = (TextView)rootView.findViewById(R.id.tvInterviewMark);
        tvSelectionStatus = (TextView)rootView.findViewById(R.id.tvSelectionStatus);

        btnSelect = (Button) rootView.findViewById(R.id.btnSelect);
        btnSelect.setTransformationMethod(null);
        btnSelect.setOnClickListener(this);
        btnReject = (Button) rootView.findViewById(R.id.btnReject);
        btnReject.setTransformationMethod(null);
        btnReject.setOnClickListener(this);

        //Retrieve the bundle element
        if(null != getArguments()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Notification Id : "+getArguments().getString("notification_id"));
            sb.append("\n"+getArguments().getString("notification"));
            tvNotificationDesc.setText(sb.toString());
            try {
                mJObjExamProfile = new JSONObject(getArguments().getString("exam_profile"));
                 /*private Button btnSelect,btnReject;
        private TextView tvNotificationDesc,tvOlExamTotal,tvInterviewTotal,tvCutoff;
        private TextView tvOlExamMark,tvInterviewMark,tvSelectionStatus;
        private String mExamCode;
        private String mStudentId;
        private JSONObject mJObjExamProfile;*/
                tvOlExamTotal.setText(mJObjExamProfile.getString("marks_exam_total"));
                tvInterviewTotal.setText(mJObjExamProfile.getString("marks_interview_total"));
                tvCutoff.setText(mJObjExamProfile.getString("marks_total_cutoff"));
                tvOlExamMark.setText(mJObjExamProfile.getString("marks_exam_acquired"));
                tvInterviewMark.setText(mJObjExamProfile.getString("marks_interview_acquired"));
                String status = mJObjExamProfile.getString("selection_status");
                if(!TextUtils.equals(status,"pending")){//Then show the button to take action select/reject
                    btnSelect.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                }
                tvSelectionStatus.setText(status);
                mExamCode = mJObjExamProfile.getString("exam_code");
                mStudentId = mJObjExamProfile.getString("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            case R.id.btnSelect:

                if(validEntry(true)){
                    new StudentSelectionTask().execute();
                }
                break;
            case R.id.btnReject:

                if(validEntry(false)){
                    new StudentSelectionTask().execute();
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
    private boolean validEntry(boolean status) {
        boolean flag = true ;

        if(TextUtils.isEmpty(mStudentId)){
            AppUtils.showAlert(mContext, "Alert", "Invalid user information.");
            flag = false ;
        }else if(TextUtils.isEmpty(mExamCode)){
            AppUtils.showAlert(mContext, "Alert", "Exam not conducted yet.");
            flag = false ;
        }else{
            mJObjReq = new JSONObject();
            try {
                if(status){
                    mJObjReq.put("selection_status", "selected");
                }else{
                    mJObjReq.put("selection_status", "rejected");
                }
                mJObjReq.put("exam_code", mExamCode);
                mJObjReq.put("user_id", mStudentId);
                mJObjReq.put("admin_user_id", ProfileModel.user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry


    /******************************************************
     * This task is used to update student selection status
     *******************************************************/

    private class StudentSelectionTask extends AsyncTask<String, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        StudentSelectionTask(){
            //constructor
            Log.w(TAG,"StudentSelectionTask started...");
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

                Log.d(TAG,"###Request data--->"+mJObjReq);
                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.EXAM_SELECTION);
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
    }//end class NotificationTask
}//end class
