package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.SvgRatingBar;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RatingFragment extends Fragment {

    TextView serviceTitle;
    SvgRatingBar ratingBar;
    ImageView closeButton;

    EditText comment;

    MaterialCardView publishButton;

    private OkHttpClient okHttpClient;

    private int serviceId;
    private String postServiceRateURL;

    private ServiceData serviceData;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ratingBar = view.findViewById(R.id.rating_bar);
        closeButton = view.findViewById(R.id.close_button);
        serviceTitle = view.findViewById(R.id.service_title);
        comment = view.findViewById(R.id.comment);

        int rating = RatingFragmentArgs.fromBundle(getArguments()).getRating();
        ratingBar.setRating(rating);

        serviceData = RatingFragmentArgs.fromBundle(getArguments()).getServiceData();
        serviceId = serviceData.getServiceId();
        serviceTitle.setText(RatingFragmentArgs.fromBundle(getArguments()).getServiceData().getTitle());

        postServiceRateURL = "https://bafix-api.onrender.com/services/" + serviceId + "/rate";

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                RatingFragmentDirections.ActionRatingFragmentToServiceFragment action =
                        RatingFragmentDirections.actionRatingFragmentToServiceFragment(0, serviceData);
                navController.navigate(action);
            }
        });

        publishButton = view.findViewById(R.id.publish_button);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOnClickPublishButton(view);
            }
        });
    }

    void handleOnClickPublishButton(View view) {
        rateService((int) ratingBar.getRating(), String.valueOf(comment.getText()), view);
    }

    public void rateService(int rate, String message, View view) {
        okHttpClient = new OkHttpClient();

        String url = "https://bafix-api.onrender.com/services/" + serviceId + "/rate";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rate", rate);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        String token = SharedPreferencesManager.getStoredToken(requireActivity());

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Log.d("DEBUGGING", request.toString());
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) {
                    return;
                }

                Log.d("DEBUGGING", response.toString());
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "¡Calificación enviada!", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(view);
                    RatingFragmentDirections.ActionRatingFragmentToServiceFragment action =
                            RatingFragmentDirections.actionRatingFragmentToServiceFragment(rate, serviceData);
                    navController.navigate(action);
                });
            }
        });
    }
}