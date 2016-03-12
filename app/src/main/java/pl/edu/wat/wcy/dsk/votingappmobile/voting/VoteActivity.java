package pl.edu.wat.wcy.dsk.votingappmobile.voting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import pl.edu.wat.wcy.dsk.votingappmobile.Survey;
import pl.edu.wat.wcy.dsk.votingappmobile.R;
import pl.edu.wat.wcy.dsk.votingappmobile.User;

public class VoteActivity extends AppCompatActivity {
    private User mUser;
    private Survey mSurvey;

    private TextView mQuestion;
    private RadioGroup mRadioGroup;
    private Button mVoteButton;

    private int answerId;
    private SendAnswerTask sendAnswerTask = null;

    private View mProgressView;
    private View mVotingFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vote);

        mVotingFormView = findViewById(R.id.voting_form);
        mProgressView = findViewById(R.id.vote_progress);

        mQuestion = (TextView) findViewById(R.id.question);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mVoteButton = (Button) findViewById(R.id.vote);
        mVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doVote();
            }
        });

        Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra("user");
        mSurvey = (Survey) intent.getSerializableExtra("survey");
        createRadioButtons();
    }

    private void doVote() {
        sendAnswerTask = new SendAnswerTask(answerId);
        sendAnswerTask.execute((Void) null);
    }

    private void createRadioButtons() {
        if (mSurvey == null) {
            mQuestion.setText("Brak aktywnej ankiety :(");
            mVoteButton.setVisibility(View.INVISIBLE);
            return;
        }

        mQuestion.setText(mSurvey.getQuestion());
        for (Map.Entry<Integer, String> e : mSurvey.getAnswers().entrySet()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(e.getKey());
            radioButton.setText(e.getValue());
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRadioButtonClicked(view);
                }
            });
            mRadioGroup.addView(radioButton);
        }
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        if (checked)
            answerId = view.getId();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mVotingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVotingFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVotingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mVotingFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class SendAnswerTask extends AsyncTask<Void, Void, Boolean> {

        private final int answerId;

        SendAnswerTask( int answerId) {
            this.answerId = answerId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlString = "http://orangepi.duckdns.org:1314/vote?answerId=" + answerId;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != 200)
                    return false;

                InputStream inputStream = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = rd.readLine()) != null) {
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            sendAnswerTask = null;
            showProgress(false);

            if (success) {
                mQuestion.setText("Głos oddany - dzięki!");
                mVoteButton.setVisibility(View.INVISIBLE);
                mRadioGroup.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "Nie możesz oddać głosu :(" + answerId, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            sendAnswerTask = null;
            showProgress(false);
        }
    }
}
