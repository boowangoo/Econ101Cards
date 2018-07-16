package io.github.boowangoo.econ101cards.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Random;

import io.github.boowangoo.econ101cards.data.model.Question;
import io.github.boowangoo.econ101cards.data.model.QuestionOpener;
import io.github.boowangoo.econ101cards.data.model.Settings;
import io.github.boowangoo.econ101cards.eventbus.events.MessageEvent;
import io.github.boowangoo.econ101cards.fragments.PracticeFragment;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PracticePagerAdapter extends FragmentStatePagerAdapter {

    RealmResults<Question> questions;
    Settings settings;
    QuestionOpener questionOpener;

    public PracticePagerAdapter(FragmentManager fm, RealmResults<Question> questions,
                                Settings settings) {
        super(fm);
        this.questions = questions;
        this.settings = settings;
        this.questionOpener = settings.getQuestionOpener();
    }

    @Override
    public Fragment getItem(int pos) {
        String questionId;

        if (settings.getUseCurrQuestionId()) {
            questionId = questions.get(pos).getQuestionId();

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(rlm -> settings.setUseCurrQuestionId(false));
            realm.close();
        } else if (settings.getRandomizeQuestionOrder()) {
            questionId = getRandomQuestion().getQuestionId();
        } else {
            questionId = questions.get(pos).getQuestionId();
        }
        return PracticeFragment.newInstance(questionId);
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    private Question getRandomQuestion() {
        Random rand = new Random();
        Question randomQuestion;
        boolean isNewQuestion = false;

        do {
            randomQuestion = questions.get(rand.nextInt(questions.size()));

            isNewQuestion = true;

            for(String id : settings.getLatestQuestionIds()) {
                if (randomQuestion.getQuestionId() == id) {
                    isNewQuestion = false;
                    break;
                }
            }
        } while (!isNewQuestion);

        return randomQuestion;
    }
}
