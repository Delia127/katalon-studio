package com.kms.katalon.zendesk;

public class ZendeskCreateTicketRequest {
    private ZendeskTicket ticket;

    public ZendeskTicket getTicket() {
        return ticket;
    }

    public void setTicket(ZendeskTicket ticket) {
        this.ticket = ticket;
    }
    
    public ZendeskCreateTicketRequest(ZendeskTicket ticket) {
        this.ticket = ticket;
    }
}
