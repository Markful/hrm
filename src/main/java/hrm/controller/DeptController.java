package hrm.controller;

import hrm.domain.Dept;
import hrm.service.OtherServiceInterface;
import hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**   
 * @Description: 处理部门请求控制器
 * @author   
 * @version V1.0   
 */

@Controller
public class DeptController {

    @Autowired
    @Qualifier("hrmService")
    private OtherServiceInterface otherServiceInterface;

    @RequestMapping("/dept/selectDept")
    public ModelAndView selectDept(Integer pageIndex, Dept dept, Model model){
        //如果没有得到username或status就表明是从左边点击来的，那么就分页显示所有的
        String name = dept.getName();

        ModelAndView modelAndView = new ModelAndView();
        PageModel pageModel = new PageModel();


        //表示点击查询过来的
        if(pageIndex == null){
            pageIndex = 1;
        }
        //在service层判断是否username和status为null，这里返回的userlike最好不要分页，全部返回就行了
        List<Dept> depts = otherServiceInterface.findDept(dept, pageModel);

        if(depts == null || depts.size() == 0){
            modelAndView.setViewName("dept/dept");
            return modelAndView;
        }

        //要在这层告诉页面 总页数
        pageModel.setPageSize(pageModel.getPageSize());
        pageModel.setRecordCount(depts.size());
        pageModel.setPageIndex(pageIndex);

        List<Dept> retDept =  new ArrayList<>();
        //然后根据当前页数来选择List中返回那几条
        if(pageModel.getTotalSize()-1 == 0){
            //只有一页
            for (int i = 0; i <depts.size() ; i++) {
                retDept.add(depts.get(i));
            }
            //index从1开始
        }else if(pageModel.getTotalSize() == pageIndex){
            //最后一页
            for (int i = pageModel.getPageSize()*(pageModel.getTotalSize()-1); i <depts.size() ; i++) {
                retDept.add(depts.get(i));
            }
        }else {
            //中间页
            for (int i = (pageIndex-1)*pageModel.getPageSize();
                 i <((pageIndex-1)*pageModel.getPageSize()+pageModel.getPageSize()) ;
                 i++) {
                retDept.add(depts.get(i));
            }
        }
        System.out.println("pageModel.getPageIndex()======" + pageModel.getPageIndex());
        System.out.println("pageModel.getTotalSize()======" + pageModel.getTotalSize());
        //将分页信息塞入
        model.addAttribute("pageModel", pageModel);

        model.addAttribute("depts", retDept);

        modelAndView.setViewName("dept/dept");

        return modelAndView;


    }

    @RequestMapping("/dept/removeDept")
    public ModelAndView removeDept(Integer[] ids){
        //批量删除
        for (int i = 0; i <ids.length ; i++) {
            otherServiceInterface.removeDeptById(ids[i]);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/dept/selectDept");
        return modelAndView;

    }

    @RequestMapping("/dept/updateDept")
    public ModelAndView updateDept(Dept dept, Model model, Integer flag){

        ModelAndView modelAndView = new ModelAndView();

        //这里是查询界面的跳转操作
        if(flag == 1){
            model.addAttribute("dept",dept);
            modelAndView.setViewName("dept/showUpdateDept");
            return modelAndView;
        }
        //这里是修改页面的修改
        else if(flag == 2){
            otherServiceInterface.modifyDept(dept);
        }

        modelAndView.setViewName("redirect:/dept/selectDept");
        return modelAndView;

    }

    @RequestMapping("/dept/addDept")
    public ModelAndView addDept(Dept dept, Integer flag, Model model){

        ModelAndView modelAndView = new ModelAndView();

        //这里是添加界面的跳转操作
        if(flag == 1){
            modelAndView.addObject("dept",dept);
            modelAndView.setViewName("dept/showAddDept");
            return modelAndView;
        }
        //这里是添加页面的修改
        else if(flag == 2){
            otherServiceInterface.addDept(dept);
        }

        modelAndView.setViewName("redirect:/dept/selectDept");
        return modelAndView;

    }

 
}
