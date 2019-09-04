package com.polotechnologies.mydoctor.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.databinding.FragmentLoginBinding;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding mFragmentLoginBinding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private NavHostFragment hostFragment;

    public static boolean isDoctor;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentLoginBinding = FragmentLoginBinding.inflate(inflater);

        inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore  = FirebaseFirestore.getInstance();

        hostFragment = (NavHostFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);

        if(mAuth.getCurrentUser() !=null){

            checkUserCategory();

            if(!isDoctor){
                hostFragment.getNavController().navigate(R.id.action_loginFragment_to_homeFragment);
            }else {
                hostFragment.getNavController().navigate(R.id.action_loginFragment_to_doctorsHomeFragment);
            }

        }

        mFragmentLoginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });

        mFragmentLoginBinding.loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });
        return mFragmentLoginBinding.getRoot();
    }

    private void checkUserCategory() {

        CollectionReference documentReference = mFirestore.collection("doctorsProfile");
        String uid = mAuth.getCurrentUser().getUid();

        final Query query = documentReference.whereEqualTo("uId",uid );

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                isDoctor = !queryDocumentSnapshots.isEmpty();
            }
        });
    }

    private void loginUser() {
        if(isEditTextEmpty()){
            return;
        }
        String email = mFragmentLoginBinding.tvLoginEmail.getText().toString().trim();
        String password = mFragmentLoginBinding.tvLoginPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkUserCategory();
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                if(!isDoctor){
                    hostFragment.getNavController().navigate(R.id.action_loginFragment_to_homeFragment);
                }else {
                    hostFragment.getNavController().navigate(R.id.action_loginFragment_to_doctorsHomeFragment);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isEditTextEmpty() {
        if(mFragmentLoginBinding.tvLoginEmail.getText().toString().isEmpty()){
            mFragmentLoginBinding.tvLoginEmail.setError("Required");

            return true;
        }

        if(mFragmentLoginBinding.tvLoginPassword.getText().toString().isEmpty()){
            mFragmentLoginBinding.tvLoginPassword.setError("Required");

            return true;
        }

        return false;

    }


}
