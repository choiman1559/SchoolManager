package com.school.manager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.school.manager.other.OtherFragment;
import com.school.manager.post.PostFragment;
import com.school.manager.table.TableFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragments.add(new PostFragment());
        mFragments.add(new TableFragment());
        mFragments.add(new OtherFragment());

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        ViewPager2 viewPager = findViewById(R.id.viewpager);

        FragmentStateAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getLifecycle());
        adapter.saveState();

        viewPager.setSaveEnabled(true);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_0) viewPager.setCurrentItem(0);
            else if (itemId == R.id.page_1) viewPager.setCurrentItem(1);
            else if (itemId == R.id.page_2) viewPager.setCurrentItem(2);

            return true;
        });
    }

    public static class TabsPagerAdapter extends FragmentStateAdapter {

        public TabsPagerAdapter(FragmentManager fm, Lifecycle lifecycle) {
            super(fm, lifecycle);
        }

        @Override
        public boolean containsItem(long itemId) {
            return (long) mFragments.hashCode() == itemId || (long) mFragments.get(0).hashCode() == itemId || (long) mFragments.get(1).hashCode() == itemId || (long) mFragments.get(2).hashCode() == itemId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public long getItemId(int position) {
            return mFragments.get(position).hashCode();
        }
    }
}