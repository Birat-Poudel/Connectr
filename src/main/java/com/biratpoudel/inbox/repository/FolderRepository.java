package com.biratpoudel.inbox.repository;

import java.util.List;

import com.biratpoudel.inbox.model.Folder;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface FolderRepository extends CassandraRepository<Folder, String> {

    List<Folder> findAllById(String id);

}
