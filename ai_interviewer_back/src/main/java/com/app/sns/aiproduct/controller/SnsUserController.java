package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.constant.DataDictionary;
import com.app.sns.aiproduct.constant.ServiceCodeEnum;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.lock.LockManager;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.SnsUserVO;
import com.app.sns.aiproduct.service.UserService;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.locks.ReentrantLock;

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
        Page<SnsUser> page = new Page<>(snsUserVO.getPageNum(), snsUserVO.getPageSize());
        QueryWrapper<SnsUser> wrapper = new QueryWrapper<>();
        if (snsUserVO.getGmtCreateStart() != null && snsUserVO.getGmtCreateEnd() != null) {
            wrapper.lambda().between(SnsUser::getGmtCreate, snsUserVO.getGmtCreateStart(), snsUserVO.getGmtCreateEnd());
        }
        wrapper.lambda().eq(SnsUser::getRoleId, DataDictionary.ROLE_CONTRACT.getKey());
        return JsonResult.ok(userService.page(page, wrapper));
    }
    @PostMapping("/create")
    public SnsUser createUser(@RequestBody SnsUserVO userVO,HttpServletRequest request) {
        Long creator = JWTUtil.getUserIdFromToken(request);
        return userService.createUser(creator,userVO);
    }

    @PostMapping("/update")
    public SnsUser updateUser( @RequestBody SnsUserVO userVO,HttpServletRequest request) {
        Long creator = JWTUtil.getUserIdFromToken(request);
        ReentrantLock lock = lockManager.getLock(SnsUser.class, userVO.getId());

        lock.lock();
        try {
            return userService.updateUser(creator,userVO);
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
