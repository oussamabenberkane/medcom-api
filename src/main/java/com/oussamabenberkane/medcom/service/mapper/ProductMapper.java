package com.oussamabenberkane.medcom.service.mapper;

import com.oussamabenberkane.medcom.domain.Product;
import com.oussamabenberkane.medcom.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {}
