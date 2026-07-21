package com.example.myfile.core.repository;

import com.example.myfile.core.model.FileType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/** Ban toi thieu de core chay doc lap. C se thay the bang MimeUtils day du. */
@Singleton
public class DefaultFileTypeResolver implements FileTypeResolver {

    private static final Set<String> IMAGE   = set("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic");
    private static final Set<String> VIDEO   = set("mp4", "mkv", "avi", "mov", "3gp", "webm");
    private static final Set<String> AUDIO   = set("mp3", "wav", "flac", "aac", "ogg", "m4a");
    private static final Set<String> ARCHIVE = set("zip", "rar", "7z", "tar", "gz");
    private static final Set<String> DOC     = set("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx");
    private static final Set<String> TEXT    = set("txt", "log", "json", "xml", "md", "csv");

    @Inject
    public DefaultFileTypeResolver() { }

    @Override
    public FileType resolve(String fileName, boolean isDirectory) {
        if (isDirectory) return FileType.FOLDER;
        String ext = extensionOf(fileName);
        if (IMAGE.contains(ext))   return FileType.IMAGE;
        if (VIDEO.contains(ext))   return FileType.VIDEO;
        if (AUDIO.contains(ext))   return FileType.AUDIO;
        if (ARCHIVE.contains(ext)) return FileType.ARCHIVE;
        if (DOC.contains(ext))     return FileType.DOCUMENT;
        if (TEXT.contains(ext))    return FileType.TEXT;
        if ("apk".equals(ext))     return FileType.APK;
        return FileType.OTHER;
    }

    @Override
    public String mimeTypeOf(String fileName) {
        switch (resolve(fileName, false)) {
            case IMAGE:  return "image/*";
            case VIDEO:  return "video/*";
            case AUDIO:  return "audio/*";
            case TEXT:   return "text/plain";
            default:     return "*/*";
        }
    }

    private static String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        return (dot <= 0 || dot == name.length() - 1)
                ? "" : name.substring(dot + 1).toLowerCase();
    }

    private static Set<String> set(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
