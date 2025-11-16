package com.oussamabenberkane.medcom.service.mapper;

import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pharmacy} and its DTO {@link PharmacyDTO}.
 */
@Mapper(componentModel = "spring")
public interface PharmacyMapper extends EntityMapper<PharmacyDTO, Pharmacy> {}
