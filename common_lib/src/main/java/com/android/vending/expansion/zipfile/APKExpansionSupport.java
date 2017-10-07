package com.android.vending.expansion.zipfile;
/*
 * Copyright (C) 2012 The Android Open Source Project
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
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import android.content.Context;

public class APKExpansionSupport {
    public static String[] getAPKExpansionFiles(Context ctx, int mainVersion, int patchVersion) {
        String packageName = ctx.getPackageName();
        Vector<String> ret = new Vector<>(2);
        File expPath = ctx.getObbDir();
        // Check that expansion file path exists
        if (expPath.exists()) {
            if ( mainVersion > 0 ) {
                String strMainPath = expPath + File.separator + "main." + mainVersion + "." + packageName + ".obb";
                File main = new File(strMainPath);
                if ( main.isFile() ) {
                    ret.add(strMainPath);
                }
            }
            if ( patchVersion > 0 ) {
                String strPatchPath = expPath + File.separator + "patch." + mainVersion + "." + packageName + ".obb";
                File main = new File(strPatchPath);
                if ( main.isFile() ) {
                    ret.add(strPatchPath);
                }
            }
        }
        String[] retArray = new String[ret.size()];
        ret.toArray(retArray);
        return retArray;
    }

    public static ZipResourceFile getResourceZipFile(String[] expansionFiles) throws IOException {
        ZipResourceFile apkExpansionFile = null;
        for (String expansionFilePath : expansionFiles) {
            if ( null == apkExpansionFile ) {
                apkExpansionFile = new ZipResourceFile(expansionFilePath);
            } else {
                apkExpansionFile.addPatchFile(expansionFilePath);
            }
        }
        return apkExpansionFile;
    }

    public static ZipResourceFile getAPKExpansionZipFile(Context ctx, int mainVersion, int patchVersion) throws IOException{
        String[] expansionFiles = getAPKExpansionFiles(ctx, mainVersion, patchVersion);
        return getResourceZipFile(expansionFiles);
    }
}