# CORE — base dự án MyFile

Hợp đồng chung cho cả 3 thành viên. **Push xong thì FREEZE** — muốn đổi signature phải báo cả nhóm.

## Cấu trúc

```
app/src/main/java/com/example/myfile/
├── FileManagerApp.java          @HiltAndroidApp
├── MainActivity.java            (của bạn, thêm @AndroidEntryPoint)
└── core/
    ├── AppExecutors.java        disk / background / main thread
    ├── model/                   FileItem, FileType, SortOption,
    │                            OperationResult, UiState
    ├── repository/              FileRepository (interface),
    │                            LocalFileRepositoryImpl,
    │                            FileTypeResolver, DefaultFileTypeResolver
    ├── operation/               FileOperation (interface),
    │                            AbstractFileOperation (template method),
    │                            ProgressListener, OperationException
    ├── base/                    BaseActivity, BaseFragment, BaseViewModel,
    │                            BaseAdapter, FileItemDiffCallback
    ├── di/                      RepositoryModule (Hilt)
    └── event/                   FileChangedEvent, OperationProgressEvent,
                                 OperationCompleteEvent
```

## Ai làm gì tiếp theo

| | Package sở hữu |
|---|---|
| **A** | `ui/main/`, `feature/browser/` — MainActivity, FileListFragment, FileAdapter, NavigationManager, SortStrategy |
| **B** | `feature/operation/` — Copy/Move/Delete/Rename/CreateFolder, Service, SelectionManager, Clipboard, dialogs |
| **C** | `feature/viewer/`, `feature/archive/` — ViewerFactory, Image/Video/Exif viewer, Zip/Unzip |

## 5 quy tắc bắt buộc

1. Activity / Fragment / Adapter **không được import `java.io.File`** — chỉ dùng `FileItem`
2. Mọi thao tác dài phải kế thừa `AbstractFileOperation`, không tự tạo Thread
3. View không gọi thẳng `FileRepository` — luôn qua ViewModel
4. Xong thao tác phải post `FileChangedEvent` để danh sách tự refresh
5. `targetSdk 29` + `requestLegacyExternalStorage` — không nâng lên 30+ giữa chừng

## Viết một Operation mới (mẫu cho B và C)

```java
public class DeleteOperation extends AbstractFileOperation<Integer> {

    private final FileRepository repository;
    private final List<FileItem> targets;

    public DeleteOperation(FileRepository repository, List<FileItem> targets) {
        this.repository = repository;
        this.targets = targets;
    }

    @Override
    protected Integer doExecute() throws Exception {
        int done = 0;
        for (FileItem item : targets) {
            throwIfCancelled();                                    // hỗ trợ Huỷ
            OperationResult<Boolean> r = repository.delete(item);
            if (!r.isSuccess()) throw new OperationException(r.getErrorMessage());
            done++;
            publishProgress(done, targets.size(), item.getName()); // bắn tiến độ
        }
        return done;
    }

    @Override
    public String getDisplayName() {
        return "Đang xoá " + targets.size() + " mục";
    }
}
```

`execute()` là `final` — lớp con không sửa được khung try/catch/cancel. Đây là **Template Method pattern**, nhớ nhắc trong báo cáo.

## Mẫu ViewModel

```java
@HiltViewModel
public class FileBrowserViewModel extends BaseViewModel<List<FileItem>> {

    private final FileRepository repository;

    @Inject
    public FileBrowserViewModel(AppExecutors executors, FileRepository repository) {
        super(executors);
        this.repository = repository;
    }

    public void load(String path) {
        setLoading();
        runOnDisk(() -> {
            OperationResult<List<FileItem>> r = repository.listFiles(path, false);
            if (!r.isSuccess())             setError(r.getErrorMessage());
            else if (r.getData().isEmpty()) setEmpty();
            else                            setSuccess(r.getData());
        });
    }
}
```

## Mẫu Fragment

```java
public class FileListFragment extends BaseFragment<FragmentFileListBinding> {

    @Override
    protected FragmentFileListBinding inflateBinding(LayoutInflater i, ViewGroup c) {
        return FragmentFileListBinding.inflate(i, c, false);
    }

    @Override
    protected void observeData() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            switch (state.getStatus()) {
                case LOADING: showLoading();                       break;
                case SUCCESS: adapter.submitList(state.getData()); break;
                case EMPTY:   showEmpty();                         break;
                case ERROR:   showError(state.getMessage());       break;
            }
        });
    }
}
```

## Checklist Hilt

- [ ] `FileManagerApp` có `@HiltAndroidApp`, manifest khai `android:name=".FileManagerApp"`
- [ ] Activity có `@AndroidEntryPoint`
- [ ] Fragment có `@AndroidEntryPoint`
- [ ] ViewModel có `@HiltViewModel` + `@Inject` constructor

## Lưu ý build

- AGP cũ hơn 7.3 → xoá dòng `namespace` trong `app/build.gradle`
- Sau khi đổi package hàng loạt: Build → Clean Project → Rebuild
- `mipmap/ic_launcher` dùng lại của project, gói này không kèm
