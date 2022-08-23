package ru.splendidpdf.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.config.properties.ValidationProperties;
import ru.splendidpdf.exception.ValidationException;
import ru.splendidpdf.model.ImageFormat;

@Service
@RequiredArgsConstructor
public class ImageValidationService {
    private final ValidationProperties properties;

    public void validateFileAndInputParams(MultipartFile image, ImageFormat from, ImageFormat to) {
        validateInputParameters(from, to);
        validateFile(image, from);
    }

    private void validateInputParameters(ImageFormat from, ImageFormat to) {
        if (ObjectUtils.anyNull(from, to)) {
            throw new ValidationException("Input parameters must be presented");
        }
    }

    private void validateFile(MultipartFile file, ImageFormat format) {
        validateFileSize(file.getSize());

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
