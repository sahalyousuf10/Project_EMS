package com.example.sahal.Springbootmultithreading2.Feign;

import com.example.sahal.Springbootmultithreading2.dto.CityDto;
import com.example.sahal.Springbootmultithreading2.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import static com.example.sahal.Springbootmultithreading2.constant.Constant.*;

@FeignClient(name = "company-api",
        url = COMPANY_SERVICE_URL,
        fallback = CompanyFeignFallback.class)
public interface CompanyFeignService {

    @GetMapping("/name/{name}")
    ResponseEntity<CompanyDto> findCompanyByName(@PathVariable String name);

    @GetMapping("/{id}")
    ResponseEntity<CompanyDto> findCompanyById(@PathVariable long id);
}
