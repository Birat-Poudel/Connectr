package com.biratpoudel.inbox.repository;

import com.biratpoudel.inbox.model.MessageList;
import com.biratpoudel.inbox.model.MessageListKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface MessageListRepository extends CassandraRepository<MessageList, MessageListKey> {

    // List<MessageList> findAllByKey(MessageListKey key);

    List<MessageList> findAllByKey_IdAndKey_Label(String id, String label);
}
