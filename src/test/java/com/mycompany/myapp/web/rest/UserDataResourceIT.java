package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.UserData;
import com.mycompany.myapp.repository.UserDataRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserDataResourceIT {

    private static final String DEFAULT_RF_ID = "AAAAAAAAAA";
    private static final String UPDATED_RF_ID = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESTRICTED = false;
    private static final Boolean UPDATED_RESTRICTED = true;

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/user-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDataMockMvc;

    private UserData userData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createEntity(EntityManager em) {
        UserData userData = new UserData()
            .rfId(DEFAULT_RF_ID)
            .restricted(DEFAULT_RESTRICTED)
            .count(DEFAULT_COUNT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return userData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createUpdatedEntity(EntityManager em) {
        UserData userData = new UserData()
            .rfId(UPDATED_RF_ID)
            .restricted(UPDATED_RESTRICTED)
            .count(UPDATED_COUNT)
            .updatedAt(UPDATED_UPDATED_AT);
        return userData;
    }

    @BeforeEach
    public void initTest() {
        userData = createEntity(em);
    }

    @Test
    @Transactional
    void createUserData() throws Exception {
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();
        // Create the UserData
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isCreated());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate + 1);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getRfId()).isEqualTo(DEFAULT_RF_ID);
        assertThat(testUserData.getRestricted()).isEqualTo(DEFAULT_RESTRICTED);
        assertThat(testUserData.getCount()).isEqualTo(DEFAULT_COUNT);
        assertThat(testUserData.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createUserDataWithExistingId() throws Exception {
        // Create the UserData with an existing ID
        userData.setId(1L);

        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get all the userDataList
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId().intValue())))
            .andExpect(jsonPath("$.[*].rfId").value(hasItem(DEFAULT_RF_ID)))
            .andExpect(jsonPath("$.[*].restricted").value(hasItem(DEFAULT_RESTRICTED.booleanValue())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        // Get the userData
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL_ID, userData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userData.getId().intValue()))
            .andExpect(jsonPath("$.rfId").value(DEFAULT_RF_ID))
            .andExpect(jsonPath("$.restricted").value(DEFAULT_RESTRICTED.booleanValue()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingUserData() throws Exception {
        // Get the userData
        restUserDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData
        UserData updatedUserData = userDataRepository.findById(userData.getId()).get();
        // Disconnect from session so that the updates on updatedUserData are not directly saved in db
        em.detach(updatedUserData);
        updatedUserData.rfId(UPDATED_RF_ID).restricted(UPDATED_RESTRICTED).count(UPDATED_COUNT).updatedAt(UPDATED_UPDATED_AT);

        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getRfId()).isEqualTo(UPDATED_RF_ID);
        assertThat(testUserData.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
        assertThat(testUserData.getCount()).isEqualTo(UPDATED_COUNT);
        assertThat(testUserData.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData.updatedAt(UPDATED_UPDATED_AT);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getRfId()).isEqualTo(DEFAULT_RF_ID);
        assertThat(testUserData.getRestricted()).isEqualTo(DEFAULT_RESTRICTED);
        assertThat(testUserData.getCount()).isEqualTo(DEFAULT_COUNT);
        assertThat(testUserData.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData.rfId(UPDATED_RF_ID).restricted(UPDATED_RESTRICTED).count(UPDATED_COUNT).updatedAt(UPDATED_UPDATED_AT);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getRfId()).isEqualTo(UPDATED_RF_ID);
        assertThat(testUserData.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
        assertThat(testUserData.getCount()).isEqualTo(UPDATED_COUNT);
        assertThat(testUserData.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userData))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();
        userData.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserData() throws Exception {
        // Initialize the database
        userDataRepository.saveAndFlush(userData);

        int databaseSizeBeforeDelete = userDataRepository.findAll().size();

        // Delete the userData
        restUserDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, userData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
