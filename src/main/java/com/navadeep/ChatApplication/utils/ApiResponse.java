package com.navadeep.ChatApplication.utils;

public record ApiResponse(boolean success, String message, Object data) {
}