package com.example.msp.legaldesire;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Lawyer_Appointment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    String user_id;
    boolean isLawyer;

    public Lawyer_Appointment() {
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
        View view = inflater.inflate(R.layout.lawyer__appointment, null);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(new CustomAdapterPager(getActivity().getSupportFragmentManager(), getActivity().getApplicationContext()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        return view;
    }

    private class CustomAdapterPager extends FragmentStatePagerAdapter {
        private String[] fragments = {"Professional Appointments", "Personal Appointments"};

        public CustomAdapterPager(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
            bundle.putString("User ID", user_id);
            bundle.putBoolean("isLawyer", isLawyer);
            switch (position) {
                case 0:
                    My_Appointment my_appointment = new My_Appointment();
                    my_appointment.setArguments(bundle);
                    return my_appointment;

                case 1:
                    My_Appointment2 my_appointment2 = new My_Appointment2();
                    my_appointment2.setArguments(bundle);
                    return my_appointment2;
                default:
                    My_Appointment my_appointment3 = new My_Appointment();
                    my_appointment3.setArguments(bundle);
                    return my_appointment3;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }

}
