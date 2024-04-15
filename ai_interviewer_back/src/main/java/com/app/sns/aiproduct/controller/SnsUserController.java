package com.app.sns.aiproduct.controller;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.entity.InterviewerInfo;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.InterviewerInfoVO;
import com.app.sns.aiproduct.pojo.vo.SnsUserVO;
import com.app.sns.aiproduct.service.UserService;
import com.app.sns.aiproduct.web.JWTUtil;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.app.sns.aiproduct.web.ServiceCode.INVALID_OLD_PASSWORD;

@RestController
@RequestMapping("/snsUser")
public class SnsUserController {
    @Autowired
    private UserService userService;
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UserLoginInfoInDTO userLoginInfoInDTO) {
        boolean success = userService.updatePassword(userLoginInfoInDTO.getId(), userLoginInfoInDTO.getOldPassword(), userLoginInfoInDTO.getNewPassword());
        if (success) {
            return JsonResult.ok("Password updated successfully");
        } else {
            throw new ServiceException(INVALID_OLD_PASSWORD, "Failed to update password. Invalid old password.");
        }
    }


    @GetMapping("/list")
    public JsonResult list(@RequestBody SnsUserVO snsUserVO) {
        Page<SnsUser> page = new Page<>(snsUserVO.getPageNum(), snsUserVO.getPageSize());
        QueryWrapper<SnsUser> wrapper = new QueryWrapper<>();
        if (snsUserVO.getGmtCreateStart() != null && snsUserVO.getGmtCreateEnd() != null) {
            wrapper.lambda().between(SnsUser::getGmtCreate, snsUserVO.getGmtCreateStart(), snsUserVO.getGmtCreateEnd());
        }
        wrapper.lambda().eq(SnsUser::getRoleId, "2");
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
        return userService.updateUser(creator,userVO);
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
