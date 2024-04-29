package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.utils.SvgRatingBar;

public class RatingFragment extends Fragment {

    SvgRatingBar ratingBar;
    ImageView closeButton;

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

        int rating = RatingFragmentArgs.fromBundle(getArguments()).getRating();
        ratingBar.setRating(rating);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUGGING", "CLOSE");
                /*Navigation
                        .findNavController(view)
                        .popBackStack();*/
                // FIXME: No funciona
            }
        });
    }
}