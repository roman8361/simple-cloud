package ru.kravchenko.cloud.server.database;

/**
 * @author Roman Kravchenko
 */
public interface AutoService {

    String getNickname(String login, String password);

    void stop();

    boolean chekRegistry(String login, String password);

    boolean start();

}
