package com.biratpoudel.inbox.service;

import com.biratpoudel.inbox.model.Folder;
import com.biratpoudel.inbox.model.UnreadMessageStat;
import com.biratpoudel.inbox.repository.FolderRepository;
import com.biratpoudel.inbox.repository.UnreadMessageStatRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService{

    private final FolderRepository folderRepository;
    private final UnreadMessageStatRepository unreadMessageStatRepository;

    public FolderServiceImpl(FolderRepository folderRepository, UnreadMessageStatRepository unreadMessageStatRepository) {
        this.folderRepository = folderRepository;
        this.unreadMessageStatRepository = unreadMessageStatRepository;
    }

    @Override
    public List<Folder> findAllById(String id) {
        return folderRepository.findAllById(id);
    }

    @Override
    public List<Folder> fetchDefaultFolders(String id){
        return Arrays.asList(
                new Folder(id, "Inbox", "blue"),
                new Folder(id, "Sent", "green"),
                new Folder(id, "Important", "red")
        );
    }
}