package com.buixuanphat.spot_on.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;


    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename(); // abc.pdf

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "format", "pdf",
                            "folder", "file",
                            "public_id", originalName
                    )
            );

            Map<String, String> response = new HashMap<>();
            response.put("url", uploadResult.get("secure_url").toString());
            response.put("id", uploadResult.get("public_id").toString());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Upload tệp thất bại: " + e.getMessage());
        }
    }



    public void deleteFile(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "invalidate", true
                    )
            );

            if (!"ok".equals(result.get("result"))) {
                throw new RuntimeException("Xoá tệp thất bại: " + result.get("result"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xoá tệp: " + e.getMessage());
        }
    }

    public Map<String, String> uploadImage(MultipartFile image) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "images",
                            "resource_type", "image"
                    )
            );

            Map<String, String> response = new HashMap();
            response.put("url", uploadResult.get("secure_url").toString());
            response.put("id", uploadResult.get("public_id").toString());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage());
        }
    }


    public void deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );

            if (!"ok".equals(result.get("result"))) {
                throw new RuntimeException("Xoá ảnh thất bại: " + result.get("result"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xoá ảnh: " + e.getMessage());
        }
    }


}
