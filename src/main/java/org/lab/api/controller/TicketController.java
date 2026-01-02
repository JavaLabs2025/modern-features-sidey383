package org.lab.api.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.lab.api.authorization.AuthorizationProvider;
import org.lab.api.command.*;
import org.lab.api.dto.ListAnswer;
import org.lab.api.dto.ticket.CreateTicketRequest;
import org.lab.api.dto.ticket.TicketAssignRequest;
import org.lab.api.dto.ticket.UpdateTicketRequest;
import org.lab.api.mapper.TicketMapper;
import org.lab.data.entity.TicketStatus;
import org.lab.serice.CommandExecutor;
import org.mapstruct.factory.Mappers;

public class TicketController extends AbstractController {

    private final TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);

    public TicketController(CommandExecutor commandExecutor, AuthorizationProvider authProvider) {
        super(commandExecutor, authProvider);
    }

    @Override
    public void endpointSetup(Javalin javalin) {
        javalin
                .post("/api/ticket", authWrap(this::createTicket))
                .put("/api/ticket",authWrap(this::updateTicket))
                .get("/api/me/tickets", authWrap(this::getMyTickets))
                .get("/api/tickets", authWrap(this::getTickets))
                .get("/api/ticket", authWrap(this::getTicket))
                .get("/api/ticket/developers", authWrap(this::getTicketDevelopers))
                .post("/api/ticket/accepted", authWrap(this::setTicketAccepted))
                .post("/api/ticket/in-progress", authWrap(this::setTicketInProgress))
                .post("/api/ticket/completed", authWrap(this::setTicketCompleted))
                .post("/api/ticket/assign", authWrap(this::assignDeveloper))
                .post("/api/ticket/deassign", authWrap(this::deassignDeveloper));
    }

    public void createTicket(Context context) {
        var request = context.bodyAsClass(CreateTicketRequest.class);
        var ticket = execute(new CreateTicketCommand(request.milestoneId(), request.name(), request.description()));
        context.json(ticketMapper.toAnswer(ticket));
    }

    public void updateTicket(Context context) {
        var request = context.bodyAsClass(UpdateTicketRequest.class);
        var ticket = execute(new UpdateTicketCommand(request.ticketId(), request.name(), request.description()));
        context.json(ticketMapper.toAnswer(ticket));
    }

    public void getMyTickets(Context context) {
        var tickets = execute(new GetMyTicketsCommand());
        context.json(new ListAnswer<>(ticketMapper.toAnswer(tickets)));
    }

    public void getTickets(Context context) {
        var milestoneId = extractLongParam(context, "milestoneId");
        var tickets = execute(new GetTicketsCommand(milestoneId));
        context.json(new ListAnswer<>(ticketMapper.toAnswer(tickets)));
    }


    public void getTicket(Context context) {
        var ticketId = extractLongParam(context, "ticketId");
        var ticket = execute(new GetTicketCommand(ticketId));
        context.json(ticketMapper.toAnswer(ticket));
    }

    public void setTicketAccepted(Context context) {
        var ticketId = extractLongParam(context, "ticketId");
        var ticket = execute(new ChangeTicketStatusCommand(ticketId, TicketStatus.ACCEPTED));
        context.json(ticketMapper.toAnswer(ticket));
    }

    public void setTicketInProgress(Context context) {
        var ticketId = extractLongParam(context, "ticketId");
        var ticket = execute(new ChangeTicketStatusCommand(ticketId, TicketStatus.IN_PROCESS));
        context.json(ticketMapper.toAnswer(ticket));
    }

    public void setTicketCompleted(Context context) {
        var ticketId = extractLongParam(context, "ticketId");
        var ticket = execute(new ChangeTicketStatusCommand(ticketId, TicketStatus.COMPLETED));
        context.json(ticketMapper.toAnswer(ticket));
    }

    public void getTicketDevelopers(Context context) {
        var ticketId = extractLongParam(context, "ticketId");
        var ticketDevelopers = execute(new GetTicketDevelopers(ticketId));
        context.json(new ListAnswer<>(ticketDevelopers));
    }

    public void assignDeveloper(Context context) {
        var request =context.bodyAsClass(TicketAssignRequest.class);
        execute(new AddTicketDeveloperCommand(request.ticketId(), request.userId()));
    }

    public void deassignDeveloper(Context context) {
        var request =context.bodyAsClass(TicketAssignRequest.class);
        execute(new RemoveTicketDeveloperCommand(request.ticketId(), request.userId()));
    }

}
