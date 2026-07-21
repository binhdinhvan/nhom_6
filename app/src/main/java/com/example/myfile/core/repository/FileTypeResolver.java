package com.example.myfile.core.repository;

import com.example.myfile.core.model.FileType;

/**
 * Tach viec doan loai file ra khoi repository.
 * Core cung cap ban mac dinh; thanh vien C se thay bang MimeUtils that
 * chi bang cach doi @Binds trong RepositoryModule -> khong sua repository.
 */
public interface FileTypeResolver {
    FileType resolve(String fileName, boolean isDirectory);
    String mimeTypeOf(String fileName);
}
