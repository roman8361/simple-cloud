package ru.kravchenko.cloud.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Roman Kravchenko
 */

@Getter
@Setter
public class AutoMessageToClient extends AbstractMessage {

    private boolean chekRegistry;

}
