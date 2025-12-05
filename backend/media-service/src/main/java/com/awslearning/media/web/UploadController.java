package com.awslearning.media.web;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
public class UploadController {
  @PostMapping(value = "/api/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws Exception {
    String filename = System.currentTimeMillis() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
    Path dir = Path.of("uploads");
    Files.createDirectories(dir);
    Path dest = dir.resolve(filename);
    file.transferTo(dest.toFile());
    return Map.of("filename", filename, "path", dest.toString());
  }
}

