package team.tcc.app.main;


import android.app.ProgressDialog;
import android.content.Context;
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
import team.tcc.app.model.CompanyModel;
import team.tcc.app.model.StudentTrainingProfileModel;
import team.tcc.app.model.TrainingModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class StudentTrainingFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "StudentTrainingFragment";


    private Context mContext;
    FragmentTransaction mFt ;
    private Button mBtnSubmit;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerTraining;
    private Button btnFeedback;
    private TextView tvTrainingDetails,mth1Amt,mth1Status,mth2Amt,mth2Status,mth3Amt,mth3Status,mth4Amt,mth4Status,mth5Amt,mth5Status,mth6Amt,mth6Status;
    private TextView tvFeedback1,tvFeedback2,tvFeedback3,tvFeedback4,tvFeedback5,tvFeedback6;
    private String mTrainingCode;
    private JSONObject mJObjTraingProfile;
    private TrainingModel mTrainingModel;

    /*This will hold the request data */
    private JSONObject mJObjReq;
    public StudentTrainingFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_student_training, container, false);



        spinnerTraining = (Spinner)rootView.findViewById(R.id.spinnerTraining);
        tvTrainingDetails = (TextView)rootView.findViewById(R.id.tvTrainingDetails);
        mth1Amt = (TextView)rootView.findViewById(R.id.mth1Amt);
        mth1Status = (TextView)rootView.findViewById(R.id.mth1Status);
        mth2Amt = (TextView)rootView.findViewById(R.id.mth2Amt);
        mth2Status = (TextView)rootView.findViewById(R.id.mth2Status);
        mth3Amt = (TextView)rootView.findViewById(R.id.mth3Amt);
        mth3Status = (TextView)rootView.findViewById(R.id.mth3Status);
        mth4Amt = (TextView)rootView.findViewById(R.id.mth4Amt);
        mth4Status = (TextView)rootView.findViewById(R.id.mth4Status);
        mth5Amt = (TextView)rootView.findViewById(R.id.mth5Amt);
        mth5Status = (TextView)rootView.findViewById(R.id.mth5Status);
        mth6Amt = (TextView)rootView.findViewById(R.id.mth6Amt);
        mth6Status = (TextView)rootView.findViewById(R.id.mth6Status);
        tvFeedback1 = (TextView)rootView.findViewById(R.id.tvFeedback1);
        tvFeedback2 = (TextView)rootView.findViewById(R.id.tvFeedback2);
        tvFeedback3 = (TextView)rootView.findViewById(R.id.tvFeedback3);
        tvFeedback4 = (TextView)rootView.findViewById(R.id.tvFeedback4);
        tvFeedback5 = (TextView)rootView.findViewById(R.id.tvFeedback5);
        tvFeedback6 = (TextView)rootView.findViewById(R.id.tvFeedback6);

        btnFeedback = (Button) rootView.findViewById(R.id.btnFeedback);
        btnFeedback.setTransformationMethod(null);
        btnFeedback.setOnClickListener(this);



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
                mTrainingCode = mTrainingModel.getTraining_code();
                Log.d("TAG", "###Training Selected--->" + mTrainingCode);
                //Go for the task fetching training profile
                new TrainingProfileTask().execute(mTrainingCode);
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
            case R.id.btnFeedback:

                if(validEntry()){
                    //for for feedback
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    StudentFeedbackFragment sff = new StudentFeedbackFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("training_profile", mJObjTraingProfile.toString());
                    sff.setArguments(bundle);
                    ft.replace(R.id.frame_container,sff );
                    ft.addToBackStack("StudentFeedbackFragment");
                    ft.commit();
                }
                break;
            default:
                break;
        }
    }//end of onClick

    /**
     * This method checks whether Details entered are valid by Syntax or not
     * and wraps it in a json obj
     * @author Amod
     * @date 25Mar2017
     */
    private boolean validEntry() {
        boolean flag = true ;
        if(null == mJObjTraingProfile || !(mJObjTraingProfile.length() > 0)) {
            AppUtils.showAlert(mContext, "Alert", "You are not enrolled for this training.");
            flag = false ;
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task is used to fetch the training details of
     * a student by it student id and training code
     *******************************************************/

    private class TrainingProfileTask extends AsyncTask<String, Void, Void> {
        JSONObject respObj = null ;
        String msg = null;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("fetching student training profile");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(String... arg0) {

            try {
                mJObjReq = new JSONObject();
                mJObjReq.put("training_code",arg0[0]);
                mJObjReq.put("user_id",mPreference.getUserId());
                System.out.println(TAG + "Before sending to server------------------>>>" + mJObjReq.toString());

                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.FETCH_TRAINING_PROFILE);
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
                    mJObjTraingProfile = respObj.getJSONObject("training_profile");
                    if(null != mJObjTraingProfile && mJObjTraingProfile.length()>0){
                        Log.d(TAG, "Student training profile--->" + mJObjTraingProfile.toString());
                        /*private TextView tvTrainingDetails,mth1Amt,mth1Status,mth2Amt,mth2Status,mth3Amt,mth3Status,mth4Amt,mth4Status,mth5Amt,mth5Status,mth6Amt,mth6Status;
                        private TextView tvFeedback1,tvFeedback2,tvFeedback3,tvFeedback4,tvFeedback5,tvFeedback6*/
                        StringBuffer sb = new StringBuffer();
                        sb.append("Company Code : "+mJObjTraingProfile.getString("comp_code")+"\n");
                        sb.append("Training : "+mJObjTraingProfile.getString("training_desc") + "\n");
                        sb.append("Project Submitted : "+(TextUtils.equals(mJObjTraingProfile.getString("project_submission_status"),"Y") ? "Yes" : "No")+"\n");
                        sb.append("Certificate Issued : "+(TextUtils.equals(mJObjTraingProfile.getString("issue_certificate_status"),"Y") ? "Yes" : "No"));
                        tvTrainingDetails.setText(sb.toString());
                        mth1Amt.setText(mJObjTraingProfile.getString("mth1_stipen_amt"));
                        mth2Amt.setText(mJObjTraingProfile.getString("mth2_stipen_amt"));
                        mth3Amt.setText(mJObjTraingProfile.getString("mth3_stipen_amt"));
                        mth4Amt.setText(mJObjTraingProfile.getString("mth4_stipen_amt"));
                        mth5Amt.setText(mJObjTraingProfile.getString("mth5_stipen_amt"));
                        mth6Amt.setText(mJObjTraingProfile.getString("mth6_stipen_amt"));

                        mth1Status.setText(TextUtils.equals(mJObjTraingProfile.getString("mth1_stipen_rcv"),"Y") ? "Received" : "Pending");
                        mth2Status.setText(TextUtils.equals(mJObjTraingProfile.getString("mth2_stipen_rcv"),"Y") ? "Received" : "Pending");
                        mth3Status.setText(TextUtils.equals(mJObjTraingProfile.getString("mth3_stipen_rcv"),"Y") ? "Received" : "Pending");
                        mth4Status.setText(TextUtils.equals(mJObjTraingProfile.getString("mth4_stipen_rcv"),"Y") ? "Received" : "Pending");
                        mth5Status.setText(TextUtils.equals(mJObjTraingProfile.getString("mth5_stipen_rcv"),"Y") ? "Received" : "Pending");
                        mth6Status.setText(TextUtils.equals(mJObjTraingProfile.getString("mth6_stipen_rcv"),"Y") ? "Received" : "Pending");

                        tvFeedback1.setText(mJObjTraingProfile.getString("mth1_feedback"));
                        tvFeedback2.setText(mJObjTraingProfile.getString("mth2_feedback"));
                        tvFeedback3.setText(mJObjTraingProfile.getString("mth3_feedback"));
                        tvFeedback4.setText(mJObjTraingProfile.getString("mth4_feedback"));
                        tvFeedback5.setText(mJObjTraingProfile.getString("mth5_feedback"));
                        tvFeedback6.setText(mJObjTraingProfile.getString("mth6_feedback"));
                    }else{
                        AppUtils.showAlert(mContext, "Error", "Student Training profile not found.");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }
        }
    };
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
}//end class
