package birthday.nanit.com.happybirthday.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Project: Nanit
 * Created by Gil on 5/15/2018.
 */
public class FileProvider extends android.support.v4.content.FileProvider {

    private static final String IMAGE_FILE_NAME = "local_image";

    @Nullable
    public static File getImageFile(@NonNull Context context) {

        final File imagesDir = new File(context.getFilesDir(), "images");

        if (imagesDir.exists() || imagesDir.mkdirs()) {
            File imageFile = new File(imagesDir, IMAGE_FILE_NAME);
            try {
                if (imageFile.exists() || imageFile.createNewFile()) {
                    return imageFile;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
