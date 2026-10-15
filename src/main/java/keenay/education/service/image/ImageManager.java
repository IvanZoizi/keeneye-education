package keenay.education.service.image;

import keenay.education.exception.errors.ImageDeleteException;
import keenay.education.exception.errors.ImageUploadException;
import keenay.education.security.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageManager {

    private final ImageService imageService;

    public <T> T uploadThen(MultipartFile photo, CustomUserDetail user,
                            Function<String, T> action) {
        String uploaded = null;
        try {
            if (photo != null && !photo.isEmpty()) {
                uploaded = uploadPhoto(photo, user);
            }
            return action.apply(uploaded);
        } catch (RuntimeException ex) {
            log.error("Failed work with photo: {}", photo.getOriginalFilename(), ex);
            deleteSafePhoto(uploaded);
            throw ex;
        }
    }

    public String uploadPhoto(MultipartFile photo, CustomUserDetail user) {
        try {
            return imageService.uploadPhoto(photo, user);
        } catch (ImageUploadException ex) {
            log.error("Failed to upload photo: {}", photo.getOriginalFilename(), ex);
            throw ex;
        }
    }

    public void deleteSafePhoto(String photo) {
        if (photo == null || photo.isEmpty()) return;
        try {
            imageService.deletePhoto(photo);
        } catch (ImageDeleteException ex) {
            log.error("Failed to delete photo: {}", photo, ex);
        }
    }

    @Named("photoUrlToPresigned")
    public String getPhoto(String photo) {
        try {
            return imageService.getUrlForPhoto(photo);
        } catch (Exception ex) {
            log.error("Failed to resolve photo url: {}", photo, ex);
            return photo;
        }
    }
}