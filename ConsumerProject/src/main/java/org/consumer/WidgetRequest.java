package org.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Mirrors the structure of the JSON from Appendix A
 * It will use the Jackson library to automatically convert JSON strings into object of this class.
 */
public class WidgetRequest {

    private String type;
    private String requestId;
    private String widgetId;
    private String owner;
    private String label;
    private String description;

    @JsonProperty("otherAttributes")
    private List<OtherAttribute> otherAttributes;

    public static class OtherAttribute {
        private String name;
        private String value;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
    public List<OtherAttribute> getOtherAttributes() {
        return otherAttributes;
    }
    public void setOtherAttributes(List<OtherAttribute> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }
}
