package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UserData;
import com.mycompany.myapp.repository.UserDataRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.UserData}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserDataResource {

    private final Logger log = LoggerFactory.getLogger(UserDataResource.class);

    private static final String ENTITY_NAME = "userData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserDataRepository userDataRepository;

    public UserDataResource(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    /**
     * {@code POST  /user-data} : Create a new userData.
     *
     * @param userData the userData to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userData, or with status {@code 400 (Bad Request)} if the userData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-data")
    public ResponseEntity<UserData> createUserData(@RequestBody UserData userData) throws URISyntaxException {
        log.debug("REST request to save UserData : {}", userData);
        if (userData.getId() != null) {
            throw new BadRequestAlertException("A new userData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userData.setUpdatedAt(ZonedDateTime.now());
        UserData result = userDataRepository.save(userData);
        return ResponseEntity
            .created(new URI("/api/user-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-data/:id} : Updates an existing userData.
     *
     * @param id the id of the userData to save.
     * @param userData the userData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userData,
     * or with status {@code 400 (Bad Request)} if the userData is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-data/{id}")
    public ResponseEntity<UserData> updateUserData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserData userData
    ) throws URISyntaxException {
        log.debug("REST request to update UserData : {}, {}", id, userData);
        if (userData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userData.setUpdatedAt(ZonedDateTime.now());
        UserData result = userDataRepository.save(userData);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userData.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-data/:id} : Partial updates given fields of an existing userData, field will ignore if it is null
     *
     * @param id the id of the userData to save.
     * @param userData the userData to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userData,
     * or with status {@code 400 (Bad Request)} if the userData is not valid,
     * or with status {@code 404 (Not Found)} if the userData is not found,
     * or with status {@code 500 (Internal Server Error)} if the userData couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserData> partialUpdateUserData(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserData userData
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserData partially : {}, {}", id, userData);
        if (userData.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userData.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserData> result = userDataRepository
            .findById(userData.getId())
            .map(existingUserData -> {
                if (userData.getRfId() != null) {
                    existingUserData.setRfId(userData.getRfId());
                }
                if (userData.getRestricted() != null) {
                    existingUserData.setRestricted(userData.getRestricted());
                }
                if (userData.getCount() != null) {
                    existingUserData.setCount(userData.getCount());
                }
                if (userData.getUpdatedAt() != null) {
                    existingUserData.setUpdatedAt(userData.getUpdatedAt());
                }

                return existingUserData;
            })
            .map(userDataRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userData.getId().toString())
        );
    }

    /**
     * {@code GET  /user-data} : get all the userData.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userData in body.
     */
    @GetMapping("/user-data")
    public List<UserData> getAllUserData() {
        log.debug("REST request to get all UserData");
        return userDataRepository.findAll();
    }

    /**
     * {@code GET  /user-data/:id} : get the "id" userData.
     *
     * @param id the id of the userData to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userData, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-data/{id}")
    public ResponseEntity<UserData> getUserData(@PathVariable Long id) {
        log.debug("REST request to get UserData : {}", id);
        Optional<UserData> userData = userDataRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userData);
    }

    /**
     * {@code DELETE  /user-data/:id} : delete the "id" userData.
     *
     * @param id the id of the userData to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-data/{id}")
    public ResponseEntity<Void> deleteUserData(@PathVariable Long id) {
        log.debug("REST request to delete UserData : {}", id);
        userDataRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
