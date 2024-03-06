import request from '@/utils/request'

// 查询年平均价格列表
export function listAvgYear(query) {
  return request({
    url: '/avgYear/avgYear/list',
    method: 'get',
    params: query
  })
}

// 查询年平均价格详细
export function getAvgYear(ayid) {
  return request({
    url: '/avgYear/avgYear/' + ayid,
    method: 'get'
  })
}


