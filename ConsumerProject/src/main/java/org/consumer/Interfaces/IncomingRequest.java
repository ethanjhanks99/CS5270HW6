package org.consumer.Interfaces;

import org.consumer.Widget;
import org.consumer.WidgetRequest;

import java.util.Optional;

/**
 * Defines the contract for retrieving widget requests.
 * Implementations of this interface are responsible for fetching
 * the next available request from a specific data source.
 */
public interface IncomingRequest {

    Optional<WidgetRequest> getNextRequest();
}
