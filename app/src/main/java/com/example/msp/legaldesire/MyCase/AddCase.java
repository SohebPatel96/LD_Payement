package com.example.msp.legaldesire.MyCase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.msp.legaldesire.R;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddCase extends AppCompatActivity {

    String mUserID;
    boolean isLawyer;
    String first_date = "", second_date, third_date;
    EditText mCourtName, mPlace, mCaseName;
    TextView mSelectDate1, mSelectedDate2, mSelectedDate3;
    Button mAddCase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_case);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F17A12")));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Add Case </font>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Add Cases </font>"));
        }

        Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");

        mCourtName = (EditText) findViewById(R.id.edit_courtname);
        mPlace = (EditText) findViewById(R.id.edit_place);
        mCaseName = (EditText) findViewById(R.id.edit_casename);
        mSelectDate1 = (TextView) findViewById(R.id.text_selectdate);
        mSelectedDate2 = (TextView) findViewById(R.id.text_selectdate2);
        mSelectedDate3 = (TextView) findViewById(R.id.text_selectdate3);
        mAddCase = (Button) findViewById(R.id.btn_addcase);

        mSelectDate1.setText("Select 1st case date *");
        mSelectedDate2.setText("Select 2nd case date");
        mSelectedDate3.setText("Select 3rd case date");

        mAddCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });


    }

    public void insertData() {
        if (mCourtName.getText().toString().isEmpty() || mPlace.getText().toString().isEmpty()
                || mCaseName.toString().isEmpty() || first_date.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill all mandatory fields", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference db;
            if (isLawyer) {
                db = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mUserID).child("Case");
            } else {
                db = FirebaseDatabase.getInstance().getReference().child("User").child("Regular").child(mUserID).child("Case");
            }

            HashMap<String, Object> addcase = new HashMap<String, Object>();
            addcase.put("Court Name", mCourtName.getText().toString());
            addcase.put("Place", mPlace.getText().toString());
            addcase.put("Case Name", mCaseName.getText().toString());
            addcase.put("Date 1", first_date);
            addcase.put("Date 2", second_date);
            addcase.put("Date 3", third_date);

            db.push().setValue(addcase).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Case Added ", Toast.LENGTH_SHORT).show();
                        mAddCase.setEnabled(false);
                        mAddCase.setAlpha(0.4f);
                    } else {
                        Toast.makeText(getBaseContext(), "Failed to add case details. Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    public void showDatePickerDialog1(View v) {
        //   DialogFragment newFragment = new TimeSelect();
        //  newFragment.show(getFragmentManager(), "timePicker");

        // TODO Auto-generated method stub
        //To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(AddCase.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                selectedmonth = selectedmonth + 1;
                first_date = ("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                mSelectDate1.setText("First date:" + first_date);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();

    }

    public void showDatePickerDialog2(View v) {
        if (first_date.trim().isEmpty()) {
            Toast.makeText(getBaseContext(), "Select 1st date before entering 2nd date", Toast.LENGTH_SHORT).show();
        } else {
            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(AddCase.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                    selectedmonth = selectedmonth + 1;
                    second_date = ("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                    mSelectedDate2.setText("Second date:" + second_date);
                }
            }, mYear, mMonth, mDay);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
    }

    public void showDatePickerDialog3(View v) {
        if (first_date.trim().isEmpty()) {
            Toast.makeText(getBaseContext(), "Select 1st date before entering 3rd date", Toast.LENGTH_SHORT).show();
        } else if (second_date == null) {
            Toast.makeText(getBaseContext(), "Select 2nd date before entering 3rd date", Toast.LENGTH_SHORT).show();

        } else {

            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(AddCase.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    selectedmonth = selectedmonth + 1;
                    third_date = ("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                    mSelectedDate3.setText("Third date:" + third_date);
                }
            }, mYear, mMonth, mDay);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
    }


}
