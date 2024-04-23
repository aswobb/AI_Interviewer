package com.app.sns.aiproduct.service.impl;

import com.app.sns.aiproduct.constant.DataDictionary;
import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.lock.LockManager;
import com.app.sns.aiproduct.mapper.BillingCourseMapper;
import com.app.sns.aiproduct.mapper.UserBillingHistoryMapper;
import com.app.sns.aiproduct.mapper.UserMapper;
import com.app.sns.aiproduct.pojo.entity.BillingCourse;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.entity.UserBillingHistory;
import com.app.sns.aiproduct.pojo.vo.SnsUserVO;
import com.app.sns.aiproduct.pojo.vo.UserBillingHistoryVO;
import com.app.sns.aiproduct.service.InterviewerInfoService;
import com.app.sns.aiproduct.service.UserService;
import com.app.sns.aiproduct.util.EmptyUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SnsUser> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private BillingCourseMapper billingCourseMapper;
    @Resource
    private UserBillingHistoryMapper userBillingHistoryMapper;
    @Resource
    private InterviewerInfoService interviewerInfoService;

    @Override
    public SnsUser getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<SnsUser> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    @Transactional
    public SnsUser createUser(Long creator, SnsUserVO userVO) {
        QueryWrapper<SnsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SnsUser::getUsername, userVO.getUsername());
        queryWrapper.lambda().eq(SnsUser::getEnable, 0);
        Integer integer = userMapper.selectCount(queryWrapper);
        if (integer > 0) {
            throw new ServiceException(ServiceCodeEnum.ERR_CONFLICT);
        }
        SnsUser snsUser = new SnsUser();
        BeanUtils.copyProperties(userVO, snsUser);
        snsUser.setId(null);
        snsUser.setRoleId("2");
        snsUser.setEnable(0);
        snsUser.setUserNum(snsUser.getUsername());
        if (!EmptyUtil.isNull(userVO.getUserBillingHistoryVO().getCourseId())) {
            BillingCourse billingCourse = billingCourseMapper.selectById(userVO.getUserBillingHistoryVO().getCourseId());
            snsUser.setCourseId(userVO.getUserBillingHistoryVO().getCourseId());
            if (DataDictionary.BILLING_COUSE_CUSTOM.getValue().equals(billingCourse.getCourseName())) {
                snsUser.setRemainNum(userVO.getUserBillingHistoryVO().getCourseCustomNum());
            } else {
                snsUser.setRemainNum(billingCourse.getQuestionsNum());
            }
        }
        LocalDateTime currentTime = LocalDateTime.now();
        snsUser.setEffectiveTime(currentTime.plus(1, ChronoUnit.YEARS));
        snsUser.setGmtCreate(LocalDateTime.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        String formattedDate = currentTime.format(formatter);
        snsUser.setJoinTime(formattedDate);
        userMapper.insert(snsUser);
        if (!EmptyUtil.isNull(snsUser.getRemainNum()) && snsUser.getRemainNum() > 0) {
            interviewerInfoService.batchCreate(snsUser.getId(), 20);
        }
        if (!EmptyUtil.isNull(userVO.getUserBillingHistoryVO().getCourseId())) {
            UserBillingHistory userBillingHistory = new UserBillingHistory();
            userBillingHistory.setCourseId(snsUser.getCourseId());
            userBillingHistory.setAddUsageCount(snsUser.getRemainNum());
            userBillingHistory.setRemainNum(snsUser.getRemainNum());
            userBillingHistory.setUserId(snsUser.getId());
            userBillingHistory.setCreator(creator);
            userBillingHistory.setGmtCreate(LocalDateTime.now());
            userBillingHistoryMapper.insert(userBillingHistory);
        }
        return snsUser;
    }

    @Override
    @Transactional
    public SnsUser updateUser(Long creator, SnsUserVO userVO) {


        SnsUser oldData = userMapper.selectById(userVO.getId());
        QueryWrapper<SnsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SnsUser::getUsername, userVO.getUsername());
        queryWrapper.lambda().eq(SnsUser::getEnable, 0);
        queryWrapper.lambda().ne(SnsUser::getId, userVO.getId());
        Integer integer = userMapper.selectCount(queryWrapper);
        if (integer > 0) {
            throw new ServiceException(ServiceCodeEnum.ERR_CONFLICT);
        }
        oldData.setUsername(userVO.getUsername());
        oldData.setPassword(userVO.getPassword());
        oldData.setContractor(userVO.getContractor());
        boolean isChangeRemainNum = false;
        if (!EmptyUtil.isNull(userVO.getUserBillingHistoryVO().getCourseId())) {
            Integer remainNum = EmptyUtil.isNull(oldData.getRemainNum()) ? 0 : oldData.getRemainNum();
            UserBillingHistory userBillingHistory = new UserBillingHistory();
            BillingCourse billingCourse = billingCourseMapper.selectById(userVO.getUserBillingHistoryVO().getCourseId());
            if (DataDictionary.BILLING_COUSE_CUSTOM.getValue().equals(billingCourse.getCourseName())) {
                oldData.setRemainNum(userVO.getUserBillingHistoryVO().getCourseCustomNum() + remainNum);
                userBillingHistory.setAddUsageCount(userVO.getUserBillingHistoryVO().getCourseCustomNum());
            } else {

                oldData.setCourseId(billingCourse.getId());
                oldData.setRemainNum(billingCourse.getQuestionsNum() + remainNum);
                userBillingHistory.setAddUsageCount(billingCourse.getQuestionsNum());
            }

            userBillingHistory.setCourseId(userVO.getUserBillingHistoryVO().getCourseId());

            userBillingHistory.setRemainNum(oldData.getRemainNum());
            userBillingHistory.setUserId(oldData.getId());
            userBillingHistory.setCreator(creator);
            userBillingHistory.setGmtUpdate(LocalDateTime.now());
            userBillingHistoryMapper.insert(userBillingHistory);
            isChangeRemainNum = true;
        }
