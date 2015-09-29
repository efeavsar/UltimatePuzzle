package com.lonict.android.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.WindowCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Efe Avsar on 30/08/2015.
 */
public class PuzzleMatrix  {

    private int matrix_wideness ;
    private HashMap puzzle_items;
    private Context context  ;
    private LinearLayout linear_main;
    private static LinearLayout linear_rightTop;
    private int [] item_colors ;
    DisplayMetrics display_metrics ;
    private int target_color ;
    private ImageView target_image ;
    private TextView target_text;
    private int game_mode;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private final static Random color_random = new Random();

    public PuzzleMatrix(Context context,int matrix_wideness,int[] item_colors,int game_mode)
    {
        this.matrix_wideness = matrix_wideness;
        this.item_colors= item_colors;
        this.context = context ;
        this.game_mode= game_mode;
        createMatrix();
    }

    public  int getMatrix_wideness()
    {
        return matrix_wideness;
    }
    public int[] getItem_colors()
    {
        return item_colors ;
    }
    public HashMap getPuzzle_items()
    {
        return puzzle_items;
    }
    public LinearLayout getPuzzleLinearLayout()
    {
        return linear_main;
    }
    public void recycleBitmaps()
    {
        for (Bitmap b : bitmapList) {
            b.recycle();
            b=null;
        }
        System.gc();
    }
    public void initRightTopImages()
    {
        /*Put unique colors to top right top*/
        linear_rightTop = new LinearLayout(context);
        linear_rightTop.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) ;
        params.setMargins(Math.round(display_metrics.density * 1.5F), Math.round(display_metrics.density * 1.5F),
                Math.round(display_metrics.density * 1.5F), Math.round(display_metrics.density * 1.5F));
        for (int i : item_colors)
        {
            ImageView image_right = new ImageView(context);
            linear_rightTop.addView(
                    getImageGradient(image_right,i),
                    params);
        }
    }

    public static LinearLayout getLinear_rightTop ()
    {
        return linear_rightTop;
    }
    public ImageView getRandomItemImage(int id)
    {
        PuzzleUtils.Log("xxlenght", item_colors.length + "");

        int random = color_random.nextInt(item_colors.length);
        int image_color = item_colors[random] ;
        ImageView image = new ImageView(context);
        image.setId(id);
        ImageView image_next= getImageGradient(image,image_color);
        puzzle_items.put(id, image_color);
        return image_next;
    }

    public void setNextImageDrawable(ImageView image)
    {
        int image_color =getNextColor((int) image.getTag());
        ImageView image_next= getImageGradient(image,image_color);
        puzzle_items.put(image_next.getId(), image_color);
        setTargetImageCount();
    }
    public void setPreviousImageDrawable(ImageView image)
    {
        int image_color =getPreviousColor((int) image.getTag());
        ImageView image_next= getImageGradient(image,image_color);
        puzzle_items.put(image_next.getId(), image_color);
        setTargetImageCount();
    }


    public ImageView getImageGradient(ImageView image, int image_color)
    {
        int[] colors = new int[] { image_color, context.getResources().getColor(R.color.puzzle_color_gradient)};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL,colors);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientRadius((float) (Math.sqrt(2) * 60));
        gradientDrawable.setCornerRadius(Math.round(display_metrics.density * 4F));

        Bitmap bitmap = Bitmap.createBitmap(Math.round( display_metrics.heightPixels/(1150F/100F)),
                Math.round( display_metrics.heightPixels/(1150F/100F)),
                Bitmap.Config.ARGB_8888);
        bitmapList.add(bitmap);
        Canvas canvas = new Canvas(bitmap);
        gradientDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        gradientDrawable.draw(canvas);
        image.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
        image.setTag(image_color);
        //added for correcting memory errors!
        return image;
    }

    public final void onImageClick(int id,int move_type)
    {
        PuzzleUtils.Log("xxId", id + "");
        MainActivity mainActivity = ((MainActivity) context) ;
        ImageView image_center = (ImageView) mainActivity.findViewById(id);
        ImageView image_left ;
        ImageView image_right ;
        ImageView image_bottom ;
        ImageView image_top ;

        RotateAnimation animation = new RotateAnimation(180f, 0f, image_center.getWidth() / 2, image_center.getHeight() / 2);
        animation.setRepeatCount(0);
        animation.setDuration(500);

        if (game_mode==PuzzleUtils.GAME_MODE_MOVE)

        {
            if (game_mode==PuzzleUtils.GAME_MODE_MOVE)
            {
                if (move_type==PuzzleUtils.MOVE_UP)
                {

                    for (int k=0;k<matrix_wideness;k++)
                    {
                        int box = 0 ;
                        box = (id%matrix_wideness)+k*matrix_wideness;
                        if ((id%matrix_wideness)==0) box = matrix_wideness+k*matrix_wideness;
                        setNextImageDrawable((ImageView) mainActivity.findViewById(box));
                        ((ImageView) mainActivity.findViewById(box)).startAnimation(animation);
                    }
                } else
                if (move_type==PuzzleUtils.MOVE_DOWN)
                {
                    for (int k=0;k<matrix_wideness;k++)
                    {
                        int box = 0 ;
                        box = (id%matrix_wideness)+k*matrix_wideness;
                        if ((id%matrix_wideness)==0) box = matrix_wideness+k*matrix_wideness;
                        setPreviousImageDrawable((ImageView) mainActivity.findViewById(box));
                        ((ImageView) mainActivity.findViewById(box)).startAnimation(animation);
                    }
                }

                if (move_type==PuzzleUtils.MOVE_RIGHT)
                {
                    for (int k=0;k<matrix_wideness;k++)
                    {
                        int box = 0 ;
                        if (isRightEdge(id)) {
                            box = id + (id % matrix_wideness)-k ;
                        } else
                        {
                            box = id+1-(id%matrix_wideness)+k ;
                        }
                        setNextImageDrawable((ImageView) mainActivity.findViewById(box));
                        ((ImageView) mainActivity.findViewById(box)).startAnimation(animation);
                    }
                } else if(move_type==PuzzleUtils.MOVE_LEFT)
                {
                    for (int k=0;k<matrix_wideness;k++)
                    {
                        int box = 0 ;
                        if (isRightEdge(id))
                        {
                            box = id+(id%matrix_wideness)-k ;
                        } else
                        {
                            box = id+1-(id%matrix_wideness)+k ;
                        }
                        setPreviousImageDrawable((ImageView) mainActivity.findViewById(box));
                        ((ImageView) mainActivity.findViewById(box)).startAnimation(animation);
                    }
                }
            }
        }else
        {
            switch (matrix_wideness)
            {
                case 2:
                    setNextImageDrawable((ImageView) mainActivity.findViewById(id));
                    image_center.startAnimation(animation);

                    if (isLeftEdge(id)) {
                        image_right = (ImageView) mainActivity.findViewById(id+1);
                        setNextImageDrawable(image_right);
                        image_right.startAnimation(animation);
                    }
                    if (isRightEdge(id))
                    {
                        image_left = (ImageView) mainActivity.findViewById(id-1);
                        setNextImageDrawable(image_left) ;
                        image_left.startAnimation(animation);
                    }
                    if (isBottomEdge(id))
                    {
                        image_top = (ImageView) mainActivity.findViewById(id-matrix_wideness);
                        setNextImageDrawable(image_top);
                        image_top.startAnimation(animation);
                    }
                    if (isTopEdge(id))
                    {
                        image_bottom = (ImageView) mainActivity.findViewById(id+matrix_wideness);
                        setNextImageDrawable(image_bottom);
                        image_bottom.startAnimation(animation);
                    }
                    break;
                case 3: //same as 2!
                    setNextImageDrawable((ImageView) mainActivity.findViewById(id));
                    image_center.startAnimation(animation);
                    if (isLeftEdge(id)) {
                        image_right = (ImageView) mainActivity.findViewById(id+1);
                        setNextImageDrawable(image_right);
                        image_right.startAnimation(animation);
                    }
                    if (isRightEdge(id))
                    {
                        image_left = (ImageView) mainActivity.findViewById(id-1);
                        setNextImageDrawable(image_left) ;
                        image_left.startAnimation(animation);
                    }
                    if (isBottomEdge(id))
                    {
                        image_top = (ImageView) mainActivity.findViewById(id-matrix_wideness);
                        setNextImageDrawable(image_top);
                        image_top.startAnimation(animation);
                    }
                    if (isTopEdge(id))
                    {
                        image_bottom = (ImageView) mainActivity.findViewById(id+matrix_wideness);
                        setNextImageDrawable(image_bottom);
                        image_bottom.startAnimation(animation);
                    }
                    break;
                case 4:
                    setNextImageDrawable((ImageView) mainActivity.findViewById(id));
                    image_center.startAnimation(animation);
                    //image_bottom = (ImageView) mainActivity.findViewById(id+matrix_wideness);
                    //image_top = (ImageView) mainActivity.findViewById(id-matrix_wideness);
                    if (!isLeftEdge(id)) {
                        image_left = (ImageView) mainActivity.findViewById(id-1);
                        setNextImageDrawable(image_left);
                        image_left.startAnimation(animation);
                    }
                    if (!isRightEdge(id))
                    {
                        image_right = (ImageView) mainActivity.findViewById(id+1);
                        setNextImageDrawable(image_right) ;
                        image_right.startAnimation(animation);
                    }
                    break;
                case 5:
                    setNextImageDrawable((ImageView) mainActivity.findViewById(id));
                    image_center.startAnimation(animation);
                    if (isTopEdge(id))
                    {
                        image_bottom = (ImageView) mainActivity.findViewById(id+matrix_wideness);
                        setNextImageDrawable(image_bottom);
                        image_bottom.startAnimation(animation);
                    }
                    if (isBottomEdge(id))
                    {
                        image_top = (ImageView) mainActivity.findViewById(id-matrix_wideness);
                        setNextImageDrawable(image_top);
                        image_top.startAnimation(animation);
                    } else
                    {
                        if ((id%5)>3)
                        {
                            image_left = (ImageView) mainActivity.findViewById(id-1);
                            setNextImageDrawable(image_left);
                            image_left.startAnimation(animation);
                        } else if ((id%5)!=3)
                        {
                            image_right = (ImageView) mainActivity.findViewById(id+1);
                            setNextImageDrawable(image_right);
                            image_right.startAnimation(animation);
                        }
                    }
                    break;
                case 6:
                    setNextImageDrawable((ImageView) mainActivity.findViewById(id));
                    image_center.startAnimation(animation);
                    if (isTopEdge(id))
                    {
                        image_bottom = (ImageView) mainActivity.findViewById(id+matrix_wideness);
                        setNextImageDrawable(image_bottom);
                        image_bottom.startAnimation(animation);
                    }
                    if (isBottomEdge(id))
                    {
                        image_top = (ImageView) mainActivity.findViewById(id-matrix_wideness);
                        setNextImageDrawable(image_top);
                        image_top.startAnimation(animation);
                    } else
                    {
                        if ((id%5)>3)
                        {
                            image_left = (ImageView) mainActivity.findViewById(id-1);
                            setNextImageDrawable(image_left);
                            image_left.startAnimation(animation);
                        } else if ((id%5)!=3)
                        {
                            image_right = (ImageView) mainActivity.findViewById(id+1);
                            setNextImageDrawable(image_right);
                            image_right.startAnimation(animation);
                        }
                    }
                    break;
                default:
                    //setNextImageDrawable((ImageView) mainActivity.findViewById(id));
                    image_center.startAnimation(animation);
                    if (!isLeftEdge(id))
                    {
                        image_left = (ImageView) mainActivity.findViewById(id-1);
                        setNextImageDrawable(image_left) ;
                        image_left.startAnimation(animation);
                    }
                    if (!isRightEdge(id))
                    {
                        image_right = (ImageView) mainActivity.findViewById(id+1);
                        setNextImageDrawable(image_right) ;
                        image_right.startAnimation(animation);
                    }
                    if (!isBottomEdge(id))
                    {
                        image_bottom = (ImageView) mainActivity.findViewById(id+matrix_wideness);
                        setNextImageDrawable(image_bottom);
                        image_bottom.startAnimation(animation);
                    }
                    if (!isTopEdge(id))
                    {
                        image_top = (ImageView) mainActivity.findViewById(id-matrix_wideness);
                        setNextImageDrawable(image_top);
                        image_top.startAnimation(animation);
                    }
            }
        }
        TextView move_count = (TextView) mainActivity.findViewById(R.id.textView_move_count);
        int move_count_ = new Integer(move_count.getText().toString()).intValue();
        move_count_ -- ; //= move_count_ +1 ;
        move_count.setText(move_count_ + "");
        if (move_count_==0)
        {
            mainActivity.showPopup(PuzzleUtils.DIALOG_TYPE_MOVE_COUNT_END);
            mainActivity.animateBody(true,false);
        }
        if (mainActivity.isAllTargeted())
        {
            animateAllForFinish();
        }
    }
    public void animateAllForFinish()
    {
        MainActivity mainActivity = ((MainActivity) context);
        mainActivity.animateBody(true, true);
        setLevelPreference(item_colors.length+1+(matrix_wideness*matrix_wideness));
        if (item_colors.length == 5)
        {
            setLevelPreference(2+((matrix_wideness+1)*(matrix_wideness+1)));
        }
    }

    public void setLevelPreference(int level)
    {
        MainActivity activity = (MainActivity) context;
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.pref_key_file_name),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (activity.getisTimerGame())
        {
            int max_level = sharedPref.getInt(activity.getString(R.string.pref_current_level_for_timer),0);
            if (level>max_level)
            {
                PuzzleUtils.Log("xxLevelPref","1-"+level);
                editor.putInt(activity.getString(R.string.pref_current_level_for_timer), level);
                editor.commit();
            }
        }
        else if ( activity.getisMoveCountGame())
        {
            int max_level = sharedPref.getInt(activity.getString(R.string.pref_current_level_for_move_count),0);
            if (level>max_level) {
                PuzzleUtils.Log("xxLevelPref", "2-" + level);
                editor.putInt(activity.getString(R.string.pref_current_level_for_move_count), level);
                editor.commit();
            }
        }
    }

    public int getPreviousColor(int color_resource_id)
    {
        try
        {
            PuzzleUtils.Log("xxNextColor", color_resource_id + "");
            int index = 0 ;
            int i=0 ;
            for (i=0 ; i< item_colors.length;i++)
            {
                if (item_colors[i]==color_resource_id)
                    index = i ;
            }
            return item_colors[index-1];
        }catch (ArrayIndexOutOfBoundsException n)
        {
            PuzzleUtils.Log("xxColor", "Not Found");
            return item_colors[item_colors.length-1];
        }
    }
    public int getNextColor(int color_resource_id)
    {
        try
        {
            PuzzleUtils.Log("xxNextColor", color_resource_id + "");
            int index = 0 ;
            int i=0 ;
            for (i=0 ; i< item_colors.length;i++)
            {
                if (item_colors[i]==color_resource_id)
                    index = i ;
            }
           return item_colors[index+1];
        }catch (ArrayIndexOutOfBoundsException n)
        {
            PuzzleUtils.Log("xxColor", "Not Found");
           return item_colors[0];
        }
    }
    public void createMatrix()
    {
        display_metrics = new DisplayMetrics();
        ((MainActivity)context).getWindowManager().getDefaultDisplay().getMetrics(display_metrics);

        int square = matrix_wideness*matrix_wideness;
        puzzle_items = new HashMap();
        linear_main = new LinearLayout(context);
        linear_main.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params_child = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linear_child = new LinearLayout(context);
        linear_child.setOrientation(LinearLayout.HORIZONTAL);
        linear_child.setBackgroundColor(((MainActivity) context).getResources().getColor(R.color.puzzle_background_color));
        LinearLayout.LayoutParams params_image = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                                 ViewGroup.LayoutParams.WRAP_CONTENT);
        params_image.setMargins(Math.round(display_metrics.density * 1.5F), Math.round(display_metrics.density * 1.5F),
                Math.round(display_metrics.density * 1.5F), Math.round(display_metrics.density * 1.5F));
        for (int i=1 ; i<=square;i++ )
        {
            //puzzle_items.put(i, image);
            ImageView image = getRandomItemImage(i);

            if (game_mode==PuzzleUtils.GAME_MODE_TOUCH)
            {
                //Normal game mode
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onImageClick(v.getId(),0);
                    }
                });
            }else if (game_mode == PuzzleUtils.GAME_MODE_MOVE)
                //MOVE LIKE CUBE
            {
                View.OnTouchListener touchListener = new View.OnTouchListener() {
                    float startY =0  ;
                    float endY =0 ;
                    float startX =0  ;
                    float endX =0 ;
                    float trashold =75 ;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {


                        if ( event.getAction() == MotionEvent.ACTION_DOWN )
                        {
                            startY = event.getRawY();
                            startX = event.getRawX();
                        }
                        if ( event.getAction() == MotionEvent.ACTION_UP )
                        {

                            endY = event.getRawY() ;
                            endX = event.getRawX() ;
                            if (endY-startY>trashold)
                            {
                                //DOWN
                                onImageClick(v.getId(),PuzzleUtils.MOVE_DOWN);
                            }else if (startY-endY >trashold)
                            {
                                //UP
                                onImageClick(v.getId(),PuzzleUtils.MOVE_UP);
                            }else if (endX-startX>trashold) {
                                //RIGHT
                                onImageClick(v.getId(),PuzzleUtils.MOVE_RIGHT);

                            }else if (startX-endX>trashold)
                            {
                                //LEFT
                                onImageClick(v.getId(),PuzzleUtils.MOVE_LEFT);
                            }
                        }
                        return true;
                    }
                };
                image.setOnTouchListener(touchListener);
            }
            linear_child.addView(image, params_image);

            if(isRightEdge(i)&&i>1)
            {
                linear_main.addView(linear_child, params_child);
                linear_child = new LinearLayout(context);
                linear_child.setBackgroundColor( ((MainActivity)context).getResources().getColor(R.color.puzzle_background_color) );
                linear_child.setOrientation(LinearLayout.HORIZONTAL);
            }
        }
        linear_main.addView(linear_child, params_child);
        target_text= (TextView) ((MainActivity) context).findViewById(R.id.textView_target_count) ;
        target_image = getRandomItemImage(-99);
        target_color = (int)target_image.getTag();

        ((ImageView) ((MainActivity) context).findViewById(R.id.imageView_target_color))
                .setImageDrawable(target_image.getDrawable());
        ((ImageView) ((MainActivity) context).findViewById(R.id.imageView_target_color))
                .setTag(target_color);
        setTargetImageCount();
        initRightTopImages();
    }

    public void setTargetImageCount()
    {
        int count = 0 ;
        //except last one -1 , total 82
        for(int i =1;i<=puzzle_items.size()-1;i++ )
        {
            if ((int)puzzle_items.get(i)==target_color )
            {
                count++;
            }
        }
        target_text.setText(count+"");
    }

    public boolean isRightEdge(int item_index)
    {
        if ((item_index%matrix_wideness)==0 ) return true ;
        return false;
    }
    public boolean isLeftEdge(int item_index)
    {
        if ((item_index%matrix_wideness)==1 ) return true ;
        return false;
    }
    public boolean isBottomEdge(int item_index)
    {
        if ( item_index>(matrix_wideness*matrix_wideness-matrix_wideness) ) return true ;
        return false;
    }
    public boolean isTopEdge(int item_index)
    {
        if (item_index<matrix_wideness+1) return true ;
        return false;
    }
    public static class Builder
    {
        private int matrix_wideness ;
        private int[] item_colors;
        private Context context;
        private int game_mode ;

        public Builder setGameMode(int game_mode)
        {
            this.game_mode = game_mode;
            return this;
        }
        public Builder setMatrix_wideness(int matrix_wideness)
        {
            this.matrix_wideness = matrix_wideness;
            return this;
        }
        public Builder setItem_colors(int[] item_colors)
        {
            this.item_colors = item_colors;
            return this;
        }
        public Builder setContext(Context context)
        {
            this.context = context;
            return this ;
        }
        public PuzzleMatrix Build(Context context)
        {
            return new PuzzleMatrix(this.context,
                                    this.matrix_wideness,
                                    this.item_colors,
                                    this.game_mode
                                   );
            //return PuzzleMatrix
        }
    }
}
