package com.bigfatj.okpro.fragments;


import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bigfatj.okpro.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    ImageButton one, two;
    Button cop;

    public static EmergencyFragment newInstance(int position) {
        EmergencyFragment f = new EmergencyFragment();

        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);

        f.setArguments(b);
        return f;
    }

    public EmergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_emergency, container, false);
        one = (ImageButton) v.findViewById(R.id.contact_one);
        two = (ImageButton) v.findViewById(R.id.contact_two);
        cop = (Button) v.findViewById(R.id.cops);
        ViewCompat.setElevation(cop, 5);
        cop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:100"));
                startActivity(callIntent);
            }
        });
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 1);

            }
        });

        return v;
    }


}