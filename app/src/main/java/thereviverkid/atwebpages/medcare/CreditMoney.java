package thereviverkid.atwebpages.medcare;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import thereviverkid.atwebpages.medcare.DataRetrievalClass.UserDetails;

public class CreditMoney extends AppCompatActivity {

   private TextView EName, ENumber, EBalance;
   private EditText SMobile,OBalance;
   private Button bSearch,CreditAmount;
   private DatabaseReference databaseReference;
   private  String num,amou;
   private Float balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_money);
        EName =findViewById(R.id.eName);
        ENumber =findViewById(R.id.eNumberD);
        EBalance =findViewById(R.id.eBalance);
        SMobile=(EditText) findViewById(R.id.eNumber3);
        OBalance=(EditText) findViewById(R.id.eAmount);
        bSearch=findViewById(R.id.bSearchUser);
        CreditAmount=findViewById(R.id.bCredit);
        databaseReference= FirebaseDatabase.getInstance().getReference("UserDetails");





/*
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // String mobilenumber=SMobile.getText().toString();
               // Toast.makeText(CreditMoney.this,mobilenumber, Toast.LENGTH_LONG).show();
                FirebaseDatabase.getInstance().getReference().child("UserDetails").orderByChild("MobileNo").equalTo("914267676497").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){

                            final UserDetails userDetails=snapshot.getValue(UserDetails.class);
                            userDetails.setUserBalance("2000");
                            Toast.makeText(CreditMoney.this, "Success", Toast.LENGTH_SHORT).show();



                            }else{

                                Toast.makeText(CreditMoney.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

 */

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                num=SMobile.getText().toString().trim();
                if(num.isEmpty()){

                    SMobile.setError("Please Enter the Mobile Number of User");
                    return;

                }else{

                    LoadDetails();

                }


            }
        });




        CreditAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amou=OBalance.getText().toString().trim();
                if(num.isEmpty()){

                    OBalance.setError("Please Enter Amount to be credited");
                    return;

                }else{
                    creditMoney();

                }

            }
        });

    }

    private void LoadDetails() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("UserDetails");

        Query phoneQuery = ref.orderByChild("MobileNo").equalTo("+91"+num);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    final UserDetails userDetails=singleSnapshot.getValue(UserDetails.class);
                    EName.setText("Name           :"+userDetails.getFirstName()+" " + userDetails.getLastName());
                    ENumber.setText("Number       :"+userDetails.getMobileNo());
                    EBalance.setText("Balance     :"+userDetails.getUserBalance());
                    balance= Float.valueOf(userDetails.getUserBalance());




                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    private void creditMoney() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("UserDetails");

        Query phoneQuery = ref.orderByChild("MobileNo").equalTo("+91"+num);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the reference to the child node
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference childRef = childSnapshot.getRef().child("UserBalance");

                    // Set the value of the child node
                    Float b,a;
                    b=Float.valueOf(balance);
                    a=Float.valueOf(amou);
                    balance= b+a;
                    childRef.setValue(balance.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreditMoney.this, "Amount Credited Successfully", Toast.LENGTH_LONG).show();
                            LoadDetails();
                            OBalance.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreditMoney.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });




    }

}