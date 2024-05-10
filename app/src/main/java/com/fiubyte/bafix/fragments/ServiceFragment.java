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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ProviderData;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ProvidersDataGenerator;
import com.fiubyte.bafix.utils.ServicesListManager;
import com.fiubyte.bafix.utils.ServicesListManager.ServiceRateCallback;
import com.fiubyte.bafix.utils.SvgRatingBar;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

public class ServiceFragment extends Fragment implements View.OnClickListener {
    private DataViewModel dataViewModel;
    MaterialCardView providerCardView;
    ServiceData serviceData;
    ImageView backButton;
    ImageView favoriteButton;

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

        if (ServiceFragmentArgs.fromBundle(getArguments()).getServiceData() != null) {
            serviceData = ServiceFragmentArgs.fromBundle(getArguments()).getServiceData();
        }

        ImageView providerPhoto = view.findViewById(R.id.provider_picture);
        Picasso.with(this.getContext()).load(serviceData.getServicePhotoURL()).resize(600, 600).centerCrop().into(providerPhoto);

        ((TextView) view.findViewById(R.id.service_title)).setText(serviceData.getTitle());
        ((TextView) view.findViewById(R.id.service_description)).setText(serviceData.getDescription());
        ((TextView) view.findViewById(R.id.availability_days)).setText(serviceData.getAvailabilityDays());
        ((TextView) view.findViewById(R.id.availability_time)).setText(serviceData.getAvailabilityTime());
        ((TextView) view.findViewById(R.id.provider_name)).setText(serviceData.getProviderName());
        ((TextView) view.findViewById(R.id.max_distance)).setText("A " + serviceData.getMaxDistance() + " km");
        ((TextView) view.findViewById(R.id.provider_phone)).setText(serviceData.getProviderPhone());

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

        favoriteButton = view.findViewById(R.id.favorite_button);
        if (Boolean.TRUE.equals(dataViewModel.isServiceFaved().getValue())) {
            favService();
        }
        favoriteButton.setOnClickListener(favButtonView -> {
            String token = SharedPreferencesManager.getStoredToken(requireActivity());
            if (Boolean.FALSE.equals(dataViewModel.isServiceFaved().getValue())) {
                ServicesListManager.favService(token, serviceData.getServiceId(), new ServiceRateCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("DEBUGGING", "Fav service was successful");
                        favService();
                        showFavSuccess();
                    }

                    @Override
                    public void onError() {
                        Log.e("ERROR", "Fav service was unsuccessful");
                        showFavError();
                    }
                });
            } else {
                ServicesListManager.unfavService(token, serviceData.getServiceId(), new ServiceRateCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("DEBUGGING", "Unfav service was successful");
                        unfavService();
                        showUnfavSuccess();
                    }

                    @Override
                    public void onError() {
                        Log.e("ERROR", "Unfav service was unsuccessful");
                        showFavError();
                    }
                });
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

    private void favService() {
        favoriteButton.setImageResource(R.drawable.favorite);
        dataViewModel.isServiceFaved().postValue(true);
    }

    private void unfavService() {
        favoriteButton.setImageResource(R.drawable.not_favorite);
        dataViewModel.isServiceFaved().postValue(false);
    }

    private void showFavError() {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(),
                        "Ha ocurrido un error.\nPor favor, intenta de nuevo más tarde.",
                        Toast.LENGTH_SHORT)
                .show());
    }

    private void showFavSuccess() {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(),
                        "¡Agregado a favoritos con éxito!",
                        Toast.LENGTH_SHORT)
                .show());
    }

    private void showUnfavSuccess() {
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(),
                        "¡Quitado de favoritos con éxito!",
                        Toast.LENGTH_SHORT)
                .show());
    }
}