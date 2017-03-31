package team.tcc.app.main;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

/**
 * This Activity used for Registration of User
 * for the first time
 * Created by Nandinee on 28-09-2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "RegisterActivity:";

    private View view, mobileLayout,emailLayout,pincodeLayout;
    private TextView  loginTv;
    private Button btnRegister;
    private EditText nameET, phoneET,emailET,unameET,passET,cnfPassET;
    private RadioGroup userTypeRadio;

    private static String mName,mPhone,mEmail,mUname,mPass,mCnfPass,mUserType;


    /*Used for storing the current activity's context*/
    private Context mContext;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*Used for Database operations*/
    private DatabaseHelper mDbHelper;
    /*This will hold the AppPreference*/
    AppSharedPreference mPreference;
    /*This will hold the registration details entered by the user*/
    private JSONObject jsonObjRegDetails;
    /*Used for opening new Activity from this Activity*/
    private Intent mIntent;
    FragmentManager mFragmentManager ;
    FragmentTransaction mFragmentTransaction ;

    /*Google API Key for Location*/
    //String key="AIzaSyCTOeNeK8ZiLpXrkAggoXkSr7CaVFcKkZw";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "### onCreate() invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        mPreference = new AppSharedPreference(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginTv = (TextView) findViewById(R.id.loginLink);
        loginTv.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.registerButton);
        btnRegister.setOnClickListener(this);
        //private EditText nameET, phoneET,emailET,unameET,passET,cnfPassET,userTypeRadio;
        nameET = (EditText) findViewById(R.id.etName);
        phoneET = (EditText) findViewById(R.id.etPhoneNo);
        emailET = (EditText) findViewById(R.id.etEmailID);
        unameET = (EditText) findViewById(R.id.etUsername);
        passET = (EditText) findViewById(R.id.etPassword);
        cnfPassET = (EditText) findViewById(R.id.etConfirmPassword);
        userTypeRadio = (RadioGroup) findViewById(R.id.radioUserType);


    }//end onCreate
    // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape mode", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait mode", Toast.LENGTH_SHORT).show();
        }
    }//end onConfigurationChanged


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        view = v;
        switch (v.getId()) {

            case R.id.loginLink:
                mIntent = new Intent(this, LoginActivity.class);
                startActivity(mIntent);
                break;
            case R.id.registerButton:
                if(validEntries()){
                    Log.d(TAG,"Entries validated...");
                    if(AppUtils.checkInternet(mContext)) {
                        new RegistrationTask().execute();
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);
                    }
                }
                break;


        }
    }//end of onClick




    /**
     * This class object is going to save the user details in database before
     * going for actual registration in server with active flag false
     * Then send asynch request to server for Registration
     * and get success/failure response
     */
    private class RegistrationTask extends AsyncTask<Void, Void, Void>{
            JSONObject respObj = null ;
            RegistrationTask(){
                //constructor
                Log.d(TAG,"RegistrationTask started...");
            }
            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setTitle("Registration in progress");
                pd.setMessage("Please wait...");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    /*//Save data in local database first
                    mDbHelper = new DatabaseHelper(getApplicationContext());
                    mDbHelper.logUserDetails(jsonObjRegDetails);
                    mDbHelper.close();*/


                    if(AppUtils.checkInternet(getApplicationContext())){

                        respObj = HttpUtils.sendToServer(getApplicationContext(), jsonObjRegDetails, AppContstants.REG_REQ_ID);
                    }else{
                        System.out.println(TAG + AppContstants.NO_INTERNET);
                    }
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
                    if (null != respObj && respObj.getString("status").equalsIgnoreCase(AppContstants.MSG_SUCCESS)) {
                        Log.d(TAG, "Registration request successfully processed..");
                        Log.d(TAG, "Messsage--->" + respObj.getString("msg"));



                        if(respObj.getString("msg").equalsIgnoreCase(AppContstants.MSG_DUPLICATE_REGISTRATION)){//show message to login or choose forgot password
                            AppUtils.showAlert(mContext, "Message", "User already registered please try login or forgot password");
                        }else{
                            //Save data in local database and preference after saving in server
                            //mPreference.saveUserId(respObj.getString("user_id"));

                            //save user details in local
                            /*jsonObjRegDetails.put("user_id", respObj.getString("user_id"));
                            mDbHelper = new DatabaseHelper(getApplicationContext());
                            mDbHelper.logUserDetails(jsonObjRegDetails,false);
                            mDbHelper.close();*/
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                    RegisterActivity.this);
                            alertDialog.setTitle("Message");
                            alertDialog.setMessage("Thank you for Registering with BITP.");

                            alertDialog.setNeutralButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });

                            alertDialog.show();

                        }

                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.MSG_REG_FAIL);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_REG_FAIL);
                }

            }
    }//end class RegistrationTask

    /**
     * This method is used for validating the values entered by the user
     * and displaying corresponding error related UIs
     */
    private boolean validEntries() {

        boolean flag = true ;
        //private static String mName,mPhone,mEmail,mUname,mPass,mCnfPass,mUserType;
        mName = nameET.getText().toString();
        mPhone = phoneET.getText().toString();
        mEmail = emailET.getText().toString();
        mUname = unameET.getText().toString();
        mPass = passET.getText().toString();
        mCnfPass = cnfPassET.getText().toString();
        mUserType = ((RadioButton)(userTypeRadio.findViewById(userTypeRadio.getCheckedRadioButtonId()))).getText().toString();

        if(mName.equalsIgnoreCase("")){
            AppUtils.showAlert(mContext, "Error", "Please Enter Name.");
            flag = false;
        }else if(!(mPhone.length() > 0) || mPhone.length() != 10){
            AppUtils.showAlert(mContext, "Error", AppContstants.INVALID_PHONE_NO);
            flag = false;
        }else if(mEmail.equalsIgnoreCase("")|| !(mEmail.matches(AppContstants.EMAIL_PATTERN))){
            AppUtils.showAlert(mContext, "Error", AppContstants.INVALID_EMAIL_ID);
            flag = false;
        }else if(mUname.equalsIgnoreCase("")){
            AppUtils.showAlert(mContext, "Error", "Please Enter Username.");
            flag = false;
        }else if(mPass.equalsIgnoreCase("")|| mCnfPass.equalsIgnoreCase("") || !TextUtils.equals(mPass,mCnfPass)){
            AppUtils.showAlert(mContext, "Error", "Invalid Passwords Entered.");
            flag = false;
        }else{
            //initialize with data for future use
            jsonObjRegDetails = new JSONObject();
            try {
                jsonObjRegDetails.put("name", mName);
                jsonObjRegDetails.put("phone", mPhone);
                jsonObjRegDetails.put("email", mEmail);
                //jsonObjRegDetails.put("imei_no", "");//AppUtils.getImeiNo(mContext) with permission in marshmallow
                jsonObjRegDetails.put("uname", mUname);
                jsonObjRegDetails.put("pass", mPass);
                jsonObjRegDetails.put("user_type",mUserType);

                Log.d(TAG,"###Registration details--->"+jsonObjRegDetails.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }//end of validEntries
    @Override
    public void onResume() {
        Log.d(TAG, "###onResume...");
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "###onBackPressed...");
        super.onBackPressed();
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "###onDestroy...");
        super.onDestroy();

    }
}
