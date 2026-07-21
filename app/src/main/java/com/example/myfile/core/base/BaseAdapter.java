package com.example.myfile.core.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ABSTRACT CLASS bao ListAdapter + DiffUtil.
 * Lop con chi khai bao 2 quy tac so sanh, khong phai viet lai DiffUtil.Callback.
 *
 * @param <T>  kieu item
 * @param <VH> kieu ViewHolder
 */
public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder>
        extends ListAdapter<T, VH> {

    protected OnItemClickListener<T> clickListener;
    protected OnItemLongClickListener<T> longClickListener;

    protected BaseAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.longClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item, int position);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(T item, int position);
    }
}
