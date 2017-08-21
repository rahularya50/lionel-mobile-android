package com.noemptypromises.rahularya.lionel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String AUTO = "auto";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    protected Boolean isAuto;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Intent intent = getIntent();
        if (intent.getBooleanExtra(WelcomeScreen.AUTO, true))
        {
            isAuto = true;
            showProgress(true);
            SharedPreferences login = getSharedPreferences("usercreds", 0);
            //Log.d(TAG, "PROGRAM start");
            mAuthTask = new UserLoginTask(login.getString("username", "username"), login.getString("password", "password"));
            mAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Log.d(TAG, "PROGRAM enter");
            enterMain();
        }
        else
        {
            isAuto = false;
        }
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

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //Log.d(TAG, "Test");

            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Document ml1;
        private Document mCal;
        private Document ml3;
        private Document ml4;
        private Document ml5;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String TAG = WelcomeScreen.class.getSimpleName();
            try {
                Connection.Response loginForm = Jsoup.connect("https://lionel2.kgv.edu.hk/login/index.php")
                        .method(Connection.Method.GET)
                        .timeout(0)
                        .execute();

                Connection.Response l1 = Jsoup.connect("https://lionel2.kgv.edu.hk/login/index.php")
                        .data("username", mEmail)
                        .data("password", mPassword)
                        .data("rememberusername", "0")
                        .cookies(loginForm.cookies())
                        .method(Connection.Method.POST)
                        .timeout(0)
                        .execute();

                Log.d(TAG, "PROGRAM l1 " + l1.toString());

                Log.d(TAG, "PROGRAM loginForm length " + loginForm.body().length());

                Log.d(TAG, "PROGRAM doc length " + l1.body().length());

                if (l1.body().lastIndexOf("Log out") == -1)
                {
                    Log.d(TAG, "PROGRAM fail due to length");
                    return false;
                }

                Connection.Response cal = Jsoup.connect("http://lionel.kgv.edu.hk/kgv-additions/Calendar/master.php?style=small")
                        .cookies(l1.cookies())
                        .method(Connection.Method.GET)
                        .timeout(0)
                        .execute();

                mCal = cal.parse();

                Log.d(TAG, "PROGRAM id " + l1.body().lastIndexOf("http://lionel.kgv.edu.hk/user/view.php?id="));

                int uidStartPos = l1.body().lastIndexOf("<a alt=\"summary\" class=\" \" href=\"https://lionel2.kgv.edu.hk/local/mis/students/summary.php?sid=");

                String uid = l1.body().substring(uidStartPos+95, uidStartPos+99);

                //String a = l1.parse().select(".menu").get(0).ownText();

                //String uid = a.replaceAll("[^0-9]", "");

                //String uid = a.substring(a.length() - 20, a.length() - 16);

                ml1 = l1.parse();

                //String uid = regexer("Logout<a>\\)<div>[^>]*<div>", l1.parse().html(), 1);

                Log.d(TAG, "PROGRAM uid: " + uid);

                //Log.d(TAG, "PROGRAM doc length " + l2.body().length());

                Connection.Response l3 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/misc/printtimetable.php?sid=" + uid)
                        .cookies(l1.cookies())
                        .method(Connection.Method.GET)
                        .timeout(0)
                        .execute();

                ml3 = l3.parse();

                Connection.Response l4 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/mobile/myhomework.php")
                        .cookies(l1.cookies())
                        .method(Connection.Method.GET)
                        .timeout(0)
                        .execute();

                ml4 = l4.parse();

                Connection.Response l5 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/bulletin/bulletin.php")
                        .cookies(l1.cookies())
                        .method(Connection.Method.GET)
                        .timeout(0)
                        .execute();

                ml5 = l5.parse();
            }
            catch (IOException e) {
                return false;
            }

            SharedPreferences settings = getSharedPreferences("usercreds", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", mEmail);

            SecurePW.context = getBaseContext();

            editor.putString("password", SecurePW.encrypt(mPassword));
            editor.putString("l1", ml1.html());
            editor.putString("cal", mCal.html());
            editor.putString("timetable", ml3.html());
            editor.putString("homework", ml4.html());
            editor.putString("bulletin", ml5.html());
            editor.commit();

            SharedPreferences check = getSharedPreferences("active", 0);
            editor = check.edit();
            editor.putBoolean("active", true);
            editor.commit();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            Log.d(TAG, "isAuto:" + isAuto);
            //showProgress(false);

            if (success && !isAuto) {
                enterMain();
            } else if (!isAuto)
            {
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void enterMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.hold, R.anim.slide_out_down);
    }

}

