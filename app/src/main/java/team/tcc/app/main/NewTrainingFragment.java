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
import team.tcc.app.model.CompanyModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class NewTrainingFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "NewTrainingFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Spinner spinnerCompany;
    private EditText etTrainingDesc, etMth1, etMth2, etMth3, etMth4,etMth5,etMth6;
    private Button btnCreateTraining;
    private String mComp,mDesc,mAmtMth1,mAmtMth2,mAmtMth3,mAmtMth4,mAmtMth5,mAmtMth6;

    private JSONObject mJObjReq;
    public NewTrainingFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_new_training, container, false);



        spinnerCompany = (Spinner)rootView.findViewById(R.id.spinnerCompany);
        etTrainingDesc = (EditText)rootView.findViewById(R.id.etTrainingDesc);
        etMth1 = (EditText)rootView.findViewById(R.id.etMth1);
        etMth2 = (EditText)rootView.findViewById(R.id.etMth2);
        etMth3 = (EditText)rootView.findViewById(R.id.etMth3);
        etMth4 = (EditText)rootView.findViewById(R.id.etMth4);
        etMth5 = (EditText)rootView.findViewById(R.id.etMth5);
        etMth6 = (EditText)rootView.findViewById(R.id.etMth6);

        btnCreateTraining = (Button) rootView.findViewById(R.id.btnCreateTraining);
        btnCreateTraining.setTransformationMethod(null);
        btnCreateTraining.setOnClickListener(this);



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
        spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CompanyModel cm = (CompanyModel)parent.getSelectedItem();
                mComp =  cm.getComp_code();
                Log.d("TAG","###Company Selected--->"+mComp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Get companies names
        if(AppUtils.checkInternet(mContext)) {
            populateCompanies();
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
            case R.id.btnCreateTraining:

                if(validEntry()){
                    //add new training
                    if(AppUtils.checkInternet(mContext)) {
                        new TrainingTask().execute();
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
        /*private Spinner spinnerCompany,spinnerNotification;
        private EditText etTrainingDesc, etMth1, etMth2, etMth3, etMth4,etMth5,etMth6;
        private Button btnCreateTraining;
        private String mComp,mNotification,mDesc,mAmtMth1,mAmtMth2,mAmtMth3,mAmtMth4,mAmtMth5,mAmtMth6;*/

        mDesc = etTrainingDesc.getText().toString();
        mAmtMth1 = etMth1.getText().toString();
        mAmtMth2 = etMth2.getText().toString();
        mAmtMth3 = etMth3.getText().toString();
        mAmtMth4 = etMth4.getText().toString();
        mAmtMth5 = etMth5.getText().toString();
        mAmtMth6 = etMth6.getText().toString();

        if(TextUtils.isEmpty(mComp)) {
            AppUtils.showAlert(mContext, "Alert", "Invalid company code.");
            flag = false ;
        }else if(!(mDesc.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Description cannot be blank.");
            flag = false ;
        }else if(!(mAmtMth1.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Stipend amount cannot be blank.");
            flag = false ;
        }else if(!(mAmtMth2.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Stipend amount cannot be blank.");
            flag = false ;
        }else if(!(mAmtMth3.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Stipend amount cannot be blank.");
            flag = false ;
        }else if(!(mAmtMth4.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Stipend amount cannot be blank.");
            flag = false ;
        }else if(!(mAmtMth5.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Stipend amount cannot be blank.");
            flag = false ;
        }else if(!(mAmtMth6.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Stipend amount cannot be blank.");
            flag = false ;
        }else{
            mJObjReq = new JSONObject();
            try {

                mJObjReq.put("mth1_stipen_amt", mAmtMth1);
                mJObjReq.put("mth2_stipen_amt", mAmtMth2);
                mJObjReq.put("mth3_stipen_amt", mAmtMth3);
                mJObjReq.put("mth4_stipen_amt", mAmtMth4);
                mJObjReq.put("mth5_stipen_amt", mAmtMth5);
                mJObjReq.put("mth6_stipen_amt", mAmtMth6);
                mJObjReq.put("comp_code", mComp);
                mJObjReq.put("training_desc", mDesc);
                mJObjReq.put("created_by",mPreference.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task is used to create a new training activity
     * under a company
     *******************************************************/

    private class TrainingTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        TrainingTask(){
            //constructor
            Log.w(TAG, "TrainingTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("Creating New Training");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {


                respObj = HttpUtils.sendToServer(mContext, mJObjReq, AppContstants.NEW_TRAINING);
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
     * This method used to fetch company details
     **********************************************************/
    private void populateCompanies() {
        System.out.println(TAG + " ENTRY----> populateCompanies");


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            JSONObject respObj = null ;
            String msg = null;
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(mContext);
                pd.setTitle("fetching companies");
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {

                try {
                        JSONObject reqObj = new JSONObject();

                    System.out.println(TAG+"Before sending to server------------------>>>" + reqObj.toString());

                    respObj = HttpUtils.sendToServer(mContext, reqObj, AppContstants.FETCH_COMPANY);
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
                        JSONArray jaComp = respObj.getJSONArray("companies");
                        ArrayList<CompanyModel> alComps = new ArrayList<>();
                        if(jaComp.length() > 0){
                            CompanyModel cm = null ;
                            for(int i = 0 ; i < jaComp.length(); i++){
                                cm = new CompanyModel();
                                cm.setComp_code(jaComp.getJSONObject(i).getString("comp_code"));
                                cm.setComp_name(jaComp.getJSONObject(i).getString("comp_name"));
                                alComps.add(cm);
                            }
                            //Setting adapter to show the items in the spinner
                            //spinnerCompany.setAdapter(new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, mVehicles));

                        }
                        ArrayAdapter<CompanyModel> adapter =
                                new ArrayAdapter<CompanyModel>(
                                        mContext,
                                        android.R.layout.simple_spinner_item,
                                        alComps);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCompany.setAdapter(adapter);
                        System.out.println(TAG + "spinner length----->" + spinnerCompany.getChildCount());
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
