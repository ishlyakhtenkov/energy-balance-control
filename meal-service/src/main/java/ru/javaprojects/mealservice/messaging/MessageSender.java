package ru.javaprojects.mealservice.messaging;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {

    public void sendDateCreatedMessage(LocalDate date, long userId) {
        //TODO: SEND MESSAGE TO QUEUE TO CREATE BX FOR CURRENT DATE
    }
}