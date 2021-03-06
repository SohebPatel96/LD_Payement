package com.example.msp.legaldesire;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterLawyer extends AppCompatActivity {

    public final String TAG = "registerlawyer123";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    Button mSubmit;
    EditText mEmailField, mPasswordField, mNameField, mContactField, mCityField, mContact1, mContact2, mContact3, mContact4, mContact5;
    TextView mTextView;
    CheckBox mCivil, mCriminal, mIPR, mTaxation, mInsurance, mMedical, mMotorVehicle;
    String mType, mUserId, mEmail, mName, mProfilePic;
    String type1 = "-", type2 = "-", type3 = "-", type4 = "-", type5 = "-", type6 = "-", type7 = "-";

    double mLatitude = 0, mLongitude = 0;
    GoogleMap mGoogleMap;
    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_lawyer);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F17A12")));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm Appointment </font>", Html.FROM_HTML_MODE_LEGACY));

        } else {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Confirm Appointment </font>"));
        }

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mAuth = FirebaseAuth.getInstance();

        mEmailField = (EditText) findViewById(R.id.edit_email_field2);
        mPasswordField = (EditText) findViewById(R.id.edit_password_field2);
        mNameField = (EditText) findViewById(R.id.editText_name23);
        mContactField = (EditText) findViewById(R.id.editText_contactno);
        mCityField = (EditText) findViewById(R.id.editText_city2);
        mCivil = (CheckBox) findViewById(R.id.checkBox1);
        mCriminal = (CheckBox) findViewById(R.id.checkBox2);
        mIPR = (CheckBox) findViewById(R.id.checkBox3);
        mTaxation = (CheckBox) findViewById(R.id.checkBox4);
        mInsurance = (CheckBox) findViewById(R.id.checkBox5);
        mMedical = (CheckBox) findViewById(R.id.checkBox6);
        mMotorVehicle = (CheckBox) findViewById(R.id.checkBox7);
        mMapView = (MapView) findViewById(R.id.map_View2);
        mContact1 = (EditText) findViewById(R.id.edit_contactlawyer1);
        mContact2 = (EditText) findViewById(R.id.edit_contactlawyer2);
        mContact3 = (EditText) findViewById(R.id.edit_contactlawyer3);
        mContact4 = (EditText) findViewById(R.id.edit_contactlawyer4);
        mContact5 = (EditText) findViewById(R.id.edit_contactlawyer5);
        mSubmit = (Button) findViewById(R.id.btn_lawyer_submit_register2);
        mTextView = (TextView) findViewById(R.id.txt_domain2);
        mTextView.setText("@legaldesire.com");

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mGoogleMap.clear();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        mLatitude = latLng.latitude;
                        mLongitude = latLng.longitude;
                        mGoogleMap.addMarker(markerOptions);
                    }
                });
                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //User has previously accepted this permission
                    if (ActivityCompat.checkSelfPermission(getBaseContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Not in api-23, no need to prompt
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (mPasswordField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (mNameField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Name1`", Toast.LENGTH_SHORT).show();
                } else if (!mCivil.isChecked() && !mCriminal.isChecked() && !mTaxation.isChecked() && !mIPR.isChecked() && !mInsurance.isChecked() && !mMedical.isChecked() && !mMotorVehicle.isChecked()) {
                    Toast.makeText(RegisterLawyer.this, "Select Lawyer Type", Toast.LENGTH_SHORT).show();
                } else if (mContactField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Contact Number", Toast.LENGTH_SHORT).show();
                } else if (mCityField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter City", Toast.LENGTH_SHORT).show();
                } else if (mLatitude == 0 && mLongitude == 0) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Office Location", Toast.LENGTH_SHORT).show();
                } else if (mContact1.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Contact 1", Toast.LENGTH_SHORT).show();
                } else if (mContact2.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Contact 2", Toast.LENGTH_SHORT).show();
                } else if (mContact3.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Contact 3", Toast.LENGTH_SHORT).show();
                } else if (mContact4.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Contact 4", Toast.LENGTH_SHORT).show();
                } else if (mContact5.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterLawyer.this, "Enter your Contact 5", Toast.LENGTH_SHORT).show();
                } else {
                    if (mCivil.isChecked())
                        type1 = "Civil";
                    if (mCriminal.isChecked())
                        type2 = "Criminal";
                    if (mIPR.isChecked())
                        type3 = "IPR";
                    if (mTaxation.isChecked())
                        type4 = "Taxation";
                    if (mInsurance.isChecked())
                        type5 = "Insurance";
                    if (mMedical.isChecked())
                        type6 = "Medical";
                    if (mMotorVehicle.isChecked())
                        type7 = "MotorVehicle";

                    String email = mEmailField.getText().toString() + "@legaldesire.com";
                    String password = mPasswordField.getText().toString();
                    createAccount(email, password);

                }
            }
        });


    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "REgistration successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    HashMap<String, Object> insertData = new HashMap<String, Object>();
                    insertData.put("User ID", user.getUid());
                    insertData.put("Email", user.getEmail());
                    insertData.put("Name", mNameField.getText().toString().trim());
                    insertData.put("Verified", false);
                    insertData.put("Profile_Pic", "empty");
                    insertData.put("Contact", mContactField.getText().toString().trim());
                    insertData.put("City", mCityField.getText().toString().trim());
                    insertData.put("Type", mType);
                    insertData.put("Latitude", mLatitude);
                    insertData.put("Longitude", mLongitude);
                    insertData.put("Type 1", type1);
                    insertData.put("Type 2", type2);
                    insertData.put("Type 3", type3);
                    insertData.put("Type 4", type4);
                    insertData.put("Type 5", type5);
                    insertData.put("Type 6", type6);
                    insertData.put("Type 7", type7);
                    double rating = 1.0001d;
                    insertData.put("Rating", rating);
                    insertData.put("Emergency Contact 1", mContact1.getText().toString().trim());
                    insertData.put("Emergency Contact 2", mContact2.getText().toString().trim());
                    insertData.put("Emergency Contact 3", mContact3.getText().toString().trim());
                    insertData.put("Emergency Contact 4", mContact4.getText().toString().trim());
                    insertData.put("Emergency Contact 5", mContact5.getText().toString().trim());

                    mDatabase.child(user.getUid()).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterLawyer.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterLawyer.this, Login.class));
                        }
                    });
                } else {
                    Toast.makeText(RegisterLawyer.this, "Registration FAILED:" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
