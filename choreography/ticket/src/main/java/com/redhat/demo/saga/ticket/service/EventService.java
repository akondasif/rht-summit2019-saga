package com.redhat.demo.saga.ticket.service;

import com.redhat.demo.saga.ticket.event.ProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@ApplicationScoped
public class EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    @Inject
    EntityManager entityManager;

    @Transactional
    public ProcessedEvent processEvent(ProcessedEvent processedEvent) {

        entityManager.persist(processedEvent);
        entityManager.flush();

        return processedEvent;
    }

    public boolean isEventProcessed(String orderId, String eventType) {
        ProcessedEvent processedEvent;
        try {

            processedEvent = (ProcessedEvent) entityManager.createNamedQuery("ProcessedEvent.findByEventType")
                    .setParameter("correlationId", orderId)
                    .setParameter("eventType", eventType)
                    .getSingleResult();

        } catch (NoResultException nre) {
            LOGGER.info("ProcessedEvent not found {} - {}", orderId, eventType);
            return false;
        }
        return processedEvent != null;
    }
}
