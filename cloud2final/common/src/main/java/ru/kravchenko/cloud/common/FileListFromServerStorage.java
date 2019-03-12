package ru.kravchenko.cloud.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Roman Kravchenko
 */

@Getter
@Setter
public class FileListFromServerStorage extends AbstractMessage {

    private List<String> filesListFromServerStorage;

    public FileListFromServerStorage(List<String> filesListFromServerStorage) {
        this.filesListFromServerStorage = filesListFromServerStorage;
    }

}
