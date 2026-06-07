package ru.yandex.practicum.my_market_app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.util.exception.StorageException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ImageService {

    @Value("${FILE_LOCATION:./market-app/config/static/}")
    private String BASE_PATH;

    private final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    public Mono<String> uploadImage(Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            try {
                Path path = Paths.get(BASE_PATH).normalize();

                String contentType = Optional.ofNullable(filePart.headers().getContentType())
                        .map(MimeType::toString).orElse("empty filetype");

                if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
                    return Mono.error(new StorageException("Unsupported file type: " + contentType));
                }

                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                UUID uuid = UUID.randomUUID();

                String fileExtension = StringUtils.getFilenameExtension(filePart.filename());
                String newFilename = String.join("", uuid.toString(), ".", fileExtension);
                Path newFilePath = path.resolve(newFilename);
                log.info("BASE PATH====>" + newFilePath.toAbsolutePath());
                log.info("FILENAME====>" + newFilename);
                return filePart.transferTo(newFilePath).then(Mono.just(newFilename));
            } catch (IOException e) {
                log.error("Error in uploadImage {}", e.getMessage(), e);
                return Mono.error(e);
            }
        });
    }

}
