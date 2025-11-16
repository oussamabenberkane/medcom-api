package com.oussamabenberkane.medcom.service.mapper;

import com.oussamabenberkane.medcom.domain.Notification;
import com.oussamabenberkane.medcom.domain.Pharmacy;
import com.oussamabenberkane.medcom.domain.User;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.service.dto.NotificationDTO;
import com.oussamabenberkane.medcom.service.dto.PharmacyDTO;
import com.oussamabenberkane.medcom.service.dto.UserDTO;
import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "watchlist", source = "watchlist", qualifiedByName = "watchlistId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "pharmacy", source = "pharmacy", qualifiedByName = "pharmacyId")
    @Mapping(target = "watchlistItem", source = "watchlistItem", qualifiedByName = "watchlistItemId")
    NotificationDTO toDto(Notification s);

    @Named("watchlistId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WatchlistDTO toDtoWatchlistId(Watchlist watchlist);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("pharmacyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PharmacyDTO toDtoPharmacyId(Pharmacy pharmacy);

    @Named("watchlistItemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WatchlistItemDTO toDtoWatchlistItemId(WatchlistItem watchlistItem);
}
