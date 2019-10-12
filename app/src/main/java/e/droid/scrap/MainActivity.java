package e.droid.scrap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import e.droid.scrap.Model.User;
import e.droid.scrap.Prevalent.Prevalent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton=findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        registerButton=findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
        Paper.init(this);
        String userPhoneKey=Paper.book().read(Prevalent.userPhoneKey); //retrieve the login details for the user
        String userPasswordKey=Paper.book().read(Prevalent.userPasswordKey);
        if(userPhoneKey!="" && userPasswordKey!=""){
            //means we have a saved value
            if(!TextUtils.isEmpty(userPhoneKey)&&!TextUtils.isEmpty(userPasswordKey))
            allowAccess(userPhoneKey,userPasswordKey);
        }
    }

    private void allowAccess(final String num, final String pass) {
        DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users").child(num).exists()){
                    User userData=dataSnapshot.child("users").child(num).getValue(User.class);
                    if (userData.getPhone().equals(num)){
                        if (userData.getPassword().equals(pass)){
                            Toast.makeText(MainActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this,"Incorrect password",Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this,"No such user exists",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
    });
}}
