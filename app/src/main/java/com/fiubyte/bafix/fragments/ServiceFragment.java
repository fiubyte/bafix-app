package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.adapters.ServiceOpinionsListAdapter;
import com.fiubyte.bafix.entities.ProviderData;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.entities.ServiceOpinionData;
import com.fiubyte.bafix.entities.ServiceTab;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ProvidersDataGenerator;
import com.fiubyte.bafix.utils.RatingBarInterface;
import com.fiubyte.bafix.utils.RecylcerViewInterface;
import com.fiubyte.bafix.utils.SvgRatingBar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceFragment extends Fragment implements View.OnClickListener, RatingBarInterface
        , Observer<ArrayList<ServiceData>> {
    private ServiceTab currentTab = ServiceTab.INFORMATION;
    private Map<ServiceTab, LinearLayout> tabs;
    private LinearLayout informationLayout;
    private LinearLayout opinionsLayout;
    private TabLayout tabLayout;
    private RecyclerView opinionsRecylerView;
    private DataViewModel dataViewModel;
    MaterialCardView providerCardView;
    ServiceData serviceData;
    ImageView backButton;
    SvgRatingBar ratingBar;
    TextView pendingApprovalText;
    TextView ratingAverageText, ratingReviewsAmount, noOpinionsText;
    RatingBar ratingAverageBar;
    LinearLayout ratingLayout;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(ServiceFragmentArgs.fromBundle(getArguments()).getServiceData() != null){
            serviceData = ServiceFragmentArgs.fromBundle(getArguments()).getServiceData();
            currentTab = ServiceFragmentArgs.fromBundle(getArguments()).getCurrentTab();
            Log.d("DEBUGGING", "currentTab: " + currentTab);
        }

        informationLayout = view.findViewById(R.id.information_layout);
        opinionsLayout = view.findViewById(R.id.opinions_layout);

        tabs = new HashMap<>();
        tabs.put(ServiceTab.INFORMATION, informationLayout);
        tabs.put(ServiceTab.OPINIONS, opinionsLayout);

        setupTabLayout(view);
        updateCurrentTab();

        opinionsRecylerView = view.findViewById(R.id.opinions_recylcer_view);
        setupOpinionsRecylcerView();

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getCurrentServices().observe(requireActivity(), this);

        ImageView providerPhoto = view.findViewById(R.id.provider_picture);
        Picasso.with(this.getContext()).load(serviceData.getServicePhotoURL()).resize(600, 600).centerCrop().into(providerPhoto);

        ((TextView)view.findViewById(R.id.service_title)).setText(serviceData.getTitle());
        ((TextView)view.findViewById(R.id.service_description)).setText(serviceData.getDescription());
        ((TextView)view.findViewById(R.id.availability_days)).setText(serviceData.getAvailabilityDays());
        ((TextView)view.findViewById(R.id.availability_time)).setText(serviceData.getAvailabilityTime());
        ((TextView)view.findViewById(R.id.provider_name)).setText(serviceData.getProviderName());
        ((TextView)view.findViewById(R.id.max_distance)).setText("A " + serviceData.getMaxDistance() + " km");
        ((TextView)view.findViewById(R.id.provider_phone)).setText(serviceData.getProviderPhone());

        providerCardView = view.findViewById(R.id.provider_container);
        providerCardView.setOnClickListener(this);

        backButton = view.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        pendingApprovalText = view.findViewById(R.id.pending_approval_text);
        ratingBar = view.findViewById(R.id.rating_bar);

        setCurrentRatingView();

        if(serviceData.getOwnRating() == null) {
            ratingBar.setRating(0);
        } else {
            ratingBar.setRating(serviceData.getOwnRating());
        }
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                handleOnRatingChanged(view, rating);
            }
        });

        setAverageRatingInfo(view);
    }

    private void setAverageRatingInfo(View view) {
        ratingAverageText = view.findViewById(R.id.rating_average_text);
        ratingReviewsAmount = view.findViewById(R.id.rating_reviews_amount);
        ratingAverageBar = view.findViewById(R.id.rating_average_bar);
        noOpinionsText = view.findViewById(R.id.no_opinions_text);
        ratingLayout = view.findViewById(R.id.rating_layout);

        if (serviceData.getRatingAverage() == null) {
            noOpinionsText.setVisibility(View.VISIBLE);
            ratingLayout.setVisibility(View.GONE);
        } else {
            noOpinionsText.setVisibility(View.GONE);
            ratingLayout.setVisibility(View.VISIBLE);
            ratingAverageText.setText(String.valueOf(serviceData.getRatingAverage()));
            ratingAverageBar.setRating(serviceData.getRatingAverage().floatValue());
            ratingReviewsAmount.setText("(" + serviceData.getOpinions().size() + ")");
        }
    }

    private void setCurrentRatingView() {
        if(ratingBar == null || pendingApprovalText == null) {
            return;
        }
        if(serviceData.getOwnRatingApproved() || serviceData.getOwnRating() == null) {
            ratingBar.setVisibility(View.VISIBLE);
            pendingApprovalText.setVisibility(View.GONE);
        } else {
            ratingBar.setVisibility(View.GONE);
            pendingApprovalText.setVisibility(View.VISIBLE);
        }
    }

    private void handleOnRatingChanged(View view, float v) {
        ServiceFragmentDirections.ActionServiceFragmentToRatingFragment action =
                ServiceFragmentDirections.actionServiceFragmentToRatingFragment((int) v, serviceData, currentTab);

        Navigation
                .findNavController(view)
                .navigate(action);
    }

    private void setupOpinionsRecylcerView() {
        opinionsRecylerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        ServiceOpinionsListAdapter adapter = new ServiceOpinionsListAdapter(
                requireContext(),
                serviceData.getOpinions(),
                serviceData.getOwnRating(), serviceData.getOwnRatingApproved(), this
        );
        opinionsRecylerView.setAdapter(adapter);
    }

    private void setupTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);

        if(serviceData.getOpinions().isEmpty()) {
            tabLayout.removeTab(tabLayout.getTabAt(ServiceTab.OPINIONS.ordinal()));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().contains("Información")) {
                    onInformationTabClicked();
                } else if (tab.getText().toString().contains("Opiniones")) {
                    onOpinionsTabClicked();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(currentTab.ordinal()).select();
    }

    private void onOpinionsTabClicked() {
        currentTab = ServiceTab.OPINIONS;
        updateCurrentTab();
    }

    private void onInformationTabClicked() {
        Log.d("DEBUGGING", "information");
        currentTab = ServiceTab.INFORMATION;
        updateCurrentTab();
    }

    private void updateCurrentTab() {
        Log.d("DEBUGGING", "currentTab: " + currentTab);

        tabs.forEach((tabType, layout) -> {
            if (tabType == currentTab) {
                layout.setVisibility(View.VISIBLE);
            } else {
                layout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        ProviderData providerData = new ProviderData(serviceData.getProviderId(),
                                                     serviceData.getProviderName(),
                                                     serviceData.getGeoPoint(),
                                                     ProvidersDataGenerator.generateProviderServicesList(dataViewModel.getCurrentServices().getValue(), serviceData.getProviderId()),
                                                     serviceData.getMaxDistance(),
                                                     serviceData.getProviderPhotoURL(),
                                                     serviceData.getProviderPhone()

        );

        ServiceFragmentDirections.ActionServiceFragmentToProviderFragment action = ServiceFragmentDirections.actionServiceFragmentToProviderFragment(providerData);

        Navigation
                .findNavController(view)
                .navigate(action);
    }

    @Override
    public void onBarClicked(int rate) {
        handleOnRatingChanged(requireView(), rate);
    }

    @Override
    public void onChanged(ArrayList<ServiceData> serviceData) {
        setCurrentRatingView();
    }
}