package com.biratpoudel.inbox.repository;

import com.biratpoudel.inbox.model.UnreadMessageStat;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface UnreadMessageStatRepository extends CassandraRepository<UnreadMessageStat, String> {

    List<UnreadMessageStat> findAllById(String userId);

    @Query("UPDATE unread_message_stat SET unReadCount = unReadCount + 1 WHERE user_id = ?0 AND label = ?1")
    public void incrementUnreadCount(String userId, String label);

    @Query("UPDATE unread_message_stat SET unReadCount = unReadCount - 1 WHERE user_id = ?0 AND label = ?1")
    public void decrementUnreadCount(String userId, String label);
}
