package _team.earnedit.controller;

import _team.earnedit.global.ApiResponse;
import _team.earnedit.service.FileUploadService;
import com.amazonaws.services.s3.AmazonS3Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@Tag(name = "S3 API", description = "S3 파일 업로드 API")
public class FileUploadController {

    private final FileUploadService fileUploadService;


    @PostMapping
    @Operation(summary = "사진 업로드", description = "입력받은 사진 또는 파일을 S3에 업로드합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadFile(file);

        return ResponseEntity.ok(ApiResponse.success("file successfully uploaded", url));
    }
}
