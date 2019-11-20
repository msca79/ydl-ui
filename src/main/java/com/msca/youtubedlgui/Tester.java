package com.msca.youtubedlgui;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Tester {
    public static void main(String[] args) throws YoutubeDLException {

        try {
            Process proc = Runtime.getRuntime().exec("cmd set PATH=%PATH%;D:\\WORK\\SOURCES\\msca-ydl\\src\\main\\resources\\bin");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        }
        catch (IOException e1) {
        }

        System.out.println("Path has been changed");


        YoutubeDL.executablePath = "D:\\WORK\\SOURCES\\msca-ydl\\src\\main\\resources\\bin\\youtube-dl.exe";

        YoutubeDLRequest request = new YoutubeDLRequest();
        request.setUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        request.setOption("extract-audio");
        request.setOption("audio-format","mp3");
        request.setDirectory("d:\\WORK\\SOURCES\\msca-ydl\\test\\");

        YoutubeDLResponse response = YoutubeDL.execute(request);

        System.out.println("done");
    }
}
