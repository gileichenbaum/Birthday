package birthday.nanit.com.happybirthday.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import birthday.nanit.com.happybirthday.R;
import birthday.nanit.com.happybirthday.utils.Constants;

public class MainFragment extends Fragment implements TextWatcher, SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String FRAGMENT_DATE_PICKER_TAG = "date_picker_fragment";
    private static final String FRAGMENT_IMAGE_SELECTION_TAG = "image_selection_fragment";
    private final static DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();

    private TextView mTxtName;
    private TextView mBtnBirthday;
    private ImageView mImageView;
    private TextView mBtnShowBirthday;

    private DatePickerFragment mDatePickerFragment;
    private ImageSelectionFragment mImageSelectionFragment;

    private SharedPreferences mSharedPreferences;

    private long mBirthDate;
    private String mName;
    private String mImageUri;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            mDatePickerFragment = (DatePickerFragment) fragmentManager.findFragmentByTag(FRAGMENT_DATE_PICKER_TAG);
            mImageSelectionFragment = (ImageSelectionFragment) fragmentManager.findFragmentByTag(FRAGMENT_IMAGE_SELECTION_TAG);
        }

        if (mDatePickerFragment == null) {
            mDatePickerFragment = new DatePickerFragment();
        }

        if (mImageSelectionFragment == null) {
            mImageSelectionFragment = new ImageSelectionFragment();
        }

        mDatePickerFragment.setShowsDialog(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTxtName = view.findViewById(R.id.main_fragment_txt_name);
        mBtnBirthday = view.findViewById(R.id.main_fragment_btn_birthday_txt_date);
        mImageView = view.findViewById(R.id.main_fragment_img);
        mBtnShowBirthday = view.findViewById(R.id.main_fragment_btn_show_birthday_screen);

        mTxtName.addTextChangedListener(this);

        mBtnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(view);
            }
        });

        mBtnShowBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    mImageSelectionFragment.show(fragmentManager, FRAGMENT_IMAGE_SELECTION_TAG);
                }
            }
        });

        restoreFromPrefs(mSharedPreferences);
    }

    private void restoreFromPrefs(SharedPreferences preferences) {
        setBirthDate(preferences.getLong(Constants.PREF_BIRTH_DATE, 0L));
        setName(preferences.getString(Constants.PREF_NAME, null));
        setImageUri(preferences.getString(Constants.PREF_IMAGE_URI, null));
        onPropertyChanged();
    }

    private void showDatePicker(View view) {
        final FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            mDatePickerFragment.show(fragmentManager, FRAGMENT_DATE_PICKER_TAG);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTxtName.removeTextChangedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        mSharedPreferences.edit().putString(Constants.PREF_NAME, editable.toString()).apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (Constants.PREF_BIRTH_DATE.equals(key)) {
            setBirthDate(mSharedPreferences.getLong(Constants.PREF_BIRTH_DATE, 0L));
        } else if (Constants.PREF_NAME.equals(key)) {
            setName(mSharedPreferences.getString(Constants.PREF_NAME, null));
        } else if (Constants.PREF_IMAGE_URI.equals(key)) {
            setImageUri(mSharedPreferences.getString(Constants.PREF_IMAGE_URI, null));
        }

        onPropertyChanged();
    }

    private void setBirthDate(long dateMillis) {
        mBirthDate = dateMillis;

        if (dateMillis > 0) {
            mBtnBirthday.setText(DATE_FORMAT.format(new Date(dateMillis)));
        } else {
            mBtnBirthday.setText(null);
        }
    }

    private void setImageUri(@Nullable String uri) {

        mImageUri = uri;

        if (TextUtils.isEmpty(uri)) {
            mImageView.setImageResource(R.drawable.ic_photo_white);
        } else {
            mImageView.setImageURI(Uri.parse(uri));
        }
    }

    public void setName(@Nullable String name) {
        mName = name;
        if (!TextUtils.isEmpty(mName) && !mName.contentEquals(mTxtName.getText())) {
            mTxtName.setText(mName);
        }
    }

    private void onPropertyChanged() {
        boolean hasAllData = mBirthDate > 0 && !TextUtils.isEmpty(mName);
        mBtnShowBirthday.setEnabled(hasAllData);
    }
}
