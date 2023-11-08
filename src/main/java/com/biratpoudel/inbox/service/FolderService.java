package com.biratpoudel.inbox.service;

import com.biratpoudel.inbox.model.Folder;

import java.util.List;
import java.util.Map;

public interface FolderService {

    List<Folder> findAllById(String id);
    List<Folder> fetchDefaultFolders(String id);
}
