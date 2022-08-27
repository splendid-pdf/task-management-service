package ru.splendidpdf.api.dto;

import lombok.Value;

@Value
public class TaskInfoDto {
    String status;
    String resultUrl;
    String createdAt;
}
