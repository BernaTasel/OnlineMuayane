package com.bernatasel.onlinemuayene.utils;

import android.os.Handler;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bernatasel.onlinemuayene.R;

import java.util.ArrayList;

public class MyFM<T extends MyFM.MyFragment> {
    private static final String TAG = MyFM.class.getSimpleName();

    public static abstract class MyFragment extends Fragment {
        protected abstract boolean onBack();
    }

    public enum ANIM {
        NONE, LEFT, RIGHT
    }

    private final Handler handler = new Handler();

    private final FragmentManager supportFragmentManager;
    private final View viewClickPrevent;
    private final ArrayList<Integer> containerArr;
    private boolean isAnimating;
    private T currentFragment;

    public MyFM(FragmentManager supportFragmentManager,
                View viewClickPrevent,
                ArrayList<Integer> containerArr) {
        this.supportFragmentManager = supportFragmentManager;
        this.viewClickPrevent = viewClickPrevent;
        this.containerArr = containerArr;

        supportFragmentManager.addOnBackStackChangedListener(() -> {
            int backStackEntryCount = supportFragmentManager.getBackStackEntryCount();
            if (backStackEntryCount == 0) {
                currentFragment = null;
                return;
            }
            FragmentManager.BackStackEntry backEntry = supportFragmentManager.getBackStackEntryAt(backStackEntryCount - 1);
            String tag = backEntry.getName();
            currentFragment = (T) supportFragmentManager.findFragmentByTag(tag);
        });
    }

    public FragmentManager getSupportFragmentManager() {
        return supportFragmentManager;
    }

    public void addFragment(T myFragment, ANIM anim, boolean addToBackStack) {
        addFragment(myFragment, 0, anim, addToBackStack);
    }

    public void addFragment(T myFragment, int containerIndex, ANIM anim) {
        addFragment(myFragment, containerIndex, anim, true);
    }

    public void addFragment(T myFragment, int containerIndex, ANIM anim, boolean addToBackStack) {
        isAnimating = true;
        viewClickPrevent.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (anim == ANIM.LEFT) {
//            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else if (anim == ANIM.RIGHT) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }
//        if (currentFragment != null) transaction.remove(currentFragment);
        String newTAG = "Fragment" + (getBackStackEntryCount() + 1);
        transaction.add(containerArr.get(containerIndex), myFragment, newTAG);
        if (addToBackStack) transaction.addToBackStack(newTAG);
        transaction.commit();

        currentFragment = myFragment;

        handler.postDelayed(() -> {
            viewClickPrevent.setVisibility(View.GONE);
            isAnimating = false;
        }, anim == ANIM.NONE ? 0 : 300);
    }

    public void replaceFragment(T myFragment, ANIM anim) {
        replaceFragment(myFragment, 0, anim);
    }

    public void replaceFragment(T myFragment, int containerIndex, ANIM anim) {
        isAnimating = true;
        viewClickPrevent.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (anim == ANIM.LEFT) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        } else if (anim == ANIM.RIGHT) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        }
//        if (currentFragment != null) transaction.remove(currentFragment);
        transaction.replace(containerArr.get(containerIndex), myFragment);
//        transaction.addToBackStack(null);
        transaction.commit();

        currentFragment = myFragment;

        handler.postDelayed(() -> {
            viewClickPrevent.setVisibility(View.GONE);
            isAnimating = false;
        }, anim == ANIM.NONE ? 0 : 210);
    }

    public void remove(T myFragment) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.remove(myFragment);
        transaction.commit();
    }

    public T getCurrentFragment() {
        return currentFragment;
    }

    public boolean popBackStack() {
        if (isAnimating) return false;
        supportFragmentManager.popBackStack();
        return true;
    }

    public int getBackStackEntryCount() {
        return supportFragmentManager.getBackStackEntryCount();
    }

    public boolean onBackPressed() {
        if (isAnimating) return false;
        T myFragment = getCurrentFragment();
        if (myFragment != null) return myFragment.onBack();
        return true;
    }

    public void clearAll() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }
}
