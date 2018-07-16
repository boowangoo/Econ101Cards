package io.github.boowangoo.econ101cards.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.data.model.Settings;
import io.github.boowangoo.econ101cards.eventbus.events.MessageEvent;
import io.github.boowangoo.econ101cards.eventbus.events.QuestionIdEvent;
import io.realm.Realm;

public class SettingsFragment extends DialogFragment {

    private Realm realm;
    private Settings settings;

    private Switch randQuestionOrderSw;
    private Switch randOptionOrderSw;

    public static SettingsFragment newInstance() {
        SettingsFragment frag = new SettingsFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);

        randQuestionOrderSw = rootView.findViewById(R.id.rand_question_order_sw);
        randOptionOrderSw = rootView.findViewById(R.id.rand_option_order_sw);

        realm = Realm.getDefaultInstance();
        settings = realm.where(Settings.class).findFirst();

        randQuestionOrderSw.setChecked(settings.getRandomizeQuestionOrder());
        randOptionOrderSw.setChecked(settings.getRandomizeOptionOrder());

        randQuestionOrderSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            realm.executeTransaction(realm -> {
                settings.setRandomizeQuestionOrder(isChecked);
            });
        });
        randOptionOrderSw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            realm.executeTransaction(realm -> {
                settings.setRandomizeOptionOrder(isChecked);
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
        realm.close();
    }
}
