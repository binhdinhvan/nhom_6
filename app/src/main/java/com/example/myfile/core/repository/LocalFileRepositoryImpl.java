package com.example.myfile.core.repository;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfile.core.model.FileItem;
import com.example.myfile.core.model.OperationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Hien thuc FileRepository bang java.io.File (hop le voi targetSdk 29
 * + requestLegacyExternalStorage=true).
 *
 * Neu sau nay doi sang SAF/MediaStore, chi can viet class moi implement
 * FileRepository va doi binding trong RepositoryModule.
 */
@Singleton
public class LocalFileRepositoryImpl implements FileRepository {

    private static final int BUFFER_SIZE = 8 * 1024;

    private final FileTypeResolver typeResolver;

    @Inject
    public LocalFileRepositoryImpl(FileTypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    @Override
    public OperationResult<List<FileItem>> listFiles(@NonNull String directoryPath, boolean showHidden) {
        File dir = new File(directoryPath);
        if (!dir.exists())      return OperationResult.error(new IOException("Duong dan khong ton tai"));
        if (!dir.isDirectory()) return OperationResult.error(new IOException("Khong phai thu muc"));
        if (!dir.canRead())     return OperationResult.error(new SecurityException("Khong co quyen doc"));

        File[] children = dir.listFiles();
        if (children == null) return OperationResult.error(new IOException("Khong doc duoc thu muc"));

        List<FileItem> result = new ArrayList<>(children.length);
        for (File f : children) {
            if (!showHidden && f.isHidden()) continue;
            result.add(map(f));
        }
        return OperationResult.success(result);
    }

    @Override
    public OperationResult<FileItem> getFile(@NonNull String path) {
        File f = new File(path);
        if (!f.exists()) return OperationResult.error(new IOException("File khong ton tai"));
        return OperationResult.success(map(f));
    }

    @Override
    public String getRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public boolean exists(@NonNull String path) {
        return new File(path).exists();
    }

    @Override
    public long calculateSize(@NonNull FileItem item) {
        return sizeOf(item.toFile());
    }

    @Override
    public String generateUniqueName(@NonNull String parentPath, @NonNull String desiredName) {
        File target = new File(parentPath, desiredName);
        if (!target.exists()) return desiredName;

        String base = desiredName;
        String ext = "";
        int dot = desiredName.lastIndexOf('.');
        if (dot > 0) {
            base = desiredName.substring(0, dot);
            ext = desiredName.substring(dot);
        }
        int index = 1;
        String candidate;
        do {
            candidate = base + " (" + index + ")" + ext;
            index++;
        } while (new File(parentPath, candidate).exists() && index < 1000);
        return candidate;
    }

    @Override
    public OperationResult<FileItem> createFolder(@NonNull String parentPath, @NonNull String name) {
        String safeName = generateUniqueName(parentPath, name);
        File folder = new File(parentPath, safeName);
        if (!folder.mkdirs()) return OperationResult.error(new IOException("Khong tao duoc thu muc"));
        return OperationResult.success(map(folder));
    }

    @Override
    public OperationResult<FileItem> rename(@NonNull FileItem item, @NonNull String newName) {
        File src = item.toFile();
        File parent = src.getParentFile();
        if (parent == null) return OperationResult.error(new IOException("Khong xac dinh duoc thu muc cha"));

        File dest = new File(parent, newName);
        if (dest.exists()) return OperationResult.error(new IOException("Ten da ton tai"));
        if (!src.renameTo(dest)) return OperationResult.error(new IOException("Doi ten that bai"));
        return OperationResult.success(map(dest));
    }

    @Override
    public OperationResult<Boolean> delete(@NonNull FileItem item) {
        boolean ok = deleteRecursive(item.toFile());
        return ok ? OperationResult.success(true)
                  : OperationResult.error(new IOException("Xoa that bai: " + item.getName()));
    }

    @Override
    public OperationResult<FileItem> copy(@NonNull FileItem source,
                                          @NonNull String destDirPath,
                                          @Nullable ProgressCallback listener) {
        File src = source.toFile();
        String uniqueName = generateUniqueName(destDirPath, source.getName());
        File dest = new File(destDirPath, uniqueName);

        if (isSubPath(src, new File(destDirPath))) {
            return OperationResult.error(new IOException("Khong the copy thu muc vao chinh no"));
        }
        try {
            long total = sizeOf(src);
            long[] copied = new long[]{0};
            copyRecursive(src, dest, total, copied, listener);
            if (listener != null && listener.isCancelled()) {
                deleteRecursive(dest);
                return OperationResult.cancelled();
            }
            return OperationResult.success(map(dest));
        } catch (IOException e) {
            deleteRecursive(dest);
            return OperationResult.error(e);
        }
    }

    @Override
    public OperationResult<FileItem> move(@NonNull FileItem source, @NonNull String destDirPath) {
        File src = source.toFile();
        File dest = new File(destDirPath, generateUniqueName(destDirPath, source.getName()));

        if (isSubPath(src, new File(destDirPath))) {
            return OperationResult.error(new IOException("Khong the di chuyen thu muc vao chinh no"));
        }
        // Cung volume -> rename() rat nhanh
        if (src.renameTo(dest)) return OperationResult.success(map(dest));

        // Khac volume -> copy roi xoa
        OperationResult<FileItem> copyResult = copy(source, destDirPath, null);
        if (!copyResult.isSuccess()) return copyResult;
        if (!deleteRecursive(src)) {
            return OperationResult.error(new IOException("Da copy nhung khong xoa duoc ban goc"));
        }
        return copyResult;
    }

    // ---------------- private helpers ----------------

    private FileItem map(File f) {
        boolean isDir = f.isDirectory();
        return new FileItem(
                f.getAbsolutePath(),
                f.getName(),
                isDir ? 0 : f.length(),
                f.lastModified(),
                isDir,
                f.isHidden(),
                typeResolver.resolve(f.getName(), isDir),
                isDir ? null : typeResolver.mimeTypeOf(f.getName())
        );
    }

    private long sizeOf(File f) {
        if (f.isFile()) return f.length();
        File[] children = f.listFiles();
        if (children == null) return 0;
        long total = 0;
        for (File c : children) total += sizeOf(c);
        return total;
    }

    private boolean deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) {
                    if (!deleteRecursive(c)) return false;
                }
            }
        }
        return f.delete();
    }

    private void copyRecursive(File src, File dest, long total, long[] copied,
                               @Nullable ProgressCallback listener) throws IOException {
        if (listener != null && listener.isCancelled()) return;

        if (src.isDirectory()) {
            if (!dest.exists() && !dest.mkdirs()) {
                throw new IOException("Khong tao duoc thu muc " + dest.getName());
            }
            File[] children = src.listFiles();
            if (children != null) {
                for (File c : children) {
                    copyRecursive(c, new File(dest, c.getName()), total, copied, listener);
                    if (listener != null && listener.isCancelled()) return;
                }
            }
            return;
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                if (listener != null && listener.isCancelled()) return;
                out.write(buffer, 0, read);
                copied[0] += read;
                if (listener != null) listener.onBytesCopied(copied[0], total);
            }
            out.flush();
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    /** Chan truong hop copy/move thu muc vao chinh no hoac vao con cua no. */
    private boolean isSubPath(File parent, File child) {
        if (!parent.isDirectory()) return false;
        String p = parent.getAbsolutePath();
        String c = child.getAbsolutePath();
        return c.equals(p) || c.startsWith(p + File.separator);
    }

    private void closeQuietly(java.io.Closeable c) {
        if (c == null) return;
        try { c.close(); } catch (IOException ignored) { }
    }
}
