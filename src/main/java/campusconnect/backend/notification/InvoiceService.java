package campusconnect.backend.notification;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final SpringTemplateEngine templateEngine;

    public byte[] generateInvoice(Map<String, Object> vars) throws IOException {

        Context context = new Context();
        context.setVariables(vars);

        String html = templateEngine.process("email/invoice", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);
        builder.run();

        return outputStream.toByteArray();
    }
}
