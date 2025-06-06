package com.example.yourproject.controller;

import com.example.yourproject.dto.ApiResponse;
import com.example.yourproject.dto.WeatherDto;
import com.example.yourproject.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 天气信息控制器
 */
@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    
    private final WeatherService weatherService;
    
    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    /**
     * 获取默认城市（北京）的天气信息
     */
    @GetMapping("/current")
    public ApiResponse<WeatherDto> getCurrentWeather() {
        try {
            logger.info("获取默认城市天气信息");
            WeatherDto weather = weatherService.getDefaultWeather();
            return ApiResponse.success("获取天气信息成功", weather);
        } catch (Exception e) {
            logger.error("获取天气信息失败", e);
            return ApiResponse.error("获取天气信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据城市编码获取天气信息
     */
    @GetMapping("/current/{cityCode}")
    public ApiResponse<WeatherDto> getCurrentWeatherByCode(@PathVariable String cityCode) {
        try {
            logger.info("获取城市天气信息，城市编码: {}", cityCode);
            WeatherDto weather = weatherService.getCurrentWeather(cityCode);
            return ApiResponse.success("获取天气信息成功", weather);
        } catch (Exception e) {
            logger.error("获取天气信息失败，城市编码: {}", cityCode, e);
            return ApiResponse.error("获取天气信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据城市名称获取天气信息
     */
    @GetMapping("/city/{cityName}")
    public ApiResponse<WeatherDto> getCurrentWeatherByCity(@PathVariable String cityName) {
        try {
            logger.info("获取城市天气信息，城市名称: {}", cityName);
            WeatherDto weather = weatherService.getCurrentWeatherByCity(cityName);
            return ApiResponse.success("获取天气信息成功", weather);
        } catch (Exception e) {
            logger.error("获取天气信息失败，城市名称: {}", cityName, e);
            return ApiResponse.error("获取天气信息失败: " + e.getMessage());
        }
    }
} 