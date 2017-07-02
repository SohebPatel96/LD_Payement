package com.example.msp.legaldesire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Lawyer_Profile extends Fragment {

    final String TAG = "profilelawyer123";
    String mEmail, mName, mCity, mContact, mUserID, mLawyerID, mUserName, mProfileUri, mType, lawyer_profile_pic, user_profile_pic;
    Uri mProfilepic;
    boolean isLawyer;
    ImageView mImageProfilePic;
    Button mAddChat, mVisitLocation, mAppointment, mSubmitReview, mViewReview;
    boolean mIfChatExist;
    TextView mNameText, mCityText, mTypeText;
    EditText mEditReview;
    RatingBar mRatingBar, mLawyerRating;
    double latitude, longitude;
    DatabaseReference mDatabase;
    long rating;
    double average_rating;
    ArrayList<String> reviewName = new ArrayList<>();
    ArrayList<String> reviewContent = new ArrayList<>();
    ArrayList<Float> reviewRating = new ArrayList<>();

    TextView name1, name2, review1, review2;
    RatingBar rating1, rating2;
    Review_Adapter review_adapter;

    public Chat_Lawyer_Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        mLawyerID = bundle.getString("Lawyer ID");
        isLawyer = bundle.getBoolean("isLawyer");
        Log.d("checkthis123", mUserID + "  :" + mLawyerID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat__lawyer__profile, container, false);
        mImageProfilePic = (ImageView) view.findViewById(R.id.lawyerprofilepic);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        mLawyerRating = (RatingBar) view.findViewById(R.id.lawyer_rating);
        mTypeText = (TextView) view.findViewById(R.id.lawyertxt_type);
        mCityText = (TextView) view.findViewById(R.id.lawyertxt_city);
        mAddChat = (Button) view.findViewById(R.id.btn_addtochat);
        mAppointment = (Button) view.findViewById(R.id.btn_hire);
        mVisitLocation = (Button) view.findViewById(R.id.btn_visit_location);
        mSubmitReview = (Button) view.findViewById(R.id.btn_submit_review);
        mRatingBar = (RatingBar) view.findViewById(R.id.rating);
        mEditReview = (EditText) view.findViewById(R.id.edit_write_review);
        mViewReview = (Button) view.findViewById(R.id.btn_all_reviews);
        name1 = (TextView) view.findViewById(R.id.txt_name);
        rating1 = (RatingBar) view.findViewById(R.id.rating2);
        review1 = (TextView) view.findViewById(R.id.txt_review);
        name2 = (TextView) view.findViewById(R.id.txt_name2);
        rating2 = (RatingBar) view.findViewById(R.id.rating3);
        review2 = (TextView) view.findViewById(R.id.txt_review2);

        name1.setVisibility(View.GONE);
        review1.setVisibility(View.GONE);
        rating1.setVisibility(View.GONE);

        name2.setVisibility(View.GONE);
        review2.setVisibility(View.GONE);
        rating2.setVisibility(View.GONE);

        mViewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Review_Lawyer.class);
                intent.putExtra("lawyer_id", mLawyerID);
                startActivity(intent);
            }
        });
        //mListView = (ListView) view.findViewById(R.id.list_reviews);
        //  review_adapter = new Review_Adapter(getContext(), reviewName, reviewContent, reviewRating);
        //  mListView.setAdapter(review_adapter);
        final DatabaseReference reviewDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mLawyerID).child("Review");
        reviewDatabase.limitToFirst(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    count = count + 1;

                    long rating = post.child("Rating").getValue(Long.class);
                    String review = post.child("Review").getValue(String.class);
                    String user_name = post.child("User Name").getValue(String.class);

                    Log.d(TAG, rating + "," + review + "," + user_name);
                    if (count == 1) {
                        if (rating != 0 || review != null || user_name != null) {
                            name1.setVisibility(View.VISIBLE);
                            review1.setVisibility(View.VISIBLE);
                            rating1.setVisibility(View.VISIBLE);
                            name1.setText(user_name);
                            review1.setText(review);
                            rating1.setRating(rating);
                        }
                    }
                    if (count == 2) {
                        if (rating != 0 || review != null || user_name != null) {
                            name2.setVisibility(View.VISIBLE);
                            review2.setVisibility(View.VISIBLE);
                            rating2.setVisibility(View.VISIBLE);
                            name2.setText(user_name);
                            review2.setText(review);
                            rating2.setRating(rating);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /* reviewDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                reviews(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                reviews(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = (long) mRatingBar.getRating();
                Log.d(TAG, "Rating:" + rating);
            }
        });
        final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
        root3.child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                mUserName = dataSnapshot.child("Name").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Permission Denied:Lawyers cannot write a review", Toast.LENGTH_SHORT).show();
                } else {
                    if (rating == 0.0f || mEditReview.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getContext(), "Select Rating and Write a Review before Submitting", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), " Review", Toast.LENGTH_SHORT).show();
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mLawyerID).child("Review").child(mUserID);
                        HashMap<String, Object> insertData = new HashMap<String, Object>();
                        insertData.put("User Name", mUserName);
                        insertData.put("Rating", rating);
                        insertData.put("Review", mEditReview.getText().toString());
                        String key = database.push().getKey();
                        database.setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Thank you for your Honest Review", Toast.LENGTH_SHORT).show();
                                mEditReview.setText("");
                                mRatingBar.setRating(0.0f);
                                if (task.isSuccessful()) {

                                    DatabaseReference reviewDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mLawyerID).child("Review");
                                    reviewDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int n = 0, totalrating = 0;
                                            for (DataSnapshot post : dataSnapshot.getChildren()) {
                                                long rat = post.child("Rating").getValue(Long.class);
                                                n = n + 1;
                                                totalrating = (int) (totalrating + rat);
                                            }
                                            average_rating = (double) totalrating / n + 0.001d;
                                            Log.d(TAG, "avg_rating =" + totalrating + "/" + n + "=" + average_rating);

                                            DatabaseReference reviewDatabase2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mLawyerID);
                                            HashMap<String, Object> insertData = new HashMap<String, Object>();
                                            insertData.put("Rating", average_rating);
                                            reviewDatabase2.updateChildren(insertData);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        });

                    }
                }
            }
        });

        mAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Lawyers cannot make appointments", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Appointment_BookLawyer appointment_bookLawyer = new Appointment_BookLawyer();
                    Bundle bundle = new Bundle();
                    bundle.putString("User ID", mUserID);
                    bundle.putString("Lawyer ID", mLawyerID);
                    appointment_bookLawyer.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, appointment_bookLawyer).commit();
                }
            }
        });

        mVisitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Lawyers cannot make appointments", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Appointment_BookLawyer2 appointment_bookLawyer2 = new Appointment_BookLawyer2();
                    Bundle bundle = new Bundle();
                    bundle.putString("User ID", mUserID);
                    bundle.putString("Lawyer ID", mLawyerID);
                    bundle.putDouble("Latitude", latitude);
                    bundle.putDouble("Longitude", longitude);
                    appointment_bookLawyer2.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, appointment_bookLawyer2).commit();
                }
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mDatabase.child(mLawyerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEmail = (String) dataSnapshot.child("Email").getValue();
                Log.d("checkthis123", mEmail);
                mName = (String) dataSnapshot.child("Name").getValue();
                Log.d("checkthis123", mName);
                mContact = (String) dataSnapshot.child("Contact").getValue();
                mProfilepic = Uri.parse(dataSnapshot.child("Profile_Pic").getValue(String.class));
                mCity = (String) dataSnapshot.child("City").getValue();
                latitude = dataSnapshot.child("Latitude").getValue(double.class);
                longitude = dataSnapshot.child("Longitude").getValue(double.class);
                String type1 = (String) dataSnapshot.child("Type 1").getValue();
                String type2 = (String) dataSnapshot.child("Type 2").getValue();
                String type3 = (String) dataSnapshot.child("Type 3").getValue();
                String type4 = (String) dataSnapshot.child("Type 4").getValue();
                String type5 = (String) dataSnapshot.child("Type 5").getValue();
                String type6 = (String) dataSnapshot.child("Type 6").getValue();
                String type7 = (String) dataSnapshot.child("Type 7").getValue();
                double rating = (Double) dataSnapshot.child("Rating").getValue();
                if (mProfilepic.toString().equals("empty")) {
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                } else {
                    Picasso.with(getContext()).load(mProfilepic).into(mImageProfilePic);
                }

                boolean isFirst = false;
                if (type1.equals("Civil")) {
                    isFirst = true;
                    Log.d(TAG, "Incivil");
                    mTypeText.setText(type1);
                }
                if (type2.equals("Criminal")) {
                    if (isFirst) {
                        mTypeText.setText(mTypeText.getText() + "," + type2);
                    } else {
                        isFirst = true;
                        mTypeText.setText(type2);

                    }
                }
                if (type3.equals("IPR")) {
                    if (isFirst) {
                        mTypeText.setText(mTypeText.getText() + "," + type3);
                    } else {
                        isFirst = true;
                        mTypeText.setText(type3);

                    }
                }
                if (type4.equals("Taxation")) {
                    if (isFirst) {
                        mTypeText.setText(mTypeText.getText() + "," + type4);
                    } else {
                        isFirst = true;
                        mTypeText.setText(type4);

                    }
                }
                if (type5.equals("Insurance")) {
                    if (isFirst) {
                        mTypeText.setText(mTypeText.getText() + "," + type5);
                    } else {
                        isFirst = true;
                        mTypeText.setText(type3);

                    }
                }
                if (type6.equals("Medical")) {
                    if (isFirst) {
                        mTypeText.setText(mTypeText.getText() + "," + type6);
                    } else {
                        isFirst = true;
                        mTypeText.setText(type6);

                    }
                }
                if (type7.equals("MotorVehicle")) {
                    if (isFirst) {
                        mTypeText.setText(mTypeText.getText() + "," + type7);
                    } else {
                        //  isFirst = true;
                        mTypeText.setText(type7);

                    }
                }
                mLawyerRating.setRating((float) rating);
                mNameText.setText(mName);
                mCityText.setText(mCity);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Permission Denied:Lawyers cannot add to Chatlist", Toast.LENGTH_SHORT).show();
                } else {
                    final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");
                    final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
                    final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
                    root2.child(mLawyerID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            lawyer_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    root3.child(mUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                            mUserName = dataSnapshot.child("Name").getValue(String.class);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    root.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String lawyer_id = postSnapshot.child("Lawyer ID").getValue(String.class);
                                String user_id = postSnapshot.child("User ID").getValue(String.class);
                                if (lawyer_id.equals(mLawyerID) && user_id.equals(mUserID)) {
                                    mIfChatExist = true;
                                    Toast.makeText(getContext(), "Lawyer already added to chat list", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!mIfChatExist) {
                                //   navigateToBaseActivity();
                                // makePayment();
                                Intent intent = new Intent(getActivity(), Confirm_Chat_Payement.class);
                                intent.putExtra("Lawyer ID", mLawyerID);
                                intent.putExtra("Lawyer Name", mName);
                                intent.putExtra("User ID", mUserID);
                                intent.putExtra("User Name", mUserName);
                                intent.putExtra("Lawyer profile", lawyer_profile_pic);
                                intent.putExtra("User profile", user_profile_pic);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

       /* mAddChat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (isLawyer) {
                                                Toast.makeText(getContext(), "Permission Denied:Lawyers cannot add to Chatlist", Toast.LENGTH_SHORT).show();
                                            } else {
                                                final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");
                                                final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
                                                final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
                                                root2.child(mLawyerID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        lawyer_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                root3.child(mUserID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        user_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                                        mUserName = dataSnapshot.child("Name").getValue(String.class);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                root.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            String lawyer_id = postSnapshot.child("Lawyer ID").getValue(String.class);
                                                            String user_id = postSnapshot.child("User ID").getValue(String.class);
                                                            if (lawyer_id.equals(mLawyerID) && user_id.equals(mUserID)) {
                                                                mIfChatExist = true;
                                                                Toast.makeText(getContext(), "Lawyer already added to chat list", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        if (!mIfChatExist) {
                                                            Log.d(TAG, "Chat conversation doesn't exist");
                                                            HashMap<String, Object> Message = new HashMap<String, Object>();
                                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                                            map.put("Lawyer ID", mLawyerID);
                                                            map.put("Lawyer Name", mName);
                                                            map.put("User ID", mUserID);
                                                            map.put("User Name", mUserName);
                                                            map.put("Message", Message);
                                                            map.put("Lawyer Seen", true);
                                                            map.put("User Seen", true);
                                                            map.put("Lawyer profile", lawyer_profile_pic);
                                                            map.put("User profile", user_profile_pic);


                                                            root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Lawyer added to chat list", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Log.d(TAG, "Failed to add lawyer to chat list, an unknown error occured");
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

        );*/
        return view;
    }

    public void reviews(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            long rating = (long) ((DataSnapshot) i.next()).getValue();
            String review = (String) ((DataSnapshot) i.next()).getValue();
            String user_name = (String) ((DataSnapshot) i.next()).getValue();
            Log.d(TAG, "name:" + user_name);
            reviewRating.add((float) rating);
            reviewContent.add(review);
            reviewName.add(user_name);
            review_adapter.notifyDataSetChanged();


        }
    }

    /*   final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");

                HashMap<String, Object> Message = new HashMap<String, Object>();
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Lawyer ID", mLawyerID);
                map.put("Lawyer Name", mName);
                map.put("User ID", mUserID);
                map.put("User Name", mUserName);
                map.put("Message", Message);
                map.put("Lawyer Seen", true);
                map.put("User Seen", true);
                map.put("Lawyer profile", lawyer_profile_pic);
                map.put("User profile", user_profile_pic);

                root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Lawyer added to chat list", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Failed to add lawyer to chat list, an error occured:" + task.getException());
                        }
                    }
                });*/


    private double getAmount() {
        Double amount = 50.0;
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
        Log.d(TAG, "got hash:" + hash);
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



  /* */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Inside onactivity result()");

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
        builder.setTitle("Payment Status");
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
