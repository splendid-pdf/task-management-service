package ru.splendidpdf.model.event;

import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.model.ImageFormat;

import java.time.LocalDateTime;
import java.util.Base64;

@Value
public class ImageConversionEvent {
    String taskId;
    String formatTo;
    String formatFrom;
    String encodedContent;
    String timestamp;

    @SneakyThrows
    public static ImageConversionEvent createEvent(MultipartFile file,
                                                   String taskId,
                                                   ImageFormat from,
                                                   ImageFormat to) {
        return new ImageConversionEvent(
                taskId,
                to.getKey(),
                from.getKey(),
                Base64.getEncoder().encodeToString(file.getBytes()),
                LocalDateTime.now().toString());
    }
}
