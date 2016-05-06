package com.frankegan.verdant.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.frankegan.verdant.ImgurAPI;
import com.frankegan.verdant.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PinFragment extends DialogFragment {
    EditText editText;
    Button submitButton, cancelButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PinFragment.
     */
    public static PinFragment newInstance() {
        PinFragment fragment = new PinFragment();
        return fragment;
    }

    public PinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.pin_fragment, container, false);
        editText = (EditText) v.findViewById(R.id.pin_field);
        submitButton = (Button) v.findViewById(R.id.submit_pin);
        submitButton.setOnClickListener((View b) -> {
            Log.i("frankegan", "Pin Entered");
            ImgurAPI.getInstance().requestTokenWithPin(editText.getText().toString());
        });

        cancelButton = (Button) v.findViewById(R.id.cancel);
        cancelButton.setOnClickListener((View c) -> PinFragment.this.dismiss());

        return v;
    }
}
