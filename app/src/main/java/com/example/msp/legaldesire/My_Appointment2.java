package com.example.msp.legaldesire;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class My_Appointment2 extends Fragment {
    ArrayList<String> mAppointmentDate = new ArrayList<>();
    ArrayList<String> mAppointmentTime = new ArrayList<>();
    ArrayList<String> mAppointmentType = new ArrayList<>();
    ArrayList<String> mLawyerNames = new ArrayList<>();
    ArrayList<String> mOfficeLocation = new ArrayList<>();
    String mLawyerID, mUserID, mUserName, mLawyerName;
    boolean isLawyer = false;

    ListView listView;



    public My_Appointment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my__appointment2, container, false);
        listView = (ListView) view.findViewById(R.id.list_appointments);

        Log.d("appointment123","inside Regular");
        final My_Appointment_Lawyer_Adapter my_appointment_regular_adapter = new My_Appointment_Lawyer_Adapter(getContext(), mAppointmentDate, mAppointmentTime, mAppointmentType, mLawyerNames, mOfficeLocation);
        listView.setAdapter(my_appointment_regular_adapter);
        final DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mUserID).child("Personal_Appointment");
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mAppointmentDate.add(postSnapshot.child("Appointment Date").getValue(String.class));
                    mAppointmentTime.add(postSnapshot.child("Appointment Time").getValue(String.class));
                    mAppointmentType.add(postSnapshot.child("Appointment Type").getValue(String.class));
                    mLawyerNames.add(postSnapshot.child("Lawyer Name").getValue(String.class));
                    mOfficeLocation.add(postSnapshot.child("Lawyer Office Address").getValue(String.class));
                    my_appointment_regular_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
