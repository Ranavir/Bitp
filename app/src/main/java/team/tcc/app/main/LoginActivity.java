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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

/**
 * This activity used for Logging in the user with
 * the credentials entered
 * Created by Ranvir on 28-09-2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = LoginActivity.class.getSimpleName()+": ";

    private TextView forgotPasswordTv,registerTv,errEmailTV, errPasswordTV;
    private EditText unameET,passwordET;
    private Button btnSubmit;
    /*Used to save user entered email id and password*/
    private String username,password;
    /*This will hold the flag for type of login (local or server)*/
    private String mNetworkFlag ;
    /*Used for storing the current activity's context*/
    private Context mContext;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*Used for Database operations*/
    private DatabaseHelper mDbHelper;
    /*This will hold the profile details availed from database*/
    private JSONObject mJObjProfile;
    /*This will hold the profile details availed from database*/
    private JSONObject jObjCredentials;
    /*Used for opening new Activity from this Activity*/
    private Intent mIntent;
    /*This will hold the AppPreference*/
    AppSharedPreference mPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "###onCreate invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        mPreference = new AppSharedPreference(getApplicationContext());

        btnSubmit = (Button) findViewById(R.id.loignButton);
        btnSubmit.setTransformationMethod(null);
        btnSubmit.setOnClickListener(this);

        forgotPasswordTv = (TextView) findViewById(R.id.forgotPasswordTV);
        forgotPasswordTv.setOnClickListener(this);

        registerTv = (TextView) findViewById(R.id.registerTv);
        registerTv.setOnClickListener(this);

        unameET = (EditText) findViewById(R.id.etUname);
        passwordET = (EditText) findViewById(R.id.etPassword);


    }//end of onCreate
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "###onCreateOptionsMenu invoked");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"###onOptionsItemSelected invoked");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case R.id.change_url:
                mIntent = new Intent(mContext, ChangeUrlActivity.class);
                startActivity(mIntent);
                break;
            default:
                Toast.makeText(mContext,"Invalid option...",Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.forgotPasswordTV:
                mIntent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(mIntent);
                break;

            case R.id.registerTv:
                mIntent = new Intent(this, RegisterActivity.class);
                startActivity(mIntent);
                break;
            case R.id.loignButton:
                if(validEntries()){
                    if(AppUtils.checkInternet(mContext)) {
                        new LiveLoginTask().execute();
                        /*mIntent = new Intent(mContext, MainActivity.class);
                        // set the new task and clear flags
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();*/
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);
                    }
                }
                break;


        }
    }//end onClick


    /**
     * This class object is going match the user supplied email id and password
     * from server and if success then gets the profile details and saves in local
     * else shows error dialog
     * @author Ranavir Dash
     * @date 05102016
     */
    private class LiveLoginTask extends AsyncTask<Void, Void, Void> {
        JSONObject respObj = null ;
        LiveLoginTask(){
            //constructor
            Log.d(TAG, "LiveLoginTask initiated...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("Logging in");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if(AppUtils.checkInternet(getApplicationContext())){
                respObj = HttpUtils.sendToServer(getApplicationContext(), jObjCredentials, AppContstants.LOGIN_REQ_ID);

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
                    if(respObj.getString("status").equalsIgnoreCase(AppContstants.MSG_SUCCESS)){
                        //check availability of profile info
                        int user_id = respObj.getInt("user_id");
                        String user_type = respObj.getString("user_type");
                        String phnno = respObj.getString("phnno");
                        String email = respObj.getString("email");
                        String name = respObj.getString("name");
                        mPreference.createUserLoginSession(user_id,user_type,phnno,email,name);

                        //check availability of profile info
                        JSONObject jObjProfile = respObj.getJSONObject("profile_info");
                        //save user details in local because no data was there in local so live login happening
                        mDbHelper = new DatabaseHelper(mContext);
                        if(null != jObjProfile && jObjProfile.length()>0){//log profile info too
                            long i = mDbHelper.logUserProfileInfo(jObjProfile);
                            Log.d(TAG,"###rowId--->"+i);
                        }else{
                            Log.w(TAG, "Currently Profile information not available!!!");
                        }
                        mDbHelper.close();


                         /*Start sync adapter sync*/
                        //SyncUtils.TriggerRefresh();

                        mIntent = new Intent(mContext, MainActivity.class);
                        // set the new task and clear flags
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                    }else{//status failure (invalid user or server error)
                        String msg = respObj.getString("msg");
                        AppUtils.showAlert(mContext, "Message", msg);
                    }

                }else{
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_SERVER_ERROR);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Message", AppContstants.MSG_SERVER_ERROR);
            }

        }
    }//end class LiveLoginTask
    /**
     * This method makes client side validation for the entries by user
     * for syntax
     * @return
     */
    private boolean validEntries() {
        System.out.println(TAG + "Entry--->validEntries ");
        boolean flag = true;
        username ="";
        password = "";

        username = unameET.getText().toString();
        password = passwordET.getText().toString();

        if(!(username.length() > 0)){
            AppUtils.showAlert(mContext, "Error", "Username is Required.");
            flag = false;
        }else if(!(password.length() > 0 )){
            AppUtils.showAlert(mContext, "Error", "Password is Required.");
            flag = false;
        }else{



            //initialize with data for future use
            jObjCredentials = new JSONObject();
            try {
                jObjCredentials.put("username", username);
                jObjCredentials.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println(TAG + "Exit--->validEntries ");
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
