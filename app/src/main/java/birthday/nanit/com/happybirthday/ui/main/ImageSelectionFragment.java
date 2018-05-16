package birthday.nanit.com.happybirthday.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import birthday.nanit.com.happybirthday.BuildConfig;
import birthday.nanit.com.happybirthday.R;
import birthday.nanit.com.happybirthday.utils.PreferenceUtil;

/**
 * Project: Nanit
 * Created by Gil on 5/15/2018.
 */
public class ImageSelectionFragment extends DialogFragment {


    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_IMAGE_SELECTION = 101;
    private Uri mImageFileUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fragment_image_selection_btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Context context = view.getContext();

                final File imagesDir = new File(context.getFilesDir(), "images");

                if (imagesDir.exists() || imagesDir.mkdirs()) {

                    File imageFile = new File(imagesDir, "image");

                    try {
                        if (imageFile.exists() || imageFile.createNewFile()) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            mImageFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, imageFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        view.findViewById(R.id.fragment_image_selection_btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_picture)), REQUEST_IMAGE_SELECTION);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (REQUEST_CAMERA == requestCode) {
                final Context context = getContext();
                if (context != null) {
                    PreferenceUtil.saveImageUri(context, mImageFileUri.toString());
                }
            } else if (REQUEST_IMAGE_SELECTION == requestCode) {
                Uri uri = data.getData();
                if (uri != null) {
                    final Context context = getContext();
                    if (context != null) {
                        PreferenceUtil.saveImageUri(context, uri.toString());
                    }
                }
            }
            dismiss();
        }
    }
}
