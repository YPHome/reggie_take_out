package com.yphome.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yphome.reggie.common.R;
import com.yphome.reggie.entity.Employee;
import com.yphome.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody  Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null){
            return R.error("登陆失败！");
        }

        if(!emp.getPassword().equals(password)){
            return R.error("密码错误！");
        }

        if(emp.getStatus()==0){
            return R.error("用户已禁用！");
        }

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");

    }

    @PostMapping
    public R<String> save (HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息{}", employee.toString());

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));

        employeeService.save(employee);
        return R.success("新增员工成功！");

    }

}
