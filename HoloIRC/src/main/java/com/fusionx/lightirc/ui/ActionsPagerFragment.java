package com.fusionx.lightirc.ui;

import com.fusionx.lightirc.R;
import com.fusionx.lightirc.adapters.ActionPagerAdapter;
import com.fusionx.lightirc.constants.FragmentType;
import com.fusionx.relay.Server;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActionsPagerFragment extends Fragment implements IgnoreListFragment
        .IgnoreListCallback, ActionsFragment.Callbacks {

    private ViewPager mActionViewPager;

    private ActionPagerAdapter mActionsPagerAdapter;

    private Callbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mActionsPagerAdapter = new ActionPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.non_scrollable_view_pager, container);

        mActionViewPager = (ViewPager) view;
        mActionViewPager.setAdapter(mActionsPagerAdapter);

        return view;
    }

    private ActionsFragment getActionFragment() {
        return (ActionsFragment) mActionsPagerAdapter.getItem(0);
    }

    private IgnoreListFragment getIgnoreFragment() {
        return (IgnoreListFragment) mActionsPagerAdapter.getItem(1);
    }

    public void switchToIgnoreFragment() {
        ((ActionBarActivity) getActivity()).startSupportActionMode(getIgnoreFragment()
                .mMultiSelectionController);
        mActionViewPager.setCurrentItem(1, true);
    }

    @Override
    public void switchToIRCActionFragment() {
        mActionViewPager.setCurrentItem(0, true);
    }

    @Override
    public String getServerTitle() {
        return mCallbacks.getServerTitle();
    }

    public SlidingMenu.OnCloseListener getIgnoreFragmentListener() {
        return getIgnoreFragment();
    }

    public SlidingMenu.OnOpenListener getActionFragmentListener() {
        return getActionFragment();
    }

    public void updateConnectionStatus(final boolean isConnected) {
        getActionFragment().updateConnectionStatus(isConnected);
    }

    public void onPageChanged(FragmentType type) {
        getActionFragment().onTabChanged(type);
    }

    @Override
    public String getNick() {
        return mCallbacks.getNick();
    }

    @Override
    public void closeOrPartCurrentTab() {
        mCallbacks.onRemoveCurrentFragment();
    }

    @Override
    public boolean isConnectedToServer() {
        return mCallbacks.isConnectedToServer();
    }

    @Override
    public Server getServer() {
        return mCallbacks.getServer();
    }

    @Override
    public void closeAllSlidingMenus() {
        mCallbacks.closeAllSlidingMenus();
    }

    @Override
    public void disconnectFromServer() {
        mCallbacks.disconnectFromServer();
    }

    public interface Callbacks {

        public String getServerTitle();

        public String getNick();

        public void onRemoveCurrentFragment();

        public boolean isConnectedToServer();

        public Server getServer();

        public void closeAllSlidingMenus();

        public void disconnectFromServer();
    }
}