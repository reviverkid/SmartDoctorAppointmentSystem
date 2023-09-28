package thereviverkid.atwebpages.medcare.Adapters;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import thereviverkid.atwebpages.medcare.CreditMoney;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.PatientAppointmentRequest;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.UserDetails;
import thereviverkid.atwebpages.medcare.PatientFragments.FixAppointment;
import thereviverkid.atwebpages.medcare.PatientFragments.PendingAppointmentFragment;
import thereviverkid.atwebpages.medcare.PatientMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.List;

public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.ViewHolder> {

    private Context context;
    private List<PatientAppointmentRequest> appointmentRequestList;
    private ProgressDialog progressDialog;
    private Float balance;


    public MyAppointmentAdapter(Context context, List<PatientAppointmentRequest> appointmentRequestList) {
        this.context = context;
        this.appointmentRequestList = appointmentRequestList;
        progressDialog= new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_patient_apt,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientAppointmentRequest request=appointmentRequestList.get(position);
        holder.doc_name.setText(request.getName());
        holder.spl.setText("Specialization: "+request.getSpecialization());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setCancelable(false).setMessage("Are you sure you want to cancel the appointment of Dr. "+request.getName()+" for "+request.getDateAndTime()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Cancelling...");
                                progressDialog.show();
                                FirebaseDatabase.getInstance().getReference().child("ConfirmedDocAppointments").child(request.getDocID()).child(request.getDoctorAppointKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(request.getPatientAppointKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        getBalanceD();
                                                        Toast.makeText(context, "Cancelled2", Toast.LENGTH_SHORT).show();
                                                        ((PatientMainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container, new PendingAppointmentFragment(),"Pending Appointments").addToBackStack(null).commit();


                                                    }else {
                                                        progressDialog.dismiss();
                                                        ReusableFunctionsAndObjects.showMessageAlert(context, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    ReusableFunctionsAndObjects.showMessageAlert(context, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                                }
                                            });
                                        }else {
                                            progressDialog.dismiss();
                                            ReusableFunctionsAndObjects.showMessageAlert(context, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        ReusableFunctionsAndObjects.showMessageAlert(context, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                    }
                                });
                            }
                        }).setNegativeButton("No",null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView doc_name,spl;
        AppCompatButton cancel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doc_name=itemView.findViewById(R.id.doc_name);
            cancel=itemView.findViewById(R.id.cancel);
            spl=itemView.findViewById(R.id.spl);
        }
    }

    private void getBalanceD() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("UserDetails");

        Query phoneQuery = ref.orderByChild("UserId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    final UserDetails userDetails=singleSnapshot.getValue(UserDetails.class);

                    balance= Float.valueOf(userDetails.getUserBalance());


                   creditMoney();










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

        Query phoneQuery = ref.orderByChild("UserId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the reference to the child node
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference childRef = childSnapshot.getRef().child("UserBalance");

                    // Set the value of the child node

                    balance= balance+50;
                    childRef.setValue(balance.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ReusableFunctionsAndObjects.showMessageAlert(context.getApplicationContext(), "Payment Credited", "Your Current Balance is "+balance.toString(), "OK",(byte)0);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ReusableFunctionsAndObjects.showMessageAlert(context.getApplicationContext(), "Payment failed", "Your Current Balance is "+balance.toString(), "OK",(byte)0);
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
