package team.tcc.app.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import team.tcc.app.R;
import team.tcc.app.model.NotificationModel;

/**
 * This class is used for all the Application related
 * Utility methods
 * Created by
 */
public class AppUtils {
    public static final String TAG = AppUtils.class.getSimpleName()+": ";

    /**
     * Error Dialog Alert
     *
     * @param title
     * @param msg
     */
    public static void showAlert(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.show();
    }

    /**
     * This method is used for checking Internet connectivity
     * Must specify android.permission.INTERNET permission in Manifest file
     * @param context
     * @return boolean
     */
    public static boolean checkInternet(Context context) {
        Log.d(TAG, " ### checkInternet");
        // check Internet connection is available
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
            return true;
        } else {
            Log.d(TAG,AppContstants.NO_INTERNET);
            return false;
        }
    }//end of checkInternet

    /**
     *This method is used for getting the ImeiNo of the Android device
     * @param context
     * @return String
     */
    public static String getImeiNo(Context context) {
        Log.d(TAG, " ### getImeiNo");
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mngr.getDeviceId();
        return mngr.getDeviceId();
    }//end of getImeiNo

    /**
     * This Utility method used for get the current date
     * using a specific date format default to dd-MM-yyyy
     * Ex- 26-Sept-2016
     * @param format
     * @return String
     */
    public static String getDate(String format){
        if(format == null || format.isEmpty()){
            format = "dd-MM-yyyy" ;
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        String to_date = dateFormat.format(date);
        return to_date;
    }//end of getDate

    /**
     * This method used to give current time in
     * HH:mm:ss format
     * @return String
     */
    public static String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currTime = sdf.format(c.getTime());
        System.out.println(TAG + "utils currTime ::  " + currTime);
        return currTime;
    }//end of getCurrentTime

    /**
     * Used to get the encrypted password and during
     * comparision of MD5 hashes
     * @param input
     * @return
     */
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }//end of getMD5

    /**********************************************************************************
     * This method used to match the notification eligibility criteria and profile
     * and return the eligibility status evaluated
     *
     * @param notification
     * @param jObjProfile
     * @return
     * @date 22-03-2017
     * @author Amod
     ***********************************************************************************/
    public static boolean isEligible(NotificationModel notification, JSONObject jObjProfile) {
        boolean isEligible = false;
        Double hscEligibleMark = (null != notification.getHsc_percentage()) ? notification.getHsc_percentage() : 0.0 ;
        Double intrmEligibleMark = (null != notification.getIntrm_percentage()) ? notification.getIntrm_percentage() : 0.0 ;
        Double gradEligibleMark = (null != notification.getGrad_percentage()) ? notification.getGrad_percentage() : 0.0 ;
        Double hscMark = 0.0;
        Double intrmMark = 0.0;
        Double gradMark = 0.0;
        try {
            hscMark = !TextUtils.isEmpty(jObjProfile.getString("percentage_hsc")) ? Double.parseDouble(jObjProfile.getString("percentage_hsc")) : 0.0;
            intrmMark = !TextUtils.isEmpty(jObjProfile.getString("percentage_intrm")) ? Double.parseDouble(jObjProfile.getString("percentage_intrm")) : 0.0;
            gradMark = !TextUtils.isEmpty(jObjProfile.getString("percentage_grad")) ? Double.parseDouble(jObjProfile.getString("percentage_grad")) : 0.0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"hscMark-->"+hscMark+"intrmMark-->"+intrmMark+"gradMark-->"+gradMark);
        Log.d(TAG,"hscEligibleMark-->"+hscEligibleMark+"intrmEligibleMark-->"+intrmEligibleMark+"gradEligibleMark-->"+gradEligibleMark);

        if(hscMark >= hscEligibleMark && intrmMark >= intrmEligibleMark && gradMark >= gradEligibleMark){
            isEligible = true;
        }
        return isEligible;
    }
}//end class
