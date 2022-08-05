package com.example.sahal.Springbootmultithreading2.Feign;

import com.example.sahal.Springbootmultithreading2.dto.CityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import static com.example.sahal.Springbootmultithreading2.constant.Constant.*;

@FeignClient(name = "city-api",
        url = CITY_SERVICE_URL,
        fallback = CityFeignFallback.class)
public interface CityFeignService {

    @GetMapping("/name/{name}")
    ResponseEntity<CityDto> findCityByName(@PathVariable String name);

    @GetMapping("/{id}")
    ResponseEntity<CityDto> findCityById(@PathVariable long id);
}
