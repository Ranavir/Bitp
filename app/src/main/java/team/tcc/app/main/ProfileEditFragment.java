package team.tcc.app.main;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;
import team.tcc.app.util.HttpUtils;

public class ProfileEditFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ProfileEditFragment:";


    private Context mContext;
    FragmentTransaction mFt ;
    private Button btnProfileCancel, btnProfileSubmit;
    LinearLayout mLayoutStudent;
    private Spinner prefixSpinner,castSpinner;
    private EditText nameET,emailET,phoneET,dobET,fatherNameET,motherNameET,fatherOccupET,motherOccupET,addrET,nationalityET;
    private RadioGroup disabilityType;
    private EditText identificationET,boardHSCET,percentHSCET,hscYopET,boardIntrmET,percentIntrmET,intrmYopET,boardGradET,percentGradET,gradYopET;

    private String mGender,mQuota,mName,mEmail,mPhone,mDob,mFatherName,mMotherName,mFatherOccp,mMotherOccp,mAddr,mNationality;
    private String mDisabilityType,mIdentification,mBoardHSC,mPercentHSC,mYopHSC,mBoardIntrm,mPercentIntrm,mYopIntrm,mBoardGrad,mPercentGrad,mYopGrad;

    Dialog dialog;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;
    /*Used for Database operations*/
    private DatabaseHelper mDbHelper;
    /*This will hold the profile details availed from database*/
    private JSONObject mJObjProfile;



    public ProfileEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "###onAttach invoked");
        super.onAttach(context);
        mContext = context;
        mPreference = new AppSharedPreference(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "###onCreateView invoked");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mLayoutStudent = (LinearLayout)rootView.findViewById(R.id.studentLayout);
        if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_STUDENT)){//student
            mLayoutStudent.setVisibility(View.VISIBLE);
        }
        btnProfileCancel = (Button)rootView.findViewById(R.id.btnProfileCancel);
        btnProfileSubmit = (Button)rootView.findViewById(R.id.btnProfileSubmit);
        btnProfileCancel.setOnClickListener(this);
        btnProfileSubmit.setOnClickListener(this);

        prefixSpinner = (Spinner)rootView.findViewById(R.id.prefixSpinner);
        castSpinner = (Spinner)rootView.findViewById(R.id.castSpinner);

        nameET = (EditText)rootView.findViewById(R.id.nameET);
        emailET = (EditText)rootView.findViewById(R.id.emailET);
        phoneET = (EditText)rootView.findViewById(R.id.phoneET);
        dobET = (EditText)rootView.findViewById(R.id.dobET);
        fatherNameET = (EditText)rootView.findViewById(R.id.fatherNameET);
        motherNameET = (EditText)rootView.findViewById(R.id.motherNameET);
        fatherOccupET = (EditText)rootView.findViewById(R.id.fatherOccupET);
        motherOccupET = (EditText)rootView.findViewById(R.id.motherOccupET);
        addrET = (EditText)rootView.findViewById(R.id.addrET);
        nationalityET = (EditText)rootView.findViewById(R.id.nationalityET);
        disabilityType = (RadioGroup)rootView.findViewById(R.id.disabilityType);

        identificationET = (EditText)rootView.findViewById(R.id.identificationET);
        boardHSCET = (EditText)rootView.findViewById(R.id.boardHSCET);
        percentHSCET = (EditText)rootView.findViewById(R.id.percentHSCET);
        hscYopET = (EditText)rootView.findViewById(R.id.hscYopET);
        boardIntrmET = (EditText)rootView.findViewById(R.id.boardIntrmET);
        percentIntrmET = (EditText)rootView.findViewById(R.id.percentIntrmET);
        intrmYopET = (EditText)rootView.findViewById(R.id.intrmYopET);
        boardGradET = (EditText)rootView.findViewById(R.id.boardGradET);
        percentGradET = (EditText)rootView.findViewById(R.id.percentGradET);
        gradYopET = (EditText)rootView.findViewById(R.id.gradYopET);

        mDbHelper = new DatabaseHelper(mContext);
        mJObjProfile = mDbHelper.getUserProfileInfo();
        mDbHelper.close();

        if (mJObjProfile != null && mJObjProfile.length() > 0) {
            Log.d(TAG, "profile details--->" + mJObjProfile.toString());
            initUIComponents();
        }

        return rootView;
    }//end onCreateView

    private void initUIComponents() {

        try {


            String sex = mJObjProfile.getString("gender");
            String compareValue = sex ;
            String[] availableValues = getResources().getStringArray(R.array.g_prefix);
            int pos = 0;
            for(int i = 0 ; i < availableValues.length ; i++){
                if(availableValues[i].equals(compareValue)){
                    pos = i;
                    break;
                }
            }
            Log.d(TAG, "sex spinner pos--->" + pos);
            prefixSpinner.setSelection(pos);

            nameET.setText(mPreference.getUserDetails().get("name").toString());
            phoneET.setText(mPreference.getUserDetails().get("phnno").toString());
            emailET.setText(mPreference.getUserDetails().get("email").toString());

            dobET.setText(mJObjProfile.getString("dob"));
            fatherNameET.setText(mJObjProfile.getString("name_father"));
            fatherOccupET.setText(mJObjProfile.getString("occupation_father"));
            motherNameET.setText(mJObjProfile.getString("name_mother"));
            motherOccupET.setText(mJObjProfile.getString("occupation_mother"));
            addrET.setText(mJObjProfile.getString("address"));
            nationalityET.setText(mJObjProfile.getString("nationality"));


            String cast = mJObjProfile.getString("quota");
            compareValue = cast ;
            availableValues = getResources().getStringArray(R.array.castType);
            pos = 0;
            for(int i = 0 ; i < availableValues.length ; i++){
                if(availableValues[i].equals(compareValue)){
                    pos = i;
                    break;
                }
            }
            Log.d(TAG,"quota spinner pos--->"+pos);
            castSpinner.setSelection(pos);
            //myProfileDisability.setText(mJObjProfile.getString("disability"));
            String disability = mJObjProfile.getString("disability");
            if(TextUtils.equals(disability,"N")){
                ((RadioButton)disabilityType.getChildAt(1)).setChecked(true);
            }
            identificationET.setText(mJObjProfile.getString("identification_mark"));

            boardHSCET.setText(mJObjProfile.getString("hsc_board"));
            percentHSCET.setText(mJObjProfile.getString("percentage_hsc"));
            hscYopET.setText(mJObjProfile.getString("yop_hsc"));
            boardIntrmET.setText(mJObjProfile.getString("intrm_board"));
            percentIntrmET.setText(mJObjProfile.getString("percentage_intrm"));
            intrmYopET.setText(mJObjProfile.getString("yop_intrm"));
            boardGradET.setText(mJObjProfile.getString("grad_board"));
            percentGradET.setText(mJObjProfile.getString("percentage_grad"));
            gradYopET.setText(mJObjProfile.getString("yop_grad"));



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }//end setProfileUIComponents

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "###onActivityCreated invoked");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "###onViewCreated invoked");
        super.onViewCreated(view, savedInstanceState);


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
    public void onDestroyView() {
        Log.d(TAG, "###onDestroyView...");
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "###onDestroy...");
        super.onDestroy();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProfileCancel:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnProfileSubmit:
                if(validEntries()){
                    Log.d(TAG,"Entries validated...");
                    if(AppUtils.checkInternet(mContext)) {
                        new ProfileUpdateTask().execute();
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
     * This class object is going to save the user details in database before
     * going for actual registration in server with active flag false
     * Then send asynch request to server for Registration
     * and get success/failure response
     */
    private class ProfileUpdateTask extends AsyncTask<Void, Void, Void> {
        JSONObject respObj = null ;
        ProfileUpdateTask(){
            //constructor
            Log.d(TAG,"ProfileUpdateTask started...");
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(mContext);
            pd.setTitle("Profile updation in progress");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                    /*//Save data in local database first
                    mDbHelper = new DatabaseHelper(getApplicationContext());
                    mDbHelper.logUserDetails(jsonObjRegDetails);
                    mDbHelper.close();*/

                    respObj = HttpUtils.sendToServer(mContext, mJObjProfile, AppContstants.PROFILE_UPDATE);

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
                if (null != respObj && respObj.getString("status").equalsIgnoreCase(AppContstants.MSG_SUCCESS)) {
                    Log.d(TAG, "Profile update request successfully processed..");
                    Log.d(TAG, "Messsage--->" + respObj.getString("msg"));

                    mDbHelper = new DatabaseHelper(mContext);
                    mDbHelper.logUserProfileInfo(mJObjProfile);
                    mDbHelper.close();

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            mContext);
                    alertDialog.setTitle("Message");
                    alertDialog.setMessage("Your profile updated successfully.");

                    alertDialog.setNeutralButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();

                }else{
                    AppUtils.showAlert(mContext, "Message", AppContstants.MSG_UPDATE_PROFILE_FAIL);
                }
            }catch (Exception e){
                e.printStackTrace();
                AppUtils.showAlert(mContext, "Message", AppContstants.MSG_UPDATE_PROFILE_FAIL);
            }

        }
    }//end class RegistrationTask
    /**
     * This method is used for validating the values entered by the user
     * and displaying corresponding error related UIs
     */
    private boolean validEntries() {

        boolean flag = true ;

        mGender = prefixSpinner.getSelectedItem().toString();
        mQuota = castSpinner.getSelectedItem().toString();
        mName = nameET.getText().toString();
        mPhone = phoneET.getText().toString();
        mEmail = emailET.getText().toString();
        mDob = dobET.getText().toString();
        mFatherName = fatherNameET.getText().toString();
        mMotherName = motherNameET.getText().toString();
        mFatherOccp = fatherOccupET.getText().toString();
        mMotherOccp = motherOccupET.getText().toString();
        mAddr = addrET.getText().toString();
        mNationality = nationalityET.getText().toString();
        if(TextUtils.equals("Yes",((RadioButton)(disabilityType.findViewById(disabilityType.getCheckedRadioButtonId()))).getText().toString())){
            mDisabilityType = "Y";
        }else{
            mDisabilityType = "N";
        }


        mIdentification = identificationET.getText().toString();
        mBoardHSC = boardHSCET.getText().toString();
        mPercentHSC = percentHSCET.getText().toString();
        mYopHSC = hscYopET.getText().toString();
        mBoardIntrm = boardIntrmET.getText().toString();
        mPercentIntrm = percentIntrmET.getText().toString();
        mYopIntrm = intrmYopET.getText().toString();
        mBoardGrad = boardGradET.getText().toString();
        mPercentGrad = percentGradET.getText().toString();
        mYopGrad = gradYopET.getText().toString();

        if(mName.equalsIgnoreCase("")){
            AppUtils.showAlert(mContext, "Error", "Please Enter Name.");
            flag = false;
        }else if(mEmail.equalsIgnoreCase("")|| !(mEmail.matches(AppContstants.EMAIL_PATTERN))){
            AppUtils.showAlert(mContext, "Alert", AppContstants.INVALID_EMAIL_ID);
            flag = false;
        }else if(!(mPhone.length() > 0) || mPhone.length() != 10){
            AppUtils.showAlert(mContext, "Alert", AppContstants.INVALID_PHONE_NO);
            flag = false;
        }else if(!mDob.equalsIgnoreCase("") && !(mDob.matches(AppContstants.DOB_PATTERN))){
            AppUtils.showAlert(mContext, "Alert", AppContstants.INVALID_DOB);
            flag = false;
        }else{
            //initialize with data for future use
            try {
                mJObjProfile = new JSONObject();
                mJObjProfile.put("user_id",ProfileModel.user_id);
                mJObjProfile.put("gender", mGender);
                mJObjProfile.put("dob", mDob);
                mJObjProfile.put("name_father",mFatherName);
                mJObjProfile.put("name_mother",mMotherName);
                mJObjProfile.put("occupation_father",mFatherOccp);
                mJObjProfile.put("occupation_mother",mMotherOccp);
                mJObjProfile.put("nationality",mNationality);
                mJObjProfile.put("address",mAddr);
                mJObjProfile.put("quota",mQuota);
                mJObjProfile.put("disability",mDisabilityType);
                mJObjProfile.put("identification_mark",mIdentification);
                mJObjProfile.put("hsc_board",mBoardHSC);
                mJObjProfile.put("percentage_hsc",mPercentHSC);
                mJObjProfile.put("yop_hsc",mYopHSC);
                mJObjProfile.put("intrm_board",mBoardIntrm);
                mJObjProfile.put("percentage_intrm",mPercentIntrm);
                mJObjProfile.put("yop_intrm",mYopIntrm);
                mJObjProfile.put("grad_board",mBoardGrad);
                mJObjProfile.put("percentage_grad",mPercentGrad);
                mJObjProfile.put("yop_grad",mYopGrad);

                //Not implemented right now so put blank in these fields
                mJObjProfile.put("pg_board","");
                mJObjProfile.put("percentage_pg","");
                mJObjProfile.put("yop_pg","");
                mJObjProfile.put("img_cert_hsc","");
                mJObjProfile.put("img_cert_intemediate","");
                mJObjProfile.put("img_cert_grad","");
                mJObjProfile.put("img_cert_pg","");
                mJObjProfile.put("img_signature","");
                mJObjProfile.put("img_profile","");

                Log.d(TAG,"###Profile to update--->"+mJObjProfile.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }//end of validEntries

}//end class
