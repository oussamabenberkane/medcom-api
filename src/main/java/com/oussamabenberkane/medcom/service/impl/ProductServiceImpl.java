package com.oussamabenberkane.medcom.service.impl;

import com.oussamabenberkane.medcom.domain.Product;
import com.oussamabenberkane.medcom.repository.ProductRepository;
import com.oussamabenberkane.medcom.security.SecurityUtils;
import com.oussamabenberkane.medcom.service.ProductService;
import com.oussamabenberkane.medcom.service.dto.ProductDTO;
import com.oussamabenberkane.medcom.service.mapper.ProductMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.oussamabenberkane.medcom.domain.Product}.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        LOG.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);

        // Set the creator
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        product.setCreatedBy(currentUserLogin);

        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    public ProductDTO update(ProductDTO productDTO) {
        LOG.debug("Request to update Product : {}", productDTO);

        // Check if user is authorized to update this product
        Optional<Product> existingProductOpt = productRepository.findById(productDTO.getId());
        if (existingProductOpt.isPresent()) {
            String currentUserLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AccessDeniedException("No authenticated user found"));

            String productCreator = existingProductOpt.get().getCreatedBy();
            if (productCreator != null && !productCreator.equals(currentUserLogin)) {
                throw new AccessDeniedException(
                    String.format("User '%s' is not authorized to update product created by '%s'", currentUserLogin, productCreator)
                );
            }

            Product product = productMapper.toEntity(productDTO);
            // Preserve the original creator
            product.setCreatedBy(existingProductOpt.get().getCreatedBy());
            // Set the updater
            product.setUpdatedBy(currentUserLogin);

            product = productRepository.save(product);
            return productMapper.toDto(product);
        } else {
            throw new IllegalArgumentException("Product not found with id: " + productDTO.getId());
        }
    }

    @Override
    public Optional<ProductDTO> partialUpdate(ProductDTO productDTO) {
        LOG.debug("Request to partially update Product : {}", productDTO);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("No authenticated user found"));

        return productRepository
            .findById(productDTO.getId())
            .map(existingProduct -> {
                // Check authorization
                String productCreator = existingProduct.getCreatedBy();
                if (productCreator != null && !productCreator.equals(currentUserLogin)) {
                    throw new AccessDeniedException(
                        String.format("User '%s' is not authorized to update product created by '%s'", currentUserLogin, productCreator)
                    );
                }

                productMapper.partialUpdate(existingProduct, productDTO);
                // Set the updater
                existingProduct.setUpdatedBy(currentUserLogin);

                return existingProduct;
            })
            .map(productRepository::save)
            .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOne(Long id) {
        LOG.debug("Request to get Product : {}", id);
        return productRepository.findById(id).map(productMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Product : {}", id);
        productRepository.deleteById(id);
    }
}
