package io.github.boowangoo.econ101cards.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.data.DataService;
import io.github.boowangoo.econ101cards.data.model.QTopic;
import io.github.boowangoo.econ101cards.data.model.Question;
import io.realm.Realm;
import io.realm.RealmResults;

public class StatsActivity extends AppCompatActivity {

    private Realm realm;
    private DataService dataService;

    private TextView outputStats;
    private String output = "";

    private RealmResults<Question> questions;

    private List<List<List<Question>>> allQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
    }

    @Override
    protected void onStart() {
        super.onStart();

        realm = Realm.getDefaultInstance();

        questions = realm.where(Question.class).findAll();

        for (int ch : dataService.getChaptersList(questions)) {
            for(QTopic t : dataService.getTopicsList(questions, ch)) {
                for (Question q : dataService.getQuestionsList(questions, ch, t)) {
                    // load allQuestions
                    while (allQuestions.size() < ch) {
                        allQuestions.add(new ArrayList<>());
                    }
                    while (allQuestions.get(ch).size() < t.getKey()) {
                        allQuestions.get(ch).add(new ArrayList<>());
                    }
                    while (allQuestions.get(ch).get(t.getKey()).size() < q.getKey()) {
                        allQuestions.get(ch).get(t.getKey()).add(q);
                    }
                }
            }
        }



        outputStats = findViewById(R.id.output_stats);
        outputStats.setText(output);

    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
    }
}
