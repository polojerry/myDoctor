package com.polotechnologies.mydoctor.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.databinding.FragmentProfileBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FragmentProfileBinding mBinding;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentProfileBinding.inflate(inflater);
        inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        NavHostFragment NavHostFragment = (NavHostFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);

        NavigationUI.setupWithNavController(mBinding.toolbarProfile,NavHostFragment.getNavController());

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mBinding.toolbarProfile.inflateMenu(R.menu.menu_profile);
        mBinding.toolbarProfile.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_save_profile){
                    saveProfile();
                    return true;
                }else{
                    return false;
                }

            }
        });

        mBinding.layoutProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return mBinding.getRoot();
    }

    private void saveProfile() {
        if(!isDetailsFilled()){
            return;
        }

        String name = mBinding.profileDisplayName.getText().toString().trim();
        String phoneNumber = mBinding.profileDisplayMobileNumber.getText().toString().trim();
        String designation = mBinding.profileDisplayDesignation.getText().toString().trim().toLowerCase();
        String speciality = mBinding.profileDisplaySpeciality.getText().toString().trim();

        if (designation.equals("doctor")){
            saveDoctorProfile(name,phoneNumber,designation,speciality);
        }else if(designation.equals("patient")){
            savePatientProfile(name,phoneNumber,designation);
        }
    }

    private void savePatientProfile(String name, String phoneNumber, String designation) {
    }

    private void saveDoctorProfile(String name, String phoneNumber, String designation, String speciality) {

        String uId = mAuth.getCurrentUser().getUid();

        DoctorProfile doctorProfile = new DoctorProfile(
                uId,
                name,
                phoneNumber,
                designation,
                speciality
        );

        mFirestore.collection("doctorsProfile")
                .document(uId).set(doctorProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getContext(), "Profile Update", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Profile Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDetailsFilled() {
        if(mBinding.profileDisplayName.getText().toString().trim().isEmpty()){
            mBinding.profileDisplayName.setError("Required");
            return false;
        }
        if(mBinding.profileDisplayMobileNumber.getText().toString().trim().isEmpty()){
            mBinding.profileDisplayMobileNumber.setError("Required");
            return false;
        }
        if(mBinding.profileDisplayDesignation.getText().toString().trim().isEmpty()){
            mBinding.profileDisplayDesignation.setError("Required");
            return false;
        }
        if(mBinding.profileDisplaySpeciality.getText().toString().trim().isEmpty()){
            mBinding.profileDisplaySpeciality.setError("Required");
            return false;
        }

        return true;

    }

}
