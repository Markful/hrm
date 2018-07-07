package hrm.controller;


import hrm.domain.Job;
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
 * @Description: 处理职位请求控制器
 * @version V1.0
 */

@Controller
public class JobController {

    @Autowired
    @Qualifier("hrmService")
    private OtherServiceInterface otherServiceInterface;

    @RequestMapping("/job/selectJob")
    public ModelAndView selectJob(Integer pageIndex, Job job, Model model){
        //如果没有得到username或status就表明是从左边点击来的，那么就分页显示所有的

        ModelAndView modelAndView = new ModelAndView();
        PageModel pageModel = new PageModel();


        //表示点击查询过来的
        if(pageIndex == null){
            pageIndex = 1;
        }
        //在service层判断是否username和status为null，这里返回的userlike最好不要分页，全部返回就行了
        List<Job> jobs = otherServiceInterface.findJob(job, pageModel);

        if(jobs == null || jobs.size() == 0){
            modelAndView.setViewName("job/job");
            return modelAndView;
        }

        //要在这层告诉页面 总页数
        pageModel.setPageSize(pageModel.getPageSize());
        pageModel.setRecordCount(jobs.size());
        pageModel.setPageIndex(pageIndex);

        List<Job> retJob =  new ArrayList<>();
        //然后根据当前页数来选择List中返回那几条
        if(pageModel.getTotalSize()-1 == 0){
            //只有一页
            for (int i = 0; i <jobs.size() ; i++) {
                retJob.add(jobs.get(i));
            }
            //index从1开始
        }else if(pageModel.getTotalSize() == pageIndex){
            //最后一页
            for (int i = pageModel.getPageSize()*(pageModel.getTotalSize()-1); i <jobs.size() ; i++) {
                retJob.add(jobs.get(i));
            }
        }else {
            //中间页
            for (int i = (pageIndex-1)*pageModel.getPageSize();
                 i <((pageIndex-1)*pageModel.getPageSize()+pageModel.getPageSize()) ;
                 i++) {
                retJob.add(jobs.get(i));
            }
        }
        System.out.println("pageModel.getPageIndex()======" + pageModel.getPageIndex());
        System.out.println("pageModel.getTotalSize()======" + pageModel.getTotalSize());
        //将分页信息塞入
        model.addAttribute("pageModel", pageModel);

        model.addAttribute("jobs", retJob);

        modelAndView.setViewName("job/job");

        return modelAndView;

    }

    @RequestMapping("/job/removeJob")
    public ModelAndView removeJob(Integer[] ids){
        //批量删除
        for (int i = 0; i <ids.length ; i++) {
            otherServiceInterface.removeJobById(ids[i]);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/job/selectJob");
        return modelAndView;
    }

    @RequestMapping("/job/updateJob")
    public ModelAndView updateJob(Job job,Model model,Integer flag){

        ModelAndView modelAndView = new ModelAndView();

        //这里是查询界面的跳转操作
        if(flag == 1){
            model.addAttribute("job",job);
            modelAndView.setViewName("job/showUpdateJob");
            return modelAndView;
        }
        //这里是修改页面的修改
        else if(flag == 2){
            otherServiceInterface.modifyJob(job);
        }

        modelAndView.setViewName("redirect:/job/selectJob");
        return modelAndView;

    }

    @RequestMapping("/job/addJob")
    public ModelAndView addJob(Job job,Integer flag,Model model){

        ModelAndView modelAndView = new ModelAndView();

        //这里是添加界面的跳转操作
        if(flag == 1){
            model.addAttribute("job",job);
            modelAndView.setViewName("job/showAddJob");
            return modelAndView;
        }
        //这里是添加页面的修改
        else if(flag == 2){
            otherServiceInterface.addJob(job);
        }

        modelAndView.setViewName("redirect:/job/selectJob");
        return modelAndView;

    }

}
