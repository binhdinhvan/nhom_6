package com.example.myfile.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

/**
 * ABSTRACT CLASS cho Fragment: tu dong quan ly vong doi ViewBinding
 * (giai phong o onDestroyView de tranh memory leak).
 */
public abstract class BaseFragment<B extends ViewBinding> extends Fragment {

    private B binding;

    protected abstract B inflateBinding(LayoutInflater inflater, @Nullable ViewGroup container);

    protected B binding() {
        if (binding == null) throw new IllegalStateException("Binding chi ton tai giua onCreateView va onDestroyView");
        return binding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = inflateBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        observeData();
    }

    protected void initViews() { }

    protected void observeData() { }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void toast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
