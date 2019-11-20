//package com.msca.youtubedlgui.backend;
//
//import com.msca.youtubedlgui.common.DownloadQueueEntity;
//import com.msca.youtubedlgui.common.StatusType;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class DownloadQueuePersistence {
//
//    private static long ID = 0;
//    private List<DownloadQueueEntity> queue = new ArrayList<>();
//
//    public void add(DownloadQueueEntity dto) {
//        dto.setStatus(StatusType.READY);
//        dto.setId(ID++);
//        dto.setCreated(LocalDateTime.now());
//        queue.add(dto);
//    }
//
//    public void delete(Long id) {
//        DownloadQueueEntity toRemove = null;
//        for (DownloadQueueEntity dto : queue) {
//            if (dto.getId() == id) {
//                toRemove = dto;
//            }
//        }
//        if (toRemove != null) {
//            queue.remove(toRemove);
//        }
//    }
//
//    public List<DownloadQueueEntity> getAll() {
//        return queue;
//    }
//}
