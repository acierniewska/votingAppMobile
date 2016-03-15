package pl.edu.wat.wcy.dsk.votingappmobile.showresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.edu.wat.wcy.dsk.votingappmobile.Answer;
import pl.edu.wat.wcy.dsk.votingappmobile.R;
import pl.edu.wat.wcy.dsk.votingappmobile.Survey;

public class ShowResultActivity extends AppCompatActivity {
    private Survey mSurvey;
    private LinearLayout answerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Intent intent = getIntent();
        mSurvey = (Survey) intent.getSerializableExtra("survey");

        answerLayout = (LinearLayout) findViewById(R.id.answer_layout);
        TextView tv = new TextView(this);
        tv.setText(mSurvey.getQuestion());
        answerLayout.addView(tv);

        for (Answer a : mSurvey.getAnswers())
        {
            tv = new TextView(this);
            tv.setText(a.getAnswer() + " - " + a.getPercent() + "%");
            answerLayout.addView(tv);
        }
    }
}
