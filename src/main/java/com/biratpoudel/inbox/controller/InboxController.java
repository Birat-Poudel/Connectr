package com.biratpoudel.inbox.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.biratpoudel.inbox.model.MessageList;
import com.biratpoudel.inbox.repository.UnreadMessageStatRepository;
import com.biratpoudel.inbox.service.FolderService;
import com.biratpoudel.inbox.service.MessageService;
import com.biratpoudel.inbox.service.UnreadMessageStatService;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import com.biratpoudel.inbox.model.Folder;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InboxController {

    private final FolderService folderService;
    private final MessageService messageService;
    private final UnreadMessageStatRepository unreadMessageStatRepository;
    private final UnreadMessageStatService unreadMessageStatService;


    public InboxController(FolderService folderService, MessageService messageService, UnreadMessageStatRepository unreadMessageStatRepository, UnreadMessageStatService unreadMessageStatService) {
        this.folderService = folderService;
        this.messageService = messageService;
        this.unreadMessageStatRepository = unreadMessageStatRepository;
        this.unreadMessageStatService = unreadMessageStatService;
    }

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model, @RequestParam(required = false) String folder) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";

        String userId = principal.getAttribute("login");

        // Fetch folders
        List<Folder> folders = folderService.findAllById(userId);
        model.addAttribute("userFolders", folders);

        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("userDefaultFolders", defaultFolders);

        model.addAttribute("stats", unreadMessageStatService.mapCountToLabels(userId));
        model.addAttribute("userName", principal.getAttribute("name"));

        // Fetch messages
        if (!StringUtils.hasText(folder)) {
            folder = "Inbox";
        }

        model.addAttribute("currentFolder", folder);

        List<MessageList> messageList = messageService.findAllByKey_IdAndKey_Label(userId, folder);

        PrettyTime p = new PrettyTime();
        messageList.forEach(messageListItem ->
        {
            UUID timeUuid = messageListItem.getKey().getTimeUUID();
            Date messageDateTime = new Date(Uuids.unixTimestamp(timeUuid));
            messageListItem.setAgoTimeString(p.format(messageDateTime));
        }
        );

        model.addAttribute("messageList", messageList);
        model.addAttribute("folderName", folder);

        return "inbox-page";
    }
}