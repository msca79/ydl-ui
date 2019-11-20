package com.msca.youtubedlgui.backend;

import com.msca.youtubedlgui.common.DownloadQueueEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface DownloadQueueDAOCustom  {

    DownloadQueueEntity findByUrl(String url);

    DownloadQueueEntity lockById(Long id);
}
