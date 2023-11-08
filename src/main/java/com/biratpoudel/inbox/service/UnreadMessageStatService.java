package com.biratpoudel.inbox.service;

import java.util.Map;

public interface UnreadMessageStatService {

    Map<String, Integer> mapCountToLabels(String userId);
}
