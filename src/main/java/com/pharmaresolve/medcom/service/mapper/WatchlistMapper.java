package com.pharmaresolve.medcom.service.mapper;

import com.pharmaresolve.medcom.domain.Pharmacy;
import com.pharmaresolve.medcom.domain.Watchlist;
import com.pharmaresolve.medcom.service.dto.PharmacyDTO;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Watchlist} and its DTO {@link WatchlistDTO}.
 */
@Mapper(componentModel = "spring")
public interface WatchlistMapper extends EntityMapper<WatchlistDTO, Watchlist> {
    @Mapping(target = "pharmacyId", source = "pharmacy.id")
    WatchlistDTO toDto(Watchlist s);

    @Mapping(target = "pharmacy", source = "pharmacyId")
    Watchlist toEntity(WatchlistDTO dto);

    default Pharmacy fromId(Long id) {
        if (id == null) return null;
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(id);
        return pharmacy;
    }
}
