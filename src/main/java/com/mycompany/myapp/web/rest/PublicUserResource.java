package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UserData;
import com.mycompany.myapp.repository.UserDataRepository;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.UserDTO;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey")
    );

    private final Logger log = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;
    private final UserDataRepository userDataRepository;

    public PublicUserResource(UserService userService, UserDataRepository userDataRepository) {
        this.userService = userService;
        this.userDataRepository = userDataRepository;
    }

    /**
     * {@code GET /users} : get all users with only the public informations - calling this are allowed for anyone.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get all public User names");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<UserDTO> page = userService.getAllPublicUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/user-access")
    public String getUserAllowed(@RequestParam String rfId) {
        Optional<UserData> userDataOptional = userDataRepository.findByRfId(rfId);
        if (userDataOptional.isEmpty()) return "REJECT";
        UserData userData = userDataOptional.get();
        if (!userData.getRestricted()) return "OK";

        String timePeriod = System.getenv("TIME_PERIOD") == null ? "20" : System.getenv("TIME_PERIOD");
        if (ZonedDateTime.now().minusSeconds(Integer.parseInt(timePeriod)).isAfter(userData.getUpdatedAt())) {
            return "OK";
        } else if (userData.getCount() == 2) {
            return "REJECT";
        } else return "OK";
    }

    @PostMapping("/user-access")
    public ResponseEntity<?> setCount(@RequestParam String rfId) {
        UserData userData = userDataRepository.findByRfId(rfId).get();
        userData.setCount((userData.getCount() + 1) % 3);
        if (userData.getCount() == 0) userData.setUpdatedAt(ZonedDateTime.now());
        userDataRepository.save(userData);
        return ResponseEntity.ok().build();
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }
}
