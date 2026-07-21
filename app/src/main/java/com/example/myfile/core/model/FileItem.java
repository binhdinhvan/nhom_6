package com.example.myfile.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * Model bat bien dai dien cho 1 file/folder.
 *
 * QUY TAC: tang UI (Activity/Fragment/Adapter) CHI duoc lam viec voi FileItem,
 * KHONG duoc import java.io.File truc tiep.
 */
public final class FileItem implements Parcelable {

    private final String path;
    private final String name;
    private final long size;
    private final long lastModified;
    private final boolean directory;
    private final boolean hidden;
    private final FileType type;
    private final String mimeType;

    public FileItem(@NonNull String path,
                    @NonNull String name,
                    long size,
                    long lastModified,
                    boolean directory,
                    boolean hidden,
                    @NonNull FileType type,
                    @Nullable String mimeType) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        this.directory = directory;
        this.hidden = hidden;
        this.type = type;
        this.mimeType = mimeType;
    }

    public String getPath()        { return path; }
    public String getName()        { return name; }
    public long getSize()          { return size; }
    public long getLastModified()  { return lastModified; }
    public boolean isDirectory()   { return directory; }
    public boolean isHidden()      { return hidden; }
    public FileType getType()      { return type; }
    public String getMimeType()    { return mimeType; }

    /** Chi tang data/domain duoc dung. UI tuyet doi khong goi. */
    public File toFile() {
        return new File(path);
    }

    @Nullable
    public String getParentPath() {
        return new File(path).getParent();
    }

    /** Phan mo rong viet thuong, khong co dau cham. Rong neu la folder. */
    public String getExtension() {
        if (directory) return "";
        int dot = name.lastIndexOf('.');
        return (dot <= 0 || dot == name.length() - 1)
                ? "" : name.substring(dot + 1).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileItem)) return false;
        FileItem other = (FileItem) o;
        return path.equals(other.path)
                && size == other.size
                && lastModified == other.lastModified;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, size, lastModified);
    }

    @NonNull
    @Override
    public String toString() {
        return "FileItem{" + path + ", dir=" + directory + "}";
    }

    // ---------- Parcelable ----------

    protected FileItem(Parcel in) {
        path = in.readString();
        name = in.readString();
        size = in.readLong();
        lastModified = in.readLong();
        directory = in.readByte() != 0;
        hidden = in.readByte() != 0;
        type = FileType.valueOf(in.readString());
        mimeType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(size);
        dest.writeLong(lastModified);
        dest.writeByte((byte) (directory ? 1 : 0));
        dest.writeByte((byte) (hidden ? 1 : 0));
        dest.writeString(type.name());
        dest.writeString(mimeType);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<FileItem> CREATOR = new Creator<FileItem>() {
        @Override public FileItem createFromParcel(Parcel in) { return new FileItem(in); }
        @Override public FileItem[] newArray(int size) { return new FileItem[size]; }
    };
}
