package com.example.msp.legaldesire;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Refund_Request extends AppCompatActivity {
    String user_id;
    boolean isLawyer;
    EditText edit_name, edit_email, edit_contact, edit_transaction, edit_amount;
    Button submit;
    RadioButton chat, office_appointment, home_appointment;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Refund");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund__request);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F17A12")));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Refund</font>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Refund</font>"));
        }
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_contact = (EditText) findViewById(R.id.edit_number);
        edit_transaction = (EditText) findViewById(R.id.edit_transaction_id);
        edit_amount = (EditText) findViewById(R.id.edit_amount);
        chat = (RadioButton) findViewById(R.id.radio_chat);
        office_appointment = (RadioButton) findViewById(R.id.radio_appointment);
        home_appointment = (RadioButton) findViewById(R.id.radio_home_appointment);
        chat.setChecked(true);
        submit = (Button) findViewById(R.id.btn_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edit_email.getText().toString();

                if (edit_name.getText().toString().isEmpty()) {
                    Toast.makeText(Refund_Request.this, "Enter your Name", Toast.LENGTH_SHORT).show();
                } else if (!isEmailValid(email)) {
                    Toast.makeText(Refund_Request.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                } else if (edit_contact.getText().toString().isEmpty()) {
                    Toast.makeText(Refund_Request.this, "Enter your Contact number", Toast.LENGTH_SHORT).show();
                } else if (edit_transaction.getText().toString().isEmpty()) {
                    Toast.makeText(Refund_Request.this, "Enter Transaction ID", Toast.LENGTH_SHORT).show();
                } else if (edit_amount.getText().toString().isEmpty()) {
                    Toast.makeText(Refund_Request.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                } else {
                    String name = edit_name.getText().toString();
                    String mail = edit_email.getText().toString();
                    String contact = edit_contact.getText().toString();
                    String transaction = edit_transaction.getText().toString();
                    String amount = edit_amount.getText().toString();
                    String service_type = "chat";
                    if (chat.isChecked())
                        service_type = "chat";
                    else if (office_appointment.isChecked())
                        service_type = "office appointment";
                    else if (home_appointment.isChecked())
                        service_type = "home appointment";

                    HashMap<String, Object> insertData = new HashMap<String, Object>();
                    insertData.put("User ID", user_id);
                    insertData.put("isLawyer", isLawyer);
                    insertData.put("Name", name);
                    insertData.put("Email", mail);
                    insertData.put("Contact", contact);
                    insertData.put("Transaction ID", transaction);
                    insertData.put("Amount", amount);
                    insertData.put("Service Type", service_type);
                    insertData.put("Resolved", false);

                    database.push().setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Refund_Request.this, "Your Request has been sent", Toast.LENGTH_SHORT).show();
                                submit.setEnabled(false);
                            } else {
                                Toast.makeText(Refund_Request.this, "Failed to send request. Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });

    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
