package com.prakhar.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import me.philio.pinentry.PinEntryView;

public class SignInActivity extends AppCompatActivity {

    String TAG = "SignIn Activity";
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    EditText phoneNum;
    String phoneNumber;
    FirebaseAuth mAuth;
    String mVerificationId;
    String OTP;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressBar progressBar;
    PhoneAuthCredential mannualCredeential;
    static PhoneAuthProvider phoneAuthProvider;
    FirebaseAuthSettings firebaseAuthSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth=FirebaseAuth.getInstance();
        phoneNum = findViewById(R.id.phone);
        progressBar = findViewById(R.id.pbar);
        phoneAuthProvider = PhoneAuthProvider.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, OTP);
    }

    public void SendOTP(View view) {
        progressBar.setVisibility(View.VISIBLE);
        phoneNumber = phoneNum.getText().toString();
        Log.d("phone",phoneNumber);
        if (!phoneNumber.isEmpty()){
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.

                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    Toast.makeText(SignInActivity.this, "Your number is Verified", Toast.LENGTH_SHORT).show();
                   signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e);

                    progressBar.setVisibility(View.GONE);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                        Toast.makeText(SignInActivity.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                    }
                    //Toast.makeText(SignInActivity.this, "Enter a Valid Number", Toast.LENGTH_SHORT).show();
                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;
//                    Intent i = new Intent(SignInActivity.this,IntermediateActivity.class);
//                    i.putExtra("verificationId",mVerificationId);
//                    i.putExtra("phoneNumber",phoneNumber);
//                    startActivity(i);
                   // SignInActivity.this.enableUserManuallyInputCode();
                    Toast.makeText(SignInActivity.this, "OTP is sent", Toast.LENGTH_SHORT).show();
                    // ...
                }
            };
        }
        // Configure faking the auto-retrieval with the whitelisted numbers.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,this,mCallbacks);
        //PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, OTP);
        }

//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //Toast.makeText(SignInActivity.this, "you are", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(SignInActivity.this,Main2Activity.class);
                            startActivity(i);
                            //FirebaseUser user = task.getResult().getUser();
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
