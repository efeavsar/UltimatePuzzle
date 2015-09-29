package com.lonict.android.puzzle;

import android.util.Log;

/**
 * Created by Efe Avsar on 02/09/2015.
 */
public class PuzzleUtils {
    public static final String TIMER_EXTRA= "xx_timer";
    public static final String MOVE_COUNT_EXTRA= "xx_move_count";
    public static final String MATRIX_WIDENESS_EXTRA= "xx_matrix_wideness";
    public static final String COLOR_COUNT_EXTRA= "xx_color_count";
    public static final String GAME_MODE_EXTRA = "xx_game_mode" ;
    public static final int MOVE_UP = 0 ;
    public static final int MOVE_DOWN = 1 ;
    public static final int MOVE_RIGHT = 2;
    public static final int MOVE_LEFT = 3 ;
    public static final int GAME_MODE_MOVE = 4 ;
    public static final int GAME_MODE_TOUCH = 5 ;
    public static final int DIALOG_TYPE_LEVEL_END = 6 ;
    public static final int DIALOG_TYPE_LEVEL_ALL_END = 7 ;
    public static final int DIALOG_TYPE_PAUSE = 8 ;
    public static final int DIALOG_TYPE_TIMER_END = 9 ;
    public static final int DIALOG_TYPE_INSANITY_INTRO = 10 ;
    public static final int DIALOG_TYPE_MOVE_COUNT_END = 11 ;

    public static final boolean IS_DEBUG_ON = false;

    public static void Log(String tag , String desc)
    {
        if (IS_DEBUG_ON) Log.d(tag,desc);
    }
}
