package com.example.msp.legaldesire;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class Refund extends Fragment {
    String user_id;
    boolean isLawyer;
    Button btn_eligible;

    public Refund() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        user_id = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.refund, container, false);
        btn_eligible = (Button) view.findViewById(R.id.btn_eligible);
        btn_eligible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Refund_Request.class);
                intent.putExtra("User ID", user_id);
                intent.putExtra("isLawyer", isLawyer);
                startActivity(intent);
            }
        });
        return view;
    }

}
