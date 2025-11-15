package com.navadeep.ChatApplication.netty;

import com.navadeep.ChatApplication.utils.Constants;
import com.navadeep.ChatApplication.utils.WS_ACTION;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.List;

public class Dispatcher {

    private final SessionManager sessionManager;
    private final ChatEventHandler chatEventHandler;
    Log log = LogFactory.getLog(Dispatcher.class);

    public Dispatcher(SessionManager sessionManager, ChatEventHandler chatEventHandler) {
        this.sessionManager = sessionManager;
        this.chatEventHandler = chatEventHandler;
    }

    public void dispatch(Long userId, MessageFrame msg) {
        log.info("Dispatching action: " + msg.getAction() + " for userId: " + userId);
        switch (msg.getAction()) {

            case WS_ACTION.SEND_MESSAGE -> chatEventHandler.sendMessageHandler(userId, msg);

            case WS_ACTION.EDIT_MESSAGE -> chatEventHandler.editMessageHandler(userId, msg);

            case WS_ACTION.DELETE_MESSAGE_FOR_ME -> chatEventHandler.deleteMessageForMeHandler(userId, msg);

            case WS_ACTION.DELETE_MESSAGE_FOR_EVERYONE -> chatEventHandler.deleteMessageForEveryoneHandler(userId, msg);

            case WS_ACTION.CREATE_NEW_CONVERSATION -> chatEventHandler.createConversationHandler(userId, msg);

            case WS_ACTION.UPDATE_CONVERSATION -> chatEventHandler.updateConversationHandler(userId, msg);

            case WS_ACTION.ADD_USER_TO_CONVERSATION -> chatEventHandler.addUserToConversationHandler(userId, msg);

            case WS_ACTION.REMOVE_USER_FROM_CONVERSATION -> chatEventHandler.removeUserFromConversationHandler(userId, msg);

            case WS_ACTION.UPDATE_PARTICIPANT_ROLE -> chatEventHandler.updateParticipantRoleHandler(userId, msg);

            case WS_ACTION.GET_CONVERSATION_PARTICIPANTS -> chatEventHandler.getConversationParticipantsHandler(userId, msg);

            case WS_ACTION.GET_USER_CONVERSATIONS -> chatEventHandler.getUserConversationsHandler(userId, msg);

            case WS_ACTION.GET_CONVERSATION -> chatEventHandler.getConversationHandler(userId, msg);

            case WS_ACTION.GET_PROFILE -> chatEventHandler.getProfileHandler(userId, msg);

            case WS_ACTION.GET_ALL_MESSAGES -> chatEventHandler.getAllMessagesHandler(userId, msg);

            case WS_ACTION.MARK_MESSAGE_AS_READ -> chatEventHandler.markMessageAsReadHandler(userId, msg);

            case WS_ACTION.MARK_CONVERSATION_MESSAGES_AS_READ -> chatEventHandler.markConversationMessagesAsReadHandler(userId, msg);

            case WS_ACTION.SEARCH_USER  -> chatEventHandler.searchUserHandler(userId, msg);

            case WS_ACTION.UPDATE_PROFILE -> chatEventHandler.updateProfileHandler(userId, msg);

            case WS_ACTION.LEAVE_CONVERSATION -> chatEventHandler.leaveConversationHandler(userId, msg);

            default -> {
                sessionManager.broadcast(
                        WsResponse.error(
                                Constants.STATUS_ERROR,
                                "Invalid Socket action"),
                        List.of(userId)
                );
                log.error("Invalid WebSocket action: " + msg.getAction());
            }
        }
    }
}
