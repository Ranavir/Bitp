package team.tcc.app.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.Utils;

/**
 * This Activity called when Application opens
 * @date 04102016
 */
public class SplashActivity extends Activity {
    public static final String TAG = SplashActivity.class.getSimpleName()+": ";
    private static int SPLASH_TIME_OUT = 3000;
    /*Timer used for showing the splash image for some time*/
    private Thread logoTimerThread = null;
    /*Used for storing the current activity's context*/
    private Context mContext;
    /*Used for Database operations*/
    //private DatabaseHelper mDbHelper;
    /*This will hold the profile details availed from database*/
    private JSONObject jObjProfile;
    /*Used for opening new Activity from this Activity*/
    private Intent mIntent;
    /*This will hold the AppPreference*/
    AppSharedPreference mPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "###onCreate invoked");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        mPreference = new AppSharedPreference(mContext);



        AppContstants.SERVER_BASE_URL = Utils.getHostURL(getApplicationContext());
        AppContstants.SERVER_URL = AppContstants.SERVER_BASE_URL+"AppController";
        AppContstants.SERVER_MEDIA_UPLOAD_URL = AppContstants.SERVER_BASE_URL+"mu";
        AppContstants.SERVER_MEDIA_DOWNLOAD_URL = AppContstants.SERVER_BASE_URL+"md";

        System.out.println(TAG+"###AppContstants.SERVER_BASE_URL "+AppContstants.SERVER_BASE_URL);
        System.out.println(TAG+"###AppContstants.SERVER_URL"+AppContstants.SERVER_URL);
        /*Get Profile Details from Local*/
        /*mDbHelper = new DatabaseHelper(mContext);
        //jObjProfile = mDbHelper.getUserDetails();
        mDbHelper.close();*/



        /*Set the values in Profile Class static members for future use*/

        logoTimerThread = new Thread() {
            public void run() {
                try {
                    int logoTimer = 0;
                    int progress = 0;
                    while (logoTimer < SPLASH_TIME_OUT) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                        progress += 2;
                    }
                    if(mPreference.isUserLoggedIn()){//already login info available for auto login
                        // Redirect MainActivity after successful login
                        //Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        mIntent = new Intent(SplashActivity.this, MainActivity.class);
                        // set the new task and clear flags
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        finish();
                    }else{

                        mIntent = new Intent(SplashActivity.this, LoginActivity.class);//for fresh login
                        startActivity(mIntent);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        logoTimerThread.start();

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

    @Override
    public void onBackPressed(){
        Log.d(TAG, "###onBackPressed invoked");
        if(logoTimerThread!=null){
            logoTimerThread.interrupt();
        }
        finish();
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "###onDestroy invoked");
        super.onDestroy();
        return;
    }

}