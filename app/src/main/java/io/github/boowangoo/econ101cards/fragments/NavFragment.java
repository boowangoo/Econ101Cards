package io.github.boowangoo.econ101cards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Set;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.data.model.Settings;
import io.realm.Realm;

public class NavFragment extends DialogFragment {

    Realm realm;


    public NavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_nav, container, false);

        EditText navInputEtv = rootView.findViewById(R.id.nav_input_etv);
        Button navSubmitBtn = rootView.findViewById(R.id.nav_submit_btn);

        realm = Realm.getDefaultInstance();

        Settings settings = realm.where(Settings.class).findFirst();

        navSubmitBtn.setOnClickListener(v -> {
            String id = navInputEtv.getText().toString();
            realm.executeTransaction(rlm -> {
                settings.setCurrQuestionId(id);
                dismiss();
            });
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
