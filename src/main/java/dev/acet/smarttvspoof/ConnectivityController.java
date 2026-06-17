package dev.acet.smarttvspoof;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectivityController {

    private static final String CHECK_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rsp>ok</rsp>";

    @GetMapping(value = "/Public/network/files/check.xml", produces = "text/xml")
    public ResponseEntity<String> checkXml() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/xml"))
                .body(CHECK_XML);
    }

    @GetMapping(value = "/openapi/sts/time/v2/tz", produces = "application/json")
    public ResponseEntity<Map<String, String>> timezone() {
        String tzName = ZoneId.systemDefault().getId();
        String tzRule = "# Zone\tNAME\t\tGMTOFF\tRULES\tFORMAT\t[UNTIL]\n"
                + "Zone\t" + tzName + "\t0:00\t-\tGMT\n";
        String tzData = Base64.getEncoder().encodeToString(tzRule.getBytes());
        return ResponseEntity.ok(Map.of(
                "RspStat", "ok",
                "Type", "1",
                "TzName", tzName,
                "TzVersion", "2021010",
                "TzData", tzData));
    }

    @GetMapping(value = "/openapi/timesync", produces = "application/octet-stream")
    public ResponseEntity<byte[]> timesync() {
        long millis = System.currentTimeMillis();
        byte[] body = ByteBuffer.allocate(16)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(millis)
                .putLong(millis)
                .array();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(body);
    }
}