package team.tcc.app.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;


/**
 * This class is used for sending HTTP requests to
 * Server
 * @author
 */

public class HttpUtils {
    public static final String TAG = HttpUtils.class.getSimpleName()+": ";


    /*************************************************************************
     * This method is used for making one Http Request to a
     * URL with a JSONObject as data within a context and gets
     * the Response as a String
     * @param applicationContext
     * @param obj
     * @return String
     * @author Ranavir Dash
     * @date 05-Oct-2016
     *************************************************************************/
    public static JSONObject sendToServer(Context applicationContext, JSONObject obj,int reqId) {
        JSONObject respObj = null;
        Log.d(TAG,"Sending user information to server.............");
        Log.d(TAG,"reqId------->"+reqId);
        Log.d(TAG,"data-------->"+obj.toString());
        try {
            ArrayList<NameValuePair> namevaluepair = new ArrayList<NameValuePair>();
            namevaluepair.add(new BasicNameValuePair("reqId", reqId+""));
            namevaluepair.add(new BasicNameValuePair("data", obj.toString()));//AppUtils.encryptData(obj.toString())


            HttpClient httpClient = new DefaultHttpClient();
            HttpParams httpParameters = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, AppContstants.REGISTRATION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, AppContstants.WAIT_TIMEOUT);
            ConnManagerParams.setTimeout(httpParameters, AppContstants.WAIT_TIMEOUT);

            Log.d(TAG, "Server URL............." + AppContstants.SERVER_URL);
            HttpPost httpPost = new HttpPost(AppContstants.SERVER_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(namevaluepair));
            HttpResponse response = httpClient.execute(httpPost);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String str = "";
            while ((str = rd.readLine()) != null) {
                line += str;
                Log.d(TAG,"line ======  " + line);
            }

            if(!line.isEmpty()){
                respObj = new JSONObject(line);
                Log.d(TAG,"Response line :: " + respObj.toString());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return respObj;
    }//end of sendToServer



}//end class HttpUtils
