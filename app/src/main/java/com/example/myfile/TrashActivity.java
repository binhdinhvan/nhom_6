package com.example.myfile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfile.data.model.FileItem;
import com.example.myfile.data.trash.TrashManager;
import com.example.myfile.ui.main.FileAdapter;
import java.util.ArrayList;
import java.util.List;

public class TrashActivity extends AppCompatActivity implements FileAdapter.OnItemClickListener {

    private TrashManager trashManager;
    private FileAdapter adapter;
    private TextView tvEmpty;
        defaultToolbar = findViewById(R.id.defaultToolbarTrash);
        selectionToolbar = findViewById(R.id.selectionToolbarTrash);
        tvSelectionCount = findViewById(R.id.tvSelectionCountTrash);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTrash);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnEmptyTrash).setOnClickListener(v -> confirmEmptyTrash());
        items = groupTrashItemsByDate(items);        adapter.updateData(items);
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

        adapter.enterSelectionMode();
        adapter.toggleSelection(item);    }

    private void showItemOptions(FileItem item) {
        String[] options = {"Restore", "Delete Permanently"};
        new AlertDialog.Builder(this)
                .setTitle(item.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        boolean ok = trashManager.restore(item.getPath());
                        Toast.makeText(this, ok ? "Restored successfully" : "Restore failed", Toast.LENGTH_SHORT).show();
                        loadTrash();
                    } else {
                        confirmSingleDelete(item);                    }
                })
                .show();
    }
        if (selectionMode) {
            defaultToolbar.setVisibility(View.GONE);
            selectionToolbar.setVisibility(View.VISIBLE);
            tvSelectionCount.setText(count + " selected");
        } else {
            defaultToolbar.setVisibility(View.VISIBLE);
            selectionToolbar.setVisibility(View.GONE);
        }
    }
    
    private void bulkRestore() {
        List<FileItem> selected = adapter.getSelectedItems();
        if (selected.isEmpty()) return;
        
        int successCount = 0;
        for (FileItem item : selected) {
            if (trashManager.restore(item.getPath())) {
                successCount++;
            }
        }
        Toast.makeText(this, "Restored " + successCount + " items", Toast.LENGTH_SHORT).show();
        adapter.exitSelectionMode();
        loadTrash();
    }
    
    private void confirmBulkDelete() {
        List<FileItem> selected = adapter.getSelectedItems();
        if (selected.isEmpty()) return;
        
        new AlertDialog.Builder(this)
                .setTitle("Delete Permanently?")
                .setMessage("Are you sure you want to permanently delete " + selected.size() + " items? This action cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    int successCount = 0;
                    for (FileItem item : selected) {
                        if (trashManager.permanentlyDelete(item.getPath())) {
                            successCount++;
                        }
                    }
                    Toast.makeText(this, "Deleted " + successCount + " items", Toast.LENGTH_SHORT).show();
                    adapter.exitSelectionMode();
                    loadTrash();
                })
                .setNegativeButton("Cancel", null)
                .show();    }
}
