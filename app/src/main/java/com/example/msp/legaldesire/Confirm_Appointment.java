package com.example.msp.legaldesire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Confirm_Appointment extends AppCompatActivity {
    public final String TAG = "appointment123";
    String user_id, user_name, lawyer_id, lawyer_name, appointment_date, appointment_time, lawyer_office_address;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__appointment);
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("User ID");
        user_name = bundle.getString("User Name");
        lawyer_id = bundle.getString("Lawyer ID");
        lawyer_name = bundle.getString("Lawyer Name");
        lawyer_office_address = bundle.getString("Lawyer Office Address");
        appointment_date = bundle.getString("Appointment Date");
        appointment_time = bundle.getString("Appointment Time");

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.btn_pay);

        textView.setText("Lawyer Name:" + lawyer_name + "\nLawyer Address:" + lawyer_office_address + "\nAppointment Date" + appointment_date + "\nAppointment Time:" + appointment_time);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });
        //  intent.putExtra("Appointment Type", "Office Appointment");

    }

    public void setBooking() {
        final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");

        HashMap<String, Object> lawyerAppointments = new HashMap<String, Object>();
        lawyerAppointments.put("User ID", user_id);
        lawyerAppointments.put("User Name", user_name);
        lawyerAppointments.put("Appointment Type", "Office Appointment");
        lawyerAppointments.put("Appointment Date", appointment_date);
        lawyerAppointments.put("Appointment Time", appointment_time);
        lawyerAppointments.put("Lawyer Office Address", lawyer_office_address);


        HashMap<String, Object> userAppointments = new HashMap<String, Object>();
        userAppointments.put("Lawyer ID", lawyer_id);
        userAppointments.put("Lawyer Name", lawyer_name);
        userAppointments.put("Appointment Type", "Office Appointment");
        userAppointments.put("Appointment Date", appointment_date);
        userAppointments.put("Appointment Time", appointment_time);
        userAppointments.put("Lawyer Office Address", lawyer_office_address);

        root2.child(lawyer_id).child("Appointment").push().setValue(lawyerAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Confirm_Appointment.this, "Booking...", Toast.LENGTH_SHORT).show();
            }
        });

        root3.child(user_id).child("Appointment").push().setValue(userAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Confirm_Appointment.this, "Appointment Confirmed, Check 'My Appointments' for further details", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Confirm_Appointment.this, Login.class);
                startActivity(intent);

            }
        });
    }

    private double getAmount() {
        Double amount = 150.0;
        return amount;
    }

    public void makePayment() {
        String phone = "8882434664";
        String productName = "product_name";
        String firstName = "piyush";
        String txnId = "0nf7" + System.currentTimeMillis();
        String email = "piyush.jain@payu.in";
        String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
        String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = true;


        String key = "2fcU3pmI";
        String merchantId = "4947182";// These credentials are from https://test.payumoney.com/
        String salt = "BxA24L2F7Z";   //  THIS WORKS

      /*  String key = "yX8OvWy1";     //These credentials are from https://www.payumoney.com/
        String merchantId = "5826688"; //THIS DOESN'T WORK
        String salt = "0vciMJBbaa";    //ERROR: "some error occurred, Try again"
      */


        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();


        builder.setAmount(getAmount())
                .setTnxId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(isDebug)
                .setKey(key)
                .setMerchantId(merchantId);

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        // Recommended
        //  calculateServerSideHashAndInitiatePayment(paymentParam);
        String hash = hashCal(key + "|" + txnId + "|" + getAmount() + "|" + productName + "|"
                + firstName + "|" + email + "|" + udf1 + "|" + udf2 + "|" + udf3 + "|" + udf4 + "|" + udf5 + "|" + salt);
        Log.d(TAG, "got hash:" + hash);
        paymentParam.setMerchantHash(hash);

        PayUmoneySdkInitilizer.startPaymentActivityForResult(Confirm_Appointment.this, paymentParam);

    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }



  /* */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Inside onactivity result()");

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

            /*if(data != null && data.hasExtra("result")){
              String responsePayUmoney = data.getStringExtra("result");
                if(SdkHelper.checkForValidString(responsePayUmoney))
                    showDialogMessage(responsePayUmoney);
            } else {
                showDialogMessage("Unable to get Status of Payment");
            }*/


            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                showDialogMessage("Payment Success Id : " + paymentId);
                setBooking();


            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "failure");
                showDialogMessage("cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");

                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {

                    } else {
                        showDialogMessage("failure");
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
                showDialogMessage("User returned without login");
            }
        }
    }


    private void showDialogMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm_Appointment.this);
        builder.setTitle("Payment Status");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
}
