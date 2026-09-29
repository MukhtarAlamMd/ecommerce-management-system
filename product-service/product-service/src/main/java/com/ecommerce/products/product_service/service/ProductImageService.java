package com.ecommerce.products.product_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {

    String uploadImage(Long productId, MultipartFile file);

}