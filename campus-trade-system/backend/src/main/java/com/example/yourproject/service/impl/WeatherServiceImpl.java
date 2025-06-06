package com.example.yourproject.service.impl;

import com.example.yourproject.dto.WeatherDto;
import com.example.yourproject.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
    
    // 高德天气API配置
    private static final String WEATHER_API_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    
    // 可以配置在application.properties中，这里先使用默认值
    @Value("${amap.weather.key:your_amap_key_here}")
    private String amapKey;
    
    // 常用城市编码映射
    private static final Map<String, String> CITY_CODE_MAP = new HashMap<>();
    
    static {
        CITY_CODE_MAP.put("南岗区", "230103");
        CITY_CODE_MAP.put("北京", "110000");
        CITY_CODE_MAP.put("上海", "310000");
        CITY_CODE_MAP.put("广州", "440100");
        CITY_CODE_MAP.put("深圳", "440300");
        CITY_CODE_MAP.put("杭州", "330100");
        CITY_CODE_MAP.put("南京", "320100");
        CITY_CODE_MAP.put("武汉", "420100");
        CITY_CODE_MAP.put("成都", "510100");
        CITY_CODE_MAP.put("西安", "610100");
        CITY_CODE_MAP.put("重庆", "500000");
    }
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public WeatherServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public WeatherDto getCurrentWeather(String cityCode) {
        try {
            logger.info("获取城市天气信息，城市编码: {}", cityCode);
            
            // 检查API Key配置
            if ("your_amap_key_here".equals(amapKey)) {
                logger.warn("高德地图API Key未配置，返回模拟数据");
                return createMockWeatherData(cityCode);
            }
            
            String url = String.format("%s?key=%s&city=%s&extensions=base&output=JSON", 
                                     WEATHER_API_URL, amapKey, cityCode);
            
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("高德天气API响应: {}", response);
            
            return parseWeatherResponse(response);
            
        } catch (Exception e) {
            logger.error("获取天气信息失败，城市编码: {}", cityCode, e);
            return createMockWeatherData(cityCode);
        }
    }
    
    @Override
    public WeatherDto getCurrentWeatherByCity(String cityName) {
        String cityCode = CITY_CODE_MAP.getOrDefault(cityName, "230103"); // 默认北京
        return getCurrentWeather(cityCode);
    }
    
    @Override
    public WeatherDto getDefaultWeather() {
        return getCurrentWeather("230103"); // 北京
    }
    
    /**
     * 解析高德天气API响应
     */
    private WeatherDto parseWeatherResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            
            // 检查API响应状态
            if (!"1".equals(root.path("status").asText())) {
                logger.warn("高德天气API返回错误状态: {}", response);
                return createMockWeatherData("230103");
            }
            
            JsonNode lives = root.path("lives");
            if (lives.isArray() && lives.size() > 0) {
                JsonNode weather = lives.get(0);
                
                WeatherDto weatherDto = new WeatherDto();
                weatherDto.setProvince(weather.path("province").asText());
                weatherDto.setCity(weather.path("city").asText());
                weatherDto.setAdcode(weather.path("adcode").asText());
                weatherDto.setWeather(weather.path("weather").asText());
                weatherDto.setTemperature(weather.path("temperature").asText());
                weatherDto.setWinddirection(weather.path("winddirection").asText());
                weatherDto.setWindpower(weather.path("windpower").asText());
                weatherDto.setHumidity(weather.path("humidity").asText());
                weatherDto.setReporttime(weather.path("reporttime").asText());
                
                logger.info("成功解析天气数据: {}", weatherDto);
                return weatherDto;
            }
            
        } catch (Exception e) {
            logger.error("解析天气API响应失败", e);
        }
        
        return createMockWeatherData("230103");
    }
    
    /**
     * 创建模拟天气数据（用于API Key未配置或API调用失败时）
     */
    private WeatherDto createMockWeatherData(String cityCode) {
        WeatherDto mockWeather = new WeatherDto();
        
        // 根据城市编码设置城市信息
        String cityName = getCityNameByCode(cityCode);
        mockWeather.setCity(cityName);
        mockWeather.setProvince(getProvinceByCity(cityName));
        mockWeather.setAdcode(cityCode);
        
        // 设置模拟天气数据
        mockWeather.setWeather("晴");
        mockWeather.setTemperature("22");
        mockWeather.setWinddirection("东南");
        mockWeather.setWindpower("3");
        mockWeather.setHumidity("65");
        mockWeather.setReporttime(java.time.LocalDateTime.now().toString());
        
        logger.info("返回模拟天气数据: {}", mockWeather);
        return mockWeather;
    }
    
    /**
     * 根据城市编码获取城市名称
     */
    private String getCityNameByCode(String cityCode) {
        for (Map.Entry<String, String> entry : CITY_CODE_MAP.entrySet()) {
            if (entry.getValue().equals(cityCode)) {
                return entry.getKey();
            }
        }
        return "北京"; // 默认返回北京
    }
    
    /**
     * 根据城市名称获取省份
     */
    private String getProvinceByCity(String cityName) {
        Map<String, String> cityProvinceMap = new HashMap<>();
        cityProvinceMap.put("北京", "北京市");
        cityProvinceMap.put("上海", "上海市");
        cityProvinceMap.put("广州", "广东省");
        cityProvinceMap.put("深圳", "广东省");
        cityProvinceMap.put("杭州", "浙江省");
        cityProvinceMap.put("南京", "江苏省");
        cityProvinceMap.put("武汉", "湖北省");
        cityProvinceMap.put("成都", "四川省");
        cityProvinceMap.put("西安", "陕西省");
        cityProvinceMap.put("重庆", "重庆市");
        
        return cityProvinceMap.getOrDefault(cityName, "北京市");
    }
} 