package com.epic.epiccrawler.HTTP;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class HTTPConnectionService {

    public HttpURLConnection getHTTPConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method to GET
        connection.setRequestMethod("GET");

        // Set headers from the cURL command
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        connection.setRequestProperty("Cache-Control", "no-cache");
        //connection.setRequestProperty("Cookie", "GPS=1; YSC=JmnNEB-Zjhw; VISITOR_INFO1_LIVE=NbT0bXFtoYU; VISITOR_PRIVACY_METADATA=CgJJThIEGgAgYg%3D%3D; PREF=tz=Asia.Calcutta");
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Pragma", "no-cache");
        //connection.setRequestProperty("Priority", "u=0, i");
        connection.setRequestProperty("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"");
        connection.setRequestProperty("sec-ch-ua-arch", "\"x86\"");
        connection.setRequestProperty("sec-ch-ua-bitness", "\"64\"");
        connection.setRequestProperty("sec-ch-ua-form-factors", "\"Desktop\"");
        //connection.setRequestProperty("sec-ch-ua-full-version", "\"129.0.6668.101\"");
        //connection.setRequestProperty("sec-ch-ua-full-version-list", "\"Google Chrome\";v=\"129.0.6668.101\", \"Not=A?Brand\";v=\"8.0.0.0\", \"Chromium\";v=\"129.0.6668.101\"");
        connection.setRequestProperty("sec-ch-ua-mobile", "?0");
        connection.setRequestProperty("sec-ch-ua-model", "\"\"");
        connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
        connection.setRequestProperty("sec-ch-ua-platform-version", "\"15.0.0\"");
        //connection.setRequestProperty("sec-ch-ua-wow64", "?0");
        connection.setRequestProperty("sec-fetch-dest", "document");
        connection.setRequestProperty("sec-fetch-mode", "navigate");
        connection.setRequestProperty("sec-fetch-site", "none");
        //connection.setRequestProperty("sec-fetch-user", "?1");
        //connection.setRequestProperty("service-worker-navigation-preload", "true");
        //connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36");
        connection.setRequestProperty("x-browser-channel", "stable");
        //connection.setRequestProperty("x-browser-copyright", "Copyright 2024 Google LLC. All rights reserved.");
        //connection.setRequestProperty("x-browser-validation", "g+9zsjnuPhmKvFM5e6eaEzcB1JY=");
        //connection.setRequestProperty("x-browser-year", "2024");


        return connection;

    }
}
