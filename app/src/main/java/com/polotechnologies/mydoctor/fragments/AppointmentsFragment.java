package com.polotechnologies.mydoctor.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.polotechnologies.mydoctor.DocAppointmentsFragment;
import com.polotechnologies.mydoctor.DoctorsHomeFragmentDirections;
import com.polotechnologies.mydoctor.HomeFragmentDirections;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.Appointments;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.dataClass.PatientProfile;
import com.polotechnologies.mydoctor.databinding.FragmentAppointmentsBinding;
import com.polotechnologies.mydoctor.databinding.FragmentDocAppointmentsBinding;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsFragment extends Fragment {

    private FirebaseFirestore mFirestore;
    private FragmentAppointmentsBinding mBinding;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;

    private Context mContext;


    public AppointmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mBinding = FragmentAppointmentsBinding.inflate(inflater);

        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_appointments, container, false);

        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = mBinding.rvPatintsAppointments;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(false);
        mContext = getContext();

        mFirestore = FirebaseFirestore.getInstance();
        loadAppointments();

        return mBinding.getRoot();
    }

    private void loadAppointments() {

        CollectionReference documentReference = mFirestore.collection("appointments");

        Query query = documentReference.
                whereEqualTo("patientsId",mAuth.getCurrentUser().getUid())
                .orderBy("dateScheduled");

        FirestoreRecyclerOptions<Appointments> options = new FirestoreRecyclerOptions.Builder<Appointments>()
                .setQuery(query, Appointments.class)
                .setLifecycleOwner(this)
                .build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Appointments, AppointmentsFragment.PatientsAppointmentViewHolder>(options){
            @NonNull
            @Override
            public AppointmentsFragment.PatientsAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_patient_appointments,parent,false);
                return new AppointmentsFragment.PatientsAppointmentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final AppointmentsFragment.PatientsAppointmentViewHolder holder,
                                            int i, @NonNull final Appointments appointments) {

                final String uId = appointments.getDoctorsId();

                DocumentReference documentReference = mFirestore
                        .collection("doctorsProfile")
                        .document(uId);

                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final DoctorProfile doctorProfile = documentSnapshot.toObject(DoctorProfile.class);

                        Picasso.get()
                                .load(doctorProfile.getImageUrl())
                                .into(holder.profileImage);

                        holder.profileName.setText(doctorProfile.getFullName());
                        holder.profileSpeciality.setText(doctorProfile.getSpeciality());
                        holder.profileSchedule.setText(appointments.getDateScheduled());

                        holder.messageDoctor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                messageDoctor(doctorProfile.getuId());

                            }
                        });

                        holder.prescribedMedicine.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPrescribedMedicine(doctorProfile.getuId());
                            }
                        });


                    }
                });


            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    private void showPrescribedMedicine(String getuId) {

        NavHostFragment fragment = (NavHostFragment)getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);

        HomeFragmentDirections.ActionHomeFragmentToViewPrescriptionFragment action =
                HomeFragmentDirections.actionHomeFragmentToViewPrescriptionFragment(getuId);


        fragment.getNavController().navigate(action);
    }

    private void messageDoctor(String uId) {
        NavHostFragment fragment = (NavHostFragment)getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);

        HomeFragmentDirections.ActionHomeFragmentToMessageFragment action =
                HomeFragmentDirections.actionHomeFragmentToMessageFragment(uId);


        fragment.getNavController().navigate(action);
    }

    class PatientsAppointmentViewHolder extends RecyclerView.ViewHolder{
        AppCompatImageView profileImage;
        AppCompatTextView profileName;
        AppCompatTextView profileSpeciality;

        AppCompatTextView profileSchedule;
        MaterialButton prescribedMedicine;
        MaterialButton messageDoctor;

        public PatientsAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.patient_appointment_request_profile_image);
            profileName = itemView.findViewById(R.id.doctors_appoint_profile_name);
            profileSpeciality = itemView.findViewById(R.id.doctors_appoint_speciality);

            profileSchedule = itemView.findViewById(R.id.doctor_date_scheduled);
            prescribedMedicine = itemView.findViewById(R.id.view_prescribed_medicine);
            messageDoctor = itemView.findViewById(R.id.message_doctor);

        }
    }
}
