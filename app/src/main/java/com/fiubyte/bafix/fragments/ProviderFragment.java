package com.fiubyte.bafix.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ProviderServiceListAdapter;
import com.fiubyte.bafix.entities.ProviderData;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.utils.RecylcerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProviderFragment extends Fragment implements RecylcerViewInterface {

    private RecyclerView recyclerView;
    private ProviderData providerData;

    private ImageView backButton;

    private static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        return inflater.inflate(R.layout.fragment_provider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        providerData = ProviderFragmentArgs.fromBundle(getArguments()).getProviderData();

        ((TextView)view.findViewById(R.id.provider_name)).setText(providerData.getName());
        ((TextView)view.findViewById(R.id.max_distance)).setText("A " + providerData.getDistance() + " km");

        TextView providerPhone = view.findViewById(R.id.provider_phone);
        providerPhone.setText(providerData.getPhone());
        providerPhone.setPaintFlags(providerPhone.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);

        providerPhone.setOnClickListener(v -> {
            onProviderPhoneClicked(v);
        });

        ImageView providerPhoto = view.findViewById(R.id.provider_picture);
        Picasso.with(this.getContext()).load(providerData.getProviderPhotoURL()).resize(600, 600).centerCrop().into(providerPhoto);

        recyclerView = view.findViewById(R.id.provider_services_recycler_view);
        initializeListView(providerData.getServicesOffered());

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation
                        .findNavController(view)
                        .popBackStack();
            }
        });
    }

    private void onProviderPhoneClicked(View v) {
        if (isAppInstalled(WHATSAPP_PACKAGE_NAME)) {
            String text = "¡Hola "
                        + providerData.getName()
                        + "! Me interesa uno de los servicios que ofreces en BAFIX";
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="
                                        + providerData.getPhone()
                                        + "&text="
                                        + text);
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(whatsappIntent);
        } else {
            Intent googlePlayStoreIntent = new
                    Intent(Intent.ACTION_VIEW,
                           Uri.parse("https://play.google.com/store/apps/details?id="
                                             + WHATSAPP_PACKAGE_NAME));
            startActivity(googlePlayStoreIntent);
        }
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getContext().getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    private void initializeListView(ArrayList<ServiceData> services) {

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        ProviderServiceListAdapter adapter = new ProviderServiceListAdapter(
                this, requireContext(),
                services
        );
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

        ProviderFragmentDirections.ActionProviderFragmentToServiceFragment action =
                ProviderFragmentDirections.actionProviderFragmentToServiceFragment(0, providerData.getServicesOffered().get(position));

        Navigation
                .findNavController(requireView())
                .navigate(action);
    }
}