package com.example.yourproject.service;

import com.example.yourproject.dto.WeatherDto;

/**
 * 天气服务接口
 */
public interface WeatherService {
    
    /**
     * 根据城市编码获取实时天气信息
     * @param cityCode 城市编码
     * @return 天气信息
     */
    WeatherDto getCurrentWeather(String cityCode);
    
    /**
     * 根据城市名称获取实时天气信息
     * @param cityName 城市名称
     * @return 天气信息
     */
    WeatherDto getCurrentWeatherByCity(String cityName);
    
    /**
     * 获取默认城市（北京）的天气信息
     * @return 天气信息
     */
    WeatherDto getDefaultWeather();
} 