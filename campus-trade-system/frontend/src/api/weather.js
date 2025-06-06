import request from '@/utils/request'

// 获取当前天气（默认城市）
export const getCurrentWeatherApi = () => {
  return request({
    url: '/api/weather/current',
    method: 'get'
  })
}

// 根据城市编码获取天气
export const getWeatherByCityCodeApi = (cityCode) => {
  return request({
    url: `/api/weather/current/${cityCode}`,
    method: 'get'
  })
}

// 根据城市名称获取天气
export const getWeatherByCityNameApi = (cityName) => {
  return request({
    url: `/api/weather/city/${encodeURIComponent(cityName)}`,
    method: 'get'
  })
} 