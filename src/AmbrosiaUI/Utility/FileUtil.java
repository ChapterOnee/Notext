package AmbrosiaUI.Utility;

import AmbrosiaUI.Widgets.Placements.VerticalPlacement;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *  A class containing static macros for unification of file operations
 */
public class FileUtil {
    public static File createFolder(String... path){
        File nw = new File(joinPath(path));

        if(nw.mkdir()) {
            return nw;
        }
        return null;
    }

    public static boolean renameFile(String before, String after){
        File file = new File(before);
        return renameFile(file, after);
    }
    public static boolean renameFile(File before, String after){
        return before.renameTo(new File(joinPath(before.getParentFile().getAbsolutePath(),after)));
    }

    public static String joinPath(String... paths){
        if(paths.length < 2){
            return paths[0];
        }

        String out = paths[0];

        int i = 0;
        for(String path: paths){
            if(i == 0){
                i++;
                continue;
            }
            out = Paths.get(out, path).toString();
            i++;
        }

        return out;
    }
}
