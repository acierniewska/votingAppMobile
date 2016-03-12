package pl.edu.wat.wcy.dsk.votingappmobile.voting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.edu.wat.wcy.dsk.votingappmobile.R;
import pl.edu.wat.wcy.dsk.votingappmobile.User;

public class VoteActivity extends AppCompatActivity {
    private User mUser;

    private TextView mQuestion;
    private RadioGroup mRadioGroup;
    private Button mVoteButton;

    private List<RadioButton> radioButtons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vote);

        mQuestion = (TextView) findViewById(R.id.question);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mVoteButton = (Button) findViewById(R.id.vote);

        Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra("user");
        Toast.makeText(getApplicationContext(), "No elo " + mUser.getFirstName() + "!", Toast.LENGTH_LONG).show();

        for (int i = 0; i < 5; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioButton.setText("Odpowiedź " + i);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRadioButtonClicked(view);
                }
            });
            radioButtons.add(radioButton);
            mRadioGroup.addView(radioButton);
        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.radioButton:
//                if (checked)
//                    break;
//            case R.id.radioButton2:
//                if (checked)
//                    break;
//        }
    }
}
