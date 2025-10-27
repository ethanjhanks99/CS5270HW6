package org.consumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the actual widget to be stored.
 * It will hold the final data that will get saved to the S3 bucket
 */
public class Widget {

    private String widgetId;
    private String owner;
    private String label;
    private String description;
    private Map<String, String> otherAttributes;

    public Widget() {}
    public Widget(WidgetRequest widgetRequest) {
        this.widgetId = widgetRequest.getWidgetId();
        this.owner = widgetRequest.getOwner();
        this.label = widgetRequest.getLabel();
        this.description = widgetRequest.getDescription();

        this.otherAttributes = new HashMap<>();

        if (widgetRequest.getOtherAttributes() != null) {
            for (WidgetRequest.OtherAttribute otherAttribute : widgetRequest.getOtherAttributes()) {
                this.otherAttributes.put(otherAttribute.getName(), otherAttribute.getValue());
            }
        }
    }

    public String getWidgetId() {
        return widgetId;
    }
    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Map<String, String> getOtherAttributes() {
        return otherAttributes;
    }
    public void setOtherAttributes(Map<String, String> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }
}
