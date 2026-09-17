package com.bca.pratima.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryImageUtils {

    @Value("${cloudinary.cloudinaryImageFolder}")
    private String cloudinaryImageFolder;

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, Integer bookId) throws IOException {

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", cloudinaryImageFolder+"/book-covers",
                        "public_id", "book-" + bookId,
                        "resource_type", "image"
                )
        );

        return result.get("secure_url").toString();
    }
}
