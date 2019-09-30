package e.droid.scrap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccount;
    private EditText name;
    private EditText number;
    private EditText password;
    private ProgressBar loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createAccount = findViewById(R.id.act_button);
        name = findViewById(R.id.register_name);
        number = findViewById(R.id.register_num);
        password = findViewById(R.id.register_pass);
       // loadingBar=new ProgressBar(this);//creates a loading bar when the
        loadingBar=findViewById(R.id.progress_circular);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username = name.getText().toString();
        String userPass= password.getText().toString();
        String userNum= number.getText().toString();

        if(username.isEmpty()){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_LONG).show();
        }else if(userPass.isEmpty()){
            Toast.makeText(this,"Password required",Toast.LENGTH_LONG).show();
        }else if(userNum.isEmpty()){
            Toast.makeText(this,"Phone number is required",Toast.LENGTH_LONG).show();
        }else{
            //now add to database
            loadingBar.setVisibility(View.VISIBLE);
            ValidatePhone(username,userNum,userPass);

        }
    }

    private void ValidatePhone(final String username, final String userNum, final String password) {
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("users").child(userNum).exists())){

                    //user does not exist
                    HashMap<String,Object> userDataMap= new HashMap<>();
                    userDataMap.put("phone",userNum);
                    userDataMap.put("password",password);
                    userDataMap.put("name",username);
                    rootRef.child("users").child(userNum).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_SHORT).show();
                                loadingBar.setVisibility(View.GONE);
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                            }else{
                                //if internet is not available or some other issue
                                loadingBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Registration failed",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"This number exists",Toast.LENGTH_LONG).show();
                    loadingBar.setVisibility(View.INVISIBLE);
                    //now redirect to the main activity
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
