package team.tcc.app.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


import java.util.HashMap;

import team.tcc.app.main.LoginActivity;
import team.tcc.app.main.MainActivity;

public class AppSharedPreference {
    public static final String TAG = AppSharedPreference.class.getSimpleName()+": ";

	SharedPreferences pref; // Shared Preferences reference
    Editor editor; // Editor reference for Shared preferences
    private Context _context; // Context

    int PRIVATE_MODE = 0; // Shared pref mode

    public static final String PREFER_NAME = "BitpPreferenceFile"; // filename

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IS_USER_LOGIN";

    private static final String KEY_USER_ID = "KEY_USER_ID"; // User ID
    private static final String KEY_USER_TYPE = "KEY_USER_TYPE";
    private static final String KEY_USER_PHONE = "KEY_USER_PHONE";
    private static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    /**
     * @param context
     */
    public AppSharedPreference(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }//end of Constructor

    /**
     * Creating login session
     *
     * @param uid
     */
    public void createUserLoginSession(int uid,String utype,String phnno,String email, String name) {
        Log.d(TAG, "Inside createUserLoginSession method ++++++++++++++++++");
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

		// Storing name, old a/c no and customer id in pref
		editor.putInt(KEY_USER_ID, uid);
        editor.putString(KEY_USER_TYPE, utype);
        editor.putString(KEY_USER_PHONE, phnno);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);

		// commit changes
		editor.commit();

	}//end of createUserLoginSession

	/**
	 * check user login status
	 * 
	 * @return boolean
	 */
	/*public boolean checkLogin() {
        Log.d(TAG, "checkLogin() method called.............");
		// Check login status
		if (this.isUserLoggedIn()) {

            Intent i = new Intent(_context, MainActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }//end of checkLogin*/

    /**
     * Getting stored session data
     *
     * @return
     */
    public HashMap<String, Object> getUserDetails() {

        Log.d(TAG,"Inside getUserDetails ++++++++++++++++++++++++++");
        // Use hashmap to store user credentials
        HashMap<String, Object> user = new HashMap<String, Object>();

        user.put("user_id", pref.getInt(KEY_USER_ID, -1));
        user.put("phnno", pref.getString(KEY_USER_PHONE, ""));
        user.put("email", pref.getString(KEY_USER_EMAIL, ""));
        user.put("name", pref.getString(KEY_USER_NAME, ""));
        user.put("user_type", pref.getString(KEY_USER_TYPE, ""));
        return user;
    }//end of getUserDetails

    /**
     * Check for login
     *
     * @return boolean
     */
    public boolean isUserLoggedIn() {
       Log.d(TAG, "Check for login ++++++++++++++++++++");
       return pref.getBoolean(IS_USER_LOGIN, false);

    }//end of isUserLoggedIn
    /**
     * logout user
     */
    public void logoutUser() {

        // delete data from preferences
        editor.clear();
        editor.commit();

        /*// After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/
    }

    /**
     * Getting stored user id
     *
     * @return
     */
    public int getUserId() {
        Log.d(TAG, "getUserId");
        return  pref.getInt(KEY_USER_ID, -1);
    }
    /**
     * Getting stored user type
     *
     * @return
     */
    public String getUserType() {
        Log.d(TAG, "getUserType");
        return  pref.getString(KEY_USER_TYPE, "");
    }
}
