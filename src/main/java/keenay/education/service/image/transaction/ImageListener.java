package keenay.education.service.image.transaction;

import keenay.education.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageListener {
    private final ImageManager imageManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteImageAfterCommit(ImageEvent imageEvent) {
        imageManager.deleteSafePhoto(imageEvent.getOldPhotoUrl());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void deleteImageAfterRollback(ImageEvent imageEvent) {
        imageManager.deleteSafePhoto(imageEvent.getNewPhotoUrl());
    }

}
