package bo.edu.uagrm.ugram.storage.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Object Storage Service — MinIO integration for file uploads.
 * Handles lab reports (PDF), DICOM files (X-rays), and any attachments.
 * US-W13, US-W14
 */
@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private static final Logger log = LoggerFactory.getLogger(ObjectStorageService.class);

    private final MinioClient minioClient;

    @Value("${app.minio.bucket-name}")
    private String bucketName;

    /**
     * Uploads a file to MinIO and returns the object key.
     */
    public String upload(MultipartFile file, String folder) {
        String objectKey = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            log.info("Uploaded file to MinIO: {}/{}", bucketName, objectKey);
            return objectKey;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a presigned URL for temporary file access (default: 1 hour).
     */
    public String getPresignedUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(1, TimeUnit.HOURS)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }

    /**
     * Downloads a file from MinIO as an InputStream.
     */
    public InputStream download(String objectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from MinIO: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a file from MinIO.
     */
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build());
            log.info("Deleted file from MinIO: {}/{}", bucketName, objectKey);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO: " + e.getMessage(), e);
        }
    }
}
