package team.tcc.app.main;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "ChangePasswordActivity:";


    private Context mContext;
    private Button mBtnSubmit;
    /*This will hold the current password*/
    private EditText mEtCurrPassword;
    /*This will hold the password to be updated*/
    private EditText mEtNewPassword;
    private CheckBox mCBShowPassword;
    private String strCurrPass = "";
    private String strNewPass = "";
    private ProgressDialog pd;
    /*This will hold the unique ids of member along with OTP to validate */
    private JSONObject jObjChangePasswordReq;
    /*This will hold the AppPreference*/
    AppSharedPreference mPreference;
    public ChangePasswordActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "###onCreate invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mPreference = new AppSharedPreference(getApplicationContext());

        mBtnSubmit = (Button)findViewById(R.id.btn_change_pw_submit);
        mBtnSubmit.setTransformationMethod(null);
        mBtnSubmit.setOnClickListener(this);

        mEtCurrPassword = (EditText)findViewById(R.id.et_current_password);
        mEtNewPassword = (EditText)findViewById(R.id.et_new_password);
        mCBShowPassword = (CheckBox)findViewById(R.id.checkbox_show_pwd);

        mCBShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           if(isChecked){
                                                               Log.d(TAG,"Show password characters...");
                                                               mEtCurrPassword.setTransformationMethod(null);
                                                               mEtNewPassword.setTransformationMethod(null);
                                                           }else{
                                                               Log.d(TAG, "Hide password characters...");
                                                               mEtCurrPassword.setTransformationMethod(new PasswordTransformationMethod());
                                                               mEtNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                                                           }
                                                       }
                                                   }
        );
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
    public void onDestroy() {
        Log.d(TAG, "###onDestroy...");
        super.onDestroy();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_change_pw_submit:

                if(validEntry()){
                    if(AppUtils.checkInternet(mContext)) {
                        Toast.makeText(mContext,"Will update new password soon...",Toast.LENGTH_SHORT).show();
                        new PasswordChangeTask().execute();
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
     * This class object is going to send new password to server for updation
     * and after success it saves it in local for future use
     *
     * @author Ranavir Dash
     * @date 06102016
     */
    private class PasswordChangeTask extends AsyncTask<Void, Void, Void> {
        JSONObject respObj = null ;
        PasswordChangeTask(){
            //constructor
            Log.d(TAG, "PasswordChangeTask initiated...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("Updating Password");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if(AppUtils.checkInternet(mContext)){
                respObj = HttpUtils.sendToServer(mContext, jObjChangePasswordReq, AppContstants.PW_CHANGE_REQ_ID);

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
                if (null != respObj) {
                    if (respObj.getString("status").equalsIgnoreCase(AppContstants.MSG_SUCCESS)) {
                        Toast.makeText(mContext, AppContstants.MSG_PW_CHANGE_SUCCESS, Toast.LENGTH_LONG).show();
                        finish();//CLOSE this activity
                    } else {//status failure (invalid user or server error)
                        String msg = respObj.getString("msg");
                        AppUtils.showAlert(mContext, "Message", msg);
                    }
                }else{
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_PW_CHANGE_FAIL);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Message", AppContstants.MSG_PW_CHANGE_FAIL);
            }

        }
    }//end class PasswordChangeTask
    /**
     * This method checks whether Passwords entered is valid by Syntax or not
     * and wraps it in a json obj
     * @author Amod
     * @date 19032017
     */
    private boolean validEntry() {
        boolean flag = true ;
        strCurrPass = mEtCurrPassword.getText().toString();
        strNewPass = mEtNewPassword.getText().toString();

        if(!(strCurrPass.length() > 0)){//Current password entry check
            AppUtils.showAlert(mContext, "Error", AppContstants.ERR_MSG_OLD_PASS_REQ);
            Log.e(TAG, "current password length <= 0");
            flag = false ;
        }else if(!(strNewPass.length() > 0)){//New password entry check
            AppUtils.showAlert(mContext, "Error", AppContstants.ERR_MSG_NEW_PASS_REQ);
            Log.e(TAG, "new password length <= 0");
            flag = false ;
        }else if(!strNewPass.matches(AppContstants.PASSWORD_PATTERN)){//New password valid pattern matching
            AppUtils.showAlert(mContext, "Error", AppContstants.ERR_MSG_NEW_PASS_FORMAT);
            Log.e(TAG,"New password pattern not matching!!!");
            flag = false ;
        }else{
            //initialize with data for future use
            jObjChangePasswordReq = new JSONObject();
            try {
                jObjChangePasswordReq.put("user_id", mPreference.getUserId());
                jObjChangePasswordReq.put("password", strNewPass);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return flag;
    }//end of validEntry
}
