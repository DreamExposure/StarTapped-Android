package org.dreamexposure.startapped.utils;

import android.os.Environment;

import org.dreamexposure.startapped.StarTappedApp;

import java.io.File;
import java.io.IOException;

/**
 * @author NovaFox161
 * Date Created: 1/2/2019
 * For Project: StarTapped
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class FileUtil {
    public static File generateTemporaryFile(String filename) throws IOException {
        String tempFileName = "20130318_010530_";

        File storageDir = StarTappedApp.getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                tempFileName,       /* prefix     "20130318_010530" */
                filename,           /* filename   "video.3gp" */
                storageDir          /* directory  "/data/sdcard/..." */
        );
    }
}