//            if (!EmptyUtil.isNull(userVO.getUserBillingHistoryVO())) {
//                UserBillingHistoryVO userBillingHistoryVO = userVO.getUserBillingHistoryVO();
//                if (!EmptyUtil.isNull(userBillingHistoryVO.getAddMonth()) && userBillingHistoryVO.getAddMonth() > 0) {
//                    LocalDateTime currentTime = LocalDateTime.now();
//                    if (EmptyUtil.isNull(oldData.getEffectiveTime()) || oldData.getEffectiveTime().isBefore(currentTime)) {
//                        oldData.setEffectiveTime(currentTime.plus(userBillingHistoryVO.getAddMonth(), ChronoUnit.MONTHS));
//                    } else {
//                        oldData.setEffectiveTime(oldData.getEffectiveTime().plus(userBillingHistoryVO.getAddMonth(), ChronoUnit.MONTHS));
//                    }
//                }
//                if (!EmptyUtil.isNull(userBillingHistoryVO.getAddUsageCount()) && userBillingHistoryVO.getAddUsageCount() > 0) {
//                    Integer usageCount = EmptyUtil.isNull(oldData.getRemainNum()) ? 0 : oldData.getRemainNum();
//                    oldData.setRemainNum(usageCount + userBillingHistoryVO.getAddUsageCount());
//                }
//                UserBillingHistory userBillingHistory = new UserBillingHistory();
//                BeanUtils.copyProperties(userBillingHistoryVO, userBillingHistory);
//                userBillingHistory.setEffectiveTime(oldData.getEffectiveTime());
//                userBillingHistory.setRemainNum(oldData.getRemainNum());
//                userBillingHistory.setUserId(oldData.getId());
//                userBillingHistory.setCreator(creator);
//                userBillingHistory.setGmtCreate(LocalDateTime.now());
//                userBillingHistoryMapper.insert(userBillingHistory);
//            }
        if (!EmptyUtil.isNull(userVO.getRemainNum())) {
            oldData.setRemainNum(userVO.getRemainNum());
            isChangeRemainNum = true;
        }
        if(isChangeRemainNum){
            LocalDateTime currentTime = LocalDateTime.now();
            oldData.setEffectiveTime(currentTime.plus(1, ChronoUnit.YEARS));
        }
        oldData.setGmtUpdate(LocalDateTime.now());
        userMapper.updateById(oldData);
        return oldData;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SnsUser user = userMapper.selectById(id);
        if (user != null) {
            user.setEnable(1);
            user.setGmtUpdate(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        SnsUser user = userMapper.selectById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            int rows = userMapper.updateById(user);
            return rows > 0;
        }
        return false;
    }
}