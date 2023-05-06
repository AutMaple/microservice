package com.autmaple.service;

import com.autmaple.model.License;
import com.autmaple.model.Organization;
import com.autmaple.repository.LicenseRepository;
import com.autmaple.service.client.OrganizationFeignClient;
import com.autmaple.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final MessageSource messages;
    private final OrganizationFeignClient organizationClient;

    public License getLicense(String organizationId, String licenseId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message", null, null), licenseId, organizationId));
        }
        Organization organization = organizationClient.getOrganization(organizationId);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }
        return license;
    }

    public License updateLicense(License license) {
        licenseRepository.save(license);
        return license;
    }

    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license;
    }

    public String deleteLicense(String organizationId, String licenseId) {
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        return String.format(messages.getMessage("license.delete.message", null, null), licenseId);
    }

    //    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    //    @Retry(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
//    @Bulkhead(name = "bulkheadLicenseService", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallbackLicenseList")
//    @Bulkhead(name = "bulkheadLicenseService", type = Bulkhead.Type.THREADPOOL)
//    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService")
    @Bulkhead(name = "licenseService")
    @CircuitBreaker(name = "licenseService")
    @Retry(name = "licenseService")
    public CompletableFuture<List<License>> getLicenseByOrganization(String organizationId) throws TimeoutException {
        log.warn("getLicenseByOrganization Correlation id: {}", UserContextHolder.getUserContext().getCorrelationId());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //        randomlyRunLog();
        return CompletableFuture.completedFuture(licenseRepository.findByOrganizationId(organizationId));
    }

    private CompletableFuture<List<License>> buildFallbackLicenseList(String organizationId, Throwable t) {
        log.error("Circuit Breaker is working.");
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("00000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available.");
        fallbackList.add(license);
        return CompletableFuture.completedFuture(fallbackList);
    }

    private void randomlyRunLog() throws TimeoutException {
        Random random = new Random();
        int randomNum = random.nextInt(10) + 1;
        if (randomNum == 10) sleep();
    }

    private void sleep() throws TimeoutException {
        System.out.println("Sleep");
        try {
            Thread.sleep(5000);
            throw new TimeoutException();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
