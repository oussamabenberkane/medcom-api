package com.oussamabenberkane.medcom.service.mapper;

import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Watchlist} and its DTO {@link WatchlistDTO}.
 */
@Mapper(componentModel = "spring")
public interface WatchlistMapper extends EntityMapper<WatchlistDTO, Watchlist> {
    @Mapping(target = "pharmacy", source = "pharmacy", qualifiedByName = "pharmacyId")
    WatchlistDTO toDto(Watchlist s);

    @Named("pharmacyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PharmacyDTO toDtoPharmacyId(Pharmacy pharmacy);
}
