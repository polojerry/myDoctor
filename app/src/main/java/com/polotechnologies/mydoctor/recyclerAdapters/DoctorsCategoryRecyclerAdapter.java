package com.polotechnologies.mydoctor.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.polotechnologies.mydoctor.HomeFragmentDirections;
import com.polotechnologies.mydoctor.R;
import com.polotechnologies.mydoctor.dataClass.DoctorCategories;

import java.util.List;

public class DoctorsCategoryRecyclerAdapter extends RecyclerView.Adapter<DoctorsCategoryRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<DoctorCategories> mDoctorCategories;
    private Fragment mFragment;

    public DoctorsCategoryRecyclerAdapter(Context context, List<DoctorCategories> doctorCategories, Fragment fragment) {
        mContext = context;
        mDoctorCategories = doctorCategories;
        mFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_doctors_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        DoctorCategories doctorCategories = mDoctorCategories.get(position);
        holder.categoryImage.setImageDrawable(mContext.getDrawable(doctorCategories.getImageId()));
        holder.categoryName.setText(doctorCategories.getCategory());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return mDoctorCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AppCompatImageView categoryImage;
        AppCompatTextView categoryName;
        int position;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image_doctor);
            categoryName = itemView.findViewById(R.id.category_doctor_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DoctorCategories clickedCategory = mDoctorCategories.get(position);
                    String category = clickedCategory.getCategory();

                    NavHostFragment fragment = (NavHostFragment)mFragment.getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.main_nav_host_fragment);

                    HomeFragmentDirections.ActionHomeFragmentToDoctorsListFragment action =
                            HomeFragmentDirections.actionHomeFragmentToDoctorsListFragment(category);

                    fragment.getNavController().navigate(action);
                    mFragment.getActivity().getSupportFragmentManager();

                }
            });


        }
    }
}
