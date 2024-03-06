import request from '@/utils/request'

// 查询foodLabor列表
export function listFoodLabor(query) {
  return request({
    url: '/foodLabor/foodLabor/list',
    method: 'get',
    params: query
  })
}

// 查询foodLabor详细
export function getFoodLabor(id) {
  return request({
    url: '/foodLabor/foodLabor/' + id,
    method: 'get'
  })
}

