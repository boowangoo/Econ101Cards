package io.github.boowangoo.econ101cards.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.activities.PracticeActivity;
import io.github.boowangoo.econ101cards.adapters.QuizBtnsAdapter;
import io.github.boowangoo.econ101cards.data.model.AOption;
import io.github.boowangoo.econ101cards.data.model.Question;
import io.github.boowangoo.econ101cards.data.model.Settings;
import io.github.boowangoo.econ101cards.eventbus.events.MessageEvent;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;

public class PracticeFragment extends Fragment {

    private Realm realm;

    private String questionId;
    private Question question;

    private Settings settings;

    private TextView topicTv;
    private TextView questionTv;
    private ProgressBar strengthPb;
    private RecyclerView quizBtnsRv;

    private RecyclerView.Adapter quizBtnsAdapter;
    private RecyclerView.LayoutManager quizBtnsLayoutManager;

    private static final String QUESTION_ID = "id";

    public static PracticeFragment newInstance(String id) {
        PracticeFragment frag = new PracticeFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(QUESTION_ID, id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        questionId = getArguments().getString(QUESTION_ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        question = realm.where(Question.class)
                .equalTo("questionId", questionId).findFirst();

        settings = realm.where(Settings.class).findFirst();

        // for tracking
        realm.executeTransaction(rlm -> {
            settings.setCurrQuestionId(questionId);
            if (settings.getUseCurrQuestionId()) {
                settings.setUseCurrQuestionId(false);
            }
        });

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_practice, container, false);

        topicTv = rootView.findViewById(R.id.topic_tv);
        topicTv.setText(String.format(Locale.CANADA,
                "%d.%d) %s",
                question.getChapter(),
                question.getTopic().getKey(),
                question.getTopic().getName()));

        questionTv = rootView.findViewById(R.id.question_tv);
        questionTv.setText(String.format(Locale.CANADA,
                "%d) %s",
                question.getKey(),
                question.getQuestion()));

        quizBtnsLayoutManager = new LinearLayoutManager(getContext());
        quizBtnsAdapter = new QuizBtnsAdapter(question.getOptions(), question, settings, getContext());

        quizBtnsRv = rootView.findViewById(R.id.quiz_btns_rv);
        quizBtnsRv.setLayoutManager(quizBtnsLayoutManager);
        quizBtnsRv.setAdapter(quizBtnsAdapter);
        // allows scrollview to start at top
        quizBtnsRv.setFocusable(false);
        quizBtnsRv.requestFocus();

        strengthPb = rootView.findViewById(R.id.strength_pb);

        setupProgressBar();

        question.addChangeListener((RealmChangeListener<Question>) question -> setupProgressBar());

        updateBookmark(question.getBookmarked());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        realm.close();
    }

    private void setupProgressBar() {
        int maxVal = strengthPb.getMax();
        int qStrength = question.getStrength() == null ? 0 : question.getStrength();

        strengthPb.setProgress(maxVal * qStrength / 100);
    }

    @Subscribe
    public void updateBookmark(MessageEvent e) {
        boolean bookmarked = question.getBookmarked();

        if (e.msg.equals("toggleBookmark")) {
            updateBookmark(!bookmarked);
        } else if (e.msg.equals("setBookmark")) {
            updateBookmark(bookmarked);
        }
    }

    private void updateBookmark(boolean bookmarked) {
        realm.executeTransaction(
                rlm -> question.setBookmarked(bookmarked));
        ((PracticeActivity) getActivity()).setBookmarkIcon(bookmarked);
    }
}
