package keenay.education.service.image.transaction;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ImageEvent {
    private final String oldPhotoUrl;
    private final String newPhotoUrl;
}
