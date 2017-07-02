package com.example.msp.legaldesire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Appointment_BookLawyer extends Fragment {
    public final String TAG = "appointment123";
    public final long ONEMONTH = 2592000;
    public final long ONEDAY = 86400;
    String mUserID, mLawyerID, mUserName, mLawyerName, mLawyerOfficeAddress;
    CircleImageView mImageProfilePic;
    TextView mNameText;
    Button mConfirmBooking;
    DatePicker datePicker;
    NumberPicker numberPicker;

    String mAppointmentDate, mAppointmentTime;

    DatabaseReference mDatabase;

    final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
    final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");


    int mDate, mMonth, mYear;

    public Appointment_BookLawyer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        mLawyerID = bundle.getString("Lawyer ID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.appointment__book_lawyer, container, false);
        mImageProfilePic = (CircleImageView) view.findViewById(R.id.lawyerprofilepic);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        mConfirmBooking = (Button) view.findViewById(R.id.btn_confirm_booking);

        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar2.clear();
        long now = System.currentTimeMillis() + 86400000;
        datePicker.setMinDate(now);
        datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 7));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(7);
        numberPicker.setDisplayedValues(new String[]{"9 AM", "10AM", "11AM", "12 PM", "1 PM", "2 PM", "3PM", "4PM", "5PM"});

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDate = calendar.get(Calendar.DATE);

        // datePicker.setMaxDate(ONEMONTH);
        // datePicker.setMinDate(ONEDAY);

        datePicker.updateDate(mYear, mMonth, mDate);


        root2.child(mLawyerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLawyerName = dataSnapshot.child("Name").getValue(String.class);
                mLawyerOfficeAddress = dataSnapshot.child("City").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        root3.child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.child("Name").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //   makePayment();
                int date = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                Log.d(TAG, "Selected date,month,year:" + date + "," + month + " ," + year);
                mAppointmentDate = date + "/" + month + "/" + year;
                int time = numberPicker.getValue();
                mAppointmentTime = appointmentTime(time);
                Intent intent = new Intent(getActivity(), Confirm_Appointment.class);
                intent.putExtra("User ID", mUserID);
                intent.putExtra("User Name", mUserName);
                intent.putExtra("Appointment Type", "Office Appointment");
                intent.putExtra("Appointment Date", mAppointmentDate);
                intent.putExtra("Appointment Time", mAppointmentTime);
                intent.putExtra("Lawyer Office Address", mLawyerOfficeAddress);
                intent.putExtra("Lawyer ID", mLawyerID);
                intent.putExtra("Lawyer Name", mLawyerName);
                startActivity(intent);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mDatabase.child(mLawyerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Uri profilepic = Uri.parse(dataSnapshot.child("Profile_Pic").getValue(String.class));
                if (profilepic.toString().equals("empty")) {
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                } else {
                    Picasso.with(getContext()).load(profilepic).error(R.drawable.empty_profile).into(mImageProfilePic);
                }
                String name = (String) dataSnapshot.child("Name").getValue();
                mNameText.setText(name);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    public void setBooking() {
        Log.d(TAG, "Present date,month,year:" + mDate + "," + mMonth + " ," + mYear);
        int date = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        Log.d(TAG, "Selected date,month,year:" + date + "," + month + " ," + year);
        mAppointmentDate = date + "/" + month + "/" + year;
        int time = numberPicker.getValue();
        mAppointmentTime = appointmentTime(time);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Appointment");

        HashMap<String, Object> lawyerAppointments = new HashMap<String, Object>();
        lawyerAppointments.put("User ID", mUserID);
        lawyerAppointments.put("User Name", mUserName);
        lawyerAppointments.put("Appointment Type", "Office Appointment");
        lawyerAppointments.put("Appointment Date", mAppointmentDate);
        lawyerAppointments.put("Appointment Time", mAppointmentTime);
        lawyerAppointments.put("Lawyer Office Address", mLawyerOfficeAddress);

        HashMap<String, Object> userAppointments = new HashMap<String, Object>();
        userAppointments.put("Lawyer ID", mLawyerID);
        userAppointments.put("Lawyer Name", mLawyerName);
        userAppointments.put("Appointment Type", "Office Appointment");
        userAppointments.put("Appointment Date", mAppointmentDate);
        userAppointments.put("Appointment Time", mAppointmentTime);
        userAppointments.put("Lawyer Office Address", mLawyerOfficeAddress);
        root2.child(mLawyerID).child("Appointment").push().setValue(lawyerAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Booking...", Toast.LENGTH_SHORT).show();
            }
        });
        root3.child(mUserID).child("Appointment").push().setValue(userAppointments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Appointment Confirmed, Check 'My Appointments' for further details", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);

            }
        });
    }


    public String appointmentTime(int n) {
        String str;
        switch (n) {
            case 0:
                str = "9 AM";
                break;
            case 1:
                str = "10 AM";
                break;
            case 2:
                str = "11 AM";
                break;
            case 3:
                str = "12 PM";
                break;
            case 4:
                str = "1 PM";
                break;
            case 5:
                str = "2 PM";
                break;
            case 6:
                str = "3 PM";
                break;
            case 7:
                str = "4 PM";
                break;
            case 8:
                str = "5 PM";
                break;

            default:
                str = "--";
                break;
        }
        return str;

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
        Log.d("app_activity123", hash);
        paymentParam.setMerchantHash(hash);

        PayUmoneySdkInitilizer.startPaymentActivityForResult(getActivity(), paymentParam);

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Payement Status");
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
