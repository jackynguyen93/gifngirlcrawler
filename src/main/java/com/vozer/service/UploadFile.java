package com.vozer.service;

import com.vozer.model.Post;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by vqnguyen on 4/10/2017.
 */
@Service
public class UploadFile {
    private final String server = "103.195.241.106";
    private final int port = 21;
    private final String user = "gifngirl";
    private final String pass = "vQ2uPHoK";
    private final String serverImgPath = "/domains/gifngirl.com/public_html/uploads/posts/t/";
    private final String serverVideoPath = "/domains/gifngirl.com/public_html/uploads/posts/videos/";

    public void upload(Map<String, String> namesAndUrls, String fileType) {

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            for (Map.Entry<String, String> nameAndUrl :namesAndUrls.entrySet()) {
                URL url = new URL(nameAndUrl.getValue());
                InputStream inputStream = new BufferedInputStream(url.openStream());

                String serverFile = "";
                if ("video".equals(fileType)) {
                    // upload to folder video
                    ftpClient.storeFile( serverVideoPath + nameAndUrl.getKey(), inputStream);
                    if (nameAndUrl.getKey().contains(".webm")) {
                        // upload to folder image for webm file
                        serverFile = serverImgPath + "s-" + nameAndUrl.getKey();
                    }
                } else {
                    serverFile = serverImgPath + nameAndUrl.getKey();
                }

                System.out.println("Start uploading file: " + serverFile);

                boolean done = ftpClient.storeFile(serverFile, inputStream);
                inputStream.close();
                if (done) {
                    System.out.println("The " + serverFile + " file is uploaded successfully.");
                }
            }

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
