package com.cy.newpt.controller;


import com.cy.newpt.mapper.CourseMapper;
import com.cy.newpt.mapper.StudentCourseMapper;
import com.cy.newpt.pojo.dto.CreateCourseDTO;
import com.cy.newpt.pojo.dto.StuDTO;
import com.cy.newpt.pojo.dto.TeaDTO;
import com.cy.newpt.pojo.dto.UpdateStuDTO;
import com.cy.newpt.pojo.entity.Course;
import com.cy.newpt.pojo.vo.PageVO;
import com.cy.newpt.pojo.vo.StuVO;
import com.cy.newpt.pojo.vo.TeaVO;
import com.cy.newpt.service.ICourseService;
import com.cy.newpt.service.impl.StuServiceImpl;
import com.cy.newpt.service.impl.TeaServiceImpl;
import com.cy.newpt.utils.JsonResult;
import com.cy.newpt.utils.RedisUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 这是管理员专用控制器
 * 暂时不添加安全等组件，先跑通功能
 */
@RestController // 声明此类为 RESTful Controller，所有方法自动返回 JSON
@RequestMapping("/api/login/admin")
public class AdminController {
    @Autowired
    private StuServiceImpl stuService;
    @Autowired
    private TeaServiceImpl teaService;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private StudentCourseMapper studentCourseMapper;


    /**
     * API 功能: 管理员创建新学生
     * - 请求方式: POST
     * - API 路径: /api/admin/student
     */
    @PostMapping("student")
    public JsonResult<StuVO> createStu(@Valid @RequestBody StuDTO dto) {
        // @RequestBody会自动把json转化为java对象，及dto类
        // @Valid会检查所有@NotEmpty是否符合约束
        // try...catch来捕获服务层可能出现的异常
        try{
            // 8. 调用 Service 干活，并获取返回的 VO
            StuVO newstu = stuService.createStu(dto);
            // 9. 使用 JsonResult.success 封装 VO，返回成功的响应
            return JsonResult.success("学生创建成功", newstu);
        } catch (Exception e) {
            // 10. 如果 Service 抛出异常 (e.g., throw new Exception("用户名已存在"))
            //     我们在这里捕获它，并返回一个“失败”的 JsonResult
            return JsonResult.failure(400,e.getMessage());
        }
    }

    /**
     * API 功能: 管理员创建新教师
     * - 请求方式: POST
     * - API 路径: /api/admin/teacher
     */
    @PostMapping("teacher")
    public JsonResult<TeaVO> createTea(@Valid @RequestBody TeaDTO dto) {
        try {
            TeaVO newtea = teaService.createTea(dto);
            return JsonResult.success("教师创建成功", newtea);
        } catch (Exception e) {
            return JsonResult.failure(400, e.getMessage());
        }
    }

//-------------------------------------------------------------------------------

    /**
     * api功能：获取所有学生信息，以分页形式返回
     * - 请求方式: GET
     * - API 路径: /login/admin/students
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("students")
    public JsonResult<PageVO<StuVO>> getStudentsList(
            // 1. @RequestParam 用来从 URL 获取 ?page=1&size=10
            // 2. defaultValue 确保在前端不传时，也能正常工作
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        try {
            // 调用服务
            PageVO<StuVO> studentPage = stuService.getStudentPage(pageNum, pageSize);
            // 成功返回
            return JsonResult.success(studentPage);
        } catch (Exception e) {
            // 失败返回
            return JsonResult.failure(500, "获取列表失败" + e.getMessage());
        }
    }

// -------------------------------------------------------------------------------------
    @DeleteMapping("/students/by-account/{account}")
    public JsonResult<Void> deleteStudentByAccount(@PathVariable("account") String account) {
        try{
            stuService.deleteStudentByAccount(account);
            return JsonResult.success("删除成功");
        } catch (Exception e) {
            return JsonResult.failure(404, e.getMessage());
        }
    }

    @PutMapping("/students/by-account/{account}")
    public JsonResult<StuVO> updateStudent(
            @PathVariable("account") String account,
            @Valid @RequestBody UpdateStuDTO dto
            ) {
        try{
            StuVO updateStu = stuService.updateStudentByAccount(account, dto);
            return JsonResult.success("更新成功", updateStu);
        } catch (Exception e) {
            return JsonResult.failure(505, e.getMessage());
        }
    }
// ----------------------------------------------------------------------------------------------

    /**
     * API功能： 发布新课程
     * POST：/api/login/admin/courses
      * @param dto 待发布课程后端视图
     * @return
     */
    @PostMapping("/courses")
    public JsonResult<Void> createCourse(@Valid @RequestBody CreateCourseDTO dto) {
        try {
            courseService.createCourse(dto);
            return JsonResult.success("课程发布成功");
        } catch (Exception e) {
            return JsonResult.failure(500, e.getMessage());}
    }

    /**
     * API 功能: 分页获取课程列表
     * GET /api/login/admin/courses
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/courses")
    public JsonResult<PageVO<Course>> getCourseList(
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
     * API：删除课程
     * DELETE /api/login/admin/courses/{id}
     * @param id
     * @return
     */

    @DeleteMapping("/courses/{id}")
    public JsonResult<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return JsonResult.success("删除成功");
    }

    @PutMapping("/courses/{id}")
    public JsonResult<Void> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CreateCourseDTO dto
    ) {
        courseService.updateCourse(id, dto);
        return JsonResult.success("修改成功");
    }

// ------------------------------------------------------------------------------------

    /**
     * API ：库存预热，把数据库里的库存同步到Redis
     * POST /api/login/admin/courses/preheat
     * @return
     */
    @PostMapping("/courses/preheat")
    public JsonResult<Void> preheatCourse() {
        try {
            // --- 1. 【新增】大清洗 ---
            // 删除所有 course:stock:... 和 course:selected:... 的缓存
            // 保证 Redis 是一张白纸，完全以数据库为准
            redisUtil.deleteByPattern("course:*");

            List<Course> allCourses = courseMapper.findAll();

            for (Course c : allCourses) {
                // --- 1. 预热库存 (String) ---
                int stock = c.getCapacity() - c.getSelectedNum();
                String stockKey = "course:stock:" + c.getId();
                redisUtil.set(stockKey, String.valueOf(stock));
                // 设置过期时间 (例如 3 天 = 259200 秒)
                redisUtil.expire(stockKey, 259200);

                // --- 2. 预热已选名单 (Set) ---
                List<String> accounts = studentCourseMapper.findStudentAccountsByCourseId(c.getId());
                if (!accounts.isEmpty()) {
                    String setKey = "course:selected:" + c.getId();

                    // 把 List 转成 Array 存入 Redis Set
                    String[] accountArray = accounts.toArray(new String[0]);
                    redisUtil.sAdd(setKey, accountArray);

                    // 设置过期时间 (也要 3 天，保持一致)
                    redisUtil.expire(setKey, 259200);
                }
            }
            return JsonResult.success("数据预热成功 (库存+名单)");
        } catch (Exception e) {
            return JsonResult.failure(500, e.getMessage());
        }
    }
}
