package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import android.util.Log;
import com.fiubyte.bafix.R;
import com.squareup.picasso.Picasso;


import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ShareBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String KEY_SERVICE_NAME = "serviceName";
    private static final String KEY_SERVICE_URL = "serviceUrl";
    private static final String KEY_IMAGE_URL = "imageUrl";

    private String serviceName;
    private String serviceUrl;
    private String imageUrl;

    public static ShareBottomSheetFragment newInstance(String serviceName, String serviceUrl, String imageUrl) {
        ShareBottomSheetFragment fragment = new ShareBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SERVICE_NAME, serviceName);
        args.putString(KEY_SERVICE_URL, serviceUrl);
        args.putString(KEY_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        Log.d("ShareBottomSheetFragment", "newInstance: " + serviceName + " " + serviceUrl + " " + imageUrl);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceName = getArguments().getString(KEY_SERVICE_NAME);
            serviceUrl = getArguments().getString(KEY_SERVICE_URL);
            imageUrl = getArguments().getString(KEY_IMAGE_URL);
            Log.d("ShareBottomSheetFragment", "onCreate: " + serviceName + " " + serviceUrl + " " + imageUrl);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_bottom_sheet, container, false);
        TextView nameTextView = view.findViewById(R.id.share_service_name_title);
        TextView urlTextView = view.findViewById(R.id.share_service_url);
        ImageView imageView = view.findViewById(R.id.share_service_image);
        Log.d("ShareBottomSheetFragment", "onCreateView aqui estoy: " + serviceName + " " + serviceUrl + " " + imageUrl);


        nameTextView.setText(serviceName);
        urlTextView.setText(serviceUrl);
        if (getContext() != null) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .resize(600, 600)
                    .centerCrop()
                    .into(imageView);
        }
        return view;
    }
}


