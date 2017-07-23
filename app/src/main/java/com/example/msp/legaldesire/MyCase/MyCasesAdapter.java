package com.example.msp.legaldesire.MyCase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.msp.legaldesire.Chat_Room_Adapter;
import com.example.msp.legaldesire.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by MSP on 7/22/2017.
 */

public class MyCasesAdapter extends ArrayAdapter<String> {

    String mUserID;
    boolean isLawyer;
    Context context;
    LayoutInflater inflater;
    ArrayList<String> court_name, place, case_date1, case_date2, case_date3, case_name, key;


    public MyCasesAdapter(Context context, String mUserID, boolean isLawyer, ArrayList<String> court_name, ArrayList<String> place
            , ArrayList<String> case_date1, ArrayList<String> case_date2,
                          ArrayList<String> case_date3, ArrayList<String> case_name, ArrayList<String> key) {
        super(context, R.layout.my_cases_adapter, case_name);
        this.context = context;
        this.mUserID = mUserID;
        this.isLawyer = isLawyer;
        this.court_name = court_name;
        this.place = place;
        this.case_date1 = case_date1;
        this.case_date2 = case_date2;
        this.case_date3 = case_date3;
        this.case_name = case_name;
        this.key = key;
    }

    public class ViewHolder {
        TextView mCourt, mPlace, mDate1, mDate2, mDate3, mCaseName;
        Button mOption;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.my_cases_adapter, null);
        final ViewHolder holder = new ViewHolder();
        holder.mCourt = (TextView) convertView.findViewById(R.id.text_courtname);
        holder.mPlace = (TextView) convertView.findViewById(R.id.text_place);
        holder.mCaseName = (TextView) convertView.findViewById(R.id.text_casename);
        holder.mDate1 = (TextView) convertView.findViewById(R.id.text_date1);
        holder.mDate2 = (TextView) convertView.findViewById(R.id.text_date2);
        holder.mDate3 = (TextView) convertView.findViewById(R.id.text_date3);
        holder.mOption = (Button) convertView.findViewById(R.id.btn_options);

           holder.mCourt.setText("Court name : " + court_name.get(position));
           holder.mPlace.setText("Place : " + place.get(position));
        holder.mCaseName.setText(case_name.get(position));
           holder.mDate1.setText("1st hearing : " + case_date1.get(position));

        if (case_date2.get(position).trim().isEmpty())
            holder.mDate2.setVisibility(View.GONE);
        else
            holder.mDate2.setText("2nd hearing : " + case_date2.get(position));
        if (case_date3.get(position).trim().isEmpty())
            holder.mDate3.setVisibility(View.GONE);
        else
            holder.mDate3.setText("3rd hearing : " + case_date3.get(position));


        holder.mOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Log.d("checking123", "button clicked kk");
                PopupMenu popup = new PopupMenu(context, holder.mOption);
                popup.getMenuInflater()
                        .inflate(R.menu.case_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String str = (String) menuItem.getTitle();

                        if (str.equals("Edit")) {
                            Log.d("checking123", "Edit");
                        } else if (str.equals("Delete")) {
                        }
                        return true;
                    }
                });
                popup.show();*/
                options(holder, position);

            }
        });
        return convertView;
    }

    public void options(ViewHolder holder, final int position) {
        PopupMenu popup = new PopupMenu(context, holder.mOption);
        popup.getMenuInflater()
                .inflate(R.menu.case_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String str = (String) menuItem.getTitle();

                if (str.equals("Edit")) {
                    edit(mUserID, isLawyer, position);
                } else if (str.equals("Delete")) {
                    delete(isLawyer, position);
                }
                return true;
            }
        });
        popup.show();
    }

    public void delete(boolean isLawyer, final int postion) {
        DatabaseReference db;
        if (isLawyer) {
            db = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mUserID).child("Case");
        } else {
            db = FirebaseDatabase.getInstance().getReference().child("User").child("Regular").child(mUserID).child("Case");
        }
        db.child(key.get(postion)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                court_name.remove(postion);
                place.remove(postion);
                case_date1.remove(postion);
                case_date2.remove(postion);
                case_date3.remove(postion);
                case_name.remove(postion);
                key.remove(postion);
                notifyDataSetChanged();


            }
        });

    }

    public void edit(String mUserID, boolean isLawyer, int position) {
        Intent intent = new Intent(getContext().getApplicationContext(), EditCase.class);
        intent.putExtra("User ID", mUserID);
        intent.putExtra("isLawyer", isLawyer);
        intent.putExtra("key", key.get(position));
        intent.putExtra("Court Name", court_name.get(position));
        intent.putExtra("Place", place.get(position));
        intent.putExtra("Date 1", case_date1.get(position));
        intent.putExtra("Date 2", case_date2.get(position));
        intent.putExtra("Date 3", case_date3.get(position));
        intent.putExtra("Case Name", case_name.get(position));
        getContext().startActivity(intent);


    }

}
