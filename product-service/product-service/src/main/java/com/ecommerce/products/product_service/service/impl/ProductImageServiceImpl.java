package com.ecommerce.products.product_service.service.impl;

import com.ecommerce.products.product_service.entity.Product;
import com.ecommerce.products.product_service.exception.ResourceNotFoundException;
import com.ecommerce.products.product_service.repository.ProductRepository;
import com.ecommerce.products.product_service.service.ProductImageService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/products");

    @Override
    public String uploadImage(
            Long productId,
            MultipartFile file
    ) {

        // 1. Check product
        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );

        // 2. Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image"
            );
        }

        // 3. Validate content type
        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        try {

            // 4. Create directory
            Files.createDirectories(uploadDirectory);

            // 5. Get original extension
            String originalFilename =
                    file.getOriginalFilename();

            String extension = "";

            if (originalFilename != null &&
                    originalFilename.contains(".")) {

                extension =
                        originalFilename.substring(
                                originalFilename.lastIndexOf(".")
                        );
            }

            // 6. Generate unique filename
            String filename =
                    UUID.randomUUID() + extension;

            // 7. Save file
            Path filePath =
                    uploadDirectory.resolve(filename);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // 8. URL stored in database
            String imageUrl =
                    "/uploads/products/" + filename;

            // 9. Update product
            product.setImageUrl(imageUrl);

            productRepository.save(product);

            return imageUrl;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to upload product image",
                    e
            );
        }
    }
}