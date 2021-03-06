package com.fusionx.lightirc.communication;

import com.fusionx.lightirc.R;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.channel.ActionEvent;
import com.fusionx.relay.event.channel.ChannelNoticeEvent;
import com.fusionx.relay.event.channel.InitialTopicEvent;
import com.fusionx.relay.event.channel.MessageEvent;
import com.fusionx.relay.event.channel.ModeEvent;
import com.fusionx.relay.event.channel.NickChangeEvent;
import com.fusionx.relay.event.channel.TopicEvent;
import com.fusionx.relay.event.channel.UserLevelChangeEvent;
import com.fusionx.relay.event.channel.WorldActionEvent;
import com.fusionx.relay.event.channel.WorldJoinEvent;
import com.fusionx.relay.event.channel.WorldKickEvent;
import com.fusionx.relay.event.channel.WorldLevelChangeEvent;
import com.fusionx.relay.event.channel.WorldMessageEvent;
import com.fusionx.relay.event.channel.WorldNickChangeEvent;
import com.fusionx.relay.event.channel.WorldPartEvent;
import com.fusionx.relay.event.channel.WorldQuitEvent;
import com.fusionx.relay.event.server.ConnectEvent;
import com.fusionx.relay.event.server.DisconnectEvent;
import com.fusionx.relay.event.server.ErrorEvent;
import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.event.server.KickEvent;
import com.fusionx.relay.event.server.MotdEvent;
import com.fusionx.relay.event.server.PrivateNoticeEvent;
import com.fusionx.relay.event.server.ServerNickChangeEvent;
import com.fusionx.relay.event.server.WhoisEvent;
import com.fusionx.relay.event.user.PrivateActionEvent;
import com.fusionx.relay.event.user.PrivateMessageEvent;
import com.fusionx.relay.event.user.WorldPrivateActionEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;
import com.fusionx.relay.util.ColourParserUtils;
import com.fusionx.relay.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Subscribe;

import android.content.Context;
import android.util.Log;

public class MessageSpannedConverter {

    public static MessageSpannedConverter sConverter;

    private final Context mContext;

    private final Bus converter;

    MessageSpannedConverter(final Context context) {
        mContext = context.getApplicationContext();
        converter = new Bus();
        converter.register(this);
    }

    public static MessageSpannedConverter getConverter(final Context context) {
        if (sConverter == null) {
            sConverter = new MessageSpannedConverter(context);
        }
        return sConverter;
    }

    @Subscribe
    public void debugDeadEvent(final DeadEvent event) {
        Log.e("HoloIRC", "Dead event of type " + event.event.getClass() + " received");
    }

    public void getEventMessage(final Event event) {
        converter.post(event);
    }

    // Messages
    @Subscribe
    public void getInitialTopicMessage(final InitialTopicEvent event) {
        final String response = mContext.getString(R.string.parser_new_topic);
        setupEvent(event, String.format(response, event.topic, event.setterNick));
    }

    /*public String getSlapMessage(final String receivingNick) {
        final String response = mContext.getString(R.string.parser_slap_message);
        return String.format(response, receivingNick);
    }*/

    @Subscribe
    public void getOnWhoisMessage(final WhoisEvent event) {
        setupEvent(event, event.whoisMessage);
    }

    @Subscribe
    public void getOnConnectedMessage(final ConnectEvent event) {
        final String response = mContext.getString(R.string.parser_connected);
        setupEvent(event, String.format(response, event.serverUrl));
    }

    @Subscribe
    public void getJoinMessage(final WorldJoinEvent event) {
        final String response = mContext.getString(R.string.parser_joined_channel);
        setupEvent(event, String.format(response, event.nick));
    }

    @Subscribe
    public void getModeChangedMessage(final UserLevelChangeEvent event) {
        final String response = mContext.getString(R.string.parser_mode_changed);
        setupEvent(event, String.format(response, event.rawMode, event.nick, event.changingNick));
    }

    @Subscribe
    public void getModeChangedMessage(final WorldLevelChangeEvent event) {
        final String response = mContext.getString(R.string.parser_mode_changed);
        setupEvent(event, String.format(response, event.rawMode, event.nick, event.changingNick));
    }

    @Subscribe
    public void getNickChangedMessage(final WorldNickChangeEvent event) {
        final String response = mContext.getString(R.string.parser_other_user_nick_change);
        setupEvent(event, String.format(response, event.oldNick, event.nick));
    }

    @Subscribe
    public void getNickChaneMessage(final NickChangeEvent event) {
        final String response = mContext.getString(R.string.parser_appuser_nick_changed);
        setupEvent(event, String.format(response, event.oldNick, event.newNick));
    }

    @Subscribe
    public void getTopicChangedMessage(final TopicEvent event) {
        final String response = mContext.getString(R.string.parser_topic_changed);
        setupEvent(event, String.format(response, event.topic, event.topicSetter));
    }

    @Subscribe
    public void getUserKickedMessage(final WorldKickEvent event) {
        final String response = mContext.getString(R.string.parser_kicked_channel);
        final String formattedResponse = String.format(response, event.nick, event.kickingNick);
        setupEvent(event, appendReasonIfNeeded(formattedResponse, event.reason));
    }

    @Subscribe
    public void getServerKickEvent(final KickEvent event) {
        final String response = mContext.getString(R.string.parser_user_kicked_channel);
        final String formattedResponse = String
                .format(response, event.channelName, event.kickingNick);
        setupEvent(event, appendReasonIfNeeded(formattedResponse, event.reason));
    }

    @Subscribe
    public void getPartMessage(final WorldPartEvent event) {
        final String response = mContext.getString(R.string.parser_parted_channel);
        final String formattedResponse = String.format(response, event.nick);
        setupEvent(event, appendReasonIfNeeded(formattedResponse, event.reason));
    }

    @Subscribe
    public void getQuitMessage(final WorldQuitEvent event) {
        final String response = mContext.getString(R.string.parser_quit_server);
        final String formattedResponse = String.format(response, event.nick);
        setupEvent(event, appendReasonIfNeeded(formattedResponse, event.reason));
    }

    @Subscribe
    public void getMessage(final WorldMessageEvent event) {
        final String response = mContext.getString(R.string.parser_message);
        setupEvent(event, String.format(response, event.nick, event.message));
    }

    @Subscribe
    public void getMessage(final MessageEvent event) {
        final String response = mContext.getString(R.string.parser_message);
        setupEvent(event, String.format(response, event.nick, event.message));
    }

    @Subscribe
    public void getMessage(final PrivateMessageEvent event) {
        final String response = mContext.getString(R.string.parser_message);
        setupEvent(event, String.format(response, event.appUserNick, event.message));
    }

    @Subscribe
    public void getMessage(final WorldPrivateMessageEvent event) {
        final String response = mContext.getString(R.string.parser_message);
        setupEvent(event, String.format(response, event.user.getColorfulNick(), event.message));
    }

    @Subscribe
    public void getNoticeMessage(final ChannelNoticeEvent event) {
        final String response = mContext.getString(R.string.parser_notice);
        setupEvent(event, String.format(response, event.originNick, event.notice));
    }

    @Subscribe
    public void getActionMessage(final ActionEvent event) {
        final String response = mContext.getString(R.string.parser_action);
        setupEvent(event, String.format(response, event.nick, event.action));
    }

    @Subscribe
    public void getActionMessage(final WorldActionEvent event) {
        final String response = mContext.getString(R.string.parser_action);
        setupEvent(event, String.format(response, event.nick, event.action));
    }

    @Subscribe
    public void getActionMessage(final PrivateActionEvent event) {
        final String response = mContext.getString(R.string.parser_action);
        setupEvent(event, String.format(response, event.appUserNick, event.action));
    }

    @Subscribe
    public void getActionMessage(final WorldPrivateActionEvent event) {
        final String response = mContext.getString(R.string.parser_action);
        setupEvent(event, String.format(response, event.user.getColorfulNick(), event.action));
    }

    @Subscribe
    public void getGenericServerMessage(final GenericServerEvent event) {
        setupEvent(event, event.message);
    }

    @Subscribe
    public void getMotdLine(final MotdEvent event) {
        setupEvent(event, event.motdLine);
    }

    @Subscribe
    public void getErrorMessage(final ErrorEvent errorEvent) {
        setupEvent(errorEvent, errorEvent.line);
    }

    @Subscribe
    public void getServerChangeMessage(final ServerNickChangeEvent event) {
        final String response = mContext.getString(R.string.parser_appuser_nick_changed);
        setupEvent(event, String.format(response, event.oldNick, event.newNick));
    }

    @Subscribe
    public void getPrivateNoticeMessage(final PrivateNoticeEvent event) {
        final String response = mContext.getString(R.string.parser_notice);
        setupEvent(event, String.format(response, event.sendingNick, event.message));
    }

    @Subscribe
    public void getModeMessage(final ModeEvent event) {
        final String response = mContext.getString(R.string.parser_mode_changed);
        setupEvent(event, String.format(response, event.mode, event.recipient,
                event.sendingUserNick));
    }

    @Subscribe
    public void getDeisconnectEvent(final DisconnectEvent event) {
        setupEvent(event, event.serverMessage);
    }

    private String appendReasonIfNeeded(final String response, final String reason) {
        return Utils.isEmpty(reason) ? response : response + " " + String.format(mContext
                .getString(R.string.parser_reason), reason);
    }

    private void setupEvent(final Event event, final String message) {
        event.store = ColourParserUtils.onParseMarkup(message);
    }
}
