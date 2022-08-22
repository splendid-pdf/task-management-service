package ru.splendidpdf.model;

import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
public enum ImageFormat {
    PNG("png", MediaType.IMAGE_PNG_VALUE),
    JPG("jpg", MediaType.IMAGE_JPEG_VALUE),
    GIF("gif", MediaType.IMAGE_GIF_VALUE),
    TIFF("tiff", "image/tiff");

    private final String key;
    private final String mediaType;

    ImageFormat(String key, String mediaType) {
        this.key = key;
        this.mediaType = mediaType;
    }
}
