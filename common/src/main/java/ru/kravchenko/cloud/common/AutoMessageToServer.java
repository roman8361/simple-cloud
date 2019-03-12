package ru.kravchenko.cloud.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Roman Kravchenko
 */

@Getter
@Setter
public class AutoMessageToServer extends AbstractMessage {

    private String login;

    private String password;

    public AutoMessageToServer(String login, String password) {
        this.login = login;
        this.password = password;
    }

}
