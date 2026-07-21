package com.example.myfile.core.base;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * ABSTRACT CLASS gom logic chung cua moi Activity: ViewBinding + setup.
 *
 * Vi du:
 *   public class MainActivity extends BaseActivity<ActivityMainBinding> {
 *       protected ActivityMainBinding inflateBinding() {
 *           return ActivityMainBinding.inflate(getLayoutInflater());
 *       }
 *   }
 */
public abstract class BaseActivity<B extends ViewBinding> extends AppCompatActivity {

    protected B binding;

    protected abstract B inflateBinding();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding();
        setContentView(binding.getRoot());
        initViews();
        observeData();
    }

    /** Setup toolbar, recyclerview, listener... */
    protected void initViews() { }

    /** Dang ky observer cho LiveData. */
    protected void observeData() { }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
