package dev.acet.smarttvspoof;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConnectivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void checkXmlReturnsConnectivityResponse() throws Exception {
        mockMvc.perform(get("/Public/network/files/check.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/xml"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rsp>ok</rsp>"));
    }

    @Test
    void timesyncReturnsBinaryPackedEpochMillis() throws Exception {
        long before = System.currentTimeMillis();

        byte[] body = mockMvc.perform(get("/openapi/timesync"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        long after = System.currentTimeMillis();

        assertEquals(16, body.length);

        ByteBuffer buf = ByteBuffer.wrap(body).order(ByteOrder.LITTLE_ENDIAN);
        long ms1 = buf.getLong();
        long ms2 = buf.getLong();

        assertEquals(ms1, ms2);
        assertTrue(ms1 >= before && ms1 <= after,
                "Timestamp %d not in range [%d, %d]".formatted(ms1, before, after));
    }

    @Test
    void timesyncIgnoresQueryParams() throws Exception {
        mockMvc.perform(get("/openapi/timesync").param("client", "T20O"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));
    }

    @Test
    void timezoneReturnsValidJsonResponse() throws Exception {
        String body = mockMvc.perform(get("/openapi/sts/time/v2/tz").param("client", "T24O"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(body);

        assertEquals("ok", json.get("RspStat").asText());
        assertEquals("1", json.get("Type").asText());
        assertFalse(json.get("TzName").asText().isEmpty());
        assertFalse(json.get("TzVersion").asText().isEmpty());

        String tzData = json.get("TzData").asText();
        assertFalse(tzData.isEmpty());
        assertDoesNotThrow(() -> Base64.getDecoder().decode(tzData));
    }

    @Test
    void timezoneIgnoresQueryParams() throws Exception {
        mockMvc.perform(get("/openapi/sts/time/v2/tz"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}