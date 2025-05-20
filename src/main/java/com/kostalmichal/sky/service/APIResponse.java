package com.kostalmichal.sky.service;

public record APIResponse<T>(String status, String message, T data, Object metadata) {

}
