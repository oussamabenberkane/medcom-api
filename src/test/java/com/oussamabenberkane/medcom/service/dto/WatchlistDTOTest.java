package com.oussamabenberkane.medcom.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.oussamabenberkane.medcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WatchlistDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WatchlistDTO.class);
        WatchlistDTO watchlistDTO1 = new WatchlistDTO();
        watchlistDTO1.setId(1L);
        WatchlistDTO watchlistDTO2 = new WatchlistDTO();
        assertThat(watchlistDTO1).isNotEqualTo(watchlistDTO2);
        watchlistDTO2.setId(watchlistDTO1.getId());
        assertThat(watchlistDTO1).isEqualTo(watchlistDTO2);
        watchlistDTO2.setId(2L);
        assertThat(watchlistDTO1).isNotEqualTo(watchlistDTO2);
        watchlistDTO1.setId(null);
        assertThat(watchlistDTO1).isNotEqualTo(watchlistDTO2);
    }
}
