package com.example.myfile.core.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.myfile.core.model.FileItem;

/** DiffUtil dung chung cho moi adapter hien thi FileItem. */
public class FileItemDiffCallback extends DiffUtil.ItemCallback<FileItem> {

    @Override
    public boolean areItemsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
        return oldItem.getPath().equals(newItem.getPath());
    }

    @Override
    public boolean areContentsTheSame(@NonNull FileItem oldItem, @NonNull FileItem newItem) {
        return oldItem.equals(newItem);
    }
}
