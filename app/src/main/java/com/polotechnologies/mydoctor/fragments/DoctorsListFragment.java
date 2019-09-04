package com.polotechnologies.mydoctor.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.core.ServerValues;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.AppointmentRequest;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.databinding.FragmentDoctorsBinding;
import com.polotechnologies.mydoctor.databinding.FragmentDoctorsListBinding;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorsListFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    private FragmentDoctorsListBinding mBinding;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;

    private Context mContext;
    public DoctorsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentDoctorsListBinding.inflate(inflater);

        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_doctors_list, container, false);

        String doctorCategory = DoctorsListFragmentArgs.fromBundle(getArguments()).getDoctorCategory();
        mAuth = FirebaseAuth.getInstance();

        mContext = getContext();

        mRecyclerView = mBinding.rvDoctorsList;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(false);

        NavHostFragment NavHostFragment = (NavHostFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        NavigationUI.setupWithNavController(mBinding.toolbarDoctors,NavHostFragment.getNavController());

        mFirestore = FirebaseFirestore.getInstance();
        loadDoctors(doctorCategory);

        return mBinding.getRoot();
    }

    private void loadDoctors(String doctorCategory) {

        CollectionReference documentReference = mFirestore.collection("doctorsProfile");

        Query query = documentReference.whereEqualTo("speciality",doctorCategory );

        FirestoreRecyclerOptions<DoctorProfile> options = new FirestoreRecyclerOptions.Builder<DoctorProfile>()
                .setQuery(query, DoctorProfile.class)
                .setLifecycleOwner(this)
                .build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<DoctorProfile,DoctorsListViewHolder>(options){
            @NonNull
            @Override
            public DoctorsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_doctors_list,parent,false);
                return new DoctorsListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final DoctorsListViewHolder doctorsListViewHolder, int i, @NonNull DoctorProfile doctorProfile) {
                doctorsListViewHolder.profileName.setText(doctorProfile.getFullName());
                doctorsListViewHolder.profileSpeciality.setText(doctorProfile.getSpeciality());
                doctorsListViewHolder.uId = doctorProfile.getuId();
                Picasso.get()
                        .load(doctorProfile.getImageUrl())
                        .fit()
                        .into(doctorsListViewHolder.profileImage);

                final String doctorsUid = doctorProfile.getuId();

                doctorsListViewHolder.bookAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doctorsListViewHolder.bookAppointment.setClickable(false);
                        requestAppointment(doctorsUid);
                    }
                });



            }
        };

        mRecyclerView.setAdapter(adapter);

    }

    private void requestAppointment(String doctorsUid) {

        AppointmentRequest request = new AppointmentRequest(
                mAuth.getCurrentUser().getUid(),
                doctorsUid,
                Timestamp.now().toDate()
        );

        mFirestore.collection("appointmentRequest").document(doctorsUid).set(request).
                addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Appointment Requested... Kindly wait for feedback",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Appointment Request Failed... Kindly try again later",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    class DoctorsListViewHolder extends RecyclerView.ViewHolder{
        AppCompatImageView profileImage;
        AppCompatTextView profileName;
        AppCompatTextView profileSpeciality;
        MaterialButton bookAppointment;
        String uId;

        public DoctorsListViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.doctor_profile_image);
            profileName = itemView.findViewById(R.id.doctor_profile_name);
            profileSpeciality = itemView.findViewById(R.id.doctor_profile_speciality);
            bookAppointment = itemView.findViewById(R.id.patient_request_appointment);


        }
    }

}
