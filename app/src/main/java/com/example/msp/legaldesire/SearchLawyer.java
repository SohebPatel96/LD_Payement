package com.example.msp.legaldesire;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchLawyer extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "searchlawyer123";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    GoogleMap mGoogleMap;

    DatabaseReference mDatabase;

    private TextView textName;
    private TextView textCase;
    private RatingBar ratingBar;
    private TextView textRating;
    private CircleImageView profieImage;
    private TextView textLocation;
    private View searchPanel, profile_details;
    private Button viewProfile;
    ListView listView;
    Review_Adapter_Profile review_adapter;


    MapView mMapView;
    Spinner spinner1, spinner2, spinner3;
    Button mSearch, mPanel, profile_details_close;

    String mEmail, mUserID, mName;

    double mLocationLat, mLocationLng;
    boolean isNear = false, isRated = false, isType = false, isLawyer;
    boolean panelVisible = true;
    int searchByDistance;
    int searchByRating;
    String searchByType;
    ArrayList<String> review_name = new ArrayList<>();
    ArrayList<Float> review_rating = new ArrayList<>();
    ArrayList<String> review_msg = new ArrayList<>();

    Marker mMarker;


    public SearchLawyer() {
        // Required empty public constructor
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override//Connect to Google API
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override//Disconnect from Google API
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    //Check if Google play services are available
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }

    //this method will return the location mode- return 3 for high accuracy which is required to accurately find user location.
    public int getLocationMode(Context context) throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
    }

    //prompt user to enable gps if disabled
    private void enableGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable GPS->High Accuracy so we can accurately determine your current location.")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Don't Enable", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner2);

        alert.show();

    }

    //prompt user to enable high accuracy to if disabled
    private void enableHighAccuracy() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable High Accuracy to accurately compute your Location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        Log.d(TAG, "onCreate ...............................");

        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGps();
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "GPS is enabled");
            try {
                int isHighAccuracyEnabled = getLocationMode(getContext());  //will return 3 if high accuracy is enabled
                if (isHighAccuracyEnabled != 3)
                    enableHighAccuracy();
                else Log.d(TAG, "High Accuracy enabled");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "GPS is disabled");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
            Log.d(TAG, "google play services not found");
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final Bundle bundle = this.getArguments();
        mEmail = bundle.getString("Email");
        mUserID = bundle.getString("User ID");
        mName = bundle.getString("Name");
        isLawyer = bundle.getBoolean("isLawyer");
    }

    public void hidePanel() {
        mPanel.setText("Show Panel");
        searchPanel.setVisibility(View.GONE);
        panelVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.search_lawyer, container, false);

        spinner1 = (Spinner) v.findViewById(R.id.spinner1);
        spinner2 = (Spinner) v.findViewById(R.id.spinner2);
        spinner3 = (Spinner) v.findViewById(R.id.spinner3);
        mSearch = (Button) v.findViewById(R.id.btn_search);
        mPanel = (Button) v.findViewById(R.id.button_panel);
        profile_details_close = (Button) v.findViewById(R.id.btn_close);

        mPanel.setText("Hide Panel");

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        searchPanel = v.findViewById(R.id.wrap_one);
        profile_details = v.findViewById(R.id.profile_details);

        profieImage = (CircleImageView) profile_details.findViewById(R.id.profilepic);
        textName = (TextView) profile_details.findViewById(R.id.text_name);
        textCase = (TextView) profile_details.findViewById(R.id.text_case);
        ratingBar = (RatingBar) profile_details.findViewById(R.id.rating);
        textRating = (TextView) profile_details.findViewById(R.id.text_rating);
        textLocation = (TextView) profile_details.findViewById(R.id.text_loc);
        listView = (ListView) profile_details.findViewById(R.id.list_reviews);
        viewProfile=(Button) profile_details.findViewById(R.id.view_lawyer_profile);
        review_adapter = new Review_Adapter_Profile(getContext(), review_name, review_msg, review_rating);
        listView.setAdapter(review_adapter);

        //  searchPanel.setVisibility(View.GONE);

        profile_details.setVisibility(View.GONE);

        mPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (panelVisible) {
                    mPanel.setText("Show Panel");
                    searchPanel.setVisibility(View.GONE);
                    panelVisible = false;
                } else {
                    mPanel.setText("Hide Panel");
                    searchPanel.setVisibility(View.VISIBLE);
                    panelVisible = true;

                }
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToProfile();
            }
        });

        profile_details_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation animationFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
                animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        profile_details.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                profile_details.startAnimation(animationFadeOut);
            }
        });

        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);//this will call the onCallBack method, which will load the map on screen

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Handling Events
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isNear = true;
                Log.d(TAG, "search by distance");
                String str = spinner1.getSelectedItem().toString();
                Log.d(TAG, str + " selected");

                if (str.equals("5Km")) {
                    searchByDistance = 5;
                    Log.d(TAG, "search by distance=" + searchByDistance);

                } else if (str.equals("10Km")) {
                    searchByDistance = 10;
                    Log.d(TAG, "search by distance=" + searchByDistance);
                } else if (str.equals("20Km")) {
                    searchByDistance = 20;
                } else if (str.equals("50Km")) {
                    searchByDistance = 50;
                } else {
                    isNear = false;
                    Log.d(TAG, "search by distance is false");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isRated = true;
                Log.d(TAG, "search by type");
                searchByRating = i + 1;

                Log.d(TAG, "search by rating:" + searchByRating);

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isType = true;
                Log.d(TAG, "search by type");
                String str = spinner3.getSelectedItem().toString();
                searchByType = str;
                Log.d(TAG, searchByType + " selected");
                if (searchByType.equals("--------")) {
                    isType = false;
                    Log.d(TAG, "search by type disabled");
                } else {
                    Log.d(TAG, "search by enabled");
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentLocation == null) {
                    Toast.makeText(getContext(), "Unable to determine location, Enable GPS -> High Accuracy", Toast.LENGTH_SHORT).show();
                } else {
                    // hidePanel();
                    categorizeData();

                }
            }
        });
        return v;
    }


    public void categorizeData() {
        Log.d(TAG, "Inside categorizeData()");
        mGoogleMap.clear();

        if (isNear == true) {//Search only by distance
            Log.d(TAG, "search by distance only");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        boolean lverified = (boolean) postSnapshot.child("Verified").getValue();
                        Log.d(TAG, "Verified:" + lverified);
                        if (lverified) {
                            String lname = postSnapshot.child("Name").getValue(String.class);
                            String lemail = postSnapshot.child("Email").getValue(String.class);
                            String laddress = postSnapshot.child("City").getValue(String.class);
                            double llat = postSnapshot.child("Latitude").getValue(double.class);
                            double llng = postSnapshot.child("Longitude").getValue(double.class);
                            double lrating = postSnapshot.child("Rating").getValue(double.class);
                            String uid = postSnapshot.child("User ID").getValue(String.class);
                            String ltype1 = (String) postSnapshot.child("Type 1").getValue();
                            String ltype2 = (String) postSnapshot.child("Type 2").getValue();
                            String ltype3 = (String) postSnapshot.child("Type 3").getValue();
                            String ltype4 = (String) postSnapshot.child("Type 4").getValue();
                            String ltype5 = (String) postSnapshot.child("Type 5").getValue();
                            String ltype6 = (String) postSnapshot.child("Type 6").getValue();
                            String ltype7 = (String) postSnapshot.child("Type 7").getValue();
                            Location marker = new Location("Location");
                            marker.setLatitude(llat);
                            marker.setLongitude(llng);
                            double distance = mCurrentLocation.distanceTo(marker);
                            Log.d(TAG, "distance:" + distance);
                            if (lrating >= searchByRating) {
                                if (searchByType.equals(ltype1) || searchByType.equals(ltype2) || searchByType.equals(ltype3) || searchByType.equals(ltype4) ||
                                        searchByType.equals(ltype5) || searchByType.equals(ltype6) || searchByType.equals(ltype7)) {
                                    if (distance <= searchByDistance * 1000) {
                                        Log.d(TAG, "distance within " + searchByDistance + ":" + distance);
                                        mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(llat, llng))
                                                .title(lname)).setTag(uid);
                                    }
                                }
                            }
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == false) {//Search only by type
            Log.d(TAG, "search by type only");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        boolean lverified = (boolean) postSnapshot.child("Verified").getValue();
                        if (lverified) {
                            String lname = postSnapshot.child("Name").getValue(String.class);
                            String lemail = postSnapshot.child("Email").getValue(String.class);
                            String laddress = postSnapshot.child("City").getValue(String.class);
                            String ltype1 = (String) postSnapshot.child("Type 1").getValue();
                            String ltype2 = (String) postSnapshot.child("Type 2").getValue();
                            String ltype3 = (String) postSnapshot.child("Type 3").getValue();
                            String ltype4 = (String) postSnapshot.child("Type 4").getValue();
                            String ltype5 = (String) postSnapshot.child("Type 5").getValue();
                            String ltype6 = (String) postSnapshot.child("Type 6").getValue();
                            String ltype7 = (String) postSnapshot.child("Type 7").getValue();
                            double llat = postSnapshot.child("Latitude").getValue(double.class);
                            double llng = postSnapshot.child("Longitude").getValue(double.class);
                            double lrating = postSnapshot.child("Rating").getValue(double.class);
                            String uid = postSnapshot.child("User ID").getValue(String.class);
                            String lpic = postSnapshot.child("Type").getValue(String.class);

                            if (lrating >= searchByRating) {
                                if (searchByType.equals(ltype1) || searchByType.equals(ltype2) || searchByType.equals(ltype3) || searchByType.equals(ltype4) ||
                                        searchByType.equals(ltype5) || searchByType.equals(ltype6) || searchByType.equals(ltype7)) {

                                    mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(llat, llng))
                                            .title(lname)).setTag(uid);

                                }
                            }
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    String lemail, lname, uemail, uname;
    String lawyer_profile_pic, user_profile_pic;
    Bundle bundle = new Bundle();
    boolean imgLoad;

    int trans;


    public void profileShow() {
        final Animation animationFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                profile_details.setVisibility(View.VISIBLE);
                //   profile_details.setAlpha(0.0f);
                Log.d(TAG, "animation start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "animation end");
                profile_details.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        profile_details.startAnimation(animationFadeIn);

    }


    MarkerWindowAdapter markerWindowAdapter;

    String name, type1, type2, type3, type4, type5, type6, type7;

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setPadding(0, 300, 0, 0);

        markerWindowAdapter = new MarkerWindowAdapter(getContext());

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMarker = marker;
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude,
                                marker.getPosition().longitude));
                mGoogleMap.animateCamera(center);
                hidePanel();
                profileShow();


                String id = (String) marker.getTag();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(id);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                        String name = dataSnapshot.child("Name").getValue(String.class);
                        String address = dataSnapshot.child("City").getValue(String.class);
                        String type1 = (String) dataSnapshot.child("Type 1").getValue();
                        String type2 = (String) dataSnapshot.child("Type 2").getValue();
                        String type3 = (String) dataSnapshot.child("Type 3").getValue();
                        String type4 = (String) dataSnapshot.child("Type 4").getValue();
                        String type5 = (String) dataSnapshot.child("Type 5").getValue();
                        String type6 = (String) dataSnapshot.child("Type 6").getValue();
                        String type7 = (String) dataSnapshot.child("Type 7").getValue();
                        double rating = dataSnapshot.child("Rating").getValue(Double.class);


                        Log.d(TAG, "profile pic=" + pic);
                        Picasso.with(getContext()).load(pic).into(profieImage);
                        textName.setText(name);
                        textCase.setText("");
                        ratingBar.setRating((float) rating);
                        float rat = (float) (rating - 0.001);
                        String r = String.valueOf(rat);
                        textRating.setText(r);
                        textLocation.setText(address);

                        if (type1.equals("Civil")) {
                            textCase.setText(textCase.getText() + "•Civil");
                        }
                        if (type2.equals("Criminal")) {
                            textCase.setText(textCase.getText() + " •Criminal");

                        }
                        if (type3.equals("IPR")) {
                            textCase.setText(textCase.getText() + " •IPR");

                        }
                        if (type4.equals("Taxation")) {
                            textCase.setText(textCase.getText() + " •Taxation");

                        }
                        if (type5.equals("Insurance")) {
                            textCase.setText(textCase.getText() + " •Insurance");

                        }
                        if (type6.equals("Medical")) {
                            textCase.setText(textCase.getText() + " •Medical");
                        }
                        if (type7.equals("MotorVehicle")) {
                            textCase.setText(textCase.getText() + " •Motor Vehicle");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                databaseReference.child("Review").limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        review_rating.clear();
                        review_msg.clear();
                        review_name.clear();
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            long rating = post.child("Rating").getValue(Long.class);
                            String review = post.child("Review").getValue(String.class);
                            String user_name = post.child("User Name").getValue(String.class);
                            review_rating.add((float) rating);
                            review_msg.add(review);
                            review_name.add(user_name);
                            review_adapter.notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }
        });




   /*     mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                String id = (String) marker.getTag();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(id);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("Name").getValue(String.class);
                        final double rating = dataSnapshot.child("Rating").getValue(Double.class);
                        type1 = (String) dataSnapshot.child("Type 1").getValue();
                        type2 = (String) dataSnapshot.child("Type 2").getValue();
                        type3 = (String) dataSnapshot.child("Type 3").getValue();
                        type4 = (String) dataSnapshot.child("Type 4").getValue();
                        type5 = (String) dataSnapshot.child("Type 5").getValue();
                        type6 = (String) dataSnapshot.child("Type 6").getValue();
                        type7 = (String) dataSnapshot.child("Type 7").getValue();
                        String pic = dataSnapshot.child("Profile_Pic").getValue(String.class);

                        Picasso.with(getContext()).load(pic).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Drawable drawImage = new BitmapDrawable(getContext().getResources(), bitmap);
                                markerWindowAdapter.drawable = drawImage;
                                markerWindowAdapter.name = name;
                                markerWindowAdapter.type1 = type1;
                                markerWindowAdapter.type2 = type2;
                                markerWindowAdapter.type3 = type3;
                                markerWindowAdapter.type4 = type4;
                                markerWindowAdapter.type5 = type5;
                                markerWindowAdapter.type6 = type6;
                                markerWindowAdapter.type7 = type7;
                                markerWindowAdapter.rating = (float) rating;
                                try {
                                    Thread.sleep(500);
                                    marker.showInfoWindow();
                                    CameraUpdate center=
                                            CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude,
                                                    marker.getPosition().longitude));
                                    mGoogleMap.moveCamera(center);//my question is how to get this center
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                markerWindowAdapter.drawable = null;
                                markerWindowAdapter.name = name;
                                markerWindowAdapter.name = name;
                                markerWindowAdapter.type1 = type1;
                                markerWindowAdapter.type2 = type2;
                                markerWindowAdapter.type3 = type3;
                                markerWindowAdapter.type4 = type4;
                                markerWindowAdapter.type5 = type5;
                                markerWindowAdapter.type6 = type6;
                                markerWindowAdapter.type7 = type7;
                                markerWindowAdapter.rating = (float) rating;
                                try {
                                    Thread.sleep(2000);
                                    marker.showInfoWindow();
                                    CameraUpdate center=
                                            CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude,
                                                    marker.getPosition().longitude));
                                    mGoogleMap.moveCamera(center);//my question is how to get this center

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                        databaseReference.child("Review").limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                markerWindowAdapter.review_rating.clear();
                                markerWindowAdapter.review_msg.clear();
                                markerWindowAdapter.review_name.clear();
                                for (DataSnapshot post : dataSnapshot.getChildren()) {
                                    long rating = post.child("Rating").getValue(Long.class);
                                    String review = post.child("Review").getValue(String.class);
                                    String user_name = post.child("User Name").getValue(String.class);
                                    markerWindowAdapter.review_rating.add((float) rating);
                                    markerWindowAdapter.review_msg.add(review);
                                    markerWindowAdapter.review_name.add(user_name);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }
        });*/

        //  mGoogleMap.setInfoWindowAdapter(markerWindowAdapter);

      /*  mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                mMarker = marker;
                imgLoad = false;
                Log.d(TAG, "imgLoad inside getInfoWindow value:" + imgLoad);
                String id = (String) marker.getTag();
                mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String lname = dataSnapshot.child("Name").getValue(String.class);
                        String ltype = dataSnapshot.child("Type").getValue(String.class);
                        String lpic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                        Log.d(TAG, "Name:" + lname);
                        infoName.setText(lname);
                        infoType.setText(ltype);
                        Picasso.with(getContext()).load(lpic).error(R.drawable.empty_profile).into(profieImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "imgLoad inside onSuccess value:" + imgLoad);
                             //   mMarker.showInfoWindow();
                            }

                            @Override
                            public void onError() {
                                imgLoad = true;
                                Log.d(TAG, "imgLoad inside onFailure value:" + imgLoad);
                              //  mMarker.showInfoWindow();
                                profieImage.setImageResource(R.drawable.empty_profile);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });*/
        bundle.putBoolean("chat_exists", false);




        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLocationLat = mCurrentLocation.getLatitude();
        mLocationLng = mCurrentLocation.getLongitude();

        Log.d(TAG, "Location change:" + mLocationLat + "," + mLocationLng);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                } else {

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                    //
                    //     Toast.makeText(OnLoginSuccessful.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    public void moveToProfile(){
        String tag = (String) mMarker.getTag();
        Bundle myBundle = new Bundle();
        myBundle.putString("Lawyer ID", tag);
        myBundle.putString("User ID", mUserID);
        myBundle.putBoolean("isLawyer", isLawyer);

        Chat_Lawyer_Profile chat_lawyer_profile = new Chat_Lawyer_Profile();
        chat_lawyer_profile.setArguments(myBundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragment_container, chat_lawyer_profile).commit();
        fragmentTransaction.addToBackStack(null);
    }
}