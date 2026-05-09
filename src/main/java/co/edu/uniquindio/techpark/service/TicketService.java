package co.edu.uniquindio.techpark.service;

import co.edu.uniquindio.techpark.model.entities.Ticket;
import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.util.CodeGenerator;

public class TicketService {
    private final CodeGenerator codeGenerator;

    public TicketService() {
        this.codeGenerator = new CodeGenerator();
    }

//    public Ticket createTicket(TicketType type, double price, double discount) throws Exception {
//        String newId = codeGenerator.generateDecimalCodeStr(8);
//
//        return new Ticket.Builder(newId, type)
//                .basePrice(price)
//                .applyAdminDiscount(discount)
//                .build();
//    }
}