package campusconnect.backend.notification;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QRCodeService {

    public String generateQRCodeBase64(String text) throws Exception {

        BitMatrix matrix = new MultiFormatWriter()
                .encode(text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(matrix, "PNG", stream);

        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }
}
