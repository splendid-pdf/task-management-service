package ru.splendidpdf.model.event;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ImageConversionEvent {
    String taskId;
    String fileName;
    String formatTo;
    String formatFrom;
    String encodedContent;
    String timestamp;
}
