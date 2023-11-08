package com.biratpoudel.inbox.config;

import com.biratpoudel.inbox.model.Folder;
import com.biratpoudel.inbox.repository.FolderRepository;
import com.biratpoudel.inbox.repository.MessageListRepository;
import com.biratpoudel.inbox.repository.MessageRepository;
import com.biratpoudel.inbox.repository.UnreadMessageStatRepository;
import com.biratpoudel.inbox.service.MessageService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class SeedData {

    private final FolderRepository folderRepository;
    private final MessageListRepository messageListRepository;
    private final MessageRepository messageRepository;
    private final UnreadMessageStatRepository unreadMessageStatRepository;
    private final MessageService messageService;


    public SeedData(FolderRepository folderRepository, MessageListRepository messageListRepository, MessageRepository messageRepository, UnreadMessageStatRepository unreadMessageStatRepository, MessageService messageService) {
        this.folderRepository = folderRepository;
        this.messageListRepository = messageListRepository;
        this.messageRepository = messageRepository;
        this.unreadMessageStatRepository = unreadMessageStatRepository;
        this.messageService = messageService;
    }

    @PostConstruct
    public void init() {
        folderRepository.save(new Folder("Birat-Poudel", "Work", "blue"));
        folderRepository.save(new Folder("Birat-Poudel", "Home", "green"));
        folderRepository.save(new Folder("Birat-Poudel", "Friends", "yellow"));

//        unreadMessageStats.incrementUnreadCount("Birat-Poudel", "Inbox");
//        unreadMessageStats.incrementUnreadCount("Birat-Poudel", "Inbox");
//        unreadMessageStats.incrementUnreadCount("Birat-Poudel", "Inbox");

        for (int i=0; i<10; i++) {

            messageService.sendMessage("Birat-Poudel", Arrays.asList("Birat-Poudel", "abc"), "Hello " + i,  "body");
//            MessageListKey key = new MessageListKey();
//            key.setId("Birat-Poudel");
//            key.setLabel("Inbox");
//            key.setTimeUUID(Uuids.timeBased());
//
//            MessageList message = new MessageList();
//            message.setKey(key);
//            message.setTo(Arrays.asList("Birat-Poudel"));
//            message.setSubject("Subject: "+ i);
//            message.setUnread(true);
//
//            messageListRepository.save(message);
//
//            Message newMessage = new Message();
//            newMessage.setId(key.getTimeUUID());
//            newMessage.setFrom("Birat-Poudel");
//            newMessage.setSubject(message.getSubject());
//            newMessage.setBody("Body "+i);
//            newMessage.setTo(message.getTo());
//            messageRepository.save(newMessage);
        }
    }
}