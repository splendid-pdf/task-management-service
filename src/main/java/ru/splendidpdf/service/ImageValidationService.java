package ru.splendidpdf.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.config.properties.ValidationProperties;
import ru.splendidpdf.exception.ValidationException;
import ru.splendidpdf.model.CompressionType;
import ru.splendidpdf.model.ImageFormat;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageValidationService {
    private final ValidationProperties properties;

    public void validateFileAndParametersForConversion(MultipartFile image, ImageFormat from, ImageFormat to) {
        validateImageFormatInputParameters(from, to);
        validateFileExtension(image, from);
        validateFileSize(image.getSize());
    }

    public void validateFileAndParametersForCompression(MultipartFile image, CompressionType compressionType) {
        if (Objects.isNull(compressionType)) {
            throw new ValidationException("Compression type must be presented");
        }

        validateFileSize(image.getSize());
    }

    private void validateImageFormatInputParameters(ImageFormat from, ImageFormat to) {
        if (ObjectUtils.anyNull(from, to)) {
            throw new ValidationException("Input parameters must be presented");
        }

        if (Objects.equals(from, to)) {
            throw new ValidationException("Input parameters must be different");
        }
    }

    private void validateFileExtension(MultipartFile file, ImageFormat format) {
        if (!format.getKey().equals(FilenameUtils.getExtension(file.getOriginalFilename()))) {
            throw new ValidationException("Format of the file must be %s".formatted(format.getKey()));
        } else if (!format.getMediaType().equals(file.getContentType())) {
            throw new ValidationException("Content type must be %s".formatted(format.getMediaType()));
        }
    }

    private void validateFileSize(long fileSizeInBytes) {
        long fileSizeInKb = fileSizeInBytes / 1024;
        if (properties.getMaxImageFileSize() < fileSizeInKb) {
            throw new ValidationException("File must not exceed %d Kb".formatted(properties.getMaxImageFileSize()));
        }
    }
}
