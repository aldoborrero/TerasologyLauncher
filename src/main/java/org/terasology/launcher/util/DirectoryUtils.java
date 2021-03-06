/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.launcher.util;

import org.terasology.launcher.util.windows.SavedGamesPathFinder;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;

public final class DirectoryUtils {

    public static final String LAUNCHER_APPLICATION_DIR_NAME = "TerasologyLauncher";
    public static final String GAME_APPLICATION_DIR_NAME = "Terasology";
    public static final String GAME_DATA_DIR_NAME = "Terasology";
    public static final String TEMP_DIR_NAME = "temp";
    public static final String CACHE_DIR_NAME = "cache";

    private static final String PROPERTY_USER_HOME = "user.home";
    private static final String ENV_APPDATA = "APPDATA";
    private static final String MAC_PATH = "Library/Application Support/";

    private DirectoryUtils() {
    }

    public static void checkDirectory(final File directory) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Can not create directory! " + directory);
        }

        if (!directory.isDirectory()) {
            throw new IOException("Directory is not a directory! " + directory);
        }

        if (!directory.canRead() || !directory.canWrite()) {
            throw new IOException("Can not read from or write into directory! " + directory);
        }
    }

    /**
     * Checks if game data is stored in the installation directory.
     */
    public static boolean containsGameData(final File gameInstallationPath) {
        if ((gameInstallationPath == null) || !gameInstallationPath.exists() || !gameInstallationPath.isDirectory()) {
            return false;
        }

        final File[] files = gameInstallationPath.listFiles();
        if ((files != null) && (files.length > 0)) {
            for (File child : files) {
                if (child.isDirectory()
                    && (child.getName().equals("SAVED_WORLDS")
                    || child.getName().equals("worlds")
                    || child.getName().equals("saves")
                    || child.getName().equals("screens")
                    || child.getName().equals("screenshots"))
                    && containsFiles(child)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean containsFiles(final File directory) {
        if ((directory == null) || !directory.exists() || !directory.isDirectory()) {
            return false;
        }

        final File[] files = directory.listFiles();
        if ((files != null) && (files.length > 0)) {
            for (File child : files) {
                if (child.isDirectory()) {
                    if (containsFiles(child)) {
                        return true;
                    }
                } else if (child.isFile()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Should only be executed once at the start.
     */
    public static File getApplicationDirectory(final OperatingSystem os, final String applicationName) {
        final File userHome = new File(System.getProperty(PROPERTY_USER_HOME, "."));
        File applicationDirectory;

        if (os.isWindows()) {
            final String envAppData = System.getenv(ENV_APPDATA);
            if ((envAppData != null) && (envAppData.length() > 0)) {
                applicationDirectory = new File(envAppData, applicationName + '\\');
            } else {
                applicationDirectory = new File(userHome, applicationName + '\\');
                // Alternatives :
                //   System.getenv("HOME")
                //   System.getenv("USERPROFILE")
                //   System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH")
            }
        } else if (os.isUnix()) {
            applicationDirectory = new File(userHome, '.' + applicationName.toLowerCase() + '/');
        } else if (os.isMac()) {
            applicationDirectory = new File(userHome, MAC_PATH + applicationName + '/');
        } else {
            applicationDirectory = new File(userHome, applicationName + '/');
        }

        return applicationDirectory;
    }

    /**
     * Should only be executed once at the start.
     */
    public static File getGameDataDirectory(final OperatingSystem os) {
        final File userHome = new File(System.getProperty(PROPERTY_USER_HOME, "."));
        File gameDataDirectory;

        if (os.isWindows()) {
            File path = null;

            final String savedGamesPath = SavedGamesPathFinder.findSavedGamesPath();
            if (savedGamesPath != null) {
                path = new File(savedGamesPath);
            }

            if (path == null) {
                final String documentsPath = SavedGamesPathFinder.findDocumentsPath();
                if (documentsPath != null) {
                    path = new File(documentsPath);
                }
            }
            if (path == null) {
                path = new JFileChooser().getFileSystemView().getDefaultDirectory();
            }

            gameDataDirectory = new File(path, GAME_DATA_DIR_NAME + '\\');
        } else if (os.isUnix()) {
            gameDataDirectory = new File(userHome, '.' + GAME_DATA_DIR_NAME.toLowerCase() + '/');
        } else if (os.isMac()) {
            gameDataDirectory = new File(userHome, MAC_PATH + GAME_DATA_DIR_NAME + '/');
        } else {
            gameDataDirectory = new File(userHome, GAME_DATA_DIR_NAME + '/');
        }

        return gameDataDirectory;
    }
}
