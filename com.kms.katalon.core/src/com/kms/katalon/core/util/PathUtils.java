package com.kms.katalon.core.util;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class PathUtils {

    private PathUtils() {
        // Disable default constructor
    }

    public static String absoluteToRelativePath(String absolutePath, String projectPath) {
        return PathUtils.getRelativePath(absolutePath, projectPath, File.separator);
    }

    public static String relativeToAbsolutePath(String relativePath, String projectAbsolutePath) throws Exception {
        Path path = new File(relativePath).toPath();
        if (path.isAbsolute()) {
            return path.toAbsolutePath().toString();
        } else {
            Path projectPath = new File(projectAbsolutePath).toPath();
            return projectPath.resolve(path).normalize().toString();
        }
    }

    private static String getRelativePath(String targetPath, String basePath, String pathSeparator) {

        // We need the -1 argument to split to make sure we get a trailing
        // "" token if the base ends in the path separator and is therefore
        // a directory. We require directory paths to end in the path
        // separator -- otherwise they are indistinguishable from files.
        String[] base = basePath.split(Pattern.quote(pathSeparator), -1);
        String[] target = targetPath.split(Pattern.quote(pathSeparator), 0);

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        String common = "";
        int commonIndex = 0;
        for (int i = 0; i < target.length && i < base.length; i++) {
            if (target[i].equals(base[i])) {
                common += target[i] + pathSeparator;
                commonIndex++;
            } else
                break;
        }

        if (commonIndex == 0) {
            // Whoops -- not even a single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized. Return the target path.
            return targetPath;
            // This should never happen when all absolute paths
            // begin with / as in *nix.
        }

        String relative = "";
        if (base.length == commonIndex) {
            // Comment this out if you prefer that a relative path not start
            // with ./
            // relative = "." + pathSeparator;
        } else {
            int numDirsUp = base.length - commonIndex;
            // The number of directories we have to backtrack is the length of
            // the base path MINUS the number of common path elements, minus
            // one because the last element in the path isn't a directory.
            for (int i = 1; i <= (numDirsUp); i++) {
                relative += ".." + pathSeparator;
            }
        }
        relative += targetPath.substring(common.length());

        return relative;
    }
}
