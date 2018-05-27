package birthday.nanit.com.happybirthday.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import birthday.nanit.com.happybirthday.R;
import birthday.nanit.com.happybirthday.TransparentPositionLayout;
import birthday.nanit.com.happybirthday.utils.PreferenceUtil;

import static birthday.nanit.com.happybirthday.ui.main.MainFragment.FRAGMENT_IMAGE_SELECTION_TAG;

/**
 * Project: Nanit
 * Created by Gil on 5/16/2018.
 */
public class BirthdayActivity extends AppCompatActivity implements ImageSelectionFragment.ImageSelectionListener {

    private static final String SAVED_INSTANCE_KEY_LAYOUT_ID = "layout_id";
    private static final Map<Integer, Integer> NUMBER_IMAGES;

    static {
        @SuppressLint("UseSparseArrays")
        Map<Integer, Integer> numberDrawableResIds = new HashMap<>();
        numberDrawableResIds.put(0, R.drawable.n0);
        numberDrawableResIds.put(1, R.drawable.n1);
        numberDrawableResIds.put(2, R.drawable.n2);
        numberDrawableResIds.put(3, R.drawable.n3);
        numberDrawableResIds.put(4, R.drawable.n4);
        numberDrawableResIds.put(5, R.drawable.n5);
        numberDrawableResIds.put(6, R.drawable.n6);
        numberDrawableResIds.put(7, R.drawable.n7);
        numberDrawableResIds.put(8, R.drawable.n8);
        numberDrawableResIds.put(9, R.drawable.n9);
        numberDrawableResIds.put(10, R.drawable.n10);
        numberDrawableResIds.put(11, R.drawable.n11);
        numberDrawableResIds.put(12, R.drawable.n12);
        numberDrawableResIds.put(9999, R.drawable.n1_half);
        NUMBER_IMAGES = Collections.unmodifiableMap(numberDrawableResIds);
    }

    private ImageSelectionFragment mImageSelectionFragment;
    private SharedPreferences mSharedPreferences;
    private int mLayoutId;
    private TransparentPositionLayout mTransparentPositionLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.birthday_activity);

        mTransparentPositionLayout = findViewById(R.id.birthday_activity_background_layout);
        if (savedInstanceState != null) {
            mLayoutId = savedInstanceState.getInt(SAVED_INSTANCE_KEY_LAYOUT_ID);
            mTransparentPositionLayout.setLayoutId(mLayoutId);
        }

        TextView name = findViewById(R.id.birthday_activity_txt_name);
        name.setText(getString(R.string.today_name_is, PreferenceUtil.getName(this)));

        Calendar calendar = GregorianCalendar.getInstance();
        Calendar birthDayCalendar = GregorianCalendar.getInstance();
        birthDayCalendar.setTimeInMillis(PreferenceUtil.getBirthday(this));

        int monthDiff = (calendar.get(Calendar.YEAR) - birthDayCalendar.get(Calendar.YEAR)) * 12 + calendar.get(Calendar.MONTH) - birthDayCalendar.get(Calendar.MONTH);

        TextView ageInterval = findViewById(R.id.birthday_activity_txt_age_interval);
        ImageView number = findViewById(R.id.birthday_activity_img_number);

        Integer numberImageResId;

        if (monthDiff <= 12) {
            numberImageResId = NUMBER_IMAGES.get(monthDiff);
            ageInterval.setText(R.string.months_old);
        } else {
            numberImageResId = NUMBER_IMAGES.get(monthDiff / 12);
            ageInterval.setText(R.string.years_old);
        }

        if (numberImageResId != null && numberImageResId != 0) {
            number.setImageResource(numberImageResId);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            mImageSelectionFragment = (ImageSelectionFragment) fragmentManager.findFragmentByTag(FRAGMENT_IMAGE_SELECTION_TAG);
        }

        if (mImageSelectionFragment == null) {
            mImageSelectionFragment = new ImageSelectionFragment();
        }

        mTransparentPositionLayout.setImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSelection(view.getContext());
            }
        });

        refreshDrawable();
    }

    private void refreshDrawable() {
        final Uri imageUri = PreferenceUtil.getImageUri(this);
        if (imageUri == null) {
            mTransparentPositionLayout.setDrawable(null);
        } else {
            final BitmapDrawable drawable;
            try {
                drawable = new BitmapDrawable(getResources(), getContentResolver().openInputStream(imageUri));
                mTransparentPositionLayout.setDrawable(drawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void showImageSelection(Context context) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            mImageSelectionFragment.setListener(this);
            mImageSelectionFragment.show(fragmentManager, FRAGMENT_IMAGE_SELECTION_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_KEY_LAYOUT_ID, mLayoutId);
    }

    @Override
    public void onImageChanged() {
        refreshDrawable();
    }
}
