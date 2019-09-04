package com.polotechnologies.mydoctor;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.dataClass.Prescription;
import com.polotechnologies.mydoctor.databinding.FragmentViewPrescriptionBinding;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPrescriptionFragment extends Fragment {

    private FragmentViewPrescriptionBinding mBinding;
    private String doctorsId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public ViewPrescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentViewPrescriptionBinding.inflate(inflater);

        inflater.inflate(R.layout.fragment_view_prescription, container, false);

        doctorsId = ViewPrescriptionFragmentArgs.fromBundle(getArguments()).getUserId();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        loadPrescription();
        return mBinding.getRoot();
    }

    private void loadPrescription() {

        final String uId = mAuth.getCurrentUser().getUid();

        DocumentReference documentReference = mFirestore
                .collection("doctorsPrescription")
                .document(uId);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Prescription prescription = documentSnapshot.toObject(Prescription.class);


                mBinding.viewPrescMedicineName.setText(prescription.getMedicineName());
                mBinding.viewPrescMedicineFrequency.setText(prescription.getMedicineFrequency());
                mBinding.viewPrescMedicineDuration.setText(prescription.getMedicineDuration());


                DocumentReference documentReference = mFirestore
                        .collection("doctorsProfile")
                        .document(doctorsId);

                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DoctorProfile doctorProfile = documentSnapshot.toObject(DoctorProfile.class);

                        mBinding.prescribedBy.setText(String.format("Prescription by:  Dr. %s", doctorProfile.getFullName()));


                    }
                });



            }
        });

    }

}
