package com.msca.youtubedlgui.backend;

import com.msca.youtubedlgui.common.DownloadQueueEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.msca.youtubedlgui.common.QDownloadQueueEntity.downloadQueueEntity;

@Component
public class DownloadQueueDAOCustomImpl implements DownloadQueueDAOCustom {

    @PersistenceContext
    protected EntityManager em;

    protected <T> JPAQuery<T> query(EntityPathBase<T> entityPath) {
        return new JPAQuery<T>(em)
                .select(entityPath)
                .from(entityPath);
    }

    public DownloadQueueEntity findByUrl(String url) {
        return query(downloadQueueEntity).where(downloadQueueEntity.url.eq(url)).fetchOne();
    }

    public DownloadQueueEntity lockById(Long id) {
        return query(downloadQueueEntity).where(downloadQueueEntity.id.eq(id)).fetchOne();
    }



}
