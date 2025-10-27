package org.consumer.Interfaces;

import org.consumer.Widget;

/**
 * Defines the contract for storing a widget.
 */
public interface WidgetStorage {
    void save(Widget widget) throws Exception;
}
