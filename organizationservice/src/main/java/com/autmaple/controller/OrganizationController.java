package com.autmaple.controller;

import com.autmaple.model.Organization;
import com.autmaple.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/organization")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {
    private final OrganizationService organizationService;

    @GetMapping("/{organizationId}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Organization> getOrganization(@PathVariable("organizationId") String organizationId, Authentication authentication) {
        String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.error(roles);
        return ResponseEntity.ok(organizationService.findById(organizationId));
    }

    @PutMapping("/{organizationId}")
    @RolesAllowed({"ADMIN", "USER"})
    public void updateOrganization(@PathVariable("organizationId") String organizationId,
                                   @RequestBody Organization organization) {
        organizationService.update(organization);
    }

    @PostMapping
    @RolesAllowed({"ADMIN", "USER"})
    public ResponseEntity<Organization> saveOrganization(@RequestBody Organization organization) {
        return ResponseEntity.ok(organizationService.create(organization));
    }

    @DeleteMapping("/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed({"ADMIN"})
    public void deleteOrganization(@PathVariable("organizationId") String organizationId, @RequestBody Organization organization) {
        organizationService.delete(organization);
    }
}
