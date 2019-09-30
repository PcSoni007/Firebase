package com.prakhar.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ManualSignIn extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String verificationCode;
    String OTP;
    EditText phone;
    EditText etOTP;
    String phoneNumber;
    Button getOtp;
    Spinner spinner;
    String phnCode;
    ProgressBar pBar;
    //Button signin;
    private FirebaseAuth mAuth;
    String TAG = "Manual SignIn Activity";
    FirebaseAuthSettings firebaseAuthSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_sign_in);
        pBar = findViewById(R.id.pbar);
        spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phn_country_code, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        getOtp = findViewById(R.id.getCode);
        //signin = findViewById(R.id.signin);
        phone = findViewById(R.id.phoneNum);
        etOTP = findViewById(R.id.otp);
        mAuth = FirebaseAuth.getInstance();
//        firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, OTP);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String pCode = adapterView.getItemAtPosition(i).toString();
        for (int j=0;j<pCode.length();j++){
            char ch = pCode.charAt(j);
            if (Character.isDigit(ch)){
                phnCode += pCode;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getCode(View view) {

        phoneNumber = "+"+phnCode;
        if (phone != null){
            phoneNumber = phoneNumber+phone.getText().toString();
            phone.setError("You must Enter the phone number to Sign In");
            phone.requestFocus();
            return;
        }
        if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()){
            phone.setError("You must Enter a valid phone number");
            phone.requestFocus();
            return;
        }
        pBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(ManualSignIn.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        pBar.setVisibility(View.GONE);
                        Log.e("verificationCode",verificationCode+"   "+OTP);
                        Toast.makeText(ManualSignIn.this, "OTP is Sent", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void signIn(View view) {
        OTP = etOTP.getText().toString();
        // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,OTP);
        try {
            Log.e("SignInVerificationCode",verificationCode+"   "+OTP);
            pBar.setVisibility(View.VISIBLE);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, OTP);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(ManualSignIn.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                            pBar.setVisibility(View.GONE);
                            Intent i=new Intent(ManualSignIn.this,Main2Activity.class);
                            startActivity(i);
                            //FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                pBar.setVisibility(View.GONE);

                            }
                        }
                    }
                });
    }

    /*public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }*/
}