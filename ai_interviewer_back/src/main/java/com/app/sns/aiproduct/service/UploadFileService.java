package com.app.sns.aiproduct.service;

import com.app.sns.aiproduct.web.JsonResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
public interface UploadFileService {
    StringBuilder uploadFile(MultipartFile file) throws IOException;
}
