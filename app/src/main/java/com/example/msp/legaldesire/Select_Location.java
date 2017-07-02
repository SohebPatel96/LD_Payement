package com.example.msp.legaldesire;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Select_Location extends AppCompatActivity {
    public String TAG = "selectlocation123";
    String mAppointmentDate, mAppointmentTime, mUserID, mLawyerID, mUserName, mLawyerName, mUserAddress;
    double mLatitude, mLongitude;
    GoogleMap mGoogleMap;
    MapView mMapView;
    TextView mtextView;
    Button mConfirm;
    EditText mEditText;

    Location user_location;

    double mCost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__location);
        Bundle extras = getIntent().getExtras();


        mAppointmentDate = extras.getString("Appointment date");
        mAppointmentTime = extras.getString("Appointment time");
        mLatitude = extras.getDouble("Latitude");
        mLongitude = extras.getDouble("Longitude");
        mUserID = extras.getString("User ID");
        mLawyerID = extras.getString("Lawyer ID");
        mUserName = extras.getString("User Name");
        mLawyerName = extras.getString("Lawyer Name");
        Log.d("selectlocation123", mAppointmentDate + "," + mAppointmentTime + "," + mLatitude + "," + mLongitude + "," + mUserID + "," + mLawyerID + "," + mUserName + "," + mLawyerName);

        mMapView = (MapView) findViewById(R.id.map_View2);
        mtextView = (TextView) findViewById(R.id.txt2);
        mEditText = (EditText) findViewById(R.id.edit_addressfield);
        mConfirm = (Button) findViewById(R.id.btn_confirm_booking);

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
                Location lawyer_location = new Location("Lawyer Location");
                lawyer_location.setLatitude(mLatitude);
                lawyer_location.setLongitude(mLongitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(mLatitude, mLongitude)).title("Lawyer Location");
                mGoogleMap.addMarker(markerOptions);

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mGoogleMap.clear();
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(latLng).title("My Location");
                        mGoogleMap.addMarker(markerOption);

                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(new LatLng(mLatitude, mLongitude)).title("Lawyer Location");
                        mGoogleMap.addMarker(markerOptions2);

                        Location lawyer_location = new Location("Lawyer Location");
                        lawyer_location.setLatitude(mLatitude);
                        lawyer_location.setLongitude(mLongitude);

                        user_location = new Location("User Location");
                        user_location.setLatitude(latLng.latitude);
                        user_location.setLongitude(latLng.longitude);

                        double distance = user_location.distanceTo(lawyer_location);
                        distance = distance / 1000;//distance is in KM

                        mCost = distance * 30;//30rs charge per KM

                        mtextView.setText("Distance:" + (distance + "KM\nCost:" + mCost + "₹"));


                    }
                });
            }

        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserAddress = mEditText.getText().toString();
                if (mUserAddress.trim().isEmpty()) {
                    Toast.makeText(Select_Location.this, "Enter Address", Toast.LENGTH_SHORT).show();
                } else if (user_location == null) {
                    Toast.makeText(Select_Location.this, "Enter Your Location", Toast.LENGTH_SHORT).show();
                } else {
                    //        makePayment();
                    Intent intent = new Intent(Select_Location.this, Confirm_Appointment2.class);
                    intent.putExtra("User ID", mUserID);
                    intent.putExtra("User Name", mUserName);
                    intent.putExtra("Address", mUserAddress);
                    intent.putExtra("Appointment Date", mAppointmentDate);
                    intent.putExtra("Appointment Time", mAppointmentTime);
                    intent.putExtra("Lawyer ID", mLawyerID);
                    intent.putExtra("Lawyer Name", mLawyerName);
                    intent.putExtra("Cost", mCost);
                    startActivity(intent);

                }
            }
        });

    }


}
