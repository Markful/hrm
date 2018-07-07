package hrm.controller;

import hrm.domain.Dept;
import hrm.domain.Employee;
import hrm.domain.Job;
import hrm.service.OtherServiceInterface;
import hrm.util.tag.PageModel;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 处理员工请求控制器
 * @version V1.0
 */
@Controller
public class EmployeeController {

    @Autowired
    @Qualifier("hrmService")
    private OtherServiceInterface otherServiceInterface;

    @RequestMapping("/employee/selectEmployee")
    public ModelAndView selectEmployee(Integer pageIndex, Employee employee, Model model, Integer job_id , Integer dept_id ){
        //如果没有得到username就表明是从左边点击来的，那么就分页显示所有的
        String name = employee.getName();

        ModelAndView modelAndView = new ModelAndView();
        PageModel pageModel = new PageModel();
        //把前端传过来的id放进去
        Job job = new Job();
        job.setId(job_id);
        Dept dept = new Dept();
        dept.setId(dept_id);
        employee.setDept(dept);
        employee.setJob(job);

        //表示点击查询过来的
        if(pageIndex == null){
            pageIndex = 1;
        }
        //在service层判断是否username和status为null，这里返回的userlike最好不要分页，全部返回就行了
        List<Employee> employees = otherServiceInterface.findEmployee(employee, pageModel);

        if(employees == null || employees.size() == 0){
            modelAndView.setViewName("employee/employee");
            return modelAndView;
        }

        //要在这层告诉页面 总页数
        pageModel.setPageSize(pageModel.getPageSize());
        pageModel.setRecordCount(employees.size());
        pageModel.setPageIndex(pageIndex);

        List<Employee> retEmployee =  new ArrayList<>();
        //然后根据当前页数来选择List中返回那几条
        if(pageModel.getTotalSize()-1 == 0){
            //只有一页
            for (int i = 0; i <employees.size() ; i++) {
                retEmployee.add(employees.get(i));
            }
            //index从1开始
        }else if(pageModel.getTotalSize() == pageIndex){
            //最后一页
            for (int i = pageModel.getPageSize()*(pageModel.getTotalSize()-1); i <employees.size() ; i++) {
                retEmployee.add(employees.get(i));
            }
        }else {
            //中间页
            for (int i = (pageIndex-1)*pageModel.getPageSize();
                 i <((pageIndex-1)*pageModel.getPageSize()+pageModel.getPageSize()) ;
                 i++) {
                retEmployee.add(employees.get(i));
            }
        }
        System.out.println("pageModel.getPageIndex()======" + pageModel.getPageIndex());
        System.out.println("pageModel.getTotalSize()======" + pageModel.getTotalSize());
        //将分页信息塞入
        model.addAttribute("pageModel", pageModel);

        model.addAttribute("employees", retEmployee);

        modelAndView.setViewName("employee/employee");

        //因为还有两个下拉列表，所以还要塞入整个depts和jobs
        List<Dept> allDept = otherServiceInterface.findAllDept();
        List<Job> allJob = otherServiceInterface.findAllJob();
        model.addAttribute("depts",allDept);
        model.addAttribute("jobs",allJob);
        return modelAndView;

    }

