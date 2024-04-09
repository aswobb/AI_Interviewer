package com.app.sns.aiproduct.controller;
import com.app.sns.aiproduct.ex.ServiceException;
import com.app.sns.aiproduct.pojo.dto.UserLoginInfoInDTO;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.service.UserService;
import com.app.sns.aiproduct.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
//    @PostMapping("/create")
//    public SnsUser createUser(@RequestBody SnsUser user) {
//        return userService.createUser(user);
//    }
//
//    @PutMapping("/update/{id}")
//    public SnsUser updateUser(@PathVariable Long id, @RequestBody SnsUser user) {
//        return userService.updateUser(id, user);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public void deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//    }
}
