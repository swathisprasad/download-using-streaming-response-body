package com.techshard.download.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping ("/api")
public class DownloadController {

    private final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @GetMapping (value = "/download", produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> download() {

        StreamingResponseBody stream = out -> {

            final String home = System.getProperty("user.home");
            final File directory = new File(home + File.separator + "Documents" + File.separator + "sample");
            final ZipOutputStream zipOut = new ZipOutputStream(out);

            if(directory.exists() && directory.isDirectory()) {
                try {
                    for (final File file : directory.listFiles()) {
                        final ZipEntry zipEntry=new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);
                        try (final InputStream inputStream = new FileInputStream(file)) {
                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = inputStream.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                        }
                        zipOut.closeEntry();
                    }
                    zipOut.finish();
                } catch (final IOException e) {
                    logger.error("Exception while reading and streaming data {} ", e);
                }
            }
        };
        logger.info("steaming response {} ", stream);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment;filename=sample.zip")
                .body(stream);
    }
}
