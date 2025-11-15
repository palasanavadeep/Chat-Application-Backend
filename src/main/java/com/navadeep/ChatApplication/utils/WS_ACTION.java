package com.navadeep.ChatApplication.utils;

public interface WS_ACTION {
    String SEND_MESSAGE = "sendMessage";
    String EDIT_MESSAGE = "editMessage";
    String DELETE_MESSAGE_FOR_ME = "deleteMessageForMe";
    String DELETE_MESSAGE_FOR_EVERYONE = "deleteMessageForEveryone";
    String CREATE_NEW_CONVERSATION = "createNewConversation";
    String UPDATE_CONVERSATION = "updateConversation";
    String ADD_USER_TO_CONVERSATION = "addUserToConversation";
    String REMOVE_USER_FROM_CONVERSATION = "removeUserFromConversation";
    String UPDATE_PARTICIPANT_ROLE = "updateParticipantRole";
    String GET_CONVERSATION_PARTICIPANTS = "getConversationParticipants";
    String GET_USER_CONVERSATIONS = "getUserConversations";
    String GET_CONVERSATION = "getConversation";
    String GET_PROFILE = "getProfile";
    String GET_ALL_MESSAGES = "getAllMessages";
    String MARK_MESSAGE_AS_READ = "markMessageAsRead";
    String MARK_CONVERSATION_MESSAGES_AS_READ = "markConversationMessagesAsRead";
    String SEARCH_USER = "searchUser";
    String UPDATE_PROFILE = "updateProfile";
    String LEAVE_CONVERSATION = "leaveConversation";
    String UPDATE_PARTICIPANT = "updateParticipant";
    String NEW_CONVERSATION = "newConversation";
    String UPDATED_CONVERSATION = "updatedConversation";
    String NEW_PARTICIPANT = "newParticipant";
    String REMOVED_PARTICIPANT = "removedParticipant";
    String REMOVED_CONVERSATION = "removedConversation";
    String NEW_MESSAGE = "newMessage";
    String DELETED_MESSAGE = "deletedMessage";
}
