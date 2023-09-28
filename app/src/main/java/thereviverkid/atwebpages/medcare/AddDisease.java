package thereviverkid.atwebpages.medcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddDisease extends AppCompatActivity {

    EditText disease,symptoms;
    Button Upload;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_disease);
        disease=findViewById(R.id.diseaseId);
        symptoms=findViewById(R.id.symptomId);
        Upload=findViewById(R.id.bUploadD);


        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String d=disease.getText().toString().trim();
                String s=symptoms.getText().toString().trim();

                if(d.isEmpty()){
                    disease.setError("please fill the details");
                    return;
                }else if(s.isEmpty()){
                    symptoms.setError("please fill the details");
                    return;
                }else{
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("DiseaseName",d);
                    hashMap.put("Symptoms",s);
                    FirebaseDatabase.getInstance().getReference().child("DiseaseAndSymptoms").push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddDisease.this, "Disease and Symptoms uploaded successfully", Toast.LENGTH_SHORT).show();
                            disease.setText("");
                            symptoms.setText("");
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddDisease.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });


    }
}