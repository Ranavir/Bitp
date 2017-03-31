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
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.model.TrainingModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class IssueCertificateFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ICertFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerTraining;
    private Button btnIssueCertificate;
    private EditText etStudentId;
    private String mTraining,mStudentId;
    private TrainingModel mTrainingModel;

    private JSONObject mJObjReq;
    public IssueCertificateFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_issue_certificate, container, false);

        spinnerTraining = (Spinner)rootView.findViewById(R.id.spinnerTraining);
        etStudentId = (EditText)rootView.findViewById(R.id.etStudentId);
        btnIssueCertificate = (Button) rootView.findViewById(R.id.btnIssueCertificate);
        btnIssueCertificate.setTransformationMethod(null);
        btnIssueCertificate.setOnClickListener(this);



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
            case R.id.btnIssueCertificate:

                if(validEntry()){
                    //Payment stipend task
                    if(AppUtils.checkInternet(mContext)) {
                        new IssueCertificateTask().execute();
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
     * @date
     */
    private boolean validEntry() {
        boolean flag = true ;
        mStudentId = etStudentId.getText().toString();
        if(TextUtils.isEmpty(mTraining)) {
            AppUtils.showAlert(mContext, "Alert", "Please select training.");
            flag = false ;
        }else if(TextUtils.isEmpty(mStudentId)) {
            AppUtils.showAlert(mContext, "Alert", "Invalid student id.");
            flag = false ;
        }else{
            mJObjReq = new JSONObject();
            try {

                mJObjReq.put("training_code", mTraining);
                mJObjReq.put("user_id", mStudentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task does the followings:
     * - The task is to issue certificate to these users
     * who are involved with trainings
     *******************************************************/

    private class IssueCertificateTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        IssueCertificateTask(){
            //constructor
            Log.w(TAG, "IssueCertificateTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("Issuing certificate");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {


                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.ISSUE_CERTIFICATE);
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
