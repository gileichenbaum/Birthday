package birthday.nanit.com.happybirthday;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class TransparentPositionLayout extends FrameLayout {

    private final static List<Integer[]> VIEW_OPTIONS;

    private final static int INDEX_BACKGROUND = 0;
    private final static int INDEX_CAMERA_ICON = 1;
    private final static int INDEX_PLACE_HOLDER = 2;

    private static final Integer[] OPTION_1 = new Integer[]{R.drawable.android_elephant_popup, R.drawable.camera_icon_yellow, R.drawable.default_place_holder_yellow};
    private static final Integer[] OPTION_2 = new Integer[]{R.drawable.android_fox_popup, R.drawable.camera_icon_green, R.drawable.default_place_holder_green};
    private static final Integer[] OPTION_3 = new Integer[]{R.drawable.android_pelican_popup, R.drawable.camera_icon_blue, R.drawable.default_place_holder_blue};

    static {
        List<Integer[]> options = new ArrayList<>();
        options.add(OPTION_1);
        options.add(OPTION_2);
        options.add(OPTION_3);
        VIEW_OPTIONS = Collections.unmodifiableList(options);
    }

    private static final String SAVED_INSTANCE_KEY_LAYOUT_ID = "layout_id";
    private int mLayoutId;

    private Drawable mDrawable;
    private ImageView mBtnCamera;
    private Drawable mBackground;

    public TransparentPositionLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public TransparentPositionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TransparentPositionLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TransparentPositionLayout, defStyle, 0);


        if (a.hasValue(R.styleable.TransparentPositionLayout_drawable)) {
            mDrawable = a.getDrawable(R.styleable.TransparentPositionLayout_drawable);
            if (mDrawable != null) {
                mDrawable.setCallback(this);
            }
        }

        int layoutMode = a.getInt(R.styleable.TransparentPositionLayout_displayMode, -1);

        a.recycle();

        mBtnCamera = new ImageView(getContext());
        mBtnCamera.setScaleType(ImageView.ScaleType.CENTER);
        addView(mBtnCamera);
        final ViewGroup.LayoutParams layoutParams = mBtnCamera.getLayoutParams();
        layoutParams.width = (int) getResources().getDimension(R.dimen.camera_button_width);
        layoutParams.height = (int) getResources().getDimension(R.dimen.camera_button_height);

        final int layoutId = isInEditMode() && layoutMode > -1 ? layoutMode : new Random(System.currentTimeMillis()).nextInt(VIEW_OPTIONS.size());
        setLayoutId(layoutId);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
        if (mBackground != null) {
            mBackground.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setBackground(Drawable background) {
        mBackground = background;
        invalidate();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        mBackground = background;
        invalidate();
    }

    public void setDrawable(Drawable drawable) {
        if (drawable == null) {
            mDrawable = ResourcesCompat.getDrawable(getResources(), VIEW_OPTIONS.get(mLayoutId)[INDEX_PLACE_HOLDER], null);
        } else {
            mDrawable = drawable;
        }
        refreshPositions(getWidth(), getHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBackground.setBounds(0, 0, w, h);
        refreshPositions(w, h);

    }

    private void refreshPositions(int w, int h) {

        if (w > 0 && h > 0) {

            final Drawable background = mBackground;

            if (background instanceof BitmapDrawable) {

                background.setBounds(0, 0, w, h);
                Bitmap bitmap = ((BitmapDrawable) background).getBitmap();

                if (bitmap != null) {

                    int imageWidth = bitmap.getWidth();
                    int imageHeight = bitmap.getHeight();

                    float widthRatio = w / (float) imageWidth;
                    float heightRatio = h / (float) imageHeight;

                    Rect transparentBounds = getTransparentBounds(bitmap);

                    final int drawableLeft = (int) (transparentBounds.left * widthRatio);
                    final int drawableTop = (int) (transparentBounds.top * heightRatio);
                    final int drawableRight = (int) (transparentBounds.right * widthRatio);
                    final int drawableBottom = (int) (transparentBounds.bottom * heightRatio);

                    if (mDrawable != null) {
                        mDrawable.setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
                    }

                    final MarginLayoutParams layoutParams = (MarginLayoutParams) mBtnCamera.getLayoutParams();
                    layoutParams.topMargin = drawableTop;
                    layoutParams.leftMargin = (int) (transparentBounds.right * widthRatio) - layoutParams.width;
                }
            }
        }
    }

    private Rect getTransparentBounds(Bitmap image) {

//        long now = System.currentTimeMillis();

        final int imageHeight = image.getHeight();
        final int imageWidth = image.getWidth();

        int top = imageHeight;
        int left = imageWidth;
        int right = 0;
        int bottom = 0;

        int x, y;

        for (x = 0; x < imageWidth; x++) {
            for (y = 0; y < imageHeight; y++) {
                if (Color.alpha(image.getPixel(x, y)) < 255) {

                    if (top > y) {
                        top = y;
                    }

                    if (bottom < y) {
                        bottom = y;
                    }

                    if (left > x) {
                        left = x;
                    }

                    if (right < x) {
                        right = x;
                    }
                }
            }
        }

//        Log.i("bounds", "took " + (System.currentTimeMillis() - now));

        return new Rect(left, top, right, bottom);
    }

    public void setLayoutId(int layoutId) {
        if (VIEW_OPTIONS.size() > layoutId) {
            mLayoutId = layoutId;
            Integer[] layoutValues = VIEW_OPTIONS.get(layoutId);
            mBackground = ResourcesCompat.getDrawable(getResources(), layoutValues[INDEX_BACKGROUND], null);
            mBtnCamera.setImageResource(layoutValues[INDEX_CAMERA_ICON]);
            mDrawable = ResourcesCompat.getDrawable(getResources(), layoutValues[INDEX_PLACE_HOLDER], null);
            refreshPositions(getWidth(), getHeight());
            invalidate();
        }
    }

    public void setImageClickListener(OnClickListener listener) {
        mBtnCamera.setOnClickListener(listener);
    }
}
