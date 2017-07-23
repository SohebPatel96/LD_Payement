package com.example.msp.legaldesire.MyCase;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.msp.legaldesire.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCases extends Fragment {
    String mUserID;
    boolean isLawyer = false;

    ArrayList<String> key = new ArrayList();
    ArrayList<String> court_name = new ArrayList();
    ArrayList<String> place = new ArrayList();
    ArrayList<String> case_name = new ArrayList();
    ArrayList<String> case_date1 = new ArrayList();
    ArrayList<String> case_date2 = new ArrayList();
    ArrayList<String> case_date3 = new ArrayList();
    MyCasesAdapter myCasesAdapter;

    ListView listView;

    DatabaseReference db;
    //= FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mUserID).child("Appointment");


    Button mAdd;


    public MyCases() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");

        if (isLawyer) {
            db = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mUserID).child("Case");
        } else {
            db = FirebaseDatabase.getInstance().getReference().child("User").child("Regular").child(mUserID).child("Case");

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.my_cases, container, false);
        mAdd = (Button) view.findViewById(R.id.btn_addcase);

        listView = (ListView) view.findViewById(R.id.list_cases);
        myCasesAdapter = new MyCasesAdapter(getContext(), mUserID, isLawyer, court_name,
                place, case_date1, case_date2, case_date3, case_name, key);
        listView.setAdapter(myCasesAdapter);

        // populateList();


        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCase.class);
                intent.putExtra("User ID", mUserID);
                intent.putExtra("isLawyer", isLawyer);
                startActivity(intent);
            }
        });

        return view;
    }

    public void populateList() {

        key.clear();
        case_name.clear();
        court_name.clear();
        place.clear();
        case_date1.clear();
        case_date2.clear();
        case_date3.clear();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    String _key = post.getKey();
                    String _case_name = post.child("Case Name").getValue(String.class);
                    String _court_name = post.child("Court Name").getValue(String.class);
                    String _place = post.child("Place").getValue(String.class);
                    String _date_1 = post.child("Date 1").getValue(String.class);
                    String _date_2 = post.child("Date 2").getValue(String.class);
                    String _date_3 = post.child("Date 3").getValue(String.class);
                    key.add(_key);
                    case_name.add(_case_name);
                    court_name.add(_court_name);
                    place.add(_place);
                    case_date1.add(_date_1);

                    if (_date_2 == null)
                        case_date2.add("");
                    else
                        case_date2.add(_date_2);

                    if (_date_3 == null)
                        case_date3.add("");
                    else
                        case_date3.add(_date_3);

                    myCasesAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }
}
