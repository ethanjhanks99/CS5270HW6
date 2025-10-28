import com.fasterxml.jackson.databind.ObjectMapper;
import org.consumer.S3RequestRetriever;
import org.consumer.WidgetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3RequestRetrieverTest {

    @Mock
    private S3Client mockS3Client;
    private S3RequestRetriever retriever;
    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        retriever = new S3RequestRetriever(mockS3Client, BUCKET_NAME);
    }

    @Test
    @DisplayName("testGetNextRequest should return a request when an object is found")
    void testGetNextRequest() throws Exception {
        S3Object s3Object = S3Object.builder().key("bogus-key-01.json").build();
        ListObjectsV2Response fakeListResponse = ListObjectsV2Response.builder()
                .contents(List.of(s3Object))
                .build();
        when( mockS3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(fakeListResponse);

        String fakeJson = "{\"requestId\": \"bogus-key-01.json\", \"widgetId\": \"w-123\"}";
        GetObjectResponse fakeResponse = GetObjectResponse.builder().build();
        ResponseBytes<GetObjectResponse> fakeResponseBytes = ResponseBytes.fromByteArray(fakeResponse, fakeJson.getBytes());
        when(mockS3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(fakeResponseBytes);

        Optional<WidgetRequest> result = retriever.getNextRequest();

        assertTrue(result.isPresent(), "An Optional with a request should be present");
        assertEquals("bogus-key-01.json", result.get().getRequestId());
        assertEquals("w-123", result.get().getWidgetId());

        verify( mockS3Client, times(1) ).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("getNextRequest should return empty when no objects are found")
    void testGetNextRequestWhenNoObjects() throws Exception {

        ListObjectsV2Response emptyListResponse = ListObjectsV2Response.builder()
                .contents(List.of())
                .build();
        when( mockS3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(emptyListResponse);

        Optional<WidgetRequest> result = retriever.getNextRequest();

        assertFalse(result.isPresent(), "The returned Optional should be empty");

        verify( mockS3Client, never()).getObjectAsBytes(any(GetObjectRequest.class));
        verify( mockS3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }
}
