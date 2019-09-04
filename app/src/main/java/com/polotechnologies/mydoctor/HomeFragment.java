package com.polotechnologies.mydoctor;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.polotechnologies.mydoctor.databinding.FragmentHomeBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    private NavController mNavController;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home,container,false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        setAuthListener();

        NavHostFragment navHostFragment = (NavHostFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_home_host);

        mNavController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bot_nav_home);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_home);

        toolbar.inflateMenu(R.menu.menu_home);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_log_out:
                        mAuth.signOut();
                        Toast.makeText(getContext(), "Log Out Successfully", Toast.LENGTH_SHORT).show();
                        return true;
                    case  R.id.fragmentProfile:
                        Navigation.findNavController(getActivity(),R.id.toolbar_home)
                                .navigate(R.id.action_homeFragment_to_profileFragment);
                        return true;
                    default:return false;
                }
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView,mNavController);

        return view;
    }

    private void setAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

    }

}
