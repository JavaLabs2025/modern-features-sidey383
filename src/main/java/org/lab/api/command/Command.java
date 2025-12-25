package org.lab.api.command;

@SuppressWarnings("unused")
public sealed interface Command<RESULT> permits AuthentificationCommand {

    boolean requireAuthorization();

}
