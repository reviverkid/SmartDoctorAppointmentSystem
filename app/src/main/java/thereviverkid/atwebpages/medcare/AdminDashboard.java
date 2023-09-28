package thereviverkid.atwebpages.medcare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminDashboard extends AppCompatActivity {
    Button AddD,AddP,AddDisease;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        AddD=findViewById(R.id.bAddDoctor);
        AddP=findViewById(R.id.bAddPayment);
        AddDisease=findViewById(R.id.bDisease);


        AddD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboard.this,DoctorRegister.class));
            }
        });

        AddP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboard.this,CreditMoney.class));
            }
        });

        AddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboard.this,AddDisease.class));
            }
        });
    }
}