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

package com.fusionx.lightirc.irc;

import android.content.Context;
import android.os.Handler;

import com.fusionx.lightirc.R;
import com.fusionx.lightirc.adapters.IRCMessageAdapter;
import com.fusionx.lightirc.communication.MessageSender;
import com.fusionx.lightirc.irc.connection.ConnectionWrapper;
import com.fusionx.lightirc.irc.event.Event;
import com.fusionx.lightirc.irc.event.ServerEvent;
import com.fusionx.lightirc.irc.writers.ServerWriter;
import com.fusionx.lightirc.util.IRCUtils;
import com.fusionx.lightirc.util.MiscUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStreamWriter;
import java.util.Iterator;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
public class Server {
    private ServerWriter writer;
    private UserChannelInterface userChannelInterface;
    private AppUser user;
    private final String title;
    private IRCMessageAdapter buffer;

    private String status = "Disconnected";

    @Getter(AccessLevel.NONE)
    private final ConnectionWrapper mWrapper;
    @Getter(AccessLevel.NONE)
    private final Context mContext;
    private final Handler mAdapterHandler;

    public Server(final String serverTitle, final ConnectionWrapper wrapper,
                  final Context context, final Handler adapterHandler) {
        title = serverTitle;
        mWrapper = wrapper;
        mContext = context;
        mAdapterHandler = adapterHandler;
    }

    public void onServerEvent(final ServerEvent event) {
        if (StringUtils.isNotBlank(event.message)) {
            mAdapterHandler.post(new Runnable() {
                @Override
                public void run() {
                    buffer.add(new Message(event.message));
                }
            });
        }
    }

    public Event privateMessageSent(final PrivateMessageUser userWhoIsNotUs,
                                    final String message, final boolean weAreSending) {
        final MessageSender sender = MessageSender.getSender(title);
        final User sendingUser = weAreSending ? user : userWhoIsNotUs;
        if (!user.isPrivateMessageOpen(userWhoIsNotUs)) {
            user.createPrivateMessage(userWhoIsNotUs);

            if (StringUtils.isNotEmpty(message)) {
                sender.sendPrivateMessage(userWhoIsNotUs, sendingUser, message);
            }

            return sender.sendNewPrivateMessage(userWhoIsNotUs.getNick());
        } else {
            if (StringUtils.isNotEmpty(message)) {
                return sender.sendPrivateMessage(userWhoIsNotUs, sendingUser, message);
            } else {
                return new Event(userWhoIsNotUs.getNick());
            }
        }
    }

    public Event privateActionSent(final PrivateMessageUser userWhoIsNotUs, final String action,
                                   final boolean weAreSending) {
        final MessageSender sender = MessageSender.getSender(title);
        final User sendingUser = weAreSending ? user : userWhoIsNotUs;
        if (!user.isPrivateMessageOpen(userWhoIsNotUs)) {
            user.createPrivateMessage(userWhoIsNotUs);

            if (StringUtils.isNotEmpty(action)) {
                sender.sendPrivateAction(userWhoIsNotUs, sendingUser, action);
            }

            return sender.sendNewPrivateMessage(userWhoIsNotUs.getNick());
        } else {
            if (StringUtils.isNotEmpty(action)) {
                return sender.sendPrivateAction(userWhoIsNotUs, sendingUser, action);
            } else {
                return new Event(userWhoIsNotUs.getNick());
            }
        }
    }

    public synchronized PrivateMessageUser getPrivateMessageUser(@NonNull final String nick) {
        final Iterator<PrivateMessageUser> iterator = user.getPrivateMessageIterator();
        while (iterator.hasNext()) {
            final PrivateMessageUser privateMessageUser = iterator.next();
            if (IRCUtils.areNicksEqual(privateMessageUser.getNick(), nick)) {
                return privateMessageUser;
            }
        }
        return new PrivateMessageUser(nick, userChannelInterface, mAdapterHandler);
    }

    public boolean isConnected(final Context context) {
        return status.equals(context.getString(R.string.status_connected));
    }

    public void disconnectFromServer(final Context context) {
        mWrapper.disconnectFromServer(context);
    }

    @Override
    public String toString() {
        return "HoloIRC " + MiscUtils.getAppVersion(mContext) + " Android IRC client";
    }

    public Context getContext() {
        return mContext;
    }

    public void cleanup() {
        writer = null;
        userChannelInterface = null;
        user = null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Server) && ((Server) o).getTitle().equals(title);
    }

    public void setupUserChannelInterface(final OutputStreamWriter streamWriter) {
        userChannelInterface = new UserChannelInterface(streamWriter, mContext, this,
                mAdapterHandler);
    }
}