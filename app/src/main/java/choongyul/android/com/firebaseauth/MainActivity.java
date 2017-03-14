package choongyul.android.com.firebaseauth;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText etEmail,etPW;
    TextView tvEmail,tvPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPW = (EditText) findViewById(R.id.etPW);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPW = (TextView) findViewById(R.id.tvPW);

        mAuth = FirebaseAuth.getInstance();

        // 로그 인 아웃을 체크하는 리스너
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // 현재 앱의 사용자 정보를 가져온다.
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // 이메일 검증이 안되어 있으면 검증 메일 발송
                    if(!user.isEmailVerified()) {
                        mailverificatiln(user);
                    } else {
                        // 정상 로그인 후처리
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private boolean emailChecked = false;
    // 최초 로그인시 이메일 인증
    public void mailverificatiln(FirebaseUser user) {
        if(!emailChecked) {

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        }
    }

    public void signup(View view) {
        String email = etEmail.getText().toString();
        String password = etPW.getText().toString();

        int checkCount = 0;
        if(!SignUtil.validateEmail(email)) {
            tvEmail.setText("이메일 형식이 잘못되었습니다.");
            checkCount++;
        }
        if(!SignUtil.validatePassword(password)) {
            tvEmail.setText("비밀번호 형식이 잘못되었습니다.");
            checkCount++;
        }

        if(checkCount > 0) {
            Toast.makeText(MainActivity.this, "형식이 잘못되었음",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "사용자 등록 실패",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 스탑에서는 리스너 체크 해제. 자원절약 차원
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void addUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "사용자 등록 실패",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}