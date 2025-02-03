package com.fiap.tc.infrastructure.core.amqp.builder;


import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;

import java.time.LocalDateTime;
import java.util.List;


public class EventMessageBuilder<T> {

    private String trackingId;
    private LocalDateTime creationDate;
    private List<T> object;

    public EventMessageBuilder trackingId(final String trackingId) {
        this.trackingId = trackingId;
        return this;
    }


    public EventMessageBuilder creationDate() {
        this.creationDate = LocalDateTime.now();
        return this;
    }

    public EventMessageBuilder content(final List<T> object) {
        this.object = object;
        return this;
    }

    public EventMessage build() {
        return new EventMessage<T>(this.creationDate, this.object);
    }

}