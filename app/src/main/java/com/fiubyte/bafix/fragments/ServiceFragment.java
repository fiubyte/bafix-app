package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ProviderData;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ProvidersDataGenerator;
import com.fiubyte.bafix.utils.SvgRatingBar;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ServiceFragment extends Fragment implements View.OnClickListener {
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
                ServiceFragmentDirections.ActionServiceFragmentToRatingFragment action =
                        ServiceFragmentDirections.actionServiceFragmentToRatingFragment((int) v, serviceData);

                Navigation
                        .findNavController(view)
                        .navigate(action);
            }
        });

        // Encuentra el botón de compartir y establece el OnClickListener
        ImageButton shareButton = view.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareBottomSheetFragment shareBottomSheet = ShareBottomSheetFragment.newInstance(
                        serviceData.getTitle() ,
                        "https://com.fiubyte.bafix/services/" + serviceData.getServiceId(),
                        serviceData.getServicePhotoURL()
                );
                shareBottomSheet.show(getParentFragmentManager(), "shareBottomSheet");
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
}