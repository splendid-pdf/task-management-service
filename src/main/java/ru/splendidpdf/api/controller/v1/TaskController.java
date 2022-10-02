package ru.splendidpdf.api.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.api.dto.TaskInfoDto;
import ru.splendidpdf.model.CompressionType;
import ru.splendidpdf.model.ImageFormat;
import ru.splendidpdf.service.TaskService;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Task Controller")
@RequestMapping("${app.endpoints.tasks-url}")
@ApiResponses(value = @ApiResponse(responseCode = "400", description = "Bad request", content = @Content))
public class TaskController {
    private final TaskService taskService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Convert image")
    @PostMapping(value = "/conversion/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createImageConversionTask(
            @RequestPart("image") MultipartFile image,
            @RequestParam("from") String from,
            @RequestParam("to") String to) {
        log.info("Received a request to convert an image '{}' from '{}' to '{}'", image.getName(), from, to);
        return taskService.createImageConversionTask(image,
                EnumUtils.getEnumIgnoreCase(ImageFormat.class, from),
                EnumUtils.getEnumIgnoreCase(ImageFormat.class, to));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Compress image")
    @PostMapping(value = "/compression/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createImageCompressionTask(
            @RequestPart("image") MultipartFile image,
            @RequestParam("compression-type") String compressionType) {
        return taskService.createImageCompressionTask(image,
                EnumUtils.getEnumIgnoreCase(CompressionType.class, compressionType));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Track task status by taskId")
    @GetMapping(value = "/{taskId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskInfoDto getTaskInfo(@PathVariable("taskId") String taskId) {
        log.info("Received a request to get a task info by id {}", taskId);
        return taskService.getTaskInfoById(taskId);
    }
}
