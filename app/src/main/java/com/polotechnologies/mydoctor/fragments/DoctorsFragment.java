package com.polotechnologies.mydoctor.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.DoctorCategories;
import com.polotechnologies.mydoctor.databinding.FragmentDoctorsBinding;
import com.polotechnologies.mydoctor.recyclerAdapters.DoctorsCategoryRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorsFragment extends Fragment {

    private FragmentDoctorsBinding mBinding;
    private RecyclerView mRecyclerView;

    private List<DoctorCategories> mDoctorCategoriesList;

    private DoctorsCategoryRecyclerAdapter mCategoryRecyclerAdapter;
    public DoctorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentDoctorsBinding.inflate(inflater);
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_doctors, container, false);

        mDoctorCategoriesList = new ArrayList<>();

        mRecyclerView = mBinding.rvDoctorsCategories;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);

        loadCategories();

        return mBinding.getRoot();
    }

    private void loadCategories() {

        int[] imageIds = {R.drawable.cardiologist, R.drawable.clinical_doctor, R.drawable.gynavology,
        R.drawable.oncologist};
        String[] categoryNames = {"Cardiologist", "Clinical Doctor", "Gynaecologist", "Oncologist"};

        for(int i = 0; i< imageIds.length; i++){

            DoctorCategories category = new DoctorCategories(
                    imageIds[i],
                    categoryNames[i]
            );

            mDoctorCategoriesList.add(category);
        }

        mCategoryRecyclerAdapter = new DoctorsCategoryRecyclerAdapter(getContext(),mDoctorCategoriesList,getParentFragment());
        mRecyclerView.setAdapter(mCategoryRecyclerAdapter);

    }

}
