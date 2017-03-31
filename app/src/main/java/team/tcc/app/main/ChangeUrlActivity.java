package team.tcc.app.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import team.tcc.app.R;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.Utils;

public class ChangeUrlActivity extends AppCompatActivity implements OnClickListener {
	Button save,close;
	EditText port,ip;
	//View customerLayout;
	ProgressDialog pd;
	TextView urlTv;
	
	String ip_address;
	String port_no;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_url);
		
		save=(Button)findViewById(R.id.cuSaveBtn);
		save.setOnClickListener(this);
		
		close=(Button)findViewById(R.id.cuCloseBtn);
		close.setOnClickListener(this);
		
		ip = (EditText)findViewById(R.id.ipAddr);
		port = (EditText)findViewById(R.id.portNum);
				
		urlTv=(TextView)findViewById(R.id.urlTv);
		
		if(AppContstants.SERVER_URL!=null){
			urlTv.setText("Current URL is : " + AppContstants.SERVER_URL);
		}else{
			urlTv.setText("Current URL Not Set");
		}		
	}

	
	

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.cuSaveBtn:
			String ipNo = ip.getText().toString();
			String portNo = port.getText().toString();
			if(ipNo.length()>0){
				DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
				String msg = dbHelper.updateHostDetails(ipNo, portNo);
				dbHelper.close();
				final AlertDialog alertDialog = new AlertDialog.Builder(ChangeUrlActivity.this).create();
    			if(msg.equals("Success")){
    				/*if(portNo.length()>0){
    					Utils.SERVER_URL="http://"+ipNo+":"+portNo+"/MeasuReach/sc";
    				}else{
    					Utils.SERVER_URL="http://"+ipNo+"/MeasuReach/sc";
    				}*/
    				
    				AppContstants.SERVER_BASE_URL = Utils.getHostURL(getApplicationContext());
					AppContstants.SERVER_URL = AppContstants.SERVER_BASE_URL+"AppController";
					AppContstants.SERVER_MEDIA_UPLOAD_URL = AppContstants.SERVER_BASE_URL+"mu";
					AppContstants.SERVER_MEDIA_DOWNLOAD_URL = AppContstants.SERVER_BASE_URL+"md";

    				alertDialog.setTitle("Alert");
        		    alertDialog.setMessage("Current Base URL is : " + AppContstants.SERVER_BASE_URL);
    			}else{
    				alertDialog.setTitle("Error");
        		    alertDialog.setMessage("Current Base URL is : " + AppContstants.SERVER_BASE_URL);
    			}
    		    
    		    
    		    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
    		    new DialogInterface.OnClickListener(){
    	  		   public void onClick(DialogInterface dialog, int which) {
    	  			 alertDialog.dismiss();
    	  			 finish();
    	  		   }
    		    });
    		    alertDialog.show();
				System.out.println("SERVER_URL : "+AppContstants.SERVER_BASE_URL);
			}else{
				Toast.makeText(getApplicationContext(), "Please insert a valid IP  address", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.cuCloseBtn:
			finish();
			break;
		}
	}



}
