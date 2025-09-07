package com.pharmaresolve.medcom.service.mapper;

import com.pharmaresolve.medcom.domain.Product;
import com.pharmaresolve.medcom.domain.Watchlist;
import com.pharmaresolve.medcom.domain.WatchlistItem;
import com.pharmaresolve.medcom.service.dto.ProductDTO;
import com.pharmaresolve.medcom.service.dto.WatchlistDTO;
import com.pharmaresolve.medcom.service.dto.WatchlistItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WatchlistItem} and its DTO {@link WatchlistItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface WatchlistItemMapper extends EntityMapper<WatchlistItemDTO, WatchlistItem> {
    @Mapping(target = "watchlistId", source = "watchlist.id")
    @Mapping(target = "productId", source = "product.id")
    WatchlistItemDTO toDto(WatchlistItem s);

    @Mapping(target = "watchlist", source = "watchlistId")
    @Mapping(target = "product", source = "productId")
    WatchlistItem toEntity(WatchlistItemDTO dto);

    default Watchlist fromWatchlistId(Long id) {
        if (id == null) return null;
        Watchlist watchlist = new Watchlist();
        watchlist.setId(id);
        return watchlist;
    }

    default Product fromProductId(Long id) {
        if (id == null) return null;
        Product product = new Product();
        product.setId(id);
        return product;
    }
}
