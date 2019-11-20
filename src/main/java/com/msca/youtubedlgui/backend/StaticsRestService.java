package com.msca.youtubedlgui.backend;

import com.msca.youtubedlgui.common.DownloadQueueEntity;
import com.msca.youtubedlgui.common.FormatType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/static/**")
@Slf4j
public class StaticsRestService {

    public static final String STATICS_PATH = "static/";

    @Autowired
    private DownloadQueueDAO dao;

    public static class StaticsStreaming implements StreamingResponseBody {

        private DownloadQueueEntity staticContent;

        public StaticsStreaming(DownloadQueueEntity staticContent) {
            this.staticContent = staticContent;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write("hello".getBytes());
        }
    }

    @GetMapping
    public ResponseEntity<StreamingResponseBody> download(final HttpServletRequest request, final HttpServletResponse response) {
        String uri = request.getRequestURI();
        String key = uri.substring(uri.lastIndexOf(STATICS_PATH) + STATICS_PATH.length());
        Long id = Long.parseLong(key);
        log.debug("static key is: {}", key);

        final DownloadQueueEntity sc = dao.lockById(id);
        if (sc == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        if (sc.getFormat().equals(FormatType.AUDIO_MP3)) {
            response.setHeader("Content-Disposition", "attachment; filename=\"somefile.mp3\"");
            response.setContentType("audio/mpeg");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=\"somefile.mp4\"");
            response.setContentType("video/mp4");
        }


        return new ResponseEntity(new StaticsStreaming(sc), HttpStatus.OK);

    }
}
