package pl.edu.wat.wcy.dsk.votingappmobile.showresult;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.edu.wat.wcy.dsk.votingappmobile.Answer;
import pl.edu.wat.wcy.dsk.votingappmobile.R;
import pl.edu.wat.wcy.dsk.votingappmobile.Survey;

public class ShowResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Intent intent = getIntent();
        Survey mSurvey = (Survey) intent.getSerializableExtra("survey");

        LinearLayout answerLayout = (LinearLayout) findViewById(R.id.answer_layout);
        TextView tv = new TextView(this);
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setText(mSurvey.getQuestion());
        answerLayout.addView(tv);

        boolean best = true;
        Answer oldAnswer = null;
        for (Answer a : mSurvey.getAnswers()) {
            tv = new TextView(this);
            if (best) {
                tv.setTextColor(ContextCompat.getColor(this, (R.color.bootstrapSuccess)));
                tv.setTypeface(null, Typeface.BOLD);
                if (oldAnswer != null && oldAnswer.getPercent() == a.getPercent()) {
                    best = false;
                }
                oldAnswer = a;
            } else {
                tv.setTextColor(ContextCompat.getColor(this, (R.color.bootstrapInfo)));
            }
            tv.setText(getString(R.string.answer_result, a.getAnswer(), a.getPercent()));
            answerLayout.addView(tv);
        }
    }
}
