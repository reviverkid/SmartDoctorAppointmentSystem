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
import thereviverkid.atwebpages.medcare.DataRetrievalClass.AppointmentRequest;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.PatientAppointmentRequest;
import thereviverkid.atwebpages.medcare.DataRetrievalClass.UserDetails;
import thereviverkid.atwebpages.medcare.DoctorFragments.AppointmentRequestFragment;
import thereviverkid.atwebpages.medcare.DoctorMainActivity;
import thereviverkid.atwebpages.medcare.R;
import thereviverkid.atwebpages.medcare.ReusableFunctionsAndObjects;

import java.util.List;

public class AppointmentRequestAdapter  extends RecyclerView.Adapter<AppointmentRequestAdapter.ViewHolder> {

    private Context context;
    private List<AppointmentRequest> appointmentRequestList;
    private ProgressDialog progressDialog;
    private Float balance;
    private String pid;

    public AppointmentRequestAdapter(Context context, List<AppointmentRequest> appointmentRequestList) {
        this.context = context;
        this.appointmentRequestList = appointmentRequestList;
        progressDialog= new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_apt_request,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentRequest appointmentRequest=appointmentRequestList.get(position);
        holder.name.setText(appointmentRequest.getName());
        holder.email.setText(appointmentRequest.getPatientEmail());
        holder.phone.setText(appointmentRequest.getPatientPhone());
        holder.datetime.setText(appointmentRequest.getDateAndTime());
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setCancelable(false).setMessage("Are you sure you want to reject the appointment request of "+appointmentRequest.getName()+" for "+appointmentRequest.getDateAndTime()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Rejecting...");
                                progressDialog.show();
                                FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(appointmentRequest.getPatientID()).child(appointmentRequest.getPatientAppointKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("PendingDocAppointments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(appointmentRequest.getDoctorAppointKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        pid=appointmentRequest.getPatientID();
                                                        try {
                                                            getBalanceD();
                                                        }
                                                        catch(Exception e) {
                                                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                        }

                                                        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                                        ((DoctorMainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container, new AppointmentRequestFragment(),"Appointment Requests").addToBackStack(null).commit();
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
                                        }else{
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
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setCancelable(false).setMessage("Are you sure you want to confirm the appointment request of "+appointmentRequest.getName()+" for "+appointmentRequest.getDateAndTime()+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("Confirming...");
                                progressDialog.show();
                                FirebaseDatabase.getInstance().getReference().child("ConfirmedDocAppointments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(appointmentRequest.getDoctorAppointKey()).setValue(appointmentRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(appointmentRequest.getPatientID()).child(appointmentRequest.getPatientAppointKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    PatientAppointmentRequest patientAppointmentRequest=snapshot.getValue(PatientAppointmentRequest.class);
                                                    FirebaseDatabase.getInstance().getReference().child("ConfirmedPatientAppointments").child(appointmentRequest.getPatientID()).child(patientAppointmentRequest.getPatientAppointKey()).setValue(patientAppointmentRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                FirebaseDatabase.getInstance().getReference().child("PendingPatientAppointments").child(appointmentRequest.getPatientID()).child(appointmentRequest.getPatientAppointKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            FirebaseDatabase.getInstance().getReference().child("PendingDocAppointments").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(appointmentRequest.getDoctorAppointKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                                                                                        ((DoctorMainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container, new AppointmentRequestFragment(),"Appointment Requests").addToBackStack(null).commit();
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
                                                                        }else{
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
                                                            }else{
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

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    progressDialog.dismiss();
                                                    ReusableFunctionsAndObjects.showMessageAlert(context, "Network Error", "Make sure you are connected to internet.", "OK",(byte)0);
                                                }
                                            });
                                        }else{
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
        TextView name,email,phone,datetime;
        AppCompatButton confirm,reject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.patient_name);
            email=itemView.findViewById(R.id.email);
            phone=itemView.findViewById(R.id.phone);
            datetime=itemView.findViewById(R.id.date_time);
            confirm=itemView.findViewById(R.id.confirm);
            reject=itemView.findViewById(R.id.reject);
        }
    }


    private void getBalanceD() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("UserDetails");

        Query phoneQuery = ref.orderByChild("UserId").equalTo(pid);
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

        Query phoneQuery = ref.orderByChild("UserId").equalTo(pid);
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
                       //     ReusableFunctionsAndObjects.showMessageAlert(context.getApplicationContext(), "Payment Credited to user", "Transaction completed", "OK",(byte)0);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        //    ReusableFunctionsAndObjects.showMessageAlert(context.getApplicationContext(), "Payment failed", "Try again Later ", "OK",(byte)0);
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
