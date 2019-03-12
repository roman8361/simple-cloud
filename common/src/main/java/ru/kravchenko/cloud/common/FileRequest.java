package ru.kravchenko.cloud.common;

import lombok.Getter;

/**
 * @author Roman Kravchenko
 */

@Getter
public class FileRequest extends AbstractMessage {

    private String CDM_Command;

    private String filename;

    public FileRequest(String CDM_Command) { this.CDM_Command = CDM_Command; }

    public FileRequest(String CDM_Command, String filename) {
        this.CDM_Command = CDM_Command;
        this.filename = filename;
    }

}
