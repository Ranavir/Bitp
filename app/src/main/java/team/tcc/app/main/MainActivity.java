package team.tcc.app.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import team.tcc.app.R;
import team.tcc.app.adapter.NotificationAdapter;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.model.NotificationModel;
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

/**
 * @author Ranvir
 *
 * This activity will add Navigation Drawer for our application and all the code related to navigation drawer.
 * We are going to extend all our other activites from this BaseActivity so that every activity will have Navigation Drawer in it.
 * This activity layout contain one frame layout in which we will add our child activity layout.   
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    private static final String TAG = "MainActivity:";
    /**
     *  Frame layout: Which is going to be used as parent layout for child activity layout.
     *  This layout is protected so that child activity can access this
     *  */
    protected FrameLayout frameLayout;
    /**
     *  This flag is used just to check that launcher activity is called first time
     *  so that we can open appropriate Activity on launch and make list item position selected accordingly.
     * */
    private static boolean isLaunch = true;
    /**
     *  Base layout node of this Activity.
     * */
    private DrawerLayout mDrawerLayout;

    /**
     * Drawer listner class for drawer open, close etc.
     */
    private ActionBarDrawerToggle actionBarDrawerToggle;
    /**
     * Navigation view for the list of navigation in app
     */
    private NavigationView navigationView ;



    /*These shows the navigation menu header phone no and email*/
    private TextView mNavTvPhone,mNavTvEmail,mNavTvVersion;
    private Button mBtnNewNotification;//for admin only
    private ListView mNotificationLV;
    private NotificationAdapter mNotificationAdapter;
    /*Used for storing the current activity's context*/
    private Context mContext;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;
    /*Used for opening new Activity from this Activity*/
    private Intent mIntent;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*Used for Database operations*/
    private DatabaseHelper mDbHelper;
    /*This will hold the profile details availed from Preference*/
    ProfileModel mProfile;
    /*This will hold the profile details availed from database*/
    private JSONObject mJObjProfile;


    android.support.v4.app.FragmentManager mFragmentManager;
    FragmentTransaction fragmentTransaction ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "###onCreate invoked");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = MainActivity.this;
        mPreference = new AppSharedPreference(getApplicationContext());
        //Get user details in from local
        HashMap<String,Object> hmProfile = mPreference.getUserDetails();
        ProfileModel.user_id = (Integer)hmProfile.get("user_id");
        ProfileModel.name = (String)hmProfile.get("name");
        ProfileModel.phnno = (String)hmProfile.get("phnno");
        ProfileModel.email = (String)hmProfile.get("email");
        ProfileModel.user_type = (String)hmProfile.get("user_type");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();

        if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_STUDENT)){
            nav_Menu.findItem(R.id.nav_interview).setVisible(false);
            nav_Menu.findItem(R.id.nav_match_making).setVisible(false);
            nav_Menu.findItem(R.id.nav_stipend).setVisible(false);
            nav_Menu.findItem(R.id.nav_proj_rep).setVisible(false);
            nav_Menu.findItem(R.id.nav_certificate).setVisible(false);
        }






        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }



        initUI();




    }//end oncreate

    // Check screen orientation or screen rotate event here
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape mode", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait mode", Toast.LENGTH_SHORT).show();
        }
    }//end onConfigurationChanged



    private void initUI() {
        View navHeaderView=navigationView.getHeaderView(0);
        mNavTvPhone = (TextView) navHeaderView.findViewById(R.id.navTvPhone);
        mNavTvEmail = (TextView) navHeaderView.findViewById(R.id.navTvEmail);
        mNavTvVersion = (TextView) navHeaderView.findViewById(R.id.navTvVersion);
        mBtnNewNotification = (Button)findViewById(R.id.btnNewNotification);
        mBtnNewNotification.setTransformationMethod(null);
        mBtnNewNotification.setOnClickListener(this);
        if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_ADMIN)){
            mBtnNewNotification.setVisibility(View.VISIBLE);
        }
        mNotificationLV = (ListView)findViewById(R.id.notificationListView);

        //Initialize Profile Details Model
        if(mProfile.user_id != -1){//valid user logged in
            try{
                mNavTvPhone.setText(ProfileModel.phnno);
                mNavTvEmail.setText(ProfileModel.email);
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    //get the app version Name for display
                    String version = pInfo.versionName;
                    //get the app version Code for checking
                    //int versionCode = pInfo.versionCode;
                    mNavTvVersion.setText("Ver : " + version);
                }catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //fetch notifications and render
            if(AppUtils.checkInternet(mContext)) {
                new NotificationTask().execute();
            }else{
                AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

            }

        }else{//No user details found so its a crime to move ahead without user details
            //Take back to login page
            mIntent = new Intent(MainActivity.this, LoginActivity.class);//for fresh login
            startActivity(mIntent);
            finish();
        }//end if else valid user
        mNotificationLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                NotificationModel selectedItem =(NotificationModel) mNotificationLV.getItemAtPosition(position);
                System.out.println("Selected Notification-------"+selectedItem.getNotification());

                if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_ADMIN)){
                    //showAdminDialog(selectedItem);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    ProcessApplFragment paf = new ProcessApplFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("notification", selectedItem);
                    paf.setArguments(bundle);

                    fragmentTransaction.replace(R.id.frame_container,paf);
                    fragmentTransaction.addToBackStack("ProcessApplFragment");
                    fragmentTransaction.commit();
                }else{
                    showStudentDialog(selectedItem);
                }

            }
        });
    }//end onCreate


    private void showStudentDialog(final NotificationModel selectedItem) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("BITP");
        alertDialog.setMessage("Apply Now ?");

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d(TAG, "###Applying for notification...");
                        //check profile and go for apply
                        //Retrieve profile information from database if available
                        mDbHelper = new DatabaseHelper(mContext);
                        mJObjProfile = mDbHelper.getUserProfileInfo();
                        mDbHelper.close();
                        if (mJObjProfile != null && mJObjProfile.length() > 0) {
                            Log.d(TAG, "profile details--->" + mJObjProfile.toString());
                            if(AppUtils.isEligible(selectedItem, mJObjProfile)) {
                                new ApplyToNotificationTask().execute(selectedItem);
                            }else{
                                Toast.makeText(mContext,"Your current profile is not eligible!!!",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(mContext,"Update your profile details then apply...",Toast.LENGTH_LONG).show();
                        }

                    }
                });

        alertDialog.show();
    }//end showStudentDialog

    @Override
    protected void onResume() {
        System.out.println(TAG + "### onResume() invoked");
        super.onResume();
    }
    @Override
    protected void onPostResume() {
        System.out.println(TAG + "### onPostResume() invoked");
        super.onPostResume();
    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "###onBackPressed invoked");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        System.out.println(TAG + " Count of Fragments in stack " + getSupportFragmentManager().getBackStackEntryCount());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {//To avoid TaskFragment pop up
            Log.d(TAG," now popped one fragment...");
            getSupportFragmentManager().popBackStackImmediate();
        }else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    MainActivity.this);
            alertDialog.setTitle("BITP");
            alertDialog.setMessage("Are you sure you want to exit ?");

            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.d(TAG, "###Finishing BaseActivity");
                            //super.onBackPressed();
                            finish();
                        }
                    });

            alertDialog.show();

        }

    }//end onBackPressed

    @Override
    protected void onStop() {
        Log.d(TAG, "###onStop invoked");
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "###onDestroy invoked");
        super.onDestroy();
        return;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "###onCreateOptionsMenu invoked");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
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
            case R.id.action_refresh:
                if(AppUtils.checkInternet(mContext)) {
                    new NotificationTask().execute();
                }else{
                    AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);

                }
                break;
            case R.id.change_pw:
                mIntent = new Intent(mContext, ChangePasswordActivity.class);
                startActivity(mIntent);
                break;
            default:
                Toast.makeText(mContext,"Invalid option...",Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "###onNavigationItemSelected invoked");
        // Handle navigation view item clicks here.
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction ;
        int id = item.getItemId();
        switch(id){
            case R.id.nav_home:
                //fm.beginTransaction().replace(R.id.frame_container,new HomeFragment()).commit();
                while(fragmentManager.getBackStackEntryCount() > 0) {
                    System.out.println(TAG + " Count of Fragments in stack " + fragmentManager.getBackStackEntryCount());
                    fragmentManager.popBackStackImmediate();

                }
                break;
            case R.id.nav_profile:
                //startActivity(new Intent(this, TestActivity.class));
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new MyProfileFragment());
                fragmentTransaction.addToBackStack("MyProfileFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_interview:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new InterviewFragment());
                fragmentTransaction.addToBackStack("InterviewFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_match_making:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new NotificationSummaryFragment());
                fragmentTransaction.addToBackStack("NotificationSummaryFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_training:
                /*
                - If Admin ask for create a new training or add trainee
                - If Student move to give feedback and check details
                 */
                if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_ADMIN)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            MainActivity.this);
                    alertDialog.setTitle("BITP Training");
                    //alertDialog.setMessage("Create New");

                    alertDialog.setNegativeButton("Create New Training",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mFragmentManager = getSupportFragmentManager();
                                    if (mFragmentManager.getBackStackEntryCount() > 0) {
                                        mFragmentManager.popBackStackImmediate();
                                    }
                                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                                    ft.replace(R.id.frame_container, new NewTrainingFragment());
                                    ft.addToBackStack("NewTrainingFragment");
                                    ft.commit();
                                }
                            });

                    alertDialog.setPositiveButton("Add Trainee",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mFragmentManager = getSupportFragmentManager();
                                    if (mFragmentManager.getBackStackEntryCount() > 0) {
                                        mFragmentManager.popBackStackImmediate();
                                    }
                                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                                    ft.replace(R.id.frame_container, new AddTraineesFragment());
                                    ft.addToBackStack("AddTraineesFragment");
                                    ft.commit();
                                }
                            });

                    alertDialog.show();
                }else{
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStackImmediate();
                    }
                    //student page
                    mFragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    ft.replace(R.id.frame_container, new StudentTrainingFragment());
                    ft.addToBackStack("StudentTrainingFragment");
                    ft.commit();
                }
                break;
            case R.id.nav_stipend:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new PayStipendFragment());
                fragmentTransaction.addToBackStack("PayStipendFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_proj_rep:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new ProjectReportFragment());
                fragmentTransaction.addToBackStack("ProjectReportFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_certificate:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new IssueCertificateFragment());
                fragmentTransaction.addToBackStack("IssueCertificateFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_placement:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new PNewsFragment());
                fragmentTransaction.addToBackStack("PNewsFragment");
                fragmentTransaction.commit();
                break;
            case R.id.nav_signout:
                //startActivity(new Intent(this, Item1Activity.class));
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to logout ?");

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                mDbHelper = new DatabaseHelper(mContext);
                                mDbHelper.clearDB();
                                mDbHelper.close();

                                mPreference.logoutUser();

                                mIntent = new Intent(mContext, LoginActivity.class);//for fresh login
                                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mIntent);
                                finish();
                                /*if(AppUtils.checkInternet(mContext)) {
                                    new LiveLogoutTask().execute();
                                }else{
                                    AppUtils.showAlert(mContext,"Message", AppContstants.NO_INTERNET);
                                }*/
                            }
                        });

                alertDialog.show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }//end of onNavigationItemSelected

    @Override
    public void onClick(View v) {
        mFragmentManager = getSupportFragmentManager();

        Bundle bundle = null;

        switch(v.getId()){
            case R.id.btnNewNotification:
                Log.d(TAG, "Button create new Notification clicked...");


                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {//Remove current TaskFragment
                    mFragmentManager.popBackStackImmediate();
                }
                NotifyFragment nnf = new NotifyFragment();
                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, nnf);
                fragmentTransaction.addToBackStack("NotifyFragment");
                fragmentTransaction.commit();

                break;

            default:
                return;
        }
    }//end onclick
    /******************************************************
     * This task is used to fetch new notifiations from server
     * and render in page
     *******************************************************/

    private class ApplyToNotificationTask extends AsyncTask<NotificationModel, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        ApplyToNotificationTask(){
            //constructor
            Log.w(TAG,"ApplyToNotificationTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("Applying to notification");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(NotificationModel... notifications) {
            NotificationModel notification = notifications[0];
            try {
                int uid = mPreference.getUserId();
                JSONObject jObjRequest = new JSONObject();
                jObjRequest.put("user_id",uid);
                jObjRequest.put("notification_id",notification.getNotification_id());
                respObj = HttpUtils.sendToServer(getApplicationContext(), jObjRequest, AppContstants.APPLY_TO_NOTIFICATION);
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
                if(null != respObj) {
                    if (respObj.getString("status").equalsIgnoreCase("SUCCESS")) {
                        String applicationId = respObj.getString("application_id");
                        Toast.makeText(mContext, "Your application submitted successfully!!!", Toast.LENGTH_LONG).show();
                        AppUtils.showAlert(mContext, "Application Id", "Your application id: " + applicationId);

                    } else {
                        AppUtils.showAlert(mContext, "Error", respObj.getString("msg"));
                    }
                }else {
                    AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class NotificationTask
    /******************************************************
     * This task is used to fetch new notifiations from server
     * and render in page
     *******************************************************/

    private class NotificationTask extends AsyncTask<Void, Void, Void>{

        JSONObject respObj = null ;
        String msg = null;
        NotificationTask(){
            //constructor
            Log.w(TAG,"NotificationTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            //pd.setTitle("Submitting your complete surveys");
            pd.setTitle("fetching notifications");
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
                    jObjRequest.put("status","new");
                    respObj = HttpUtils.sendToServer(getApplicationContext(), jObjRequest, AppContstants.FETCH_NOTIFIATIONS);
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
                if(null != respObj && respObj.getString("status").equalsIgnoreCase("SUCCESS")){
                    //Retrieve notifications and render
                    JSONArray jaNotifications = respObj.getJSONArray("notifications");
                    if(jaNotifications != null && jaNotifications.length() > 0){
                        ArrayList<NotificationModel> alNotifications = new ArrayList<>();
                        JSONObject jObj = null ;
                        NotificationModel nm = null;
                        for(int i = 0 ; i < jaNotifications.length() ; i++){
                            jObj = jaNotifications.getJSONObject(i);
                            nm = new NotificationModel();

                            nm.setNotification_id(jObj.getString("notification_id"));
                            nm.setComp_code(jObj.getString("comp_code"));
                            nm.setNotification(jObj.getString("notification"));
                            nm.setHsc_percentage(jObj.getDouble("hsc_percentage"));
                            nm.setIntrm_percentage(jObj.getDouble("intrm_percentage"));
                            nm.setGrad_percentage(jObj.getDouble("grad_percentage"));
                            nm.setPg_percentage(jObj.getDouble("pg_percentage"));
                            nm.setStatus(jObj.getString("status"));
                            alNotifications.add(nm);
                        }
                        mNotificationAdapter = new NotificationAdapter(mContext, R.layout.notification_listview_body, alNotifications);
                        mNotificationLV.setAdapter(mNotificationAdapter);
                    }else{
                        AppUtils.showAlert(mContext, "Message", AppContstants.MSG_NO_NOTIFICATIONS);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Error", AppContstants.MSG_FAIL);
            }

        }
    }//end class NotificationTask


}//end class HomeActivity
