package com.polotechnologies.mydoctor;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.dataClass.Prescription;
import com.polotechnologies.mydoctor.databinding.FragmentPrescriptionBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrescriptionFragment extends Fragment {

    private FragmentPrescriptionBinding mBinding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String patientsId;

    public PrescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = FragmentPrescriptionBinding.inflate(inflater);
        inflater.inflate(R.layout.fragment_prescription, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        patientsId = PrescriptionFragmentArgs.fromBundle(getArguments()).getUserId();

        mBinding.prescriptionPrescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prescribe();
            }
        });

        return mBinding.getRoot();
    }

    private void prescribe() {

        final String uId = mAuth.getCurrentUser().getUid();

        String medicineName = mBinding.prescMedicineName.getText().toString().trim();
        String medicineFrequency = mBinding.prescMedicineFrequency.getText().toString().trim();
        String medicineDuration = mBinding.prescMedicineDuration.getText().toString();

        Prescription prescription = new Prescription(
          patientsId,
          medicineName,
          medicineFrequency,
          medicineDuration,
          uId,
                Timestamp.now().toDate().toString()
        );

        mFirestore.collection("doctorsPrescription")
                .document(patientsId).set(prescription).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getContext(), "Prescription Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Prescription Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
