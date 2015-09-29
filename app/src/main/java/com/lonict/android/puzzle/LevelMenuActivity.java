package com.lonict.android.puzzle;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.ThemeUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.plus.PlusOneButton;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.banner.Banner;
import com.startapp.android.publish.banner.banner3d.Banner3D;
import com.startapp.android.publish.banner.bannerstandard.BannerStandard;
import com.startapp.android.publish.model.AdPreferences;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.StartAppNativeAd;

import org.w3c.dom.Text;

import java.lang.annotation.Target;

public class LevelMenuActivity extends AppCompatActivity {

    private boolean isTimerGame = false;
    private boolean isMoveCountGame = false ;
    private int gameMode ;

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, getResources().getString(R.string.startapp_app_id), true);
        setContentView(R.layout.activity_level_menu);

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
                PuzzleUtils.Log(PuzzleUtils.MOVE_COUNT_EXTRA, "contans");
                isMoveCountGame = extras.getBoolean(PuzzleUtils.MOVE_COUNT_EXTRA);
            }
            if (extras.containsKey(PuzzleUtils.GAME_MODE_EXTRA))
            {
                PuzzleUtils.Log("GAME_MODE_EXTRA", "contains");
                gameMode = extras.getInt(PuzzleUtils.GAME_MODE_EXTRA);
            }
        }
        initMenuHeader();
        createItems();
    }

    public void initMenuHeader()
    {
        TextView txt = (TextView)findViewById(R.id.textView_level_header);
        if(isTimerGame) txt.setText(getResources().getString(R.string.menu_item_2_level_header));
        else if (isMoveCountGame)txt.setText(getResources().getString(R.string.menu_item_1_level_header));
        else if (gameMode==PuzzleUtils.GAME_MODE_MOVE) txt.setText(getResources().getString(R.string.menu_item_3_level_header));
    }

    public void createItems()
    {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_level);
        boolean isichanged;
        for (int i=2;i<7;i++)
        {
            isichanged=true;
            for (int colors_count=2;colors_count<6;colors_count++)
            {

                RelativeLayout rel = new RelativeLayout(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                params.setMargins(0, (int)(getResources().getDisplayMetrics().density*4F), 0, 0);
                rel.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_second));
                rel.setLayoutParams(params);
                rel.setGravity(Gravity.CENTER);

                if (isichanged)
                {
                    BannerStandard startAppBanner = new BannerStandard (this);
                    RelativeLayout rel_banner = new RelativeLayout(this);
                    LinearLayout.LayoutParams params_banner = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params_banner.setMargins(0, (int) (getResources().getDisplayMetrics().density * 4F), 0, 0);
                    //rel_banner.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_second));
                    rel_banner.setLayoutParams(params_banner);
                    rel_banner.setGravity(Gravity.CENTER);

                    RelativeLayout.LayoutParams bannerParameters =
                            new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    rel_banner.addView(startAppBanner,bannerParameters);
                    linearLayout.addView(rel_banner);
                }

                setOnClick(rel,colors_count,i);

                RelativeLayout.LayoutParams text_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                //text_params.addRule(RelativeLayout.CENTER_HORIZONTAL);

                TextView txt = new TextView(this);
                txt.setLayoutParams(text_params);
                txt.setTextAppearance(this, android.R.style.TextAppearance_Large);
                txt.setTextColor(getResources().getColor(R.color.primary_light_base));
                txt.setText(i + "x" + i + " : " + colors_count+" "+getString(R.string.colors_string));

                rel.addView(txt);
                linearLayout.addView(rel);
                isichanged=false;
            }
        }

    }
    public void setOnClick(RelativeLayout relativeLayout,int color_count, int wideness)
    {
        long current_level = 6;
        int defaultValue = 6; //2x2 2colors = 6
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.pref_key_file_name),Context.MODE_PRIVATE);
        if (isMoveCountGame)
        {
            current_level = sharedPref.getInt(getString(R.string.pref_current_level_for_move_count), defaultValue);
            PuzzleUtils.Log("xxpreflevel",current_level+"");
        } else if (isTimerGame)
        {
            current_level = sharedPref.getInt(getString(R.string.pref_current_level_for_timer), defaultValue);
            PuzzleUtils.Log("xxpreflevel",current_level+"");
        }

        if (current_level>=((wideness*wideness)+color_count))
        {
            final int colors_count = color_count;
            final int wideness_ = wideness ;
            final RelativeLayout rel =relativeLayout ;
            rel.setOnClickListener(new View.OnClickListener() {
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
                            Intent intent = new Intent(rel.getContext(), MainActivity.class);
                            intent.putExtra(PuzzleUtils.MATRIX_WIDENESS_EXTRA, wideness_);
                            intent.putExtra(PuzzleUtils.COLOR_COUNT_EXTRA, colors_count);
                            intent.putExtra(PuzzleUtils.TIMER_EXTRA,isTimerGame);
                            intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, gameMode);
                            intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, isMoveCountGame);
                            finish();
                            startActivity(intent);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    v.startAnimation(animation);
                }
            });
        }
        else
        {
            relativeLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_second_disabled));
        }
    }

    public void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives focus.
    }

}
