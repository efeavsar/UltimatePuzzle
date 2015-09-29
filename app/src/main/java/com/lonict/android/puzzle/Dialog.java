package com.lonict.android.puzzle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Efe Avsar on 05/09/2015.
 */
public class Dialog {
    private Context context ;
    private String popupMessage ;
    private boolean show;
    private MainActivity mainActivity ;
    private ImageView nextLevelBtn ;
    private ImageView playResumeBtn ;
    private ImageView returnHomeBtn ;
    private ImageView refreshBtn;
    private Button gotItBtn;
    private android.app.Dialog dialog;
    private boolean isNextLevelCalled=false ;
    public Dialog(Context context,String message)
    {
        this.context = context;
        this.popupMessage = message ;
        this.mainActivity = (MainActivity)context;
    }
    public void showLevelEnd()
    {
        if(!show){
            isNextLevelCalled = true;
            show=true;
            dialog =null;
            dialog = new android.app.Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_main);
            TextView popupmsg = (TextView) dialog.findViewById(R.id.textView_dialog_message);
            popupmsg.setText(popupMessage);

            DisplayMetrics displayMetrics ;
            int width = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
            int height = mainActivity.getWindowManager().getDefaultDisplay().getHeight();
            dialog.getWindow().setLayout((int) (width * 0.60), (int) (height * 0.75));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            nextLevelBtn = (ImageView)dialog.findViewById(R.id.imageView_next_level);
            playResumeBtn = (ImageView)dialog.findViewById(R.id.imageView_play);
            refreshBtn = (ImageView)dialog.findViewById(R.id.imageView_refresh);
            returnHomeBtn = (ImageView)dialog.findViewById(R.id.imageView_return_home);
            gotItBtn = (Button)dialog.findViewById(R.id.button_Got_It);

            gotItBtn.setVisibility(View.GONE);
            initDialogButtons();

            //all levels have finished
            if (mainActivity.getMatrixWideness()==6&&mainActivity.getItemColorCount()==5)
            {
                nextLevelBtn.setVisibility(View.GONE);
                refreshBtn.setVisibility(View.GONE);
                playResumeBtn.setVisibility(View.GONE);
            }else
            {
                Animation animation = new AlphaAnimation(0.25F, 1.0f);
                animation.setRepeatCount(0);
                animation.setDuration(1000);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AlphaAnimation animation_navigation = new AlphaAnimation(0.0f, 1.0f);
                        animation_navigation.setRepeatCount(4);
                        animation_navigation.setDuration(500);
                        nextLevelBtn.startAnimation(animation_navigation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                nextLevelBtn.setVisibility(View.VISIBLE);
                nextLevelBtn.startAnimation(animation);
            }

            // relativeLayout_list.setBackgroundResource(android.R.color.transparent);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
    }

    public ImageView getNextLevelBtn ()
    {
        return nextLevelBtn;
    }
    public ImageView getPlayResumeBtn()
    {
        return playResumeBtn;
    }
    public ImageView getReturnHomeBtn()
    {
        return returnHomeBtn;
    }
    public ImageView getRefreshBtn()
    {
        return refreshBtn;
    }
    public void showPaused()
    {
        if(!show){
            isNextLevelCalled = false;
            show=true;
            dialog =null;
            dialog = new android.app.Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_main);
            //dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 1 ), (int) (mainLayout.getHeight()* 1));
            //dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.8), (int) (mainLayout.getHeight() * 0.75));
            // relativeLayout_list.setBackgroundResource(android.R.color.transparent);
            nextLevelBtn = (ImageView)dialog.findViewById(R.id.imageView_next_level);
            playResumeBtn = (ImageView)dialog.findViewById(R.id.imageView_play);
            refreshBtn = (ImageView)dialog.findViewById(R.id.imageView_refresh);
            returnHomeBtn = (ImageView)dialog.findViewById(R.id.imageView_return_home);
            gotItBtn = (Button)dialog.findViewById(R.id.button_Got_It);
            initDialogButtons();

            TextView popupmsg = (TextView) dialog.findViewById(R.id.textView_dialog_message);
            popupmsg.setText(popupMessage);
            nextLevelBtn.setVisibility(View.GONE);
            gotItBtn.setVisibility(View.GONE);


            DisplayMetrics displayMetrics ;
            int width = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
            int height = mainActivity.getWindowManager().getDefaultDisplay().getHeight();
            dialog.getWindow().setLayout((int) (width * 0.60), (int) (height * 0.50));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);


            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
    }

    public void showInsanityIntro()
    {
        if(!show){
            isNextLevelCalled=false;
            show=true;
            dialog =null;
            dialog = new android.app.Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_main);
            //dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 1 ), (int) (mainLayout.getHeight()* 1));
            //dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.8), (int) (mainLayout.getHeight() * 0.75));
            // relativeLayout_list.setBackgroundResource(android.R.color.transparent);
            nextLevelBtn = (ImageView)dialog.findViewById(R.id.imageView_next_level);
            playResumeBtn = (ImageView)dialog.findViewById(R.id.imageView_play);
            refreshBtn = (ImageView)dialog.findViewById(R.id.imageView_refresh);
            returnHomeBtn = (ImageView)dialog.findViewById(R.id.imageView_return_home);
            gotItBtn = (Button)dialog.findViewById(R.id.button_Got_It);

            initDialogButtons();

            TextView popupmsg = (TextView) dialog.findViewById(R.id.textView_dialog_message);
            popupmsg.setText(popupMessage);
            nextLevelBtn.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.GONE);
            returnHomeBtn.setVisibility(View.GONE);
            playResumeBtn.setVisibility(View.GONE);

