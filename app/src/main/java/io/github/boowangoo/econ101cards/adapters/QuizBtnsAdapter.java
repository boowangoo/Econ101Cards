package io.github.boowangoo.econ101cards.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import io.github.boowangoo.econ101cards.R;
import io.github.boowangoo.econ101cards.data.model.AOption;
import io.github.boowangoo.econ101cards.data.model.Question;
import io.github.boowangoo.econ101cards.data.model.Settings;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class QuizBtnsAdapter extends RealmRecyclerViewAdapter<AOption, QuizBtnsAdapter.ViewHolder> {

    private Realm realm;

    private List<AOption> options;
    private List<AOption> randOptions;
    private Question question;

    private Settings settings;

    private Context context;
    private List<ViewHolder> viewHolders;

    private Button correctBtn;

    public QuizBtnsAdapter(@Nullable OrderedRealmCollection<AOption> options,
                           Question question, Settings settings, Context context) {
        super(options, true);
        this.options = options;
        this.question = question;
        this.settings = settings;
        this.context = context;

        viewHolders = new ArrayList<>();

        randOptions = new ArrayList<>(this.options);
        Collections.shuffle(randOptions);
    }

//    public QuizBtnsAdapter(Question q, Context context) {
//        this.question = q;
//        this.context = context;
//        this.viewHolders = new ArrayList<>();
//
//        realm = Realm.getDefaultInstance();
//    }

    @NonNull
    @Override
    public QuizBtnsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View v = LayoutInflater.from(context)
                .inflate(R.layout.rv_quiz_btn, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizBtnsAdapter.ViewHolder vh, int pos) {

        viewHolders.add(vh);

        final AOption option = settings.getRandomizeOptionOrder()
                ? randOptions.get(pos) : options.get(pos);

        final Button currBtn = vh.quizBtn;

        // if this button is the correct button
        final boolean isCorrect = option.getKey() == question.getAnswer();

        if (isCorrect) {
            correctBtn = currBtn;
        }

        currBtn.setText(String.format(Locale.CANADA,
                "%c) %s",
                pos + 'A',
                option.getAnswer()));

        changeQuizBtnColor(currBtn, ContextCompat.getColor(
                context, R.color.econApricot));

        currBtn.setOnClickListener(v -> {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm -> {
                evalOptionSelected(isCorrect);
                saveQuestionId();
            });
            realm.close();

            disableBtns(currBtn);
        });


    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public Button quizBtn;

        public ViewHolder(View v) {
            super(v);
            quizBtn = v.findViewById(R.id.quiz_btn);
        }
    }

    private void disableBtns(Button currBtn) {
        for (ViewHolder vh : viewHolders) {
            vh.quizBtn.setClickable(false);
            changeQuizBtnColor(vh.quizBtn, ContextCompat.getColor(context, R.color.econApricot));
        }
        changeQuizBtnColor(currBtn, ContextCompat.getColor(context, R.color.econWewak));
        changeQuizBtnColor(correctBtn, ContextCompat.getColor(context, R.color.econMagicMint));
    }

    private void changeQuizBtnColor(Button btn, int color) {
        GradientDrawable shape = (GradientDrawable) btn.getBackground();
        shape.setColor(color);

        btn.setBackground(shape);
    }

    private void evalOptionSelected(boolean isCorrect) {
        if (isCorrect) {
            question.gotCorrect();
        } else {
            question.gotIncorrect();
        }
    }

    private void saveQuestionId() {
        settings.insertIntoLatestQuestionIds(question.getQuestionId());
    }
}
