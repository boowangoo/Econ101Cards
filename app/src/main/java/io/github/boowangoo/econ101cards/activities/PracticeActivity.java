package io.github.boowangoo.econ101cards.activities;

import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.adapters.PracticePagerAdapter;
import io.github.boowangoo.econ101cards.custom_views.pagers.NoBackViewPager;
import io.github.boowangoo.econ101cards.data.model.Question;
import io.github.boowangoo.econ101cards.data.model.Settings;
import io.github.boowangoo.econ101cards.eventbus.events.MessageEvent;
import io.github.boowangoo.econ101cards.eventbus.events.QuestionIdEvent;
import io.github.boowangoo.econ101cards.fragments.NavFragment;
import io.github.boowangoo.econ101cards.fragments.SettingsFragment;
import io.realm.Realm;
import io.realm.RealmResults;

public class PracticeActivity extends AppCompatActivity {

    private Realm realm;

    private NoBackViewPager pager;
    private PracticePagerAdapter pagerAdapter;

    private BottomNavigationViewEx practiceNav;

    private RealmResults<Question> questions;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();

        questions = realm.where(Question.class).findAll();
        settings = realm.where(Settings.class).findFirst();

        // start off by going to last question
        realm.executeTransaction(rlm -> settings.setUseCurrQuestionId(true));
        pager = findViewById(R.id.practice_pager);
        pagerAdapter = new PracticePagerAdapter(getSupportFragmentManager(), questions, settings);
        pager.setAdapter(pagerAdapter);
        goToNewPage(realm);

        practiceNav = findViewById(R.id.practice_nav);

        practiceNav.enableAnimation(false);
        practiceNav.enableShiftingMode(false);
        practiceNav.enableItemShiftingMode(false);

        setPracticeNavCheckability(false);

        practiceNav.setOnNavigationItemSelectedListener((item) -> {
            setPracticeNavCheckability(true);
            switch (item.getItemId()) {
                case R.id.nav_practice_main_menu:
                    openActivityAndClearTop(MainMenuActivity.class);
                    break;
                case R.id.nav_practice_select_question:
                    openQuestionNav(realm);
                    break;
                case R.id.nav_practice_bookmark:
                    EventBus.getDefault().post(new MessageEvent("toggleBookmark"));
                    break;
                case R.id.nav_practice_settings:
                    openSettings(realm);
                    break;
                default:
                    return false;
            }
            return true;
        });
        EventBus.getDefault().post(new MessageEvent("setBookmark"));


    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }

    private void openActivityAndClearTop(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setPracticeNavCheckability(boolean checkable) {
        practiceNav.getMenu().setGroupCheckable(0, checkable, true);
    }

    private void openSettings(Realm realm) {
        FragmentManager fm = getSupportFragmentManager();

        SettingsFragment sdFrag = SettingsFragment.newInstance();
        sdFrag.show(fm, getResources().getString(R.string.menu_settings));

        fm.executePendingTransactions();

        sdFrag.getDialog().setOnDismissListener(dialog -> {
            goToNewPage(realm);
        });

        setPracticeNavCheckability(false);
    }

    private void openQuestionNav(Realm realm) {
        FragmentManager fm = getSupportFragmentManager();

        NavFragment nFrag = new NavFragment();
        nFrag.show(fm, "nav");

        fm.executePendingTransactions();

        nFrag.getDialog().setOnDismissListener(dialog -> {
            goToNewPage(realm);
        });

        setPracticeNavCheckability(false);
    }

    public void setBookmarkIcon(boolean bookmarked) {
        if (practiceNav == null) {
            practiceNav = findViewById(R.id.practice_nav);
        }
        if (bookmarked) {
            practiceNav.getMenu().findItem(R.id.nav_practice_bookmark)
                    .setIcon(R.drawable.ic_bookmark_black_24dp);
        } else {
            practiceNav.getMenu().findItem(R.id.nav_practice_bookmark)
                    .setIcon(R.drawable.ic_bookmark_border_black_24dp);
        }
        setPracticeNavCheckability(false);
    }

    private void goToNewPage(Realm realm) {
        realm.executeTransaction(rlm -> settings.setUseCurrQuestionId(true));

        int startingPos = questions.indexOf(
                realm.where(Question.class).equalTo(
                        "questionId", settings.getCurrQuestionId()).findFirst());

        pager.postDelayed((Runnable) () -> {
            pager.setCurrentItem(startingPos);
        }, 200);
    }
}
