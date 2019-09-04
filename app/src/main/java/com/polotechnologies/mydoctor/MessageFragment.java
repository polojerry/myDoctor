package com.polotechnologies.mydoctor;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.polotechnologies.mydoctor.dataClass.DoctorProfile;
import com.polotechnologies.mydoctor.dataClass.Message;
import com.polotechnologies.mydoctor.dataClass.PatientProfile;
import com.polotechnologies.mydoctor.databinding.FragmentMessageBinding;
import com.polotechnologies.mydoctor.fragments.LoginFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    FragmentMessageBinding mBinding;
    private String userId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mDatabaseReference;

    RecyclerView mRecyclerView;

    private Context mContext;

    private final int MESSAGE_TYPE_SENT = 0;
    private final int MESSAGE_TYPE_RECEIVED = 1;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentMessageBinding.inflate(inflater);

        inflater.inflate(R.layout.fragment_message, container, false);
        userId = MessageFragmentArgs.fromBundle(getArguments()).getUserId();

        mRecyclerView = mBinding.rvMessages;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mContext = getContext();

        loadProfile(userId);
        loadChats(userId);

        mBinding.fabSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(userId);
            }
        });

        return mBinding.getRoot();
    }


    private void loadProfile(String userId) {

        if (LoginFragment.isDoctor) {

            DocumentReference documentReference = mFirestore
                    .collection("patientProfile")
                    .document(userId);

            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    PatientProfile patientProfile = documentSnapshot.toObject(PatientProfile.class);

                    Picasso.get()
                            .load(patientProfile.getImageUrl())
                            .into(mBinding.imgProfilePicMessages);

                    mBinding.tvProfileNameMessages.setText(patientProfile.getFullName());


                }
            });

        } else {


            DocumentReference documentReference = mFirestore
                    .collection("doctorsProfile")
                    .document(userId);

            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    DoctorProfile doctorProfile = documentSnapshot.toObject(DoctorProfile.class);

                    Picasso.get()
                            .load(doctorProfile.getImageUrl())
                            .into(mBinding.imgProfilePicMessages);

                    mBinding.tvProfileNameMessages.setText(String.format("Dr. %s", doctorProfile.getFullName()));

                }
            });
        }
    }

    private void loadChats(String userId) {

        final String currentUid = mAuth.getCurrentUser().getUid();

        Query query = mDatabaseReference
                .child("messages")
                .child(currentUid)
                .child(userId);


        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Message, MessageFragment.MessageViewHolder>(options) {

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == MESSAGE_TYPE_RECEIVED) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_received,
                            parent, false);

                    return new MessageViewHolder((view));
                } else {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_sent,
                            parent, false);

                    return new MessageViewHolder((view));
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                messageViewHolder.message.setText(message.getMessage());

            }

            @Override
            public int getItemViewType(int position) {
                Message message = getItem(position);

                if (message.getFrom().equals(currentUid)) {
                    return MESSAGE_TYPE_SENT;
                } else {
                    return MESSAGE_TYPE_RECEIVED;

                }
            }

        };

        mRecyclerView.setAdapter(adapter);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView message;
        AppCompatTextView sentTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.tv_message);
            sentTime = itemView.findViewById(R.id.sentTime);


        }
    }

    private void sendMessage(String userId) {
        if (mBinding.etMessageInput.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "Cant Send Empty Message",Toast.LENGTH_SHORT).show();
            return;
        }

        String message = mBinding.etMessageInput.getText().toString().trim();

        mBinding.etMessageInput.getText().clear();

        String currentUserId = mAuth.getCurrentUser().getUid();

        createChatIfNotExist(currentUserId,userId);

        String userReference = "messages/" + currentUserId + "/" + userId;
        String receiverReference = "messages/" + userId + "/" + currentUserId;

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("isSeen",false);
        messageMap.put("from", currentUserId);
        messageMap.put("timeStamp",ServerValue.TIMESTAMP);


        DatabaseReference messagePush  = mDatabaseReference
                .child("messages")
                .child(currentUserId)
                .child(userId)
                .push();

        String messagePushKey = messagePush.getKey();

        Map<String, Object> messageUsersMap = new HashMap<>();
        messageUsersMap.put(userReference + "/" + messagePushKey,messageMap);
        messageUsersMap.put(receiverReference + "/" + messagePushKey,messageMap);

        mDatabaseReference.updateChildren(messageUsersMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void createChatIfNotExist(final String currentUid, final String receiverUid) {

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(currentUid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(receiverUid)) {

                    Map<String, Object> addChatMap = new HashMap<>();
                    addChatMap.put("seen", false);
                    addChatMap.put("timeStamp", ServerValue.TIMESTAMP);

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("chats/" + currentUid + "/" + receiverUid, addChatMap);
                    userMap.put("chats/" + receiverUid + "/" + currentUid, addChatMap);

                    mDatabaseReference.updateChildren(userMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
