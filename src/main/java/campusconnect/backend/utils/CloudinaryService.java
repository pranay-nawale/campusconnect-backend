package campusconnect.backend.utils;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {


    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {

        try{
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return  uploadResult.get("url").toString();
        }
        catch(Exception e){
            throw new RuntimeException("File upload failed");
        }
    }
}
