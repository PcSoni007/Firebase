package com.prakhar.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import me.philio.pinentry.PinEntryView;

public class IntermediateActivity extends AppCompatActivity {

    PinEntryView pinEntryView;
    String OTP;
    PhoneAuthCredential mannualCredeential;
    static PhoneAuthProvider phoneAuthProvider;
    FirebaseAuthSettings firebaseAuthSettings;
    Button btnSignIn;
    String phoneNumber;
    String TAG = "Intermediate Activity";
    String verificationId;
    FirebaseAuth firebaseAuth;
    EditText editText;
    String smsCode = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);
        editText = findViewById(R.id.otp);
        btnSignIn = findViewById(R.id.btnSignIn);
        Intent intent = new Intent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        verificationId = intent.getStringExtra("verificationId");

//        phoneAuthProvider = PhoneAuthProvider.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, OTP);

    }

    public void Signin(View view) {
        OTP = editText.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, OTP);
        signInWithPhoneAuthCredential(credential);
       /* phoneAuthProvider.verifyPhoneNumber(phoneNumber,
                60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "Phone Number Verification:success");
                        Toast.makeText(IntermediateActivity.this, "Phone Number verfication is done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.d(TAG, "Phone Number Verification:ERROR");
                        Toast.makeText(IntermediateActivity.this, "Something Wrong happend", Toast.LENGTH_SHORT).show();
                    }
                });*/
        //SigninWithPhone(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //Toast.makeText(SignInActivity.this, "you are", Toast.LENGTH_SHORT).show();

                            Intent i=new Intent(IntermediateActivity.this,Main2Activity.class);
                            startActivity(i);

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


}
