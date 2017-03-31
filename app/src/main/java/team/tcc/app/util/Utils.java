/*Copyright (c) 2016 Silicon Tech Lab Pvt Ltd.  All rights reserved. 			           *	
 * This document is the property of Silicon Tech Lab Pvt Ltd..                          *
 * All ideas and information contained in this document are the intellectual property (IP) *
 * rights of Silicon Tech Lab Pvt Ltd.. This document is not for reference or general   *
 * distribution and is meant for use only for STL. This document shall not             *
 * be loaned to or shared with anyone, within or outside STL, including its customers. * 
 * Copying or unauthorized distribution of this document, in any form or means             *
 * including electronic, mechanical, photocopying or otherwise is illegal.                 * 
 * Use is subject to license terms only.                                                   *  
 *****************************************************************************************

 *****************************************************************************************
 *    Ver         Author                  Date        			Description		        *
 *    1.0         Ranavir               24-April-2016          	Initial Version		    *
 *****************************************************************************************
 */
package team.tcc.app.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import team.tcc.app.helper.DatabaseHelper;


/***************************************************************
 * This class holds all public static utility methods can be used by any class
 * object
 * 
 * @author office
 * @date 25042016
 ***************************************************************/
public class Utils {
	//private static Logger logger = Logger.getLogger(Utils.class);

	public static void main(String[] args) {
		System.out.println(getDate("yyyy-MM-dd HH:mm:ss"));
	}

	/********************************************************************************************************/

	public static String getHostURL(Context context){
		String URL = null;
		try{
			DatabaseHelper dbHelper = new DatabaseHelper(context);
			JSONObject hostDetail = dbHelper.getHostDetails();
			dbHelper.close();
			if(hostDetail!=null){
				String ip_address = hostDetail.getString("ip_address");
				String port_no = hostDetail.getString("port_no");
				if(port_no==null || port_no.equals("")){
					URL = "http://"+ ip_address + "/BitpWeb/";
				}else{
					URL = "http://"+ ip_address + ":" + port_no + "/BitpWeb/";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return URL;
	}
	public static String getDate(String requestFormat) {
		Calendar currDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(requestFormat);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
		String dateNow = formatter.format(currDate.getTime());
		return dateNow;
	}
	
	public static String convertTimestampToString(String timestamp) {
		String result = "";
		SimpleDateFormat inputFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			result = outputFormat.format(inputFormat.parse(timestamp));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}
	/**
	 * This method holds Global date formats for any Survey according to a code
	 *
	 * @param surveyls_dateformat
	 * @return String
	 */
	public static String getGlobalDateFormat(int surveyls_dateformat) {
		//will store 1-12 (ex- 2(dd-MM-yyyy) & 9(MM-dd-yyyy))
		String format = "dd-MM-yyyy" ;
		switch(surveyls_dateformat){
			case 2 :
				format = "dd-MM-yyyy" ;
				break;
			case 9 :
				format = "MM-dd-yyyy" ;
				break;
			default :
				break;
		}
		return format;
	}//end getGlobalDateFormat
	/**
	 * This utility method validates a date format with
	 * regards to a particular code
	 *
	 * @param value
	 * @param iCode
	 * @return
	 */
	public static boolean validateDateFormat(String value,int iCode) {
		boolean flag = false;
		switch(iCode){
			case 2 :
				//format = "dd-MM-yyyy" ;
				flag = value.matches("([0][1-9]|[1-2][0-9]|[3][0-1])-([0][1-9]|[1][0-2])-([0-9]{4})");
				break;
			case 9 :
				//format = "MM-dd-yyyy" ;
				flag = value.matches("([0][1-9]|[1][0-2])-([0][1-9]|[1-2][0-9]|[3][0-1])-([0-9]{4})");
				break;
			default :
				break;
		}
		return flag;
	}//end validateDateFormat
	/**
	 * This method gives the postgres formatted date(yyyy-mm-dd) as per the date format
	 * of  survey
	 * Ex -
	 yyyy-mm-dd 1989-05-02 yyyy-mm-dd 1989-05-02 00:00:00
	 mm-dd-yyyy 05-25-1989 yyyy-mm-dd 1989-05-25 00:00:00
	 dd-mm-yyyy 25-05-1989 yyyy-mm-dd 1989-02-05 00:00:00

	 * @param inDate
	 * @param surveyls_dateformat
	 * @return
	 */
	public static String getFormattedDate(String inDate,
										  int surveyls_dateformat) {
		String OutDate = inDate ;
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat inputFormat = null;

		switch(surveyls_dateformat){
			case 2 :
				//format = "dd-MM-yyyy" ;
				inputFormat = new SimpleDateFormat("dd-MM-yyyy");
				break;
			case 9 :
				//format = "MM-dd-yyyy" ;
				inputFormat = new SimpleDateFormat("MM-dd-yyyy");
				break;
			default :
				break;
		}
		try {
			System.out.println(inputFormat.parse(inDate));
			OutDate = outputFormat.format(inputFormat.parse(inDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return OutDate;
	}//end getFormattedDate
	/**
	 * This utility method takes current value and total value 
	 * to return approximate percentage value
	 * 
	 * @param current
	 * @param total
	 * @return int
	 */
	public static int getPercentage(double current,double total){
		Double p = (current/total) * 100 ;
		return p.intValue();
	}//end getPercentage

	/**
	 * This utility method creates a place holder string for
	 * database queries by taking length n as arguement
	 * ex - len = 3 Result- ?,?,?
	 *
	 *
	 * @param len
	 * @return
	 */
	public static String makePlaceholders(int len) {
		if (len < 1) {
			// It will lead to an invalid query anyway ..
			throw new RuntimeException("No placeholders");
		} else {
			StringBuilder sb = new StringBuilder(len * 2 - 1);
			sb.append("?");
			for (int i = 1; i < len; i++) {
				sb.append(",?");
			}
			return sb.toString();
		}
	}//end of makePlaceholders
	/**
	 *To check if the given string is a valid Json data or not
	 * @param json
	 * @return
	 * @author Amrut Mishra
	 * @date 22-12-2016
	 */

	public static boolean isJSONValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException ex) {
			//checking if the string is json array
			try {
				new JSONArray(json);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}//end of isJSONValid

}// end class