            DisplayMetrics displayMetrics ;
            int width = mainActivity.getWindowManager().getDefaultDisplay().getWidth();
            int height = mainActivity.getWindowManager().getDefaultDisplay().getHeight();
            dialog.getWindow().setLayout((int) (width * 0.60), (int) (height * 0.50));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }
    }

    public void initDialogButtons()
    {
        nextLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mainActivity.getItemColorCount() == 5) {
                    mainActivity.setItemColorCount( 2);
                    mainActivity.setMatrixWideness(mainActivity.getMatrixWideness()+1);
                } else {
                    mainActivity.setItemColorCount( mainActivity.getItemColorCount()+1);
                }
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(PuzzleUtils.COLOR_COUNT_EXTRA, mainActivity.getItemColorCount());
                intent.putExtra(PuzzleUtils.MATRIX_WIDENESS_EXTRA, mainActivity.getMatrixWideness());
                intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, mainActivity.getGameMode());
                intent.putExtra(PuzzleUtils.TIMER_EXTRA, mainActivity.getisTimerGame());
                intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, mainActivity.getisMoveCountGame());
                //intent.addFlags(Intent.Fa);
                dialog.dismiss();
                mainActivity.getAnalytics().reportActivityStop(mainActivity);
                mainActivity.finish();
                mainActivity.startActivity(intent);

            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(PuzzleUtils.COLOR_COUNT_EXTRA, mainActivity.getItemColorCount());
                intent.putExtra(PuzzleUtils.MATRIX_WIDENESS_EXTRA, mainActivity.getMatrixWideness());
                intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, mainActivity.getGameMode());
                intent.putExtra(PuzzleUtils.TIMER_EXTRA, mainActivity.getisTimerGame());
                intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, mainActivity.getisMoveCountGame());
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                dialog.dismiss();
                mainActivity.getAnalytics().reportActivityStop(mainActivity);
                mainActivity.finish();
                mainActivity.startActivity(intent);

            }
        });

        returnHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                dialog.dismiss();
                mainActivity.getAnalytics().reportActivityStop(mainActivity);
                mainActivity.finish();
                mainActivity.startActivity(intent);


            }
        });

        playResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNextLevelCalled)
                {// will be the same as next level button
                    if (mainActivity.getItemColorCount() == 5) {
                        mainActivity.setItemColorCount( 2);
                        mainActivity.setMatrixWideness(mainActivity.getMatrixWideness()+1);
                    } else {
                        mainActivity.setItemColorCount( mainActivity.getItemColorCount()+1);
                    }
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(PuzzleUtils.COLOR_COUNT_EXTRA, mainActivity.getItemColorCount());
                    intent.putExtra(PuzzleUtils.MATRIX_WIDENESS_EXTRA, mainActivity.getMatrixWideness());
                    intent.putExtra(PuzzleUtils.GAME_MODE_EXTRA, mainActivity.getGameMode());
                    intent.putExtra(PuzzleUtils.TIMER_EXTRA, mainActivity.getisTimerGame());
                    intent.putExtra(PuzzleUtils.MOVE_COUNT_EXTRA, mainActivity.getisMoveCountGame());
                    //intent.addFlags(Intent.Fa);
                    dialog.dismiss();
                    mainActivity.getAnalytics().reportActivityStop(mainActivity);
                    mainActivity.finish();
                    mainActivity.startActivity(intent);
                } else
                {// resume the normal activity
                    if (mainActivity.getisTimerGame())
                    {
                        if (mainActivity.getCountDownTimer()!=null)
                            mainActivity.getCountDownTimer().cancel();
                        mainActivity.setCountDownTimer(null);
                        mainActivity.setCountDownTimer(new CountDownTimer(mainActivity.getCurrentCountDownMiliseconds(), 1000) {
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
                                mainActivity.getCountDownTextView().setText(minute + ":" + second);
                                mainActivity.setCurrentCountDownMiliseconds(
                                        millisUntilFinished);
                            }

                            public void onFinish() {
                                mainActivity.getCountDownTextView().setText("00:00");
                                mainActivity.getCountDownTimer().cancel();
                                mainActivity.showPopup(PuzzleUtils.DIALOG_TYPE_TIMER_END);
                                mainActivity.animateBody(true, false);
                            }
                        } );
                        mainActivity.getCountDownTimer().start();
                    }
                    dialog.dismiss();
                }
            }
        });

        gotItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static class Builder
    {
        private Context context ;
        private String popupmessage ;
        public Builder setContext(Context context)
        {
            this.context = context ;
            return this;
        }
        public Builder setPopupMessage(String message)
        {
            this.popupmessage = message;
            return this;
        }
        public Dialog Build()
        {
            return new Dialog(this.context,
                        this.popupmessage);
        }
    }
}
