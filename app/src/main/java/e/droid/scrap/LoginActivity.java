package e.droid.scrap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import e.droid.scrap.Model.User;
import e.droid.scrap.Prevalent.Prevalent;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText login_number;
    private EditText login_password;
    Button loginBtn;
    TextView adminLink,notAdminLink;
    String parentDbName="users"; // required so that we can define admin access in the same file
    private CheckBox rememberMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_number=findViewById(R.id.username_login);
        login_password=findViewById(R.id.password_login);
        loginBtn=findViewById(R.id.login_btn);
        rememberMe=findViewById(R.id.remember);
        adminLink=findViewById(R.id.admin);
        notAdminLink=findViewById(R.id.not_admin);
        Paper.init(this); // the paper library is used to store user info in the android of his fone to provide remember me functionalities
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(login_number,login_password);
            }


        });
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login as admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName="admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName="users";
            }
        });
    }
    private void loginUser(EditText login_number, EditText login_password) {
        final String num=login_number.getText().toString();
        final String pass=login_password.getText().toString();
        if(num.equals("")){
            login_number.setError("Required");
            login_number.requestFocus();
            return;
        }
        if(pass.equals("")){
            login_password.setError("Required");
            login_password.requestFocus();
            return;
        }
        allowAccessToAccount(num,pass);
        //login the user
       /*


                    if(dataSnapshot.child("users").child(num).child("password").equals(pass)){
                        Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                    }else{

                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void allowAccessToAccount(final String num, final String pass) {
        if(rememberMe.isChecked()){
            //if it says remember me, then we save the values to the android os through paper
            Paper.book().write(Prevalent.userPhoneKey,num); // store this num in userphonekey
            Paper.book().write(Prevalent.userPasswordKey,pass);
        }
        DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(num).exists()){
                    User userData=dataSnapshot.child(parentDbName).child(num).getValue(User.class);
                    if (userData.getPhone().equals(num)){
                        if (userData.getPassword().equals(pass)){
                            Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this,"Incorrect password",Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"No such user exists",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
