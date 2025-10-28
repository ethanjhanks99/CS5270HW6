package org.consumer;

import org.consumer.Interfaces.IncomingRequest;
import org.consumer.Interfaces.WidgetStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Consumer {

    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    private final IncomingRequest request;
    private final WidgetStorage widgetStorage;
    private final long pollingInterval;

    private volatile boolean running = true;

    public Consumer(IncomingRequest request, WidgetStorage widgetStorage, long pollingInterval) {
        this.request = request;
        this.widgetStorage = widgetStorage;
        this.pollingInterval = pollingInterval;
    }

    public void start() {
        log.info("Starting consumer...");
        log.info("Polling for requests...");
        while (running) {
            try {
                Optional<WidgetRequest> optionalRequest = request.getNextRequest();
                if (optionalRequest.isPresent()) {
                    processRequest(optionalRequest.get());
                } else {
                    log.debug("No requests found. Waiting for {}ms...", pollingInterval);
                    Thread.sleep(pollingInterval);
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for requests to complete. Shutting down...");
                stop();

                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Error while processing request: {}", e.getMessage(), e);
            }
        }
        log.info("Consumer application has stopped.");
    }

    private void processRequest(WidgetRequest widgetRequest) {
        log.info("Processing request ID: {}", widgetRequest.getRequestId());

        if ("create".equalsIgnoreCase(widgetRequest.getType())) {
            try {
                Widget widget = new Widget(widgetRequest);

                widgetStorage.save(widget);
            } catch (Exception e) {
                log.error("Failed to save widget: {}", widgetRequest.getWidgetId(), e);
            }
        } else {
            log.warn("Unrecognized request type: {}", widgetRequest.getType());
        }
    }

    public void stop() {
        log.info("Stopping consumer...");
        this.running = false;
    }
}
