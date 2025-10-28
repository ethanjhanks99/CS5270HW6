import org.consumer.Interfaces.WidgetStorage;
import org.consumer.Widget;
import org.consumer.WidgetRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WidgetTest {

    @Test
    @DisplayName("Constructor shoudl correctly convert a WidgetRequest into a Widget")
    void shouldCorrectlyConvertFromWidgetRequest() {
        WidgetRequest widgetRequest = new WidgetRequest();
        widgetRequest.setWidgetId("w-abc-123");
        widgetRequest.setOwner("John Cena");
        widgetRequest.setLabel("John Cena's request");
        widgetRequest.setDescription("This is John Cena's request");

        WidgetRequest.OtherAttribute attribute1 = new WidgetRequest.OtherAttribute();
        attribute1.setName("color");
        attribute1.setValue("tangerine");

        WidgetRequest.OtherAttribute attribute2 = new WidgetRequest.OtherAttribute();
        attribute2.setName("size");
        attribute2.setValue("large");

        WidgetRequest.OtherAttribute attribute3 = new WidgetRequest.OtherAttribute();
        attribute3.setName("brand");
        attribute3.setValue("nike");

        widgetRequest.setOtherAttributes(List.of(attribute1, attribute2, attribute3));

        Widget widget = new Widget(widgetRequest);

        assertEquals("w-abc-123", widget.getWidgetId());
        assertEquals("John Cena", widget.getOwner());
        assertEquals("John Cena's request", widget.getLabel());
        assertEquals("This is John Cena's request", widget.getDescription());

        assertNotNull(widget.getOtherAttributes(), "Other attributes should not be null");
        assertEquals(3, widget.getOtherAttributes().size(), "There should be 3 Other attributes");
        assertEquals("tangerine", widget.getOtherAttributes().get("color"));
        assertEquals("large", widget.getOtherAttributes().get("size"));
        assertEquals("nike", widget.getOtherAttributes().get("brand"));
    }

}
