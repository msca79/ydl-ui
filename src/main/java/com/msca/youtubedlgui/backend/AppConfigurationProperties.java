package com.msca.youtubedlgui.backend;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@org.springframework.boot.context.properties.ConfigurationProperties
@Data
public class AppConfigurationProperties {


    @Value("${downloader.bin.path}")
    private String downloaderBinPath;

    @Value("${download.directory}")
    private String downloadDirectory;

}
