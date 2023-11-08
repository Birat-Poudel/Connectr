package com.biratpoudel.inbox.service;

import com.biratpoudel.inbox.model.Message;
import com.biratpoudel.inbox.model.MessageList;
import com.biratpoudel.inbox.model.MessageListKey;
import com.biratpoudel.inbox.repository.MessageListRepository;
import com.biratpoudel.inbox.repository.MessageRepository;
import com.biratpoudel.inbox.repository.UnreadMessageStatRepository;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    private final MessageListRepository messageListRepository;
    private final MessageRepository messageRepository;
    private final UnreadMessageStatRepository unreadMessageStatRepository;

    public MessageServiceImpl(MessageListRepository messageListRepository, MessageRepository messageRepository, UnreadMessageStatRepository unreadMessageStatRepository) {
        this.messageListRepository = messageListRepository;
        this.messageRepository = messageRepository;
        this.unreadMessageStatRepository = unreadMessageStatRepository;
    }

    @Override
    public List<MessageList> findAllByKey_IdAndKey_Label(String id, String label) {
        return messageListRepository.findAllByKey_IdAndKey_Label(id, label);
    }

    @Override
    public void sendMessage(String from, List<String> to, String subject, String body){
        Message message = new Message();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setBody(body);
        message.setId(Uuids.timeBased());
        messageRepository.save(message);

        to.forEach(toId -> {
            MessageList messageList = createMessageList(to, subject, message, toId, "Inbox");
            messageListRepository.save(messageList);
            unreadMessageStatRepository.incrementUnreadCount(toId, "Inbox");
        });

        MessageList messageListEntry = createMessageList(to, subject, message, from, "Sent Items");
        messageListEntry.setUnread(false);
        messageListRepository.save(messageListEntry);
    }

    @Override
    public boolean doesHaveAccess(Message message, String userId) {
        return (userId.equals(message.getFrom()) || message.getTo().contains(userId));
    }

    @Override
    public String getReplySubject(String subject) {
        return "Re: " + subject;
    }

    @Override
    public String getReplyBody(Message message) {
        return "\n\n\n------------------------------ \n" +
                "From: " + message.getFrom() + "\n" +
                "To: " + message.getTo() + "\n\n" +
                message.getBody();
    }

    private static MessageList createMessageList(List<String> to, String subject, Message message, String messageOwner, String folder) {
        MessageListKey key = new MessageListKey();
        key.setId(messageOwner);
        key.setLabel(folder);
        key.setTimeUUID(message.getId());

        MessageList messageList = new MessageList();
        messageList.setKey(key);
        messageList.setTo(to);
        messageList.setSubject(subject);
        messageList.setUnread(true);
        return messageList;
    }
}
