package campusconnect.backend.common.storage.service;

import campusconnect.backend.common.storage.dto.FileUploadResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    public FileUploadResponse uploadFile(MultipartFile file, String folder) {

        try {

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    )
            );

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            return new FileUploadResponse(url, publicId);

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }

    }


    public void deleteFile(String publicId) {

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

        } catch (Exception e) {
            throw new RuntimeException("File deletion failed");
        }

    }
}