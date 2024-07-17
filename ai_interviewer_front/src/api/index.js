import request from '@/utils/request'
/**
 * 管理者ログイン画面
 */
export const loginAPI = ({ username, password }) => {
  return request({
    url: '/users/login',
    method: 'POST',
    data: {
      username,
      password
    }
  })
}
/**
 * リフレッシュredis
 */
export const refreshRedis = (key) => {
  return request({
    url: '/refresh/userRedis/' + key,
    method: 'GET',
    params: {
    }
  })
}


/**
 * chatpageの履歴書をバックに転送
 */
export const fileSend = (fd) => {
  return request({
    url: '/chat/receiveFile',
    method: 'POST',
    data: fd
  })
}

/**
 * managerpageの履歴書をバックに転送
 */
export const fileSend2 = (fd) => {
  return request({
    url: '/companyMember/receiveFile',
    method: 'POST',
    data: fd
  })
}

/**
 * チャット内容をバックに転送１
 */

export const chatMessageSend1 = (chatmessage) => {
  return request({
    url: '/chat/sendContentByGoogleCloud',
    method: 'POST',
    data: chatmessage
  })
}

/**
 * チャット内容をバックに転送２
 */
export const MessageSend2 = (message) => {
  return request({
    url: '/chat/sendMessage',
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
    url: '/interviewerInfo/completeInterviewerInfo',
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
    url: '/companyMember/insert',
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
    url: '/users/interviewerLoginInfo',
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
    url: '/interviewerInfo/batchCreate',
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
    url: '/snsUser/updatePassword',
    method: 'POST',
    data: dada
  })
}

/**
 * 面接評判ダウンロード
 */

export const interviewInfoDownload = (ID) => {
  return request({
    url: '/interviewerInfo/downLoadCsv/' + ID,
    method: 'GET',
    params: {

    }
  })
}

/**
 * 面接評判ダウンロード
 */

export const interviewDownloadMajority = (ids) => {
  return request({
    url: '/interviewerInfo/downLoadCsv',
    method: 'GET',
    data: ids
  })
}
/**
 * CSVファイル　複数ダウンロード
 */

export const downLoadCsvsAPI = (ids) => {
  return request({
    url: '/interviewerInfo/downLoadCsvs',
    method: 'POST',
    data: ids,
    responseType: 'blob'
  })
}
/**
 * 会社員情報を取得
 */

export const memberGet = (obj) => {
  return request({
    url: '/companyMember/getAllMebmer',
    method: 'GET',
    params: obj
  })
}

/**
 * 会社員情報リストを取得
 */

export const memberListGet = (id1, pageNum1, pageSize1) => {
  return request({
    url: '/companyMember/list',
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
    url: '/interviewerInfo/updateInterviewerInfo',
    method: 'POST',
    data: paramter
  })
}

/**
 * 管理者情報変更
 */
export const managerUpdate = ({ id, username, contractor, password, userBillingHistoryVO: { courseId, courseCustomNum }, remainNum }) => {
  return request({
    url: '/snsUser/update',
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
    url: '/snsUser/create',
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
    url: '/interviewerInfo/list',
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
    url: '/snsUser/getCurrentUser',
    method: 'GET'
  })
}

/**
 * User情報削除
 */
export const memberDelete = (memberId) => {
  return request({
    url: '/companyMember/delete',
    method: 'DELETE',
    params: memberId

  })
}

/**
 * 会社情報削除
 */
export const companyDelAPI = (id) => {
  return request({
    url: '/snsUser/delete/' + id,
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
    url: '/snsUser/list',
    method: 'GET',
    params: {
      pageNum,
      pageSize
    }
  })
}

/**
 * 会社員複数削除
 */
export const companyMembersDelAPI = (ids) => {
  return request({
    url: '/companyMember/deleteByIds',
    method: 'DELETE',
    data: ids
  })
}