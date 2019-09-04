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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.polotechnologies.mydoctor.DoctorsHomeFragmentDirections;
import com.polotechnologies.mydoctor.HomeFragmentDirections;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.Chat;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.dataClass.PatientProfile;
import com.polotechnologies.mydoctor.databinding.FragmentChatsBinding;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private FragmentChatsBinding mBinding;
    private RecyclerView mRecyclerView;

    private DatabaseReference mDatabaseReference;
    private FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    private Context mContext;
    
    private Boolean isDoctor = false;
    private String currentUserId;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentChatsBinding.inflate(inflater);

        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_chats, container, false);
        mRecyclerView = mBinding.rvSessions;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();
        isDoctor = LoginFragment.isDoctor;

        mContext = getContext();

        mBinding.rvSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        
        
        loadChats();

        return mBinding.getRoot();
    }

    private void loadChats() {
        Query chatsQuery = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(currentUserId)
                .orderByChild("timestamp");

        FirebaseRecyclerOptions<Chat> options  = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatsQuery, Chat.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter  adapter = new FirebaseRecyclerAdapter<Chat, SessionAppointmentViewHolder>(options) {

            @NonNull
            @Override
            public SessionAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat,parent,false);
                return new SessionAppointmentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final SessionAppointmentViewHolder sessionAppointmentViewHolder, int i, @NonNull final Chat chat) {

                final String chatUid = getRef(i).getKey();

                sessionAppointmentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMessages(chatUid);
                    }
                });

                Query lastMessageQuery  = FirebaseDatabase.getInstance().getReference()
                        .child("messages")
                        .child(currentUserId).child(chatUid)
                        .limitToLast(1);

                lastMessageQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /*String lastMessage= dataSnapshot.child("message").toString();*/
                        sessionAppointmentViewHolder.lastMessage.setText("Click to View Messages");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(LoginFragment.isDoctor){


                    DocumentReference documentReference = mFirestore
                            .collection("patientProfile")
                            .document(chatUid);

                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            PatientProfile patientProfile = documentSnapshot.toObject(PatientProfile.class);
                            sessionAppointmentViewHolder.profileName.setText(patientProfile.getFullName());
                            Picasso.get().load(patientProfile.getImageUrl())
                                    .into(sessionAppointmentViewHolder.profileImage);

                        }
                    });

                }else{

                    DocumentReference documentReference = mFirestore
                            .collection("doctorsProfile")
                            .document(chatUid);

                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            DoctorProfile doctorProfile = documentSnapshot.toObject(DoctorProfile.class);
                            sessionAppointmentViewHolder.profileName.setText(String.format("Dr. %s", doctorProfile.getFullName()));
                            Picasso.get()
                                    .load(doctorProfile.getImageUrl())
                                    .into(sessionAppointmentViewHolder.profileImage);


                        }
                    });

                }
            }

        };

        mBinding.rvSessions.setAdapter(adapter);
    }

    private void openMessages(String chatUid) {
        NavHostFragment fragment = (NavHostFragment)getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);

        HomeFragmentDirections.ActionHomeFragmentToMessageFragment action =
                HomeFragmentDirections.actionHomeFragmentToMessageFragment(chatUid);

        fragment.getNavController().navigate(action);
    }


    class SessionAppointmentViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        AppCompatTextView profileName;
        AppCompatTextView lastMessage;

        public SessionAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.img_profile_pic_chats);
            profileName = itemView.findViewById(R.id.tv_profile_name_chats);
            lastMessage = itemView.findViewById(R.id.tv_last_message_chat);

        }
    }

}
