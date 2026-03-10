package campusconnect.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;

public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name","dafjrlqph",
                "api_key","277839339532791",
                "api_secret", "EptsH8HbyiFywNQxqpzcVFxP3As"
        ));
    }
}
