package com.msca.youtubedlgui.backend;

import com.msca.youtubedlgui.common.DownloadQueueEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadQueueDAO extends CrudRepository<DownloadQueueEntity, Long>, DownloadQueueDAOCustom {
}
