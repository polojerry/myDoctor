package com.polotechnologies.mydoctor.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.databinding.FragmentLoginBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding mFragmentLoginBinding;
    private FirebaseAuth mAuth;
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

        /*if(mAuth.getCurrentUser()!=null){
            Navigation.findNavController(getActivity(),R.id.login_button)
                    .navigate(R.id.action_loginFragment_to_homeFragment);
        }*/

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

    private void loginUser() {
        if(isEditTextEmpty()){
            return;
        }
        String email = mFragmentLoginBinding.tvLoginEmail.getText().toString().trim();
        String password = mFragmentLoginBinding.tvLoginPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getActivity(),R.id.login_button)
                        .navigate(R.id.action_loginFragment_to_homeFragment);

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
