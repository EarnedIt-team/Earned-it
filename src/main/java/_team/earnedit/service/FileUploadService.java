package _team.earnedit.service;

import _team.earnedit.global.ErrorCode;
import _team.earnedit.global.exception.file.FileException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client s3Client;

    @Transactional
    public String uploadFile(MultipartFile file) {

        log.info("[FileUploadService] 이미지 업로드 요청 - fileName = {}", file.getOriginalFilename());
        log.debug("[FileUploadService] uploadFile 요청 - fileName = {}, contentType = {}, fileSize = {}",
                file.getOriginalFilename(), file.getContentType(), file.getSize());
        return uploadFileUrl(file);
    }

    public String uploadFileUrl(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            // 실제 접근 가능한 S3 도메인 형식으로 구성
            String fileUrl = "https://" + bucket + ".s3." + s3Client.getRegionName() + ".amazonaws.com/" + fileName;
            log.debug("[FileUploadService] 생성된 S3 이미지 URL = {}", fileUrl);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 퍼블릭 읽기 권한 부여
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);

            s3Client.putObject(putObjectRequest);

            return fileUrl;
        } catch (IOException e) {
            log.error("파일 업로드 실패 - fileName: {}", file.getOriginalFilename());
            throw new FileException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Transactional
    public String uploadImageFromUrl(String imageUrl) {
        try {
            String originalFileName = getFileNameFromUrl(imageUrl);
            String fileExtension = getFileExtension(originalFileName);
            String hashedFileName = generateHashedFileName(imageUrl) + fileExtension;
            
            String s3Url = "https://" + bucket + ".s3." + s3Client.getRegionName() + ".amazonaws.com/" + hashedFileName;
            
            // S3에 이미 존재하는지 확인
            if (s3Client.doesObjectExist(bucket, hashedFileName)) {
                return s3Url;
            }
            
            // URL에서 이미지 다운로드 및 업로드
            URL url = new URL(imageUrl);
            try (InputStream inputStream = url.openStream()) {
                // 먼저 바이트 배열로 읽어서 크기 확인
                byte[] imageBytes = inputStream.readAllBytes();
                
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(getContentTypeFromExtension(fileExtension));
                metadata.setContentLength(imageBytes.length); // 크기 명시적 설정
                
                try (InputStream byteInputStream = new java.io.ByteArrayInputStream(imageBytes)) {
                    PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, hashedFileName, byteInputStream, metadata);
                    s3Client.putObject(putObjectRequest);
                }
            }
            
            return s3Url;
            
        } catch (Exception e) {
            log.error("URL 이미지 업로드 실패 - imageUrl: {}", imageUrl, e);
            throw new FileException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
    
    private String generateHashedFileName(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString().substring(0, 16);
        } catch (Exception e) {
            log.warn("해시 생성 실패, UUID 사용 - url: {}", url);
            return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
    }
    
    private String getFileNameFromUrl(String url) {
        try {
            return url.substring(url.lastIndexOf('/') + 1);
        } catch (Exception e) {
            return "image.jpg";
        }
    }
    
    private String getFileExtension(String fileName) {
        try {
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return fileName.substring(lastDotIndex);
            }
        } catch (Exception e) {
            // ignore
        }
        return ".jpg";
    }
    
    private String getContentTypeFromExtension(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".webp":
                return "image/webp";
            default:
                return "image/jpeg";
        }
    }
}
