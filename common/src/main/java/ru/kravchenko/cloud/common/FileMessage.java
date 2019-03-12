package ru.kravchenko.cloud.common;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Roman Kravchenko
 */

@Getter
@Setter
public class FileMessage extends AbstractMessage {

    private String filename;

    private byte[] data;

    public FileMessage(Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }

}
