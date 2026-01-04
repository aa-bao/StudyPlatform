import request from '../utils/request'

// 登录
export function loginApi(data) {
    return request({
        url: '/user/login',
        method: 'post',
        data
    })
}

// 注册
export function registerApi(data) {
    return request({
        url: '/user/register',
        method: 'post',
        data
    })
}

// 更新基本信息 (昵称、考研年份、科目等)
export function updateUserApi(data) {
    return request({
        url: '/user/update',
        method: 'post',
        data
    })
}

// 修改密码
export function updatePwdApi(data) {
    return request({
        url: '/user/updatePwd',
        method: 'post',
        data
    })
}

// 上传头像
export function uploadAvatarApi(formData) {
    return request({
        url: '/file/upload', 
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}