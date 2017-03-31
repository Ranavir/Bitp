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
import team.tcc.app.model.PlacementModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class NewPlacementFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "NewPlacementFragment:";


    private Context mContext;
    FragmentTransaction mFt ;

    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;

    private Button btnCreatePlacement;
    private Spinner spinnerCompany;
    private EditText etPlacementTxt;
    private String mPlacementTxt,mComp;

    /*This will hold the unique ids of member along with OTP to validate */
    private JSONObject mJSONReq;
    public NewPlacementFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_new_placement, container, false);

        // Set Gender
        spinnerCompany = (Spinner)rootView.findViewById(R.id.spinnerCompany);
        etPlacementTxt = (EditText)rootView.findViewById(R.id.etPlacementTxt);


        btnCreatePlacement = (Button) rootView.findViewById(R.id.btnCreatePlacement);
        btnCreatePlacement.setTransformationMethod(null);
        btnCreatePlacement.setOnClickListener(this);



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
            case R.id.btnCreatePlacement:

                if(validEntry()){
                    //add new notification
                    if(AppUtils.checkInternet(mContext)) {
                        new PlacementTask().execute();
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
        //private EditText etPlacementTxt;
       // private String mPlacementTxt,mComp;

        mPlacementTxt = etPlacementTxt.getText().toString();


        if(TextUtils.isEmpty(mComp)) {
            AppUtils.showAlert(mContext, "Alert", "Invalid company code.");
            flag = false ;
        }else if(!(mPlacementTxt.length() > 0)){
            AppUtils.showAlert(mContext, "Alert", "Placement text cannot be blank.");
            flag = false ;
        }else{
            mJSONReq = new JSONObject();
            try {

                mJSONReq.put("comp_code", mComp);
                mJSONReq.put("news", mPlacementTxt);
                mJSONReq.put("created_by", mPreference.getUserId());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  flag;
    }//validEntry
    /******************************************************
     * This task is used to create new placement news
     *******************************************************/

    private class PlacementTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        PlacementTask(){
            //constructor
            Log.w(TAG, "PlacementTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("creating placement news");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {


                respObj = HttpUtils.sendToServer(mContext, mJSONReq, AppContstants.NEW_PLACEMENT);
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