    @RequestMapping("/employee/removeEmployee")
    public ModelAndView removeEmployee(Integer[] ids){
        //批量删除
        for (int i = 0; i <ids.length ; i++) {
            otherServiceInterface.removeEmployeeById(ids[i]);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/employee/selectEmployee");

        return modelAndView;

    }

    @RequestMapping("/employee/updateEmployee")
    public ModelAndView updateEmployee(Employee employee, Model model, Integer flag){

        ModelAndView modelAndView = new ModelAndView();

        //这里是查询界面的跳转操作
        if(flag == 1){
            model.addAttribute("employee",employee);
            modelAndView.setViewName("employee/showUpdateEmployee");
            return modelAndView;
        }
        //这里是修改页面的修改
        else if(flag == 2){
            otherServiceInterface.modifyEmployee(employee);
            modelAndView.setViewName("redirect:/employee/selectEmployee");
        }
        //因为还有两个下拉列表，所以还要塞入整个depts和jobs
        List<Dept> allDept = otherServiceInterface.findAllDept();
        List<Job> allJob = otherServiceInterface.findAllJob();
        model.addAttribute("depts",allDept);
        model.addAttribute("jobs",allJob);

        return modelAndView;

    }

    @RequestMapping("/employee/addEmployee")
    public ModelAndView addEmployee(Employee employee, Integer flag, Model model, Integer job_id, Integer dept_id){

        ModelAndView modelAndView = new ModelAndView();
        Dept dept = new Dept();
        Job job = new Job();
        job.setId(job_id);
        dept.setId(dept_id);
        employee.setJob(job);
        employee.setDept(dept);
        //这里是添加界面的跳转操作
        if(flag == 1){
            model.addAttribute("employee",employee);

            //因为还有两个下拉列表，所以还要塞入整个depts和jobs
            List<Dept> allDept = otherServiceInterface.findAllDept();
            List<Job> allJob = otherServiceInterface.findAllJob();
            model.addAttribute("depts",allDept);
            model.addAttribute("jobs",allJob);

            modelAndView.setViewName("employee/showAddEmployee");
            return modelAndView;
        }
        //这里是添加页面的修改
        else if(flag == 2){
            otherServiceInterface.addEmployee(employee);
        }

        //因为还有两个下拉列表，所以还要塞入整个depts和jobs
        List<Dept> allDept = otherServiceInterface.findAllDept();
        List<Job> allJob = otherServiceInterface.findAllJob();
        model.addAttribute("depts",allDept);
        model.addAttribute("jobs",allJob);
        modelAndView.setViewName("redirect:/employee/selectEmployee");

        return modelAndView;

    }

    @RequestMapping("/employee/employeeExcelDownload")
    public ResponseEntity<byte[]> employeeExcelDownload(HttpServletRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {

        //创建工作簿
        HSSFWorkbook sheets = new HSSFWorkbook();
        //创建一个名字为employeeSheet的表格
        HSSFSheet employeeSheet = sheets.createSheet("employees");

        //创建红色字体
        HSSFFont font = sheets.createFont();
        font.setColor(HSSFFont.COLOR_RED);

        //创建格式
        HSSFCellStyle cellStyle = sheets.createCellStyle();
        cellStyle.setFont(font);

        //拿到雇员数据并插入到表格中
        List<Employee> employees = otherServiceInterface.findAllEmployee();
        for (int i = 0; i < employees.size(); i++) {

            //创建第i行
            HSSFRow row = employeeSheet.createRow(i);

            Class<? extends Employee> employeeClass = employees.get(0).getClass();
            Field[] fields = employeeClass.getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {
                fields[j].setAccessible(true);

                //创建第j个单元格
                HSSFCell cellJ = row.createCell(j);
                cellJ.setCellStyle(cellStyle);

                String fieldName = fields[j].getName();
                Method methodJ = employeeClass.getMethod("get" +
                        fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));

                System.out.println(new Date() + " 信息 EmployeeController.employeeExcelDownload: fieldName = "
                        + fieldName);

                cellJ.setCellValue(String.valueOf(methodJ.invoke(employees.get(i))));
            }
        }

        //创建保存表格文件的父目录
        String prefix = request.getServletContext().getRealPath("/xls");
        File dir = new File(prefix);
        if (!dir.exists()) { dir.mkdirs(); }

        //保存表格文件到服务器目录/WEB-INF/xls文件夹下
        File file = new File(prefix, "employees.xls");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        sheets.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

        //下载该xml文件到本地
        HttpHeaders httpHeaders = new HttpHeaders();
        //下载文件的名字要用ISO-8859-1进行编码，不然中文会乱码
        String downloadFileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");
        httpHeaders.setContentDispositionFormData("attachment", downloadFileName);//告知浏览器以下载方式打开
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);//设置MIME类型

        File fileToDownload = new File(prefix, downloadFileName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(fileToDownload), httpHeaders, HttpStatus.CREATED);

    }



}
