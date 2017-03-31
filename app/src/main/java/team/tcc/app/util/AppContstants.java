package team.tcc.app.util;

/**
 * This class holds all the application related constants
 * Created by Ranavir on 18-Mar-17.
 */
public class AppContstants {

    // Configuration Related Constants
    public static final String DB_NAME                                       = "Bitp";
    public static final int DB_VERSION                                       = 1;


    public static String SERVER_BASE_URL                                     = "http://203.193.144.62:8080/BitpWeb/"; // host URL 182.156.93.61
    public static String SERVER_URL                                          = SERVER_BASE_URL+"AppController"; // host URL 182.156.93.61
    public static String SERVER_MEDIA_UPLOAD_URL                             = SERVER_BASE_URL+"mu"; // host media URL
    public static String SERVER_MEDIA_DOWNLOAD_URL                           = SERVER_BASE_URL+"md"; // host media URL

    public static final int REGISTRATION_TIMEOUT                             = 30 * 1000;
    public static final int WAIT_TIMEOUT                                     = 50 * 1000;
    public static final int REG_REQ_ID                                       = 1 ;
    public static final int LOGIN_REQ_ID                                     = 2 ;
    public static final int PW_RECOVERY_REQ_ID                               = 3 ;
    public static final int PW_CHANGE_REQ_ID                                 = 4 ;
    //public static final int LOGOUT_REQ_ID                                  = 7 ;

    public static final int FETCH_NOTIFIATIONS                               = 51;
    public static final int FETCH_COMPANY 									 = 52;
    public static final int NEW_NOTIFICATION 								 = 53;
    public static final int PROFILE_UPDATE 								     = 54;
    public static final int APPLY_TO_NOTIFICATION                            = 55;
    public static final int APPLICATIONS_PRE_PROCESSING                      = 56;
    public static final int APPLICATIONS_PROCESSING                          = 57;
    public static final int FETCH_EXAMS                                      = 58;
    public static final int FETCH_EXAM_PROFILE								 = 59;
    public static final int UPDATE_EXAM_INTERVEIW							 = 60;
    public static final int FETCH_PLACEMENT_NEWS                             = 61;
    public static final int NEW_PLACEMENT                                    = 62;
    public static final int DROP_PLACEMENT_NEWS                              = 63;
    public static final int NEW_TRAINING                                     = 64;
    public static final int FETCH_TRAININGS                                  = 65;
    public static final int ADD_TRAINEES                                     = 66;
    public static final int FETCH_TRAINING_PROFILE                           = 67;
    public static final int PUSH_FEEDBACK                                    = 68;
    public static final int NOTIFICATION_RESULT 							 = 69;
    public static final int EXAM_SELECTION                                   = 70;
    public static final int PAY_STIPEND                                      = 71;
    public static final int SUBMIT_PROJECT									 = 72;
    public static final int ISSUE_CERTIFICATE								 = 73;

    public static final String MSG_FAIL = "FAILURE" ;
    public static final String MSG_SUCCESS = "SUCCESS" ;
    public static final String MSG_SERVER_ERROR	= "SERVER ERROR";
    public static final String MSG_DUPLICATE_REGISTRATION = "DUPLICATE REGISTRATION" ;
    public static final String USER_TYPE_ADMIN	    = "Admin";
    public static final String USER_TYPE_STUDENT	= "Student";

    //User Message Related Constants
    public static final String PASSWORD_PATTERN = "[a-zA-Z0-9]{5}" ;//RE for password
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String DOB_PATTERN = "(19[4-9][0-9])-([0][1-9]|[1][0-2])-([0-2][0-9]|[3][0-1])";
    public static final String INVALID_PHONE_NO = "Invalid Phone Number.";
    public static final String INVALID_EMAIL_ID = "Invalid email id.";
    public static final String INVALID_DOB = "Invalid DOB.";
    public static final String NO_INTERNET = "Internet Connection Not Available." ;
    public static final String ERR_MSG_OLD_PASS_REQ = "Old password is required." ;
    public static final String ERR_MSG_NEW_PASS_REQ = "New password is required." ;
    public static final String ERR_MSG_NEW_PASS_FORMAT = "Invalid format(allowed alpha numeric of length 5)" ;

    public static final String MSG_REG_FAIL = "Registration failed";
    public static final String MSG_UPDATE_PROFILE_FAIL = "Profile updation failed.";
    public static final String MSG_PW_RECOVERY_FAIL = "Unable to recover password!!!";
    public static final String MSG_PW_CHANGE_FAIL = "Unable to update password!!!";
    public static final String MSG_PW_CHANGE_SUCCESS = "Password changed successfully";
    public static final String MSG_NO_NOTIFICATIONS = "No new Notifications are available.";
    public static final String MSG_NO_PLACEMENTS = "No new Placement news are available.";


    //Project Queries

    //Date: 18-Mar-2017

}
