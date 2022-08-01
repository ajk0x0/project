package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserDataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserData.class);
        UserData userData1 = new UserData();
        userData1.setId(1L);
        UserData userData2 = new UserData();
        userData2.setId(userData1.getId());
        assertThat(userData1).isEqualTo(userData2);
        userData2.setId(2L);
        assertThat(userData1).isNotEqualTo(userData2);
        userData1.setId(null);
        assertThat(userData1).isNotEqualTo(userData2);
    }
}
