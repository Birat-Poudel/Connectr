package com.biratpoudel.inbox.service;

import com.biratpoudel.inbox.repository.UnreadMessageStatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UnreadMessageStatServiceImpl implements UnreadMessageStatService {

    private final UnreadMessageStatRepository unreadMessageStatRepository;

    public UnreadMessageStatServiceImpl(UnreadMessageStatRepository unreadMessageStatRepository) {
        this.unreadMessageStatRepository = unreadMessageStatRepository;
    }

    @Override
    public Map<String, Integer> mapCountToLabels(String userId) {
        List<com.biratpoudel.inbox.model.UnreadMessageStat> stats = unreadMessageStatRepository.findAllById(userId);
        return stats.stream()
                .collect(Collectors.toMap(com.biratpoudel.inbox.model.UnreadMessageStat::getLabel, com.biratpoudel.inbox.model.UnreadMessageStat::getUnreadCount));
    }
}