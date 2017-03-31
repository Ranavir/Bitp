package team.tcc.app.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

/**
 * This Activity used for retrieving password from server
 * when User forgets it's password
 * Created by Nandinee on 29-09-2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = ForgotPasswordActivity.class.getSimpleName()+": ";
    View view,emailLayout;
    TextView errEmailTV;
    EditText emailET;
    Button btnSubmit;
    /*Used for storing the current activity's context*/
    private Context mContext;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the email request to get Back password*/
    private JSONObject jObjPassReq;
    /*Used for opening new Activity from this Activity*/
    private Intent mIntent;
    public static String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "###onCreate invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        btnSubmit = (Button) findViewById(R.id.fpwSubmitBtn);
        btnSubmit.setOnClickListener(this);

        emailET = (EditText) findViewById(R.id.etEmailID);

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
            case R.id.fpwSubmitBtn:
                if(validEntry()){
                    if(AppUtils.checkInternet(mContext)) {
                        new RetrievePasswordTask().execute();
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);
                    }
                }
                break;


        }
    }//end onClick
    /**
     * This class object is going send Password Recovery request to
     * server so that password can be sent to its mail id and profile
     * details send to local so it updates the local
     *
     * @author Ranavir Dash
     * @date 05102016
     */
    private class RetrievePasswordTask extends AsyncTask<Void, Void, Void> {
        JSONObject respObj = null ;
        RetrievePasswordTask(){
            //constructor
            Log.d(TAG,"RetrievePasswordTask initiated...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("Sending new password");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if(AppUtils.checkInternet(getApplicationContext())){
                respObj = HttpUtils.sendToServer(getApplicationContext(), jObjPassReq, AppContstants.PW_RECOVERY_REQ_ID);

            }else{
                System.out.println(TAG + AppContstants.NO_INTERNET);
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
                    //save user details in local because no data was there in local so live login happening
                    /*mDbHelper = new DatabaseHelper(mContext);
                    mDbHelper.logUserDetails(respObj,true);//Here auto_login_flag is by default set to true
                    mDbHelper.close();*/
                    Toast.makeText(mContext,"Password successfully sent to mail...",Toast.LENGTH_LONG);
                    finish();//CLOSE this activity
                }else{
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_PW_RECOVERY_FAIL);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Message", AppContstants.MSG_PW_RECOVERY_FAIL);
            }

        }
    }//end class RetrievePasswordTask

    /**
     *
     * This method makes client side validation of the entries by user
     * @return
     */
    private boolean validEntry() {
        boolean flag = true;
        emailId = emailET.getText().toString();

        if(!(emailId.length() > 0)) {
            AppUtils.showAlert(mContext, "Error", "Email Id is Required.");
            flag = false;
        }else{
            //initialize with data for future use
            jObjPassReq = new JSONObject();
            try {
                jObjPassReq.put("email", emailId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
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
