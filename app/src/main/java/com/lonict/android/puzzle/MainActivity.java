package com.lonict.android.puzzle;

import android.animation.TimeAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.AlarmClock;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.games.util.BaseGameUtils;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private LinearLayout mMatrixLinearLayout ;
    private PuzzleMatrix mPuzzleMatrix;
    private boolean isTimerGame = false;
    private boolean isMoveCountGame = false ;
    private int[] itemColors;
    private int matrixWideness = 9; // should be for default 5 don't change
    private int itemColorCount = 5; // should be 5 for default don't change
    private long levelCountDownMiliseconds ;
    private long currentCountDownMiliseconds ;
    private CountDownTimer countDownTimer;
    private TextView countDownTextView;
    private int gameMode ;

    private static InterstitialAd interstitial;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    public int REQUEST_LEADERBOARD = 0 ;
    public int REQUEST_ACHIEVEMENTS = 1;

    private StartAppAd interstitialAdstartAppAd = new StartAppAd(this);

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

        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
            if (extras.containsKey(PuzzleUtils.TIMER_EXTRA))
            {
                PuzzleUtils.Log(PuzzleUtils.TIMER_EXTRA, "contains");
                isTimerGame = extras.getBoolean(PuzzleUtils.TIMER_EXTRA);
            }
            if (extras.containsKey(PuzzleUtils.MOVE_COUNT_EXTRA))
            {
                PuzzleUtils.Log(PuzzleUtils.MOVE_COUNT_EXTRA,"contans");
                isMoveCountGame = extras.getBoolean(PuzzleUtils.MOVE_COUNT_EXTRA);

            }
            if (extras.containsKey(PuzzleUtils.MATRIX_WIDENESS_EXTRA))
            {
                PuzzleUtils.Log("MATRIX_WIDENESS_EXTRA","contains");
                matrixWideness = extras.getInt(PuzzleUtils.MATRIX_WIDENESS_EXTRA);
            }
            if (extras.containsKey(PuzzleUtils.COLOR_COUNT_EXTRA))
            {
                PuzzleUtils.Log("COLOR_COUNT_EXTRA","contains");
                itemColorCount = extras.getInt(PuzzleUtils.COLOR_COUNT_EXTRA);
            }
            if (extras.containsKey(PuzzleUtils.GAME_MODE_EXTRA))
            {
                PuzzleUtils.Log("GAME_MODE_EXTRA","contains");
                gameMode = extras.getInt(PuzzleUtils.GAME_MODE_EXTRA);
            }
        }
        initAnalytics();
        createBody();
    }
    public GoogleAnalytics getAnalytics()
    {
        return analytics;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
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

    @Override
    public void onResume() {
        super.onResume();
        interstitialAdstartAppAd.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        interstitialAdstartAppAd.onPause();
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

    public void createBody()
    {
        switch (itemColorCount)
        {
            case 2:
                itemColors = new int[] {getResources().getColor(R.color.puzzle_color_1),
                        getResources().getColor(R.color.puzzle_color_2) };
                break;
            case 3:
                itemColors = new int[] {getResources().getColor(R.color.puzzle_color_1),
                        getResources().getColor(R.color.puzzle_color_2),
                        getResources().getColor(R.color.puzzle_color_3)};
                break;
            case 4:
                itemColors = new int[] {getResources().getColor(R.color.puzzle_color_1),
                        getResources().getColor(R.color.puzzle_color_2),
                        getResources().getColor(R.color.puzzle_color_3),
                        getResources().getColor(R.color.puzzle_color_4)};
                break;
            case 5:
                itemColors = new int[] {getResources().getColor(R.color.puzzle_color_1),
                        getResources().getColor(R.color.puzzle_color_2),
                        getResources().getColor(R.color.puzzle_color_3),
                        getResources().getColor(R.color.puzzle_color_4),
                        getResources().getColor(R.color.puzzle_color_5)};
                break;
        }
        RelativeLayout inner_center= (RelativeLayout)findViewById(R.id.relativeLayout_inner_center);
        mPuzzleMatrix = new PuzzleMatrix.Builder()
                .setMatrix_wideness(matrixWideness)
                .setContext(this)
                .setItem_colors(itemColors)
                .setGameMode(gameMode)
                .Build(this);
        mMatrixLinearLayout = mPuzzleMatrix.getPuzzleLinearLayout();
        inner_center.addView(mMatrixLinearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        RelativeLayout right_top = (RelativeLayout)findViewById(R.id.relativeLayout_right_all_colors);
        right_top.addView(PuzzleMatrix.getLinear_rightTop(),
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        animateBody(false, false);
        ImageView button = (ImageView)findViewById(R.id.imageButton_pause);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(PuzzleUtils.DIALOG_TYPE_PAUSE);
            }
        });

        ImageView leaderboard = (ImageView)findViewById(R.id.imageView_play_leaderboard);
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMoveCountGame)
                {
                    if (mGoogleApiClient != null && isGooglePlayAvailable()) {
                        mSignInClicked = true;
                        mGoogleApiClient.connect();
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                                getString(R.string.google_play_leaderboard_id_mover)), REQUEST_LEADERBOARD);
                    }
                }else if (isTimerGame)
                {
                    if (mGoogleApiClient != null && isGooglePlayAvailable()) {
                        mSignInClicked = true;
                        mGoogleApiClient.connect();
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                                getString(R.string.google_play_leaderboard_id_timer)), REQUEST_LEADERBOARD);
                    }
                }
            }
        });

        //set top score text
        ((TextView)findViewById(R.id.textView_top_score)).setText(getTopScorePreference());

        if(!isTimerGame)
        {
            findViewById(R.id.imageView_timer).setVisibility(View.GONE);
            findViewById(R.id.textView_time).setVisibility(View.GONE);
        }else animateClock();
        if(!isMoveCountGame)
        {
            findViewById(R.id.textView_move_count).setVisibility(View.GONE);
            findViewById(R.id.imageView_move_count).setVisibility(View.GONE);
        }else animateMoveCount();

        //instanity mode popup intro message
        if (matrixWideness==2&&itemColorCount==2&&gameMode==PuzzleUtils.GAME_MODE_MOVE)
        {
            showPopup(PuzzleUtils.DIALOG_TYPE_INSANITY_INTRO);
        }
        if (gameMode!=PuzzleUtils.GAME_MODE_MOVE)
        {
            ((ImageView)findViewById(R.id.imageView_target_color)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.textView_target_count)).setVisibility(View.GONE);
        }
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

    public boolean isAllTargeted()
    {
        ImageView image ;
        int targetcount =0 ;
        Object samecolor=0;
        int samecolorcount =1 ;
        for ( int i=0 ; i<mMatrixLinearLayout.getChildCount(); i++) {
            if (mMatrixLinearLayout.getChildAt(i) instanceof LinearLayout)
            {
                for (int k=0 ; k<((LinearLayout)mMatrixLinearLayout.getChildAt(i)).getChildCount();k++)
                {
                    try
                    {
                        image = ((ImageView)((LinearLayout)mMatrixLinearLayout.getChildAt(i)).getChildAt(k));
                        //insantity mode count only target color
                        if (gameMode==PuzzleUtils.GAME_MODE_MOVE)
                        {
                            if (image.getTag().equals(findViewById(R.id.imageView_target_color).getTag())) {
                                targetcount++;
                            }
                        } else
                        {
                            if (image.getTag().equals(samecolor)) {
                                samecolorcount++;
                            }
                            samecolor=image.getTag();
                        }
                    }
                    catch (Exception s)
                    {
                        PuzzleUtils.Log("xxIsTargeted", "ERROR!");
                    }
                }
            }
        }
        if (targetcount==matrixWideness*matrixWideness)
        {
            PuzzleUtils.Log("xxIsTargeted", "yes");
            return true ;
        }
        else if (samecolorcount==matrixWideness*matrixWideness )
        {
            return true;
        }
        return false ;
    }

    public void animateBody(boolean isLevelEndAnimation, boolean isShowpup)
    {
        AlphaAnimation animation ;
        Object target_color = findViewById(R.id.imageView_target_color).getTag();

        animation = new AlphaAnimation(0.1f, 1f);
        animation.setRepeatCount(0);
        animation.setDuration(2000);
        ImageView image ;
        for ( int i=0 ; i<mMatrixLinearLayout.getChildCount(); i++) {
            if (mMatrixLinearLayout.getChildAt(i) instanceof LinearLayout)
            {
                for (int k=0 ; k<((LinearLayout)mMatrixLinearLayout.getChildAt(i)).getChildCount();k++)
                {
                    try
                    {
                        image = ((ImageView)((LinearLayout)mMatrixLinearLayout.getChildAt(i)).getChildAt(k));
                        if ( isLevelEndAnimation) {
                            image.setEnabled(false);
                            image.startAnimation(animation);
                            //image.setAlpha(0.30F);
                        }else {
                            //insanity mode animate only target colors
                            if(gameMode==PuzzleUtils.GAME_MODE_MOVE)
                            {
                                if (!image.getTag().equals(target_color)) {
                                    image.startAnimation(animation);
                                }
                            } else image.startAnimation(animation);
                        }
                    }
                    catch (Exception s)
                    {
                        PuzzleUtils.Log("xxErrAnimateBody",s.toString());
                    }
                }
            }
        }
        if (isShowpup)
        {
            if (isTimerGame&&countDownTimer!=null)
            {
                countDownTimer.cancel();
                countDownTimer=null;
            }
//            if (isLevelEndAnimation&&!(matrixWideness==9&&itemColorCount==5)){
            if (isLevelEndAnimation&&(matrixWideness==6&&itemColorCount==5)){
                showPopup(PuzzleUtils.DIALOG_TYPE_LEVEL_ALL_END);
                setTopScorePreference();
            } else {
                if (isLevelEndAnimation) {
                    showPopup(PuzzleUtils.DIALOG_TYPE_LEVEL_END);
                    setTopScorePreference();
                }
            }
        }
    }

    public void setTopScorePreference()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_key_file_name), Context.MODE_PRIVATE);
        if (getisTimerGame())
        {
            String time_text = ((TextView)findViewById(R.id.textView_time)).getText()+"" ;
            String timer_pref = sharedPref.getString(getString(R.string.pref_top_score_for_timer), "00:00");
            int seconds_current = new Integer(time_text.substring(3,time_text.length())).intValue();
            int minutes_current = new Integer(time_text.substring(0,2)).intValue();
            int seconds_pref = new Integer(timer_pref.substring(3,timer_pref.length())).intValue();
            int minutes_pref = new Integer(timer_pref.substring(0,2)).intValue();

            if ((minutes_pref*60+seconds_pref) < (seconds_current+60*minutes_current))
            {
                SharedPreferences.Editor editor = sharedPref.edit();
                PuzzleUtils.Log("setTopScorePreference", time_text);
                editor.putString(getString(R.string.pref_top_score_for_timer), time_text);
                editor.commit();
                if (mGoogleApiClient.isConnected())
                    Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.google_play_leaderboard_id_timer), seconds_current+60*minutes_current);
            }
        }
        else if (getisMoveCountGame())
        {
            String move_count = ((TextView)findViewById(R.id.textView_move_count)).getText()+"";
            int move_count_pref = sharedPref.getInt(getString(R.string.pref_top_score_for_move_count), 0);
            if (move_count_pref < new Integer(move_count).intValue())
            {
                SharedPreferences.Editor editor = sharedPref.edit();
                PuzzleUtils.Log("setTopScorePreference2", move_count);
                editor.putInt(getString(R.string.pref_top_score_for_move_count), new Integer(move_count).intValue());
                editor.commit();
                if (mGoogleApiClient.isConnected())
                    Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.google_play_leaderboard_id_mover),  new Integer(move_count).intValue());
            }
        }
    }
    public String getTopScorePreference()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.pref_key_file_name),Context.MODE_PRIVATE);
        if (isTimerGame)
            return sharedPreferences.getString(getString(R.string.pref_top_score_for_timer), "00:00")+"";
        if (isMoveCountGame)
            return sharedPreferences.getInt(getString(R.string.pref_top_score_for_move_count), 0)+"";
        return "";
    }

    @Override
    public void onBackPressed() {
        interstitialAdstartAppAd.onBackPressed();
        showPopup(PuzzleUtils.DIALOG_TYPE_PAUSE);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        analytics.reportActivityStart(this);
        mGoogleApiClient.connect();
    }


    public void showPopup(int dialog_type)
    {
        switch (dialog_type)
        {
            case PuzzleUtils.DIALOG_TYPE_PAUSE:
            {
                interstitialAdstartAppAd.showAd();
                interstitialAdstartAppAd.loadAd();
                Dialog dialog = new Dialog.Builder()
                        .setContext(this)
                        .setPopupMessage(getResources().getString(R.string.popup_pause_message))
                        .Build();
                dialog.showPaused();
                if (isTimerGame&&countDownTimer!=null)
                {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                break ;
            }
            case PuzzleUtils.DIALOG_TYPE_TIMER_END:
            {
                Dialog dialog = new Dialog.Builder()
                        .setContext(this)
                        .setPopupMessage(getResources().getString(R.string.popup_timer_end_message))
                        .Build();
                dialog.showPaused();
                countDownTimer.cancel();
                countDownTimer=null;
                break;
            }
            case PuzzleUtils.DIALOG_TYPE_MOVE_COUNT_END:
            {
                Dialog dialog = new Dialog.Builder()
                        .setContext(this)
                        .setPopupMessage(getResources().getString(R.string.popup_move_count_end_message))
                        .Build();
                dialog.showPaused();
                break;
            }
            case PuzzleUtils.DIALOG_TYPE_LEVEL_END: {
                String popupmessage = getResources().getString(R.string.level_finish_text);
                Dialog dialog = new Dialog.Builder()
                        .setContext(this)
                        .setPopupMessage(popupmessage)
                        .Build();
                dialog.showLevelEnd();
                break;
            }
            case PuzzleUtils.DIALOG_TYPE_LEVEL_ALL_END:
            {
                String popupmessage = getResources().getString(R.string.level_finish_text);
                String achievement_id = "";
                if (itemColorCount==5)
                {
                    switch (matrixWideness)
                    {
                        case 2:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_2);
                            achievement_id = getString(R.string.achievementID_1);
                            break;
                        case 3:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_3);
                            achievement_id = getString(R.string.achievementID_2);
                            break;
                        case 4:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_4);
                            achievement_id = getString(R.string.achievementID_3);
                            break;
                        case 5:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_5);
                            achievement_id = getString(R.string.achievementID_4);
                            break;
                        case 6:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_6);
                            achievement_id = getString(R.string.achievementID_5);
                            break;
                        case 7:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_6);
                            break;
                        case 8:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_8);
                            break;
                        case 9:
                            popupmessage = getResources().getString(R.string.level_finish_text_message_for_9);
                            break;
                        default: popupmessage = getResources().getString(R.string.level_finish_text);
                    }
                }

                if (mGoogleApiClient.isConnected()&&itemColorCount==5)
                {
                    Games.Achievements.unlock(mGoogleApiClient, achievement_id);
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                            REQUEST_ACHIEVEMENTS);
                }

                Dialog dialog = new Dialog.Builder()
                        .setContext(this)
                        .setPopupMessage(popupmessage)
                        .Build();
                dialog.showLevelEnd();
                break;
            }
            case PuzzleUtils.DIALOG_TYPE_INSANITY_INTRO: {
                String popupmessage = getResources().getString(R.string.popup_insanity_intro_message);
                Dialog dialog = new Dialog.Builder()
                        .setContext(this)
                        .setPopupMessage(popupmessage)
                        .Build();
                dialog.showInsanityIntro();
                break;
            }
        }
    }

    @Override
    public void onDestroy()
    {
        if (isFinishing())
        {
            PuzzleUtils.Log("xxRecycle","true");
            mPuzzleMatrix.recycleBitmaps();
        }
        super.onDestroy();
    }

    public void animateMoveCount()
    {
        final ImageView move_count_image = (ImageView)findViewById(R.id.imageView_move_count);
        final TextView move_count_text = (TextView)findViewById(R.id.textView_move_count);
        AlphaAnimation animation = new AlphaAnimation(0f,1f);
        animation.setRepeatCount(6);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation animation_end = new AlphaAnimation(0f, 1f);
                animation_end.setRepeatCount(0);
                animation_end.setDuration(1500);
                move_count_image.startAnimation(animation_end);
                move_count_text.startAnimation(animation_end);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        move_count_text.startAnimation(animation);
        move_count_image.startAnimation(animation);

        int move = 0 ;
        switch (matrixWideness)
        {
            case 2:
                move = 2 * itemColorCount;
                break;
            case 3:
                move =  4 * itemColorCount;
                break;
            case 4:
                move = 9*itemColorCount;
                break;
            case 5:
                move = 15*itemColorCount;
                break;
            case 6:
                move = 24*itemColorCount;
                break;
            default:
                move = ((matrixWideness * matrixWideness / 2) * itemColorCount);
        }
        move_count_text.setText(move + "");
    }
    public void animateClock()
    {
        //levelCountDownMiliseconds = matrixWideness*30000 + itemColorCount*15000;

        switch (matrixWideness)
        {
            case 2:
                levelCountDownMiliseconds =  2000 + itemColorCount*3000;
                break;
            case 3:
                levelCountDownMiliseconds =  8000 + itemColorCount*2000;
                break;
            case 4:
                levelCountDownMiliseconds = 25000+itemColorCount*5000;
                break;
            case 5:
                levelCountDownMiliseconds = 90000+itemColorCount*4000;
                break;
            case 6:
                levelCountDownMiliseconds = 100000+itemColorCount*5000;
                break;
            default:
                levelCountDownMiliseconds =  matrixWideness*matrixWideness + itemColorCount*5000;
        }

        final ImageView clock = (ImageView)findViewById(R.id.imageView_timer);
        //final TextView clock_text = (TextView)findViewById(R.id.textView_time);
        countDownTextView = (TextView)findViewById(R.id.textView_time);
        AlphaAnimation animation = new AlphaAnimation(0f,1f);
        animation.setRepeatCount(5);
        animation.setDuration(500);
        countDownTimer =
                new CountDownTimer(levelCountDownMiliseconds, 1000) {
                    public void onTick(long millisUntilFinished) {
                        String minute = (millisUntilFinished / 60000) + "";
                        String second = ((millisUntilFinished / 1000) % 60) + "";
                        if (minute.length() < 2) {
                            minute = "0" + minute;
                        }
                        ;
                        if (second.length() < 2) {
                            second = "0" + second;
                        }
                        countDownTextView.setText(minute + ":" + second);
                        currentCountDownMiliseconds = millisUntilFinished;
                    }

                    public void onFinish() {
                        if (countDownTimer!=null)
                        {
                            countDownTimer.cancel();
                            countDownTextView.setText("00:00");
                            showPopup(PuzzleUtils.DIALOG_TYPE_TIMER_END);
                            animateBody(true, false);
                        }
                    }
                }.start();
        clock.startAnimation(animation);
        countDownTextView.startAnimation(animation);
    }


    //using for dialog
    public boolean getisTimerGame() {
        return isTimerGame;
    }
    public boolean getisMoveCountGame()
    {
        return isMoveCountGame;
    }
    public int getMatrixWideness(){
        return matrixWideness; // should be for default 5 don't change
    }
    public void setMatrixWideness(int i)
    {
        this.matrixWideness = i;
    }
    public int getItemColorCount(){
        return itemColorCount ; // should be 5 for default don't change
    }
    public void setItemColorCount(int i)
    {
        this.itemColorCount = i ;
    }
    public long getLevelCountDownMiliseconds()
    {
        return levelCountDownMiliseconds ;
    }
    public long getCurrentCountDownMiliseconds(){
        return currentCountDownMiliseconds ;
    }
    public void setCurrentCountDownMiliseconds(long l)
    {
        this.currentCountDownMiliseconds = l;
    }
    public CountDownTimer getCountDownTimer(){
        return countDownTimer;
    }
    public void setCountDownTimer(CountDownTimer c)
    {
        this.countDownTimer = c;
    }
    public TextView getCountDownTextView(){
        return countDownTextView;
    }
    public void setCountDownTextView(TextView s)
    {
        this.countDownTextView = s;
    }
    public int getGameMode(){
        return gameMode ;
    }

}
