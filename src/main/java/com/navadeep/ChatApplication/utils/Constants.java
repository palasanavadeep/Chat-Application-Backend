package com.navadeep.ChatApplication.utils;

public interface Constants {

    String WS_ACTION_SEND_MESSAGE = "sendMessage";
    String WS_ACTION_EDIT_MESSAGE = "editMessage";
    String WS_ACTION_DELETE_MESSAGE_FOR_ME = "deleteMessageForMe";
    String WS_ACTION_DELETE_MESSAGE_FOR_EVERYONE = "deleteMessageForEveryone";
    String WS_ACTION_CREATE_NEW_CONVERSATION = "createNewConversation";
    String WS_ACTION_UPDATE_CONVERSATION = "updateConversation";
    String WS_ACTION_ADD_USER_TO_CONVERSATION = "addUserToConversation";
    String WS_ACTION_REMOVE_USER_FROM_CONVERSATION = "removeUserFromConversation";
    String WS_ACTION_UPDATE_PARTICIPANT_ROLE = "updateParticipantRole";
    String WS_ACTION_GET_CONVERSATION_PARTICIPANTS = "getConversationParticipants";
    String WS_ACTION_GET_USER_CONVERSATIONS = "getUserConversations";
    String WS_ACTION_GET_CONVERSATION = "getConversation";
    String WS_ACTION_GET_PROFILE = "getProfile";
    String WS_ACTION_GET_ALL_MESSAGES = "getAllMessages";
    String WS_ACTION_MARK_MESSAGE_AS_READ = "markMessageAsRead";
    String WS_ACTION_MARK_CONVERSATION_MESSAGES_AS_READ = "markConversationMessagesAsRead";
    String WS_ACTION_SEARCH_USER = "searchUser";
    String WS_ACTION_UPDATE_PROFILE = "updateProfile";
    String WS_ACTION_LEAVE_CONVERSATION = "leaveConversation";
    String WS_ACTION_UPDATE_PARTICIPANT = "updateParticipant";
    String WS_ACTION_NEW_CONVERSATION = "newConversation";
    String WS_ACTION_UPDATED_CONVERSATION = "updatedConversation";
    String WS_ACTION_NEW_PARTICIPANT = "newParticipant";
    String WS_ACTION_REMOVED_PARTICIPANT = "removedParticipant";
    String WS_ACTION_REMOVED_CONVERSATION = "removedConversation";
    String WS_ACTION_NEW_MESSAGE = "newMessage";
    String WS_ACTION_DELETED_MESSAGE = "deletedMessage";

    String STATUS_ERROR = "ERROR";
    String STATUS_SUCCESS = "SUCCESS";
    String WS_SERVER_ENDPOINT = "/ws";


    String ROLE_ADMIN = "ADMIN";
    String ROLE_MEMBER = "MEMBER";
    String CONVERSATION_TYPE_PERSONAL = "PERSONAL";
    String CONVERSATION_TYPE_GROUP = "GROUP";
    String ATTACHMENT_TYPE_IMAGE = "IMAGE";
    String ATTACHMENT_TYPE_FILE = "FILE";



    String MESSAGE_STATUS_SENT = "SENT";
    String MESSAGE_STATUS_DELIVERED = "DELIVERED";
    String MESSAGE_STATUS_READ = "READ";
    String MESSAGE_STATUS_DELETED = "DELETED";



}