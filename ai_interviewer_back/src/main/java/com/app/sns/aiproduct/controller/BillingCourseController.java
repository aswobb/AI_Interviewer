package com.app.sns.aiproduct.controller;

import com.app.sns.aiproduct.pojo.entity.BillingCourse;
import com.app.sns.aiproduct.pojo.entity.SnsUser;
import com.app.sns.aiproduct.pojo.vo.SnsUserVO;
import com.app.sns.aiproduct.service.BillingCourseService;
import com.app.sns.aiproduct.web.JsonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/billingCourse")
public class BillingCourseController {
    @Resource
    private BillingCourseService billingCourseService;

    @GetMapping("/list")
    public JsonResult list(@ModelAttribute SnsUserVO snsUserVO) {
        Page<BillingCourse> page = new Page<>(0, Integer.MAX_VALUE);
        QueryWrapper<BillingCourse> wrapper = new QueryWrapper<>();
        Page<BillingCourse> infoPage = billingCourseService.page(page, wrapper);

        return JsonResult.ok(infoPage);
    }
}
