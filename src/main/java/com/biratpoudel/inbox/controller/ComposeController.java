package com.biratpoudel.inbox.controller;

import com.biratpoudel.inbox.model.Folder;
import com.biratpoudel.inbox.model.Message;
import com.biratpoudel.inbox.repository.FolderRepository;
import com.biratpoudel.inbox.repository.MessageRepository;
import com.biratpoudel.inbox.service.FolderService;
import com.biratpoudel.inbox.service.MessageService;
import com.biratpoudel.inbox.service.UnreadMessageStatService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    private final FolderRepository folderRepository;
    private final FolderService folderService;
    private final MessageService messageService;
    private final UnreadMessageStatService unreadMessageStatService;
    private final MessageRepository messageRepository;

    public ComposeController(FolderRepository folderRepository, FolderService folderService,
            MessageService messageService, UnreadMessageStatService unreadMessageStatService,
            MessageRepository messageRepository) {
        this.folderRepository = folderRepository;
        this.folderService = folderService;
        this.messageService = messageService;
        this.unreadMessageStatService = unreadMessageStatService;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/compose")
    public String getComposePage(
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID id,
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

        model.addAttribute("stats", unreadMessageStatService.mapCountToLabels(userId));

        if (StringUtils.hasText(to) && StringUtils.hasText(String.valueOf(id))) {
            Optional<Message> optionalMessage = messageRepository.findById(id);

            List<String> uniqueToIds = splitIds(to);
            model.addAttribute("toIds", String.join(", ", uniqueToIds));

            if (optionalMessage.isPresent()) {
                Message message = optionalMessage.get();
                if (messageService.doesHaveAccess(message, userId)) {
                    model.addAttribute("subject", messageService.getReplySubject(message.getSubject()));
                    model.addAttribute("body", messageService.getReplyBody(message));
                }
            }
        }
        return "compose-page";
    }

    private static List<String> splitIds(String to) {

        if (!StringUtils.hasText(to))
            return new ArrayList<String>();

        String[] splitIds = to.split(",");
        List<String> uniqueToIds = Arrays.stream(splitIds)
                .map(id -> StringUtils.trimWhitespace(id))
                .filter(id -> StringUtils.hasText(id))
                .distinct()
                .collect(Collectors.toList());
        return uniqueToIds;
    }

    @PostMapping("/sendMessage")
    public ModelAndView sendMessage(
            @RequestBody MultiValueMap<String, String> formData,
            @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return new ModelAndView("redirect:/");

        String from = principal.getAttribute("login");
        List<String> toIds = splitIds(formData.getFirst("toIds"));
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");

        messageService.sendMessage(from, toIds, subject, body);
        return new ModelAndView("redirect:/");
    }
}
