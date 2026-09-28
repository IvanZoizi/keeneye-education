package keenay.education.service.image;

import io.minio.*;
import io.minio.errors.MinioException;
import keenay.education.exception.errors.ImageDeleteException;
import keenay.education.exception.errors.ImageUploadException;
import keenay.education.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ImageService {
    private final MinioClient minioClient;
    private final String bucketName;
    private final Integer days;

    public ImageService(
            MinioClient minioClient,
            @Value("${minio.bucket-name}") String bucketName,
            @Value("${minio.day-life}") int days
    ) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.days = days;
    }

    public String uploadPhoto(MultipartFile photo, CustomUserDetail customUserDetail) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            String ext = StringUtils.getFilenameExtension(photo.getOriginalFilename());
            String objectName = "users/" + customUserDetail.getUser().getId() + "/photos/" + UUID.randomUUID()
                    + (ext != null ? "." + ext : "");
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(photo.getInputStream(), photo.getSize(), -1L)
                            .contentType(photo.getContentType())
                            .build()
            );
            return objectName;
        } catch (Exception exception) {
            throw new ImageUploadException("Failed to upload photo");
        }

    }

    public String getUrlForPhoto(String photoName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET)
                            .bucket(bucketName)
                            .object(photoName)
                            .expiry(days, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception exception) {
            throw new ImageUploadException("Failed to upload photo");
        }
    }
    public void deletePhoto(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new ImageDeleteException("Failed to delete photo");
        }
    }
}
