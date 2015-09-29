package com.lonict.android.puzzle;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusOneButton;
import com.google.games.util.BaseGameUtils;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.model.AdPreferences;
import com.startapp.android.publish.splash.SplashConfig;

import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static PlusOneButton mPlusOneButton;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private StartAppAd interstitialAdstartAppAd = new StartAppAd(this);

    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    public int REQUEST_LEADERBOARD = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN) // PLUS API
               // .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER) // Drive API
                .build();

        StartAppSDK.init(this, getResources().getString(R.string.startapp_app_id), true);
        StartAppAd.showSplash(this, savedInstanceState,
                new SplashConfig()
                        .setTheme(SplashConfig.Theme.USER_DEFINED)
                        .setCustomScreen(R.layout.lonict_splash_screen)
        );
        setContentView(R.layout.activity_main_menu);
        mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);
        initAnalytics();
        initMenu();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            PuzzleUtils.Log("xxError","connection resolving failure");
            return;
        }

        PuzzleUtils.Log("xxConnectionFailed1","connection resolving failure");
        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            PuzzleUtils.Log("xxConnectionFailed2","connection resolving failure");
            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {

                PuzzleUtils.Log("xxConnectionFailed3","connection resolving failure");
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
        PuzzleUtils.Log("xxSuspended",i+"");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
        PuzzleUtils.Log("xxConnected","true");
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.sign_in_failed);
            }
        }
    }

    public void initAnalytics()
    {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800); //will upload data to server every 10 minutes
        tracker = analytics.newTracker(getResources().getString(R.string.admob_tracking_id)); // Replace with actual tracker/property Id

        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        //tracker.setSampleRate(90);

        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        analytics.reportActivityStart(this);
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
    }

    @Override
    public void onBackPressed()
    {
        analytics.reportActivityStop(this);
        finish();
        System.exit(0);
    }


    public void initMenu()
    {
        RelativeLayout rel_time = (RelativeLayout)findViewById(R.id.rel_menu_item_1);
        setOnClick(rel_time);

        RelativeLayout rel_move_count = (RelativeLayout)findViewById(R.id.rel_menu_item_2);
        setOnClick(rel_move_count);

        RelativeLayout rel_insanity = (RelativeLayout)findViewById(R.id.rel_menu_item_3);
        setOnClick(rel_insanity);

        ImageView share = (ImageView)findViewById(R.id.imageView_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share Link")
                        .setAction("Clicked")
                        .setLabel("")
                        .build());
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Try " + getResources().getString(R.string.app_name) + " ! https://play.google.com/store/apps/details?id=com.lonict.android.tonoradio";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Try " + getResources().getString(R.string.app_name) + " !");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        ImageView leaderboard = (ImageView)findViewById(R.id.imageView_leaderboard);
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient != null && isGooglePlayAvailable()) {
                    // signed in. Show the "sign out" button and explanation.
                    // ...
                    //mSignInClicked = true;
                    //mGoogleApiClient.connect();
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getString(R.string.google_play_leaderboard_id_mover)), REQUEST_LEADERBOARD);
                }
            }
        });

        ImageView rate_btn = (ImageView)findViewById(R.id.imageView_rate);
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Rate Us Link")
                            .setAction("has clicked")
                            .setLabel("yes")
                            .build());
                    startActivity(goToMarket);

                } catch (ActivityNotFoundException e) {
                    tracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Rate Us Link")
                            .setAction("Exception: couldn't find")
                            .setLabel("forwarded to playstore manually (downloaded from different market))")
                            .build());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            }
        });
    }

    private boolean isGooglePlayAvailable ()
    {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())== ConnectionResult.SUCCESS)
        {
            PuzzleUtils.Log("xxPlayservices", "exists");
            return true;
        }else
        {
            PuzzleUtils.Log("xxPlayervices", "not exists");
            return false;
        }
    }

    public void setOnClick (RelativeLayout rel )
    {
        final RelativeLayout relativeLayout = rel ;
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlphaAnimation animation = new AlphaAnimation(0.25f, 1f);
                animation.setRepeatCount(0);
                animation.setDuration(1500);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(getApplicationContext(), LevelMenuActivity.class);
                        if(relativeLayout.getId()==R.id.rel_menu_item_1)
                        {
                            //rule based
                            intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, true);
                            intent.putExtra(PuzzleUtils.TIMER_EXTRA, false);
                            intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, PuzzleUtils.GAME_MODE_TOUCH);
                            startActivity(intent);
                            //interstitialAdstartAppAd.showAd(); // show the ad
                            //interstitialAdstartAppAd.loadAd(); // load the next ad
                        }else if (relativeLayout.getId()==R.id.rel_menu_item_2)
                        {
                            //time
                            intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, false);
                            intent.putExtra(PuzzleUtils.TIMER_EXTRA, true);
                            intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, PuzzleUtils.GAME_MODE_TOUCH);
                            startActivity(intent);
                            //interstitialAdstartAppAd.showAd(); // show the ad
                            //interstitialAdstartAppAd.loadAd(); // load the next ad
                        } else if (relativeLayout.getId()==R.id.rel_menu_item_3)
                        {
                            interstitialAdstartAppAd.showAd();
                            interstitialAdstartAppAd.loadAd(StartAppAd.AdMode.OFFERWALL);

                            //insanity
                            /*intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, false);
                            intent.putExtra(PuzzleUtils.TIMER_EXTRA, false);
                            intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, PuzzleUtils.GAME_MODE_MOVE);
                            startActivity(intent);*/
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(animation);
            }
        });
    }

    private static final int PLUS_ONE_REQUEST_CODE = 0;

    public void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives focus.
        mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.lonict.android.puzzle", PLUS_ONE_REQUEST_CODE);
        interstitialAdstartAppAd.onResume();
        interstitialAdstartAppAd.loadAd(StartAppAd.AdMode.OFFERWALL);
    }

    public  void onPause() {
        super.onPause();
        // Refresh the state of the +1 button each time the activity receives focus.
        interstitialAdstartAppAd.onPause();
    }

}
