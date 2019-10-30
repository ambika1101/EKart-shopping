package e.droid.scrap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class AdminAddActivity extends AppCompatActivity {
//this activity allows the admin to add products to the category
    String categoryName,description,price,prod_name,downloadImageUrl;
    Button addProductButton;
    ImageView img;
    private ProgressBar loadingBar;
    EditText prodName,prodDescription,prodPrice;
    static  final int GalleryPic=1;
    Uri imgUri;
    StorageReference productImagesRef;
    DatabaseReference productRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);
       //
        categoryName=getIntent().getExtras().get("category").toString();
        //Toast.makeText(this,"Welcome"+categoryName,Toast.LENGTH_SHORT).show();

        addProductButton=findViewById(R.id.add_prod_button);
        img=findViewById(R.id.select_product);
        prodName=findViewById(R.id.product_name);
        loadingBar=findViewById(R.id.progress_circular2);
        prodDescription=findViewById(R.id.product_description);
        prodPrice=findViewById(R.id.product_price);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery(); //so that user can choose the image of the product
            }
        });
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
        productImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef= FirebaseDatabase.getInstance().getReference().child("products");
    }

    private void ValidateProductData() {
        description= prodDescription.getText().toString();
        price= prodPrice.getText().toString();
        prod_name= prodName.getText().toString();
        if(imgUri==null){
            Toast.makeText(this,"Select a picture",Toast.LENGTH_LONG).show();
        }else if(description.equals("")){
            Toast.makeText(this,"Description missing",Toast.LENGTH_LONG).show();
        }else if(price.equals("")){
            Toast.makeText(this,"Price missing",Toast.LENGTH_LONG).show();
        }else{
            addProductToStorage();
        }
    }

    private void addProductToStorage() {
        loadingBar.setVisibility(View.VISIBLE);

        final StorageReference filepath= productImagesRef.child(imgUri.getLastPathSegment()+ UUID.randomUUID().toString());
        final UploadTask uploadTask=filepath.putFile(imgUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            //if the upload fails for some reason
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg=e.toString();
                Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_LONG).show();
                loadingBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(),"Image uploaded successfully",Toast.LENGTH_LONG).show();
            //after this, we will store the link of this storage to the database so tht the image can be displayed
                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                       if(!task.isSuccessful()){

                           throw task.getException();

                       }
                       downloadImageUrl=filepath.getDownloadUrl().toString();
                       return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(getApplicationContext(),"product image url received",Toast.LENGTH_LONG).show();
                            SaveProductToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductToDatabase() {
        HashMap<String,Object> productMap= new HashMap<>(); //store all details of the product
        productMap.put("image",downloadImageUrl);
        productMap.put("description",description);
        productMap.put("category",categoryName);
        productMap.put("price",price);
        productMap.put("name",prod_name);

        productRef.push().setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Added successfully",Toast.LENGTH_SHORT).show();
                    loadingBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(),AdminCategoryActivity.class));
                }else{
                    loadingBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void OpenGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!=null){
            imgUri=data.getData();//returns uri i.e. address of image on the system
            img.setImageURI(imgUri);
        }
    }
}
