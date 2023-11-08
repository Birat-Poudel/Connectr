package com.biratpoudel.inbox.controller;

import com.biratpoudel.inbox.model.Folder;
import com.biratpoudel.inbox.model.Message;
import com.biratpoudel.inbox.model.MessageList;
import com.biratpoudel.inbox.model.MessageListKey;
import com.biratpoudel.inbox.repository.FolderRepository;
import com.biratpoudel.inbox.repository.MessageListRepository;
import com.biratpoudel.inbox.repository.MessageRepository;
import com.biratpoudel.inbox.repository.UnreadMessageStatRepository;
import com.biratpoudel.inbox.service.FolderService;
import com.biratpoudel.inbox.service.MessageService;
import com.biratpoudel.inbox.service.UnreadMessageStatService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MessageViewController {

    private final FolderRepository folderRepository;
    private final FolderService folderService;
    private final MessageService messageService;
    private final MessageListRepository messageListRepository;
    private final MessageRepository messageRepository;
    private final UnreadMessageStatService unreadMessageStatService;
    private final UnreadMessageStatRepository unreadMessageStatRepository;

    public MessageViewController(FolderRepository folderRepository, FolderService folderService, MessageListRepository messageListRepository, MessageRepository messageRepository, UnreadMessageStatService unreadMessageStatService, UnreadMessageStatRepository unreadMessageStatRepository, MessageService messageService){
        this.folderRepository = folderRepository;
        this.folderService = folderService;
        this.messageListRepository = messageListRepository;
        this.messageRepository = messageRepository;
        this.unreadMessageStatService = unreadMessageStatService;
        this.unreadMessageStatRepository = unreadMessageStatRepository;
        this.messageService = messageService;
    }

    @GetMapping(value = "/messages/{id}")
    public String messageView(@RequestParam String folder,
                              @PathVariable UUID id,
                              @AuthenticationPrincipal OAuth2User principal,
                              Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";


        String userId = principal.getAttribute("login");
        List<Folder> folders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", folders);

        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("userDefaultFolders", defaultFolders);
        model.addAttribute("userName", principal.getAttribute("name"));

        Optional<Message> optionalMessage = messageRepository.findById(id);

        if(optionalMessage.isEmpty())
            return "inbox-page";

        Message message = optionalMessage.get();
        String toIds = String.join(", ",message.getTo());

//        if (messageService.doesHaveAccess(message, userId)){
//            return "redirect:/";
//        }

        model.addAttribute("message", message);
        model.addAttribute("toIds", toIds);
        model.addAttribute("stats", unreadMessageStatService.mapCountToLabels(userId));

        MessageListKey key = new MessageListKey();
        key.setId(userId);
        key.setLabel(folder);
        key.setTimeUUID(message.getId());

        Optional<MessageList> optionalMessageList = messageListRepository.findById(key);
        if (optionalMessageList.isPresent()) {
            MessageList messageList = optionalMessageList.get();
            if (messageList.isUnread()) {
                messageList.setUnread(false);
                messageListRepository.save(messageList);
                unreadMessageStatRepository.decrementUnreadCount(userId, folder);
            }
        }

        model.addAttribute("stats", unreadMessageStatService.mapCountToLabels(userId));
        return "message-page";
    }
}
