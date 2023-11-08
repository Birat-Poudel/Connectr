package com.biratpoudel.inbox.service;

import com.biratpoudel.inbox.model.Message;
import com.biratpoudel.inbox.model.MessageList;

import java.util.List;

public interface MessageService {

    List<MessageList> findAllByKey_IdAndKey_Label(String id, String label);
    void sendMessage(String from, List<String> to, String subject, String body);
    boolean doesHaveAccess(Message message, String userId);
    String getReplySubject(String subject);
    String getReplyBody(Message message);
}
