package app.zingo.employeemanagements.ui.landing;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.zingo.employeemanagements.Custom.CustomFontTextView;
import app.zingo.employeemanagements.model.Employee;
import app.zingo.employeemanagements.model.Organization;
import app.zingo.employeemanagements.ui.Company.CreateFounderScreen;
import app.zingo.employeemanagements.ui.EmployeeSignUp;
import app.zingo.employeemanagements.utils.PreferenceHandler;
import app.zingo.employeemanagements.utils.ThreadExecuter;
import app.zingo.employeemanagements.WebApi.EmployeeApi;
import app.zingo.employeemanagements.base.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerificationScreen extends AppCompatActivity {

    LinearLayout mNumberLay,mOtpLay;
    CountryCodePicker mCountry;
    TextInputEditText mPhone,mOtp;
    AppCompatButton mVerifyNum,mVerifyCode;
    CustomFontTextView mMobileNumWithCountry;
    AppCompatTextView mCancel,mResend,mTimer;

    //Firebase & Auth
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private FirebaseAuth mAuth;
    private static final String TAG = "GuestLoginScreen";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    MyCountDownTimer myCountDownTimer;

    Organization organization;
    String screen,phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_phone_verification_screen);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            mNumberLay = findViewById(R.id.number_layout);
            mOtpLay = findViewById(R.id.otp_layout);

            mCountry = findViewById(R.id.ccp);

            mPhone = findViewById(R.id.phone);
            mOtp = findViewById(R.id.otp);

            mVerifyNum = findViewById(R.id.verify_number);
            mVerifyCode = findViewById(R.id.verify_code);

            mMobileNumWithCountry = findViewById(R.id.mobile_number_text);

            mCancel = findViewById(R.id.cancel);
            mResend = findViewById(R.id.resend);
            mTimer = findViewById(R.id.timer);

            Bundle bundle = getIntent().getExtras();

            if(bundle!=null){

                organization = ( Organization )bundle.getSerializable("Company");
                screen = bundle.getString("Screen");
            }



            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    mOtpLay.setVisibility(View.GONE);
                    mNumberLay.setVisibility(View.VISIBLE);
                    mPhone.setText("");
                }
            });

            FirebaseApp.initializeApp( PhoneVerificationScreen.this);

            mAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {

                    Log.d(TAG, "onVerificationCompleted:" + credential);

                    mVerificationInProgress = false;

                    String code = credential.getSmsCode();
                    mOtp.setText(code);

                    System.out.println("Code = "+code);

                    signInWithPhoneAuthCredential(credential);


                }

                @Override
                public void onVerificationFailed(FirebaseException e) {

                    mVerificationInProgress = false;

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        mPhone.setError("Invalid phone number.");
                    } else if (e instanceof FirebaseTooManyRequestsException) {

                        Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

                    Log.d(TAG, "onCodeSent:" + verificationId);


                    mVerificationId = verificationId;
                    mResendToken = token;


                    Toast.makeText( PhoneVerificationScreen.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();

                }
            };

            mVerifyNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String phoneNumber = mPhone.getText().toString();
                    if(phoneNumber==null||phoneNumber.isEmpty()){
                        mPhone.setError("Please enter mobile number");
                        mPhone.requestFocus();
                    }else{

                        checkUserByPhone(phoneNumber,"Verify");




                    }
                }
            });

            mVerifyCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOtp.getText().toString().isEmpty()){
                        mOtp.setError("Please enter otp");
                    }else{
                        try {
                            verifyPhoneNumberWithCode(mVerificationId,mOtp.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText( PhoneVerificationScreen.this, "Please try again sometime", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            mResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phone = mPhone.getText().toString();

                    checkUserByPhone(phone,"Resend");

                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //Firebase function
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        mVerificationInProgress = true;

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() ) {

                            PreferenceHandler.getInstance( PhoneVerificationScreen.this).setPhoneNumber(mPhone.getText().toString());

                            if(screen!=null&&screen.equalsIgnoreCase("Organization")){
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("Company",organization);
                                    Intent main = new Intent( PhoneVerificationScreen.this, CreateFounderScreen.class);
                                    main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    main.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    main.putExtras(bundle);
                                    main.putExtra("PhoneNumber",phone);
                                    startActivity(main);
                                    PhoneVerificationScreen.this.finish();
                            }else if(screen!=null&&screen.equalsIgnoreCase("Employee")){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Company",organization);
                                Intent main = new Intent( PhoneVerificationScreen.this, EmployeeSignUp.class);
                                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                main.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                main.putExtras(bundle);
                                main.putExtra("PhoneNumber",phone);
                                startActivity(main);
                                PhoneVerificationScreen.this.finish();
                            }



                        } else {

                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                mOtp.setError("Invalid code.");

                            }

                        }
                    }
                });
    }


    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished/1000);

            mTimer.setText("00:"+String.format("%02d", progress));
        }

        @Override
        public void onFinish() {
            mTimer.setVisibility(View.GONE);
            mResend.setVisibility(View.VISIBLE);
        }
    }

    private void checkUserByPhone(final String phones,final String type){

        new ThreadExecuter ().execute( new Runnable() {
            @Override
            public void run() {


                EmployeeApi apiService =
                        app.zingo.employeemanagements.utils.Util.getClient().create( EmployeeApi.class);

                Call<ArrayList< Employee >> call = apiService.getUserByPhone(phones);

                call.enqueue(new Callback<ArrayList< Employee >>() {
                    @Override
                    public void onResponse( Call<ArrayList< Employee >> call, Response<ArrayList< Employee >> response) {
//                List<RouteDTO.Routes> list = new ArrayList<RouteDTO.Routes>();
                        int statusCode = response.code();

                        if(statusCode == 200 || statusCode == 204)
                        {
                            ArrayList< Employee > responseProfile = response.body();
                            if(responseProfile != null && responseProfile.size()!=0 )
                            {
                                mPhone.setError("Number Already Exists");
                                Toast.makeText( PhoneVerificationScreen.this, "Mobile already Exists", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {

                                try {

                                    if(type.equalsIgnoreCase("Verify")){
                                        mNumberLay.setVisibility(View.GONE);
                                        mOtpLay.setVisibility(View.VISIBLE);
                                        mResend.setVisibility(View.GONE);
                                        mTimer.setVisibility(View.VISIBLE);

                                        myCountDownTimer = new MyCountDownTimer(10000, 1000);
                                        myCountDownTimer.start();
                                        phone = mPhone.getText().toString();
                                        mMobileNumWithCountry.setText(""+mCountry.getSelectedCountryCodeWithPlus()+mPhone.getText().toString());
                                        startPhoneNumberVerification(mCountry.getSelectedCountryCodeWithPlus()+mPhone.getText().toString());

                                    }else if(type.equalsIgnoreCase("Resend")){
                                        resendVerificationCode(mCountry.getSelectedCountryCodeWithPlus()+mPhone.getText().toString(),mResendToken);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {

                            Toast.makeText( PhoneVerificationScreen.this,response.message(),Toast.LENGTH_SHORT).show();
                        }
//                callGetStartEnd();
                    }

                    @Override
                    public void onFailure( Call<ArrayList< Employee >> call, Throwable t) {
                        // Log error here since request failed

                        Log.e("TAG", t.toString());
                    }
                });
            }
        });
    }

}
