package com.msca.youtubedlgui.backend;

import com.msca.youtubedlgui.common.DownloadQueueEntity;
import com.msca.youtubedlgui.common.FormatType;
import com.msca.youtubedlgui.common.StatusType;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class DownloadQueueService {

    @Autowired
    private DownloadQueueDAO dao;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private AppConfigurationProperties prop;

    public Iterable<DownloadQueueEntity> listAll() {
        return dao.findAll();
    }

    public DownloadQueueEntity add(FormatType format, String url) {

        String key = url.substring(url.lastIndexOf("=") + 1);

        DownloadQueueEntity dto = new DownloadQueueEntity();
        dto.setFormat(format);
        dto.setUrl(url);
        dto.setKey(key);
        dto.setStatus(StatusType.QUEUED);
        dto.setCreatedAt(LocalDateTime.now());

        DownloadQueueEntity saved = dao.save(dto);
        jmsTemplate.convertAndSend("sampleQueue", saved.getId());

        return saved;
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }

    public void remove(Long id) {
        dao.deleteById(id);
    }

    public void retry(Long id) {
        jmsTemplate.convertAndSend("sampleQueue", id);
    }

    @JmsListener(destination = "sampleQueue")
    public void downloadQueueReceived(String msg) throws InterruptedException {

        Long id = Long.parseLong(msg);
        DownloadQueueEntity queueItem = dao.lockById(id);

        if (!StatusType.QUEUED.equals(queueItem.getStatus())) {
            log.warn("Ignore. not in QUEUED state but in: " + queueItem.getStatus());
            return;
        }

        queueItem.setStatus(StatusType.DOWNLOADING);

        try {
            download(queueItem.getUrl(), queueItem.getFormat());
            queueItem.setStatus(StatusType.FINISHED);

        } catch (Exception e) {
            queueItem.setStatus(StatusType.FAILED);
            queueItem.setMessage(e.getMessage());
            e.printStackTrace();
        }

        dao.save(queueItem);
    }

    private void download(String url, FormatType format) throws Exception {

        Process proc = Runtime.getRuntime().exec("cmd set PATH=%PATH%;" + prop.getDownloaderBinPath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        YoutubeDL.executablePath = prop.getDownloaderBinPath() + "\\youtube-dl.exe";

        YoutubeDLRequest request = new YoutubeDLRequest();
        request.setUrl(url);

        if (format.equals(FormatType.AUDIO_MP3)) {
            request.setOption("extract-audio");
            request.setOption("audio-format", "mp3");
        }else{
            request.setOption("format", "mp4");
        }

        request.setDirectory(prop.getDownloadDirectory());

        YoutubeDLResponse response = YoutubeDL.execute(request);


    }
}
