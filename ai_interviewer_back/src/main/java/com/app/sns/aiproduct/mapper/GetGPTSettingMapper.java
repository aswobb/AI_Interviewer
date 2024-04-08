package com.app.sns.aiproduct.mapper;

import com.app.sns.aiproduct.pojo.vo.ChatGPTSettingVO;
import org.springframework.stereotype.Repository;

@Repository
public interface GetGPTSettingMapper {
    ChatGPTSettingVO getGptSettingInfo(String settingname);    
}



