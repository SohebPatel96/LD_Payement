package com.example.msp.legaldesire.MyCase;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msp.legaldesire.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class EditCase extends AppCompatActivity {
    String user_id, court_name, place, case_name, date_1, date_2, date_3, key;
    boolean isLawyer = false;

    EditText mEditCourtName, mEditPlace, mEditCaseName;
    TextView mSelectedDate, mSelectedDate2, mSelectedDate3;
    Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_case);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F17A12")));
        // getSupportActionBar().setTitle(Html.fromHtml("<font color='#ff0000'>ActionBartitle </font>"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Edit case details </font>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Edit case details </font>"));
        }
        getData();

        mEditCourtName = (EditText) findViewById(R.id.edit_courtname);
        mEditPlace = (EditText) findViewById(R.id.edit_place);
        mEditCaseName = (EditText) findViewById(R.id.edit_casename);

        mSelectedDate = (TextView) findViewById(R.id.text_selectdate);
        mSelectedDate2 = (TextView) findViewById(R.id.text_selectdate2);
        mSelectedDate3 = (TextView) findViewById(R.id.text_selectdate3);

        mSubmit = (Button) findViewById(R.id.btn_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmChanges();
            }
        });

        setValue();


    }

    public void getData() {
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");
        key = bundle.getString("key");
        court_name = bundle.getString("Court Name");
        place = bundle.getString("Place");
        date_1 = bundle.getString("Date 1");
        date_2 = bundle.getString("Date 2");
        date_3 = bundle.getString("Date 3");
        case_name = bundle.getString("Case Name");
        Log.d("watching123", user_id + isLawyer + key + court_name + place);
    }

    public void setValue() {

        mEditCourtName.setText(court_name);
        mEditPlace.setText(place);
        mEditCaseName.setText(case_name);


        mSelectedDate.setText("1st date:" + date_1);

        if (date_2.trim().isEmpty())
            mSelectedDate2.setText("Select 2nd date");
        else
            mSelectedDate2.setText("2nd date:" + date_2);

        if (date_3.trim().isEmpty())
            mSelectedDate3.setText("Select 3rd date");
        else
            mSelectedDate3.setText("3rd date:" + date_3);
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
        mDatePicker = new DatePickerDialog(EditCase.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                selectedmonth = selectedmonth + 1;
                date_1 = ("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                mSelectedDate.setText("1st date:" + date_1);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();

    }

    public void showDatePickerDialog2(View v) {
        if (date_1.trim().isEmpty()) {
            Toast.makeText(getBaseContext(), "Select 1st date before entering 2nd date", Toast.LENGTH_SHORT).show();
        } else {
            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(EditCase.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                    selectedmonth = selectedmonth + 1;
                    date_2 = ("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                    mSelectedDate2.setText("2nd date:" + date_2);
                }
            }, mYear, mMonth, mDay);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
    }

    public void showDatePickerDialog3(View v) {
        if (date_1.trim().isEmpty()) {
            Toast.makeText(getBaseContext(), "Select 1st date before entering 3rd date", Toast.LENGTH_SHORT).show();
        } else if (date_2 == null || date_2.trim().isEmpty()) {
            Toast.makeText(getBaseContext(), "Select 2nd date before entering 3rd date", Toast.LENGTH_SHORT).show();

        } else {
            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(EditCase.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    selectedmonth = selectedmonth + 1;
                    date_3 = ("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                    mSelectedDate3.setText("3rd date:" + date_3);
                }
            }, mYear, mMonth, mDay);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        }
    }

    public void confirmChanges() {
        if (mEditCourtName.getText().toString().trim().isEmpty() || mEditPlace.getText().toString().trim().isEmpty() || mEditPlace.getText().toString().trim().isEmpty()
                || date_1.trim().isEmpty()) {
            Toast.makeText(EditCase.this, "Please fill all mandatory fields", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference db;
            if (isLawyer) {
                db = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(user_id).child("Case").child(key);
            } else {
                db = FirebaseDatabase.getInstance().getReference().child("User").child("Regular").child(user_id).child("Case").child(key);
            }

            HashMap<String, Object> editcase = new HashMap<String, Object>();
            editcase.put("Court Name", mEditCourtName.getText().toString());
            editcase.put("Place", mEditPlace.getText().toString());
            editcase.put("Case Name", mEditCaseName.getText().toString());
            editcase.put("Date 1", date_1);
            editcase.put("Date 2", date_2);
            editcase.put("Date 3", date_3);

            db.setValue(editcase).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditCase.this, "Edited successfully", Toast.LENGTH_SHORT).show();
                        mSubmit.setEnabled(false);
                        mSubmit.setAlpha(0.3f);
                    } else
                        Toast.makeText(EditCase.this, "Failed to edit data:" + task.getException(), Toast.LENGTH_SHORT).show();

                }

            });

        }
    }
}
