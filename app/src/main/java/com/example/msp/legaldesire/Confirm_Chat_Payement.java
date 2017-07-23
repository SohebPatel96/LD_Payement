package com.example.msp.legaldesire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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

public class Confirm_Chat_Payement extends AppCompatActivity {
    public final String TAG = "confirmchat123";
    TextView mTextView;
    Button mButton;
    String lawyer_id, lawyer_name, user_id, user_name, lawyer_profile, user_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__chat__payement);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F17A12")));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm Payment </font>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm Payment </font>"));
        }
        Bundle bundle = getIntent().getExtras();
        lawyer_id = bundle.getString("Lawyer ID");
        lawyer_name = bundle.getString("Lawyer Name");
        user_id = bundle.getString("User ID");
        user_name = bundle.getString("User Name");
        lawyer_profile = bundle.getString("Lawyer profile");
        user_profile = bundle.getString("User profile");

        mTextView = (TextView) findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.btn_pay);
        mTextView.setText("On successful payement of 50₹, lawyer '" + lawyer_name + "' will be added to your chats. Go to 'My Chats' to chat with this lawyer\n" +
                "\nDON'T WORRY!! You will be refunded if:\n" +
                "✔The Lawyer didn't reply\n" +
                "✔or you didn't find the counselling helpful");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });
    }

    private double getAmount() {
        Double amount = 50.0;
        return amount;
    }

    public void makePayment() {
        String phone = "8882434664";
        String productName = "product_name";
        String firstName = "piyush";
        String txnId = "legaldesire" + System.currentTimeMillis();
        String email = "piyush.jain@payu.in";
        String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
        String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = true;


        //   String key = "uRURJ8";
        //    String merchantId = "329037";  //Real creadentials set dubug to false and run these
        //  String salt = "zPi921sH";

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
                .setIsDebug(true)
                .setKey(key)
                .setMerchantId(merchantId);

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();

        // Recommended
        //  calculateServerSideHashAndInitiatePayment(paymentParam);
        String hash = hashCal(key + "|" + txnId + "|" + getAmount() + "|" + productName + "|"
                + firstName + "|" + email + "|" + udf1 + "|" + udf2 + "|" + udf3 + "|" + udf4 + "|" + udf5 + "|" + salt);
        Log.d(TAG, "got hash:" + hash);
        paymentParam.setMerchantHash(hash);

        PayUmoneySdkInitilizer.startPaymentActivityForResult(Confirm_Chat_Payement.this, paymentParam);

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

                final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");

                HashMap<String, Object> Message = new HashMap<String, Object>();
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Lawyer ID", lawyer_id);
                map.put("Lawyer Name", lawyer_name);
                map.put("User ID", user_id);
                map.put("User Name", user_name);
                map.put("Message", Message);
                map.put("Lawyer Seen", true);
                map.put("User Seen", true);
                map.put("Lawyer profile", lawyer_profile);
                map.put("User profile", user_profile);

                root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Confirm_Chat_Payement.this, "Lawyer added to chat list", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Failed to add lawyer to chat list, an error occured:" + task.getException());
                        }
                    }
                });

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
        AlertDialog.Builder builder = new AlertDialog.Builder(Confirm_Chat_Payement.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing,R.anim.exit2);

    }
}
