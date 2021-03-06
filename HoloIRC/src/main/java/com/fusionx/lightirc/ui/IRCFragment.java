/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.lightirc.ui;

import com.fusionx.lightirc.R;
import com.fusionx.lightirc.adapters.IRCMessageAdapter;
import com.fusionx.lightirc.constants.FragmentType;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.Event;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class IRCFragment<T extends Event> extends ListFragment implements TextView
        .OnEditorActionListener {

    String mTitle;

    EditText mMessageBox;

    IRCMessageAdapter<T> mMessageAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflate, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflate.inflate(R.layout.fragment_irc, container, false);

        // This is done here rather than in onViewCreated as a NoSaveStateFramelayout will be
        // inserted by the support library
        mMessageBox = (EditText) view.findViewById(R.id.fragment_irc_message_box);
        mMessageBox.setOnEditorActionListener(this);
        mTitle = getArguments().getString("title");

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageAdapter = new IRCMessageAdapter<>(getActivity());
        final AnimationAdapter adapter = new AlphaInAnimationAdapter(mMessageAdapter);
        adapter.setAbsListView(getListView());
        setListAdapter(adapter);

        if (savedInstanceState != null) {
            adapter.setShouldAnimateFromPosition(savedInstanceState.getInt("NUMBEROFITEMS"));
        }
        mMessageAdapter.setData(new ArrayList<>(getAdapterData()));
        getServer().getServerEventBus().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getServer().getServerEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        getListView().setSelection(getListView().getCount());
    }

    public final void onDisableUserInput() {
        mMessageBox.setEnabled(false);
    }

    @Override
    public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
        final CharSequence text = mMessageBox.getText();
        if ((event == null || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo
                .IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event
                .getKeyCode() == KeyEvent.KEYCODE_ENTER) && StringUtils.isNotEmpty(text)) {
            final String message = text.toString();
            mMessageBox.setText("");
            onSendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("NUMBEROFITEMS", mMessageAdapter.getCount());
    }

    protected abstract Server getServer();

    protected abstract List<T> getAdapterData();

    // Abstract methods
    protected abstract void onSendMessage(final String message);

    public abstract FragmentType getType();

    // Getters and setters
    public String getTitle() {
        return mTitle;
    }
}