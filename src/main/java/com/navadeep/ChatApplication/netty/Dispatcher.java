package com.navadeep.ChatApplication.netty;

import com.navadeep.ChatApplication.utils.Constants;
import java.util.List;

public class Dispatcher {

    private final SessionManager sessionManager;
    private final ChatEventHandler chatEventHandler;

    public Dispatcher(SessionManager sessionManager, ChatEventHandler chatEventHandler) {
        this.sessionManager = sessionManager;
        this.chatEventHandler = chatEventHandler;
    }

    public void dispatch(Long userId, MessageFrame msg) {

        switch (msg.getAction()) {

            case Constants.WS_ACTION_SEND_MESSAGE -> chatEventHandler.sendMessageHandler(userId, msg);

            case Constants.WS_ACTION_EDIT_MESSAGE -> chatEventHandler.editMessageHandler(userId, msg);

            case Constants.WS_ACTION_DELETE_MESSAGE_FOR_ME -> chatEventHandler.deleteMessageForMeHandler(userId, msg);

            case Constants.WS_ACTION_DELETE_MESSAGE_FOR_EVERYONE -> chatEventHandler.deleteMessageForEveryoneHandler(userId, msg);

            case Constants.WS_ACTION_CREATE_NEW_CONVERSATION -> chatEventHandler.createConversationHandler(userId, msg);

            case Constants.WS_ACTION_UPDATE_CONVERSATION -> chatEventHandler.updateConversationHandler(userId, msg);

            case Constants.WS_ACTION_ADD_USER_TO_CONVERSATION -> chatEventHandler.addUserToConversationHandler(userId, msg);

            case Constants.WS_ACTION_REMOVE_USER_FROM_CONVERSATION -> chatEventHandler.removeUserFromConversationHandler(userId, msg);

            case Constants.WS_ACTION_UPDATE_PARTICIPANT_ROLE -> chatEventHandler.updateParticipantRoleHandler(userId, msg);

            case Constants.WS_ACTION_GET_CONVERSATION_PARTICIPANTS -> chatEventHandler.getConversationParticipantsHandler(userId, msg);

            case Constants.WS_ACTION_GET_USER_CONVERSATIONS -> chatEventHandler.getUserConversationsHandler(userId, msg);

            case Constants.WS_ACTION_GET_CONVERSATION -> chatEventHandler.getConversationHandler(userId, msg);

            case Constants.WS_ACTION_GET_PROFILE -> chatEventHandler.getProfileHandler(userId, msg);

            case Constants.WS_ACTION_GET_ALL_MESSAGES -> chatEventHandler.getAllMessagesHandler(userId, msg);

            case Constants.WS_ACTION_MARK_MESSAGE_AS_READ -> chatEventHandler.markMessageAsReadHandler(userId, msg);

            case Constants.WS_ACTION_MARK_CONVERSATION_MESSAGES_AS_READ -> chatEventHandler.markConversationMessagesAsReadHandler(userId, msg);

            case Constants.WS_ACTION_SEARCH_USER  -> chatEventHandler.searchUserHandler(userId, msg);

            case Constants.WS_ACTION_UPDATE_PROFILE -> chatEventHandler.updateProfileHandler(userId, msg);

            case Constants.WS_ACTION_LEAVE_CONVERSATION -> chatEventHandler.leaveConversationHandler(userId, msg);

            default -> sessionManager.broadcast(
                    WsResponse.error(
                            Constants.STATUS_ERROR,
                            "Invalid Socket action"),
                    List.of(userId)
            );
        }
    }
}
