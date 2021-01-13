package com.lujaina.ldbeauty.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lujaina.ldbeauty.Adapters.SectionsAdapter;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.AddGalleryFragment;
import com.lujaina.ldbeauty.SP.AddInfoFragment;
import com.lujaina.ldbeauty.SelectedSalonActivity;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.Objects;


public class SelectedSalonFragment extends Fragment {


    private SPRegistrationModel section;
    ArrayList<SPRegistrationModel> sectionsList;

    private Context mContext;
    private SectionsAdapter mAdapter;
    int i;
    NavController navController;

    public SelectedSalonFragment() {
        // Required empty public constructor
    }


    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
       /* if (context instanceof MediatorInterface) {
            mMediatorCallback = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_selected_salon, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        section = (SPRegistrationModel) getActivity().getIntent().getSerializableExtra("SPRegistrationModel");
        Bundle bundle = new Bundle();


        TextView salonName = parentView.findViewById(R.id.tv_toolbar);
        ImageView salonLogo = parentView.findViewById(R.id.salonLogo);
        ImageButton back = parentView.findViewById(R.id.ib_back);
       /* BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);*/
        RecyclerView recyclerView = parentView.findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView);
        mAdapter= new SectionsAdapter(mContext);
        sectionsList = new ArrayList<>();
        addSalonSections(sectionsList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.update(sectionsList);

        if (section != null) {
            salonName.setText(section.getSalonName());
            Glide.with(mContext).load(section.getSalonImageURL()).into(salonLogo);
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
                navController.popBackStack();
            }
        });

        mAdapter.setupOnItemClickListener(new SectionsAdapter.onItemClickListener() {
            @Override
            public void afterAdapterItemClicked(int adapterPosition, SPRegistrationModel names) {
                switch (adapterPosition) {

                    case 0:
                       /* InfoFragment infoFragment = new InfoFragment();
                        infoFragment.setInfo(section);
                        mMediatorCallback.changeFragmentTo(infoFragment, InfoFragment.class.getSimpleName());
*/
                        bundle.putSerializable("info", section);
                        navController.navigate(R.id.action_selectedSalonFragment2_to_infoFragment, bundle);

                        break;

                    case 1:
                       /* CategoriesFragment categoriesFragment = new CategoriesFragment();
                        categoriesFragment.setSalonCategory(section);
                        mMediatorCallback.changeFragmentTo(categoriesFragment, CategoriesFragment.class.getSimpleName());
                       */

                        bundle.putSerializable("info", section);
                        navController.navigate(R.id.action_selectedSalonFragment2_to_categoriesFragment, bundle);
                        break;

                    case 2:
                       /* OffersFragment offers = new OffersFragment();
                        offers.setSalonOffers(section);
                        mMediatorCallback.changeFragmentTo(offers, OffersFragment.class.getSimpleName());
*/
                        bundle.putSerializable("info", section);
                        navController.navigate(R.id.action_selectedSalonFragment2_to_offersFragment, bundle);

                        break;

                    case 3:
                       /* GalleryFragment gallery = new GalleryFragment();
                        gallery.setSalonGallery(section);
                        mMediatorCallback.changeFragmentTo(gallery, GalleryFragment.class.getSimpleName());
                       */
                        bundle.putSerializable("info", section);
                        navController.navigate(R.id.action_selectedSalonFragment2_to_galleryFragment, bundle);
                       break;
                    case 4:
                        /*RatingFragment customeFeedBack = new RatingFragment();
                        customeFeedBack.setSalonFeedback(section);
                        mMediatorCallback.changeFragmentTo(customeFeedBack, RatingFragment.class.getSimpleName());
*/
                        bundle.putSerializable("info", section);
                        navController.navigate(R.id.action_selectedSalonFragment2_to_ratingFragment, bundle);
                        break;
                    case 5:
                       /* LocationFragment salonLocation = new LocationFragment();
                        salonLocation.setSalonLocation(section);
                        mMediatorCallback.changeFragmentTo(salonLocation, LocationFragment.class.getSimpleName());
*/
                        bundle.putSerializable("info", section);
                        navController.navigate(R.id.action_selectedSalonFragment2_to_locationFragment, bundle);

                        break;

                }
            }


        });
        return parentView;
    }

/*
    public void setSection(SPRegistrationModel salonsDetails) {
        section = salonsDetails;
    }
*/


    private void addSalonSections(ArrayList<SPRegistrationModel> sections) {

        SPRegistrationModel introduce;

        introduce = new SPRegistrationModel();
        introduce.setSalonSection(section.getSalonName() + " Info ");
        sections.add(introduce);


        introduce = new SPRegistrationModel();
        introduce.setSalonSection("Categories");
        sections.add(introduce);


        introduce = new SPRegistrationModel();
        introduce.setSalonSection("Offers");
        sections.add(introduce);


        introduce = new SPRegistrationModel();
        introduce.setSalonSection("Gallery");
        sections.add(introduce);


        introduce = new SPRegistrationModel();
        introduce.setSalonSection("Feedback & Rating");
        sections.add(introduce);

        introduce = new SPRegistrationModel();
        introduce.setSalonSection("Location");
        sections.add(introduce);


    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        DividerItemDecoration divider = new DividerItemDecoration(mContext, layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(divider);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}