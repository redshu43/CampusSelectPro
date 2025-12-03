package com.cy.newpt;

import com.cy.newpt.mapper.AdminMapper;
import com.cy.newpt.mapper.StuMapper;
import com.cy.newpt.pojo.entity.Role;
import com.cy.newpt.pojo.entity.Stu;
import com.cy.newpt.pojo.entity.Tea;
import com.cy.newpt.service.AdminService;
import com.cy.newpt.service.impl.AdminServiceImpl;
import com.cy.newpt.service.impl.StuServiceImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;

@SpringBootTest
class NewptApplicationTests {

    @Autowired
    private StuMapper stuMapper;


    @Autowired
    private AdminServiceImpl adminService;


    @Autowired
    private StuServiceImpl stuService;


// ---------------------------------------------------------------------------------------
    /**
     * 测试数据库是否连接
     */
    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ SUCCESS: Connected to " + conn.getMetaData().getDatabaseProductName());
            System.out.println("URL: " + conn.getMetaData().getURL());
        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
        }
    }

}
