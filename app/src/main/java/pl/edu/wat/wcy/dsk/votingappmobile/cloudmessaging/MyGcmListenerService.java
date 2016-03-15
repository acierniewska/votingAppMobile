package pl.edu.wat.wcy.dsk.votingappmobile.cloudmessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.ArrayList;
import java.util.List;

import pl.edu.wat.wcy.dsk.votingappmobile.Answer;
import pl.edu.wat.wcy.dsk.votingappmobile.R;
import pl.edu.wat.wcy.dsk.votingappmobile.Survey;
import pl.edu.wat.wcy.dsk.votingappmobile.login.LoginActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        sendNotification(message);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        message = message.replace("\"", "");
        String[] messagePart = message.split(" ");
        if (messagePart.length != 2)
            return;

        if (messagePart[0].equals("survey")) {
            message = "Nowa ankieta!";
        } else if (messagePart[0].equals("result")) {
            message = "Wyniki ankiety są już dostępne!";
        } else
            return;

        Integer surveyId = Integer.valueOf(messagePart[1].trim());
        intent.putExtra("surveyId", surveyId);
        //intent.putExtra("survey", getSurvey());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cat_track_24)
                .setContentTitle("VotingAppMobile")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(surveyId, notificationBuilder.build());
    }


    private Survey getSurvey() {
        Survey survey = new Survey();
        survey.setQuestion("Czy pokazać cycki?");
        survey.setIsClosed(true);
        List<Answer> answers = new ArrayList<>();
        Answer a = new Answer();
        a.setAnswer("Tak");
        a.setId(1);
        answers.add(a);

        a = new Answer();
        a.setAnswer("Jasne");
        a.setId(2);
        a.setNumberOfVotes(2);
        a.setPercent(50);
        answers.add(a);

        a = new Answer();
        a.setAnswer("Bardzo proszę.");
        a.setId(20);
        a.setNumberOfVotes(2);
        a.setPercent(50);
        answers.add(a);

        survey.setAnswers(answers);

        return survey;
    }
}
