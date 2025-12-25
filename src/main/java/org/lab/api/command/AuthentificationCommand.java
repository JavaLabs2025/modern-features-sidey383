package org.lab.api.command;

public sealed interface AuthentificationCommand<RESULT> extends Command<RESULT> permits CreateSessionCommand, CreateUserCommand, IssueJwtToken {
}
