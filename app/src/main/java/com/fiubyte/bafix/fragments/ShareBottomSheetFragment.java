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

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.net.Uri;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

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
        LinearLayout whatsappButton = view.findViewById(R.id.share_whatsapp_button);
        ImageView gmailButton = view.findViewById(R.id.share_gmail_button);
        ImageView copyUrlButton = view.findViewById(R.id.share_copy_url_button);  // Este es un ImageView, asegúrate de no castearlo incorrectamente.

        nameTextView.setText(serviceName);
        urlTextView.setText(serviceUrl);
        if (getContext() != null) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .resize(600, 600)
                    .centerCrop()
                    .into(imageView);
        }

        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToWhatsApp(serviceName, serviceUrl);
            }
        });
        gmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToGmail(serviceName, serviceUrl);
            }
        });

        copyUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUrlToClipboard(serviceUrl);
            }
        });
        return view;
    }

    private void shareToWhatsApp(String serviceName, String serviceUrl) {
        String message = "Mira este servicio: " + serviceName + "\n" + serviceUrl;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "WhatsApp no está instalado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToGmail(String serviceName, String serviceUrl) {
        String subject = "Mira este servicio: " + serviceName;
        String body = "Encontré este servicio interesante: " + serviceName + "\n" + serviceUrl;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setPackage("com.google.android.gm");

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "Gmail no está instalado.", Toast.LENGTH_SHORT).show();
        }
    }
    private void copyUrlToClipboard(String url) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", url);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "URL copiada al portapapeles", Toast.LENGTH_SHORT).show();
    }


}


