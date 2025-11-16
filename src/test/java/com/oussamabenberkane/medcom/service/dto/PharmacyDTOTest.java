package com.oussamabenberkane.medcom.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.oussamabenberkane.medcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PharmacyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PharmacyDTO.class);
        PharmacyDTO pharmacyDTO1 = new PharmacyDTO();
        pharmacyDTO1.setId(1L);
        PharmacyDTO pharmacyDTO2 = new PharmacyDTO();
        assertThat(pharmacyDTO1).isNotEqualTo(pharmacyDTO2);
        pharmacyDTO2.setId(pharmacyDTO1.getId());
        assertThat(pharmacyDTO1).isEqualTo(pharmacyDTO2);
        pharmacyDTO2.setId(2L);
        assertThat(pharmacyDTO1).isNotEqualTo(pharmacyDTO2);
        pharmacyDTO1.setId(null);
        assertThat(pharmacyDTO1).isNotEqualTo(pharmacyDTO2);
    }
}
