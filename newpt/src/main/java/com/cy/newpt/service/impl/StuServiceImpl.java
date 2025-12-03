package com.cy.newpt.service.impl;

import com.cy.newpt.mapper.AdminMapper;
import com.cy.newpt.mapper.StuMapper;
import com.cy.newpt.mapper.TeaMapper;
import com.cy.newpt.pojo.dto.StuDTO;
import com.cy.newpt.pojo.dto.UpdateStuDTO;
import com.cy.newpt.pojo.entity.Role;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.vo.PageVO;
import com.cy.newpt.pojo.vo.StuVO;
import com.cy.newpt.service.StuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;


@Service
public class StuServiceImpl implements StuService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private StuMapper stuMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public StuVO createStu(StuDTO dto) throws Exception {
        //1. 校验
        if (stuMapper.ifStuExist(dto.getName()) != 0) {
            throw new Exception("学生信息已经存在");
        }
        //2. 持久层转化
        Stu stu = new Stu();
        stu.setName(dto.getName());
        stu.setGender(dto.getGender());
        stu.setAge(dto.getAge());
        stu.setYear(Year.now().getValue());
        stu.setClassIdt(dto.getClassIdt());
        stu.setDepaId(dto.getDepaId());
        stu.setMajorId(dto.getMajorId());
        stu.setAccount(dto.getAccount());

        // 加密，保存加密的密码，登录验证加密的密码
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        stu.setPassword(hashedPassword);
        System.out.println(hashedPassword);

        stu.setRole("ROLE_STUDENT");

        System.out.println(stu);
        // 插入实体，保存进数据库
        adminMapper.insertStu(stu);

        //3. 返回VO视图
        StuVO stuVO = new StuVO();
        stuVO.setName(stu.getName());
        stuVO.setGender(stu.getGender());
        stuVO.setAge(stu.getAge());
        stuVO.setClassIdt(stu.getClassIdt());
        stuVO.setMajorId(stu.getMajorId());
        stuVO.setDepaId(stu.getDepaId());
        stuVO.setAccount(stu.getAccount());

        return stuVO;
    }

    @Override
    public PageVO<StuVO> getStudentPage(int pageNum, int pageSize) throws Exception {

        // 1. 启用 PageHelper 分页
        PageHelper.startPage(pageNum, pageSize);

        // 2. 调用mapper执行sql语句
        //    PageHelper 会自动拦截这条 SQL，并帮它加上 LIMIT 和 COUNT
        List<Stu> stuEntityList = stuMapper.findAll();

        // 使用 PageINfo 包装结果，获取分页信息（将所有的信息整合，包括stuEntityList和LIMIT,COUNT）
        PageInfo<Stu> pageInfo = new PageInfo<>(stuEntityList);

        // 4. 把 List<Stu> (实体) 转换成 List<StuVO> (视图)
        List<StuVO> voList = new ArrayList<>();
        for (Stu stuEntity: pageInfo.getList()) { // 用 pageInfo.getList() 获取当前页数据
            StuVO vo = new StuVO();
            BeanUtils.copyProperties(stuEntity, vo); // 自动拷贝同名属性

            voList.add(vo);
        }

        // 5. 组装，将 PageInfo 的信息和 转换后的 voList 一起装进自定义的 PageVO
        PageVO<StuVO> pageVO = new PageVO<>();
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setPages(pageInfo.getPages());
        pageVO.setList(voList);

        // 6. 返回 pageVO
        return pageVO;

    }

    @Override
    public void deleteStudentByAccount(String account) throws Exception {
        int affectedRows = stuMapper.deleteByAccount(account);
        if (affectedRows == 0) {
            throw new Exception("学生信息不存在");
        }
    }

    @Override
    public StuVO updateStudentByAccount(String account, UpdateStuDTO dto) throws Exception {
        // 1. 先判断学生信息情况，并且导入这个学生信息实体类
        Stu stuEntity = stuMapper.findByAccount(account);
        if (stuEntity == null) {
            throw new Exception("学生信息不存在");
        }

        // 2. 合并，将DTO中的新值导入学生信息实体类中
        // 只更新DTO中的非null字段
        if (dto.getName() != null) {
            stuEntity.setName(dto.getName());
        }
        if (dto.getGender() != null) {
            stuEntity.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            stuEntity.setAge(dto.getAge());
        }
        if (dto.getYear() != null) {
            stuEntity.setYear(dto.getYear());
        }
        if (dto.getClassIdt() != null) {
            stuEntity.setClassIdt(dto.getClassIdt());
        }
        if (dto.getMajorId() != null) {
            stuEntity.setMajorId(dto.getMajorId());
        }
        if (dto.getDepaId() != null) {
            stuEntity.setDepaId(dto.getDepaId());
        }
        if (dto.getPassword() != null) {
            stuEntity.setPassword(dto.getPassword());
        }

        int affectedRows = stuMapper.updateByAccount(stuEntity);
        if (affectedRows == 0) {
            throw new Exception("更新失败，数据可能已经被删除");
        }

        return convertToStuVO(stuEntity);
    }

    @Override
    public StuVO getStudentInfoByAccount(String account) {
        Stu stuEntity = stuMapper.findByAccount(account);
        if (stuEntity == null) {
            return null;
        }
        // 2. 翻译成VO
        return convertToStuVO(stuEntity);
    }


//--------------------------------------------------------------------------------
    // StuService层的utils，就放这里面了，并且修饰private，只允在许这里面使用
    /**
     * 实体转VO，传给前端
     * @param stuEntity 实体
     * @return 返回VO给前端
     */
    private StuVO convertToStuVO(Stu stuEntity) {
        StuVO vo = new StuVO();
        // 执行转换逻辑
        vo.setName(stuEntity.getName());
        vo.setGender(stuEntity.getGender());
        vo.setAge(stuEntity.getAge());
        vo.setClassIdt(stuEntity.getClassIdt());
        vo.setMajorId(stuEntity.getMajorId());
        vo.setDepaId(stuEntity.getDepaId());
        vo.setAccount(stuEntity.getAccount());
        return vo;
    }
}
