package com.example.mp;

import java.io.File;
import java.util.ArrayList;

final class Load {

    private Load() {}

    public static ArrayList<File> FileListByExtension(File root, String[] extensions) {

        ArrayList<File> fileList = new ArrayList<File>();
        for(File file : root.listFiles()) {
            if (file.isDirectory() && !file.isHidden())
                fileList.addAll(FileListByExtension(file, extensions));
            else
                for (String ext : extensions)
                    if (file.getName().endsWith(ext)) {
                        fileList.add(file);
                        break;
                    }
        }

        return fileList;
    }
}
