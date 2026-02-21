import request from '@/utils/request'

/**
 * 发送邮箱验证码
 */
export function sendMailCode(data) {
  return request({
    url: '/api/mail/sendCode',
    method: 'post',
    data
  })
}

/**
 * 验证邮箱验证码
 */
export function verifyMailCode(data) {
  return request({
    url: '/api/mail/verifyCode',
    method: 'post',
    data
  })
}
