package e.droid.scrap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    ImageView tshirt,shoe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        tshirt=findViewById(R.id.tshirt);
        shoe=findViewById(R.id.foot);
        tshirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCategoryActivity.this,AdminAddActivity.class);
                intent.putExtra("category","tshirt"); //now the admin can add this category product
                startActivity(intent);
            }
        });
        shoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCategoryActivity.this,AdminAddActivity.class);
                intent.putExtra("category","shoes"); //now the admin can add this category product
                startActivity(intent);
            }
        });
    }
}
