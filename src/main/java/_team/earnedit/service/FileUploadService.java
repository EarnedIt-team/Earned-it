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

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client s3Client;

    @Transactional
    public String uploadFile(MultipartFile file) {
        return uploadFileUrl(file);
    }

    public String uploadFileUrl(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            // 실제 접근 가능한 S3 도메인 형식으로 구성
            String fileUrl = "https://" + bucket + ".s3." + s3Client.getRegionName() + ".amazonaws.com/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 퍼블릭 읽기 권한 부여
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);

            s3Client.putObject(putObjectRequest);

            return fileUrl;
        } catch (IOException e) {
            throw new FileException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}
