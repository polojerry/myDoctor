package com.polotechnologies.mydoctor.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.dataClass.PatientProfile;
import com.polotechnologies.mydoctor.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FragmentProfileBinding mBinding;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    StorageReference mStorageReference;
    private static final int PICK_IMAGE = 100;
    private Uri selectedImageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentProfileBinding.inflate(inflater);
        inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        if(!LoginFragment.isDoctor){
            mBinding.profileDisplaySpeciality.setVisibility(View.INVISIBLE);
            mBinding.layoutSpeciality.setVisibility(View.INVISIBLE);
            mBinding.iconSpeciality.setVisibility(View.INVISIBLE);
            mBinding.iconDesignation.setVisibility(View.INVISIBLE);
            mBinding.layoutDesignation.setVisibility(View.GONE);
            mBinding.profileDisplayDesignation.setVisibility(View.GONE);
        }
        NavHostFragment NavHostFragment = (NavHostFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);

        NavigationUI.setupWithNavController(mBinding.toolbarProfile,NavHostFragment.getNavController());

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

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

        mBinding.profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        loadUserDetails();
        return mBinding.getRoot();
    }

    private void loadUserDetails() {

        if(LoginFragment.isDoctor){

            final String uId = mAuth.getCurrentUser().getUid();

            DocumentReference documentReference = mFirestore
                    .collection("doctorsProfile")
                    .document(uId);

            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    DoctorProfile doctorProfile = documentSnapshot.toObject(DoctorProfile.class);

                    Picasso.get()
                            .load(doctorProfile.getImageUrl())
                            .into(mBinding.profilePicture);

                    mBinding.profileDisplayName.setText(doctorProfile.getFullName());
                    mBinding.profileDisplayMobileNumber.setText(doctorProfile.getMobileNumber());
                    mBinding.profileDisplayDesignation.setText(doctorProfile.getDesignation());
                    mBinding.profileDisplaySpeciality.setText(doctorProfile.getSpeciality());


                }
            });

        }else{

            final String uId = mAuth.getCurrentUser().getUid();

            DocumentReference documentReference = mFirestore
                    .collection("patientProfile")
                    .document(uId);

            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    PatientProfile patientProfile = documentSnapshot.toObject(PatientProfile.class);

                    Picasso.get()
                            .load(patientProfile.getImageUrl())
                            .into(mBinding.profilePicture);

                    mBinding.profileDisplayName.setText(patientProfile.getFullName());
                    mBinding.profileDisplayMobileNumber.setText(patientProfile.getMobileNumber());

                }
            });

        }
    }

    private void selectImage() {
        Intent selectImageIntent = new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectImageIntent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            if(data!=null){
                selectedImageUri =data.getData();
                Picasso.get()
                        .load(selectedImageUri)
                        .into(mBinding.profilePicture);
            }
        }
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

    private void savePatientProfile(final String name, final String phoneNumber, final String designation) {

        final String uId = mAuth.getCurrentUser().getUid();

        final StorageReference pictureRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
        pictureRef
                .putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String pictureName = pictureRef.getPath();
                                String imageUrl = uri.toString();

                                PatientProfile patientProfile = new PatientProfile(
                                        uId,
                                        imageUrl,
                                        name,
                                        phoneNumber
                                );

                                mFirestore.collection("patientProfile")
                                        .document(uId).set(patientProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        });

                    }
                });
    }

    private void saveDoctorProfile(final String name, final String phoneNumber, final String designation, final String speciality) {

        final String uId = mAuth.getCurrentUser().getUid();

        final StorageReference pictureRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
        pictureRef
                .putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String pictureName = pictureRef.getPath();
                                String imageUrl = uri.toString();

                                DoctorProfile doctorProfile = new DoctorProfile(
                                        uId,
                                        imageUrl,
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
                        });

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

        if(LoginFragment.isDoctor){
            if(mBinding.profileDisplaySpeciality.getText().toString().trim().isEmpty()){
                mBinding.profileDisplaySpeciality.setError("Required");
                return false;
            }
        }


        return true;

    }

}
