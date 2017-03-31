package team.tcc.app.main;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import team.tcc.app.R;
import team.tcc.app.helper.AppSharedPreference;
import team.tcc.app.helper.DatabaseHelper;
import team.tcc.app.model.ProfileModel;
import team.tcc.app.util.AppContstants;
import team.tcc.app.util.AppUtils;

public class MyProfileFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "MyProfileFragment:";


    private Context mContext;
    FragmentTransaction mFt ;
    Button mBtnMore, mBtnEdit;
    ImageView mIvProfile;
    LinearLayout mUserInfoLayout,mLayoutStudent;
    Dialog dialog;
    /*Used to create progress dialog*/
    private ProgressDialog pd;
    /*This will hold the AppPreference*/
    private AppSharedPreference mPreference;
    /*Used for Database operations*/
    private DatabaseHelper mDbHelper;
    /*This will hold the profile details availed from database*/
    private JSONObject mJObjProfile;

    private TextView myProfileName,myProfilePhone,myProfileEmail,profileType;
    private TextView myProfileDob,myProfileFather,myProfileFatherOcc,myProfileMother,myProfileMotherOcc,myProfileAddr,myProfileNation;
    private TextView myProfileCast,myProfileDisability,myProfileIdentMark;
    private TextView myProfileHSCBoard,myProfileHSC,myProfileHSCYop,myProfileIntrmBoard,myProfileIntrm,myProfileIntrmYop,myProfileGradBoard,myProfileGrad,myProfileGradYop;

    public MyProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_myprofile, container, false);

        mBtnMore = (Button) rootView.findViewById(R.id.myProfileMoreBtn);
        mBtnMore.setTransformationMethod(null);
        mBtnMore.setOnClickListener(this);
        mIvProfile = (ImageView) rootView.findViewById(R.id.myProfileIv);
        mIvProfile.setOnClickListener(this);
        mBtnEdit = (Button) rootView.findViewById(R.id.editProfileIv);
        mBtnEdit.setOnClickListener(this);
        mUserInfoLayout = (LinearLayout) rootView.findViewById(R.id.userMoreInfoLayout);
        mLayoutStudent = (LinearLayout) rootView.findViewById(R.id.layoutStudent);

        //UI fields
        myProfileName = (TextView)rootView.findViewById(R.id.myProfileName);
        myProfilePhone = (TextView)rootView.findViewById(R.id.myProfilePhone);
        myProfileEmail = (TextView)rootView.findViewById(R.id.myProfileEmail);
        profileType = (TextView)rootView.findViewById(R.id.profileType);

        myProfileDob = (TextView)rootView.findViewById(R.id.myProfileDob);
        myProfileFather = (TextView)rootView.findViewById(R.id.myProfileFather);
        myProfileFatherOcc = (TextView)rootView.findViewById(R.id.myProfileFatherOcc);
        myProfileMother = (TextView)rootView.findViewById(R.id.myProfileMother);
        myProfileMotherOcc = (TextView)rootView.findViewById(R.id.myProfileMotherOcc);
        myProfileAddr = (TextView)rootView.findViewById(R.id.myProfileAddr);
        myProfileNation = (TextView)rootView.findViewById(R.id.myProfileNation);

        myProfileCast = (TextView)rootView.findViewById(R.id.myProfileCast);
        myProfileDisability = (TextView)rootView.findViewById(R.id.myProfileDisability);
        myProfileIdentMark = (TextView)rootView.findViewById(R.id.myProfileIdentMark);

        myProfileHSCBoard = (TextView)rootView.findViewById(R.id.myProfileHSCBoard);
        myProfileHSC = (TextView)rootView.findViewById(R.id.myProfileHSC);
        myProfileHSCYop = (TextView)rootView.findViewById(R.id.myProfileHSCYop);
        myProfileIntrmBoard = (TextView)rootView.findViewById(R.id.myProfileIntrmBoard);
        myProfileIntrm = (TextView)rootView.findViewById(R.id.myProfileIntrm);
        myProfileIntrmYop = (TextView)rootView.findViewById(R.id.myProfileIntrmYop);
        myProfileGradBoard = (TextView)rootView.findViewById(R.id.myProfileGradBoard);
        myProfileGrad = (TextView)rootView.findViewById(R.id.myProfileGrad);
        myProfileGradYop = (TextView)rootView.findViewById(R.id.myProfileGradYop);
        if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_STUDENT)){//Student
            mLayoutStudent.setVisibility(View.VISIBLE);
        }

        mDbHelper = new DatabaseHelper(mContext);
        mJObjProfile = mDbHelper.getUserProfileInfo();
        mDbHelper.close();

        if (mJObjProfile != null && mJObjProfile.length() > 0) {
            Log.d(TAG, "profile details--->" + mJObjProfile.toString());
            setProfileUIComponents();
        }else{
            myProfileName.setText(mPreference.getUserDetails().get("name").toString());
            myProfilePhone.setText(mPreference.getUserDetails().get("phnno").toString());
            myProfileEmail.setText(mPreference.getUserDetails().get("email").toString());
            profileType.setText(mPreference.getUserDetails().get("user_type").toString());

            if(AppUtils.checkInternet(mContext)){
                //Get profile info from server and log profile info in log for future reference as well as update the UI
                //new NetworkProfileInfoTask().execute(ProfileModel.user_id);
            }else{
                AppUtils.showAlert(mContext, "Message", AppContstants.NO_INTERNET);
            }

        }

        return rootView;
    }//end onCreateView

    private void setProfileUIComponents() {

        try {
            myProfileName.setText(mJObjProfile.getString("gender")+" "+mPreference.getUserDetails().get("name"));
            myProfilePhone.setText(mPreference.getUserDetails().get("phnno").toString());
            myProfileEmail.setText(mPreference.getUserDetails().get("email").toString());
            profileType.setText(mPreference.getUserDetails().get("user_type").toString());

            myProfileDob.setText(mJObjProfile.getString("dob"));
            myProfileFather.setText(mJObjProfile.getString("name_father"));
            myProfileFatherOcc.setText(mJObjProfile.getString("occupation_father"));
            myProfileMother.setText(mJObjProfile.getString("name_mother"));
            myProfileMotherOcc.setText(mJObjProfile.getString("occupation_mother"));
            myProfileAddr.setText(mJObjProfile.getString("nationality"));
            myProfileNation.setText(mJObjProfile.getString("address"));

            if(ProfileModel.user_type.equalsIgnoreCase(AppContstants.USER_TYPE_STUDENT)){
                myProfileCast.setText(mJObjProfile.getString("quota"));
                myProfileDisability.setText(mJObjProfile.getString("disability"));
                myProfileIdentMark.setText(mJObjProfile.getString("identification_mark"));

                myProfileHSCBoard.setText(mJObjProfile.getString("hsc_board"));
                myProfileHSC.setText(mJObjProfile.getString("percentage_hsc"));
                myProfileHSCYop.setText(mJObjProfile.getString("yop_hsc"));
                myProfileIntrmBoard.setText(mJObjProfile.getString("intrm_board"));
                myProfileIntrm.setText(mJObjProfile.getString("percentage_intrm"));
                myProfileIntrmYop.setText(mJObjProfile.getString("yop_intrm"));
                myProfileGradBoard.setText(mJObjProfile.getString("grad_board"));
                myProfileGrad.setText(mJObjProfile.getString("percentage_grad"));
                myProfileGradYop.setText(mJObjProfile.getString("yop_grad"));


            }
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
            case R.id.myProfileIv:
                //selectImage();
                break;

            case R.id.myProfileMoreBtn:
                mUserInfoLayout.setVisibility(View.VISIBLE);
                mBtnMore.setVisibility(View.GONE);
                break;

            case R.id.editProfileIv:
                //showEditProfileDialog();

                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {//Remove the profile fragment from view
                    fm.popBackStackImmediate();
                }
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_container,new ProfileEditFragment());
                ft.addToBackStack("ProfileEditFragment");
                ft.commit();
                break;
        }
    }//end of onClick


}//end class
