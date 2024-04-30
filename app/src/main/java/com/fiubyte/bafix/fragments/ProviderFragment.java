package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        ((TextView)view.findViewById(R.id.provider_phone)).setText(providerData.getPhone());

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