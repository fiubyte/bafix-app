package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.HashMap;
import java.util.Map;

public class ServiceFragment extends Fragment implements View.OnClickListener, RatingBarInterface {
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

        informationLayout = view.findViewById(R.id.information_layout);
        opinionsLayout = view.findViewById(R.id.opinions_layout);

        setupTabLayout(view);

        tabs = new HashMap<>();
        tabs.put(ServiceTab.INFORMATION, informationLayout);
        tabs.put(ServiceTab.OPINIONS, opinionsLayout);

        updateCurrentTab();

        opinionsRecylerView = view.findViewById(R.id.opinions_recylcer_view);
        setupOpinionsRecylcerView();

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        if(ServiceFragmentArgs.fromBundle(getArguments()).getServiceData() != null){
            serviceData = ServiceFragmentArgs.fromBundle(getArguments()).getServiceData();
        }

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

        ratingBar = view.findViewById(R.id.rating_bar);

        ratingBar.setRating(ServiceFragmentArgs.fromBundle(getArguments()).getRating());
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                handleOnRatingChanged(view, v);
            }
        });
    }

    private void handleOnRatingChanged(View view, float v) {
        ServiceFragmentDirections.ActionServiceFragmentToRatingFragment action =
                ServiceFragmentDirections.actionServiceFragmentToRatingFragment((int) v, serviceData);

        Navigation
                .findNavController(view)
                .navigate(action);
    }

    private void setupOpinionsRecylcerView() {
        opinionsRecylerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        ArrayList<ServiceOpinionData> opinions = new ArrayList<>();
        opinions.add(new ServiceOpinionData("Micaela Olivera", 5, "Excelente servicio! Julia es una divina. Las uñas me duraron 2 semanas enteras. Lo recomiendo!"));
        opinions.add(new ServiceOpinionData("Daniele Leandro", 4, "No tenian el color de esmalte que queria pero me encanto la atencion de Julia y el resultado fue duradero y mejor de lo que esperaba."));
        opinions.add(new ServiceOpinionData("Clara Vanier", 5, "Servicio super personalizado. Julia me entendió a la perfección lo que queria y es super atenta, además de muy bien capacitada. Trabaja con materiales de calidad."));
        opinions.add(new ServiceOpinionData("Camila Quiroga", 2, "Creo que podrian hacer mejor su trabajo. Me atendieron 1 hora mas tarde de lo acordado y para mi la puntualidad es importante."));
        opinions.add(new ServiceOpinionData("Camila Quiroga", 2, "Creo que podrian hacer mejor su trabajo. Me atendieron 1 hora mas tarde de lo acordado y para mi la puntualidad es importante."));

        ServiceOpinionsListAdapter adapter = new ServiceOpinionsListAdapter(
                requireContext(),
                opinions,
                ServiceFragmentArgs.fromBundle(getArguments()).getRating(), this
        );
        opinionsRecylerView.setAdapter(adapter);
    }

    private void setupTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
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
    }

    private void onOpinionsTabClicked() {
        Log.d("DEBUGGING", "opinions");

        currentTab = ServiceTab.OPINIONS;
        updateCurrentTab();
    }

    private void onInformationTabClicked() {
        Log.d("DEBUGGING", "information");
        currentTab = ServiceTab.INFORMATION;
        updateCurrentTab();
    }

    private void updateCurrentTab() {
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
}