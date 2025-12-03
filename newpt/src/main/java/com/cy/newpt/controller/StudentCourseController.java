package com.cy.newpt.controller;

import com.cy.newpt.mapper.CourseMapper;
import com.cy.newpt.pojo.dto.SelectCourseDTO;
import com.cy.newpt.pojo.entity.Course;
import com.cy.newpt.pojo.vo.PageVO;
import com.cy.newpt.service.ICourseService;
import com.cy.newpt.service.IStudentCourseService;
import com.cy.newpt.service.impl.CourseServiceImpl;
import com.cy.newpt.service.impl.StudentCourseServiceImpl;
import com.cy.newpt.utils.JsonResult;
import com.cy.newpt.utils.RedisUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/student/course")
public class StudentCourseController {

    @Autowired
    private IStudentCourseService iStudentCourseService;

    @Autowired
    private ICourseService iCourseService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private StudentCourseServiceImpl studentCourseServiceImpl;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * API：学生选课
     * POST：/api/student/course/select
     * @param dto
     * @return
     */
    @PostMapping("/select")
    public JsonResult<Void> selectCourse(@Valid @RequestBody SelectCourseDTO dto){
        try{
            // 从 Token 中获取当前学生账号
            String account = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // 选课
            iStudentCourseService.selectCourse(account, dto.getCourseId());
            return JsonResult.success("选课成功");
        } catch (Exception e) {
            return JsonResult.failure(500, e.getMessage());
        }
    }

    /**
     * API:查询所有的课程
     * GET：/api/student/course/list
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public JsonResult<PageVO<Course>> listCourse(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        try{
            PageVO<Course> page = courseService.getCoursePage(pageNum, pageSize);
            return JsonResult.success(page);
        } catch (Exception e) {
            return JsonResult.failure(500, e.getMessage());
        }
    }

    /**
     * API:退课
     * POST /api/student/course/drop
     * @param dto
     * @return
     */
    @PostMapping("/drop")
    public JsonResult<Void> dropCourse(@RequestBody SelectCourseDTO dto) {
        try{
            String account = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            iStudentCourseService.dropCourse(account, dto.getCourseId());
            return JsonResult.success("退课成功");
        } catch (Exception e) {
            return JsonResult.failure(500, e.getMessage());
        }
    }

    /**
     * API: 获取我的课程表
     * GET /api/student/course/my-courses
     * @return
     */
    @GetMapping("/my-courses")
    public JsonResult<List<Course>> getMyCourse() {
        String account = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Course> list = iStudentCourseService.getMyCourseList(account);
        return JsonResult.success(list);
    }

    /**
     *API: 查询选课结果
     *  * GET /api/student/course/result?courseId=1
     *  * 返回值: 0(排队中), 1(成功), -1(失败)
     * @param courseId
     * @return
     */
    @GetMapping("/result")
    public JsonResult<Integer> checkSelectionResult(
            @RequestParam Integer courseId
    ) {
        String account = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String resultKey = "course:result:" + account + ":" + courseId;
        String status = redisUtil.get(resultKey);

        if (status == null) {
            return JsonResult.success("排队中...", 0); // 没查到字条，说明还在处理
        } else if ("1".equals(status)) {
            return JsonResult.success("选课成功", 1);
        } else {
            return JsonResult.success("选课失败", -1);
        }
    }
}
