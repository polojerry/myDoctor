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
import com.polotechnologies.mydoctor.databinding.FragmentSignUpBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private FragmentSignUpBinding mFragmentSignUpBinding;
    private FirebaseAuth mAuth;
    private View mView;
    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFragmentSignUpBinding = FragmentSignUpBinding.inflate(inflater);

        mView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();

        mFragmentSignUpBinding.signUpLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_loginFragment);
            }
        });

        mFragmentSignUpBinding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        return mFragmentSignUpBinding.getRoot();
    }

    private void signUpUser() {
        if(isEditTextEmpty()){
            return;
        }
        if(!passwordMatch()){
            return;
        }


        String email = mFragmentSignUpBinding.tvSignUpEmail.getText().toString().trim();
        String password = mFragmentSignUpBinding.tvSignUpPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getContext(), "Signed Up Successfully", Toast.LENGTH_LONG).show();

                Navigation.findNavController(getActivity(),R.id.sign_up_button)
                        .navigate(R.id.action_signUpFragment_to_homeFragment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed... Try Again Later", Toast.LENGTH_SHORT).show();
                mFragmentSignUpBinding.tvSignUpPassword.setText("");
                mFragmentSignUpBinding.tvSignUpConfirmPassword.setText("");
                mFragmentSignUpBinding.tvSignUpEmail.requestFocus();
            }
        });

    }


    private boolean isEditTextEmpty() {
        if(mFragmentSignUpBinding.tvSignUpEmail.getText().toString().isEmpty()){
            mFragmentSignUpBinding.tvSignUpEmail.setError("Required");

            return true;
        }

        if(mFragmentSignUpBinding.tvSignUpPassword.getText().toString().isEmpty()){
            mFragmentSignUpBinding.tvSignUpPassword.setError("Required");

            return true;
        }

        if(mFragmentSignUpBinding.tvSignUpConfirmPassword.getText().toString().isEmpty()){
            mFragmentSignUpBinding.tvSignUpConfirmPassword.setError("Required");

            return true;
        }

        return false;

    }

    private boolean passwordMatch() {
        return mFragmentSignUpBinding.tvSignUpPassword.getText().toString().equals(
                mFragmentSignUpBinding.tvSignUpConfirmPassword.getText().toString()
        );

    }

}
