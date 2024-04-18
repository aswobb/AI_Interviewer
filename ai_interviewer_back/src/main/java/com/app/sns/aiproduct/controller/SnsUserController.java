package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.constant.DataDictionary;
import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.lock.LockManager;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.entity.BillingCourse;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.SnsUserVO;
import com.app.sns.aiproduct.service.BillingCourseService;
import com.app.sns.aiproduct.service.UserService;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 契約会社の管理員とSNSソフトの管理員
 */
@RestController
@RequestMapping("/snsUser")
public class SnsUserController {
    @Autowired
    private UserService userService;
    @Resource
    private LockManager lockManager;
    @Resource
    private BillingCourseService billingCourseService;
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UserLoginInfoInDTO userLoginInfoInDTO) {
        ReentrantLock lock = lockManager.getLock(SnsUser.class, userLoginInfoInDTO.getId());

        lock.lock();
        try {
            boolean success = userService.updatePassword(userLoginInfoInDTO.getId(), userLoginInfoInDTO.getOldPassword(), userLoginInfoInDTO.getNewPassword());
            if (success) {
                return JsonResult.ok("パスワード更新成功");
            } else {
                throw new ServiceException(ServiceCodeEnum.INVALID_OLD_PASSWORD);
            }
        }catch (Exception e){
            throw e;
        }finally {
            lock.unlock();
        }

    }

    @GetMapping("/getCurrentUser")
    public JsonResult getCurrentUser(HttpServletRequest request) {
        Long userId = JWTUtil.getUserIdFromToken(request);

        SnsUser snsUser = userService.getUserById(userId);

        return JsonResult.ok(snsUser);
    }

    @GetMapping("/list")
    public JsonResult list(@ModelAttribute SnsUserVO snsUserVO) {
        Page<SnsUser> page ;
        int pageSize = snsUserVO.getPageSize();
        if (pageSize == -1) {
            page = new Page<>(0, Integer.MAX_VALUE);
        }else{
            page = new Page<>(snsUserVO.getPageNum(), snsUserVO.getPageSize());
        }
        QueryWrapper<SnsUser> wrapper = new QueryWrapper<>();
        if (snsUserVO.getGmtCreateStart() != null && snsUserVO.getGmtCreateEnd() != null) {
            wrapper.lambda().between(SnsUser::getGmtCreate, snsUserVO.getGmtCreateStart(), snsUserVO.getGmtCreateEnd());
        }
        wrapper.lambda().eq(SnsUser::getRoleId, DataDictionary.ROLE_CONTRACT.getKey());
        List<BillingCourse> billingCourseList = billingCourseService.list();
        Map<Long, String> billingCourseMap = billingCourseList.stream()
                .collect(Collectors.toMap(BillingCourse::getId, BillingCourse::getCourseName));
        Page<SnsUser> infoPage = userService.page(page, wrapper);
        for (SnsUser snsUser : infoPage.getRecords()) {
            snsUser.setCourseName(billingCourseMap.get(snsUser.getCourseId()));
        }
        return JsonResult.ok(infoPage);
    }
    @PostMapping("/create")
    public JsonResult createUser(@RequestBody SnsUserVO userVO,HttpServletRequest request) {
        Long creator = JWTUtil.getUserIdFromToken(request);
        return JsonResult.ok(userService.createUser(creator,userVO));
    }

    @PostMapping("/update")
    public JsonResult updateUser( @RequestBody SnsUserVO userVO,HttpServletRequest request) {
        Long creator = JWTUtil.getUserIdFromToken(request);
        ReentrantLock lock = lockManager.getLock(SnsUser.class, userVO.getId());

        lock.lock();
        try {
            return JsonResult.ok(userService.updateUser(creator,userVO));
        }catch (Exception e){
            throw e;
        }finally {
            lock.unlock();
        }
    }
//
//    @GetMapping("/{id}")
//    public SnsUser getUserById(@PathVariable Long id) {
//        return userService.getUserById(id);
//    }
//
//    @GetMapping("/all")
//    public List<SnsUser> getAllUsers() {
//        return userService.getAllUsers();
//    }
//


    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
