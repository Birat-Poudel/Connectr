package com.biratpoudel.inbox.repository;

import com.biratpoudel.inbox.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface MessageRepository extends CassandraRepository<Message, UUID> {
}
