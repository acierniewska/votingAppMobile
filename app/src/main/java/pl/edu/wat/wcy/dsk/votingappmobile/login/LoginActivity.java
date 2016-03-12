package pl.edu.wat.wcy.dsk.votingappmobile.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import pl.edu.wat.wcy.dsk.votingappmobile.R;
import pl.edu.wat.wcy.dsk.votingappmobile.Survey;
import pl.edu.wat.wcy.dsk.votingappmobile.User;
import pl.edu.wat.wcy.dsk.votingappmobile.cloudmessaging.QuickstartPreferences;
import pl.edu.wat.wcy.dsk.votingappmobile.cloudmessaging.RegistrationIntentService;
import pl.edu.wat.wcy.dsk.votingappmobile.voting.VoteActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private UserRegisterTask mAuthTask = null;
    private CheckUserTask checkUserTask = null;
    // UI references.
    private AutoCompleteTextView nameTextView;
    private Button mSignInButton;
    private View mProgressView;
    private View mLoginFormView;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        nameTextView = (AutoCompleteTextView) findViewById(R.id.name);

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        showProgress(true);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                token = intent.getStringExtra("token");
                checkUserTask = new CheckUserTask(getTelephoneNumber());
                checkUserTask.execute((Void) null);
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        if (nameTextView.onCheckIsTextEditor())

            // Reset errors.
            nameTextView.setError(null);

        // Store values at the time of the login attempt.
        String name = nameTextView.getText().toString();
        String telephoneNumber = getTelephoneNumber();

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(name, telephoneNumber);
            mAuthTask.execute((Void) null);
        }
    }

    @NonNull
    private String getTelephoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String telephoneNumber = token;//tMgr.getLine1Number();
        if (telephoneNumber == null || telephoneNumber.isEmpty())
            telephoneNumber = "691231503";

        return telephoneNumber;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void switchActivity(User user) {
        Intent nextScreen = new Intent(getApplicationContext(), VoteActivity.class);
        nextScreen.putExtra("user", user);
        Survey f = new Survey();
        f.setQuestion("Czy pokazać cycki?");
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "Tak");
        map.put(20, "Bardzo proszę");
        map.put(3, "Jasne");
        f.setAnswers(map);
        nextScreen.putExtra("survey", f);
        showProgress(false);
        startActivity(nextScreen);

        finish();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    public class CheckUserTask extends AsyncTask<Void, Void, User> {
        private final String mTelephoneNumber;

        CheckUserTask(String telephoneNumber) {
            mTelephoneNumber = telephoneNumber;
        }

        @Override
        protected User doInBackground(Void... params) {
            User user = null;
            try {
                String urlString = "http://orangepi.duckdns.org:1314/users/findByPhoneNumber?phoneNumber=" + mTelephoneNumber;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "");
                int responseCode = connection.getResponseCode();
                if (responseCode != 200 && responseCode != 201)
                    return null;
                InputStream inputStream = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder("");
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                if (!sb.toString().isEmpty())
                    user = new Gson().fromJson(sb.toString(), User.class);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }

            return user;
        }

        @Override
        protected void onPostExecute(final User user) {
            mAuthTask = null;
            if (user != null) {
                switchActivity(user);
            } else {
                showProgress(false);
                nameTextView.setVisibility(View.VISIBLE);
                mSignInButton.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private StringBuilder builder;
        private String mName;
        private String mTelephoneNumber;

        UserRegisterTask(String name, String telephoneNumber) {
            mName = name;
            mTelephoneNumber = telephoneNumber;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlString = "http://orangepi.duckdns.org:1314/users/registerNew?phoneNumber=" + mTelephoneNumber + "&name=" + mName;
            builder = new StringBuilder("");
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
                    builder.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                checkUserTask = new CheckUserTask(getTelephoneNumber());
                checkUserTask.execute((Void) null);
            } else {
                nameTextView.getText().clear();
                Toast.makeText(getApplicationContext(), getString(R.string.error_sign_in), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

