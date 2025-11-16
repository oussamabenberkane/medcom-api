package com.oussamabenberkane.medcom.service.mapper;

import com.oussamabenberkane.medcom.domain.Product;
import com.oussamabenberkane.medcom.domain.Watchlist;
import com.oussamabenberkane.medcom.domain.WatchlistItem;
import com.oussamabenberkane.medcom.service.dto.ProductDTO;
import com.oussamabenberkane.medcom.service.dto.WatchlistDTO;
import com.oussamabenberkane.medcom.service.dto.WatchlistItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WatchlistItem} and its DTO {@link WatchlistItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface WatchlistItemMapper extends EntityMapper<WatchlistItemDTO, WatchlistItem> {
    @Mapping(target = "watchlist", source = "watchlist", qualifiedByName = "watchlistId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    WatchlistItemDTO toDto(WatchlistItem s);

    @Named("watchlistId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WatchlistDTO toDtoWatchlistId(Watchlist watchlist);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
