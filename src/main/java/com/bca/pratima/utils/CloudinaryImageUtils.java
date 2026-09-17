package com.bca.pratima.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryImageUtils {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, Integer bookId) throws IOException {

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "book-social-network/book-covers",
                        "public_id", "book-" + bookId,
                        "resource_type", "image"
                )
        );

        return result.get("secure_url").toString();
    }
}
