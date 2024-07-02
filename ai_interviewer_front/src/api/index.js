import request from '@/utils/request'
/**
 * 管理者ログイン画面
 */
export const loginAPI = ({ username, password }) => {
  return request({
    url: '/api/users/login',
    method: 'POST',
    data: {
      username,
      password
    }
  })
}

/**
 * chatpageの履歴書をバックに転送
 */
export const fileSend = (fd) => {
  return request({
    url: '/api/chat/receiveFile',
    method: 'POST',
    data: fd
  })
}

/**
 * managerpageの履歴書をバックに転送
 */
export const fileSend2 = (fd) => {
  return request({
    url: '/api/companyMember/receiveFile',
    method: 'POST',
    data: fd
  })
}

/**
 * チャット内容をバックに転送１
 */

export const chatMessageSend1 = (chatmessage) => {
  return request({
    url: '/api/chat/sendContentByGoogleCloud',
    method: 'POST',
    data: chatmessage
  })
}

/**
 * チャット内容をバックに転送２
 */
export const MessageSend2 = (message) => {
  return request({
    url: '/api/chat/sendMessage',
    method: 'POST',
    data:
      message
  })
}

/**
 * 面接完了
 */
export const MessageSend3 = (message) => {
  return request({
    url: '/api/interviewerInfo/completeInterviewerInfo',
    method: 'POST',
    data:
      message

  })
}

/**
 * 新社員名追加
 */
export const memberPlus = (form) => {
  return request({
    url: '/api/companyMember/insert',
    method: 'POST',
    data:
      form

  })
}

/**
 * 面接者ログイン
 */
export const interviewerLogin = ({ interviewerId, interviewerName }) => {
  return request({
    url: '/api/users/interviewerLoginInfo',
    method: 'POST',
    data: {
      interviewerId,
      interviewerName
    }
  })
}

/**
 *  面接listに新たな面接追加API
 */
export const interviewListAdd = (idid) => {
  return request({
    url: '/api/interviewerInfo/batchCreate',
    method: 'POST',
    data: {
      idid
    }
  })
}

/**
 * 管理者パースワード変更
 */
export const managerPwChange = (dada) => {
  return request({
    url: '/api/snsUser/updatePassword',
    method: 'POST',
    data: dada
  })
}

/**
 * 面接評判ダウンロード
 */

export const interviewInfoDownload = (ID) => {
  return request({
    url: '/api/interviewerInfo/downLoadCsv/' + ID,
    method: 'GET',
    params: {

    }
  })
}

/**
 * 会社員情報を取得
 */

export const memberGet = (obj) => {
  return request({
    url: '/api/companyMember/getAllMebmer',
    method: 'GET',
    params: obj
  })
}

/**
 * 会社員情報リストを取得
 */

export const memberListGet = (id1, pageNum1, pageSize1) => {
  return request({
    url: '/api/companyMember/list',
    method: 'GET',
    params: {
      userId: id1,
      pageNum: pageNum1,
      pageSize: pageSize1
    }
  })
}
/**
 * 面接情報変更 
 */
export const interviewInfoUpdate = (paramter) => {
  return request({
    url: '/api/interviewerInfo/updateInterviewerInfo',
    method: 'POST',
    data: paramter
  })
}

/**
 * 管理者情報変更
 */
export const managerUpdate = ({ id, username, contractor, password, userBillingHistoryVO: { courseId, courseCustomNum }, remainNum }) => {
  return request({
    url: '/api/snsUser/update',
    method: 'POST',
    data: {
      id,
      username,
      contractor,
      password,
      userBillingHistoryVO: {
        courseId,
        courseCustomNum
      },
      remainNum
    }
  })
}

/**
 * 管理者追加
 */

export const managerPlus = ({ id, username, contractor, password, userBillingHistoryVO: { courseId, courseCustomNum }, remainNum }) => {
  return request({
    url: '/api/snsUser/create',
    method: 'POST',
    data: {
      id,
      username,
      contractor,
      password,
      userBillingHistoryVO: {
        courseId,
        courseCustomNum
      },
      remainNum
    }
  })
}

/**
 * 面接list情報取得////////////////////////
 */

export const getInterviewMessageAPI = (pageNum, pageSize) => {
  return request({
    url: '/api/interviewerInfo/list',
    method: 'GET',
    params: {
      pageNum,
      pageSize
    }
  })
}

/**
 * カレントユーザー情報を取得////////////////////////
 */

export const getCurrentUserAPI = () => {
  return request({
    url: '/api/snsUser/getCurrentUser',
    method: 'GET'
  })
}

/**
 * User情報削除
 */
export const memberDelete = (memberId) => {
  return request({
    url: '/api/companyMember/delete',
    method: 'DELETE',
    params: memberId

  })
}

/**
 * 会社情報削除
 */
export const companyDelAPI = (id) => {
  return request({
    url: '/api/snsUser/delete/' + id,
    method: 'DELETE',
    params: {

    }
  })
}

/**
 * 会社情報を取得する
 * @param { pageNum, pageSize } 
 * @returns Promise对象
 */
export const getCompanyAPI = (pageNum, pageSize) => {
  return request({
    url: '/api/snsUser/list',
    method: 'GET',
    params: {
      pageNum,
      pageSize
    }
  })
}

