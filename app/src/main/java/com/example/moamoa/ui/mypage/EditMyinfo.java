package com.example.moamoa.ui.mypage;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moamoa.LoginActivity;
import com.example.moamoa.R;
//import com.example.moamoa.User;
import com.example.moamoa.ui.account.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditMyinfo extends AppCompatActivity {
    private DatabaseReference mDatabase;
    EditText PresentPasswordText, PasswordText, PasswordcheckText;
    Button passwordBtn;
    EditText EmailText;
    Button emailBtn;
    private int success = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_myinfo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (user != null) {
            // User is signed in
            // Name, email address, and profile photo Url
            //프로필 정보
            TextView name = findViewById(R.id.name);
            TextView Email = findViewById(R.id.mail);

            //nikname은 auth(계정 정보)에 들어가지 않으므로 database getReference()를 이용
            //닉네임 뜨는데 딜레이가 있음
            mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User User = snapshot.getValue(User.class);
                    name.setText(User.getname());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                    Toast.makeText(getApplication(),"데이터를 가져오는데 실패했습니다" , Toast.LENGTH_LONG).show();
                }
            });
            String emails = user.getEmail();
            Email.setText(emails);


            //비밀번호 변경
            passwordBtn = findViewById(R.id.password_button);
            PresentPasswordText = findViewById(R.id.presentpassword);
            PasswordText = findViewById(R.id.newpassword);
            PasswordcheckText = findViewById(R.id.passwordchk);

            TextView pwdtext = findViewById(R.id.pwdtext);
            Intent intent = getIntent(); //전달할 데이터를 받을 Intent
            //text 키값으로 데이터를 받는다. String을 받아야 하므로 getStringExtra()를 사용함
            String checkpwd = intent.getStringExtra("ppwd");

            passwordBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public synchronized void onClick(View v) {
                    ProgressDialog mDialog = new ProgressDialog(EditMyinfo.this);
                    AlertDialog.Builder alerting = new AlertDialog.Builder(EditMyinfo.this);
                    //가입 정보 가져오기
                    String ppwd = PresentPasswordText.getText().toString().trim();

                    String newpwd = PasswordText.getText().toString().trim();
                    String pwdcheck = PasswordcheckText.getText().toString().trim();

                    if (ppwd.equals("")) {
                        alerting.setMessage("현재 비밀번호를 입력해주세요");
                        alerting.show();
                    }else if (newpwd.equals("")) {
                        alerting.setMessage("새 비밀번호를 입력해주세요");
                        alerting.show();
                    } else if (pwdcheck.equals("")) {
                        alerting.setMessage("비밀번호 확인을 입력해주세요");
                        alerting.show();
                    }else if (!ppwd.equals(checkpwd)) {
                        alerting.setMessage("현재 비밀번호가 일치하지 않습니다.");
                        alerting.show();
                    }else if (newpwd.length() < 10) {
                        alerting.setMessage("비밀번호가 너무 짧습니다.");
                        alerting.show();
                    }else if (newpwd.equals(ppwd)) {
                        alerting.setMessage("현재 비밀번호와 같습니다.");
                        alerting.show();
                    } else if (!newpwd.equals(pwdcheck)) {
                        pwdtext.setText("*비밀번호가 일치하지 않습니다.");
                    } else if(!user.getEmail().equals("")) {
                        user.updatePassword(newpwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User password updated.");
                                    auth.signOut();
                                }
                            }
                        });
                        pwdtext.setText("");
                        Intent intent = new Intent(EditMyinfo.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplication(), "비밀번호가 변경되었습니다.\n다시 로그인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            });

            //이메일 변경
            emailBtn = findViewById(R.id.button7);
            EmailText = findViewById(R.id.editEmailAddress);

            emailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public synchronized void onClick(View v) {
                    AlertDialog.Builder alerting = new AlertDialog.Builder(EditMyinfo.this);

                    String emailText = EmailText.getText().toString().trim();
                    boolean em = emailText.contains("@") & emailText.contains(".");

                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(emailText)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplication(), "변경", Toast.LENGTH_LONG).show();
                                            Log.d(TAG, "User email address updated.");
                                            Email.setText(emailText);
                                        }
                                        else{
                                            Toast.makeText(getApplication(), "실패", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

//                        user.sendEmailVerification()
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Log.d(TAG, "Email sent.");
//                                            alerting.setMessage("입력하신 이메일의 인증 메일이 발송되었습니다")
//                                                    .setNeutralButton("확인", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            Intent intent = new Intent(EditMyinfo.this, MypageFragment.class);
//                                                            startActivity(intent);
//                                                            finish();
//                                                        }
//                                                    }).create().show();
//                                        }
//                                    }
//                                });
//                    }else{
////                        alerting.setMessage("다시 입력해주세요.");
////                        alerting.show();
//                    }
                }
            });

        } else {
            // No user is signed in
        }
    } // end of onCreate
} // end of class
