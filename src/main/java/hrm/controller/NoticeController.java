package hrm.controller;


import hrm.domain.Notice;
import hrm.domain.User;
import hrm.service.OtherServiceInterface;
import hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**   
 * @Description: 处理公告请求控制器
 * @version V1.0   
 */

@Controller
public class NoticeController {

    @Autowired
    @Qualifier("hrmService")
    private OtherServiceInterface otherServiceInterface;


    @RequestMapping("/notice/selectNotice")
    public ModelAndView selectNotice(Integer pageIndex, Notice notice, Model model) {
        //如果没有得到title/content就表明是从左边点击来的，那么就分页显示所有的
        String title = notice.getTitle();
        String content = notice.getContent();

        ModelAndView modelAndView = new ModelAndView();
        PageModel pageModel = new PageModel();

        //表示点击查询过来的
        if(pageIndex == null){
            pageIndex = 1;
        }
        //在service层判断是否username和status为null，这里返回的userlike最好不要分页，全部返回就行了
        List<Notice> noticelike = otherServiceInterface.findNotice(notice, pageModel);

        if(noticelike == null || noticelike.size() == 0){
            modelAndView.setViewName("user/user");
            return modelAndView;
        }

        //要在这层告诉页面 总页数
        pageModel.setPageSize(pageModel.getPageSize());
        pageModel.setRecordCount(noticelike.size());
        pageModel.setPageIndex(pageIndex);
        List<Notice> retNotice =  new ArrayList<>();
        //然后根据当前页数来选择List中返回那几条
        if(pageModel.getTotalSize()-1 == 0){
            //只有一页
            for (int i = 0; i <noticelike.size() ; i++) {
                retNotice.add(noticelike.get(i));
            }
            //index从1开始
        }else if(pageModel.getTotalSize() == pageIndex){
            //最后一页
            for (int i = pageModel.getPageSize()*(pageModel.getTotalSize()-1); i <noticelike.size() ; i++) {
                retNotice.add(noticelike.get(i));
            }
        }else {
            //中间页
            for (int i = (pageIndex-1)*pageModel.getPageSize();
                 i <((pageIndex-1)*pageModel.getPageSize()+pageModel.getPageSize()) ;
                 i++) {
                retNotice.add(noticelike.get(i));
            }
        }
        System.out.println("pageModel.getPageIndex()======" + pageModel.getPageIndex());
        System.out.println("pageModel.getTotalSize()======" + pageModel.getTotalSize());
        //将分页信息塞入
        model.addAttribute("pageModel", pageModel);

        model.addAttribute("notices", retNotice);

        modelAndView.setViewName("notice/notice");

        return modelAndView;
    }


    @RequestMapping("/notice/updateNotice")
    public ModelAndView updateNotice(Integer flag, Model model, Notice notice, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        //得到现在登录的人是谁
        User user = (User)request.getSession().getAttribute("user_session");

        //这里是修改界面的 跳转操作
        if(flag == 1){
            //显示修改之前的数据
            Integer id = notice.getId();
            Notice noticeById = otherServiceInterface.findNoticeById(id);
            //放入数据集中
            model.addAttribute("notice",noticeById);
            modelAndView.setViewName("notice/showUpdateNotice");
            return modelAndView;
        }
        //这里是修改页面的修改操作
        else if(flag == 2){
            //知道现在是谁在修改这个公告
            notice.setUser(user);
            otherServiceInterface.modifyNotice(notice);
        }

        modelAndView.setViewName("redirect:/notice/selectNotice");
        return modelAndView;

    }

    @RequestMapping("/notice/removeNotice")
    public ModelAndView removeNotice(Integer[] ids){

        //批量删除
        for (int i = 0; i <ids.length ; i++) {
            otherServiceInterface.removeNoticeById(ids[i]);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/notice/selectNotice");
        return modelAndView;

    }

    @RequestMapping("/notice/addNotice")
    public ModelAndView addDept(HttpServletRequest request, Notice notice, Integer flag, Model model){

        ModelAndView modelAndView = new ModelAndView();
        //得到登录的用户
        User user = (User)request.getSession().getAttribute("user_session");

        //这里是添加界面的跳转操作
        if(flag == 1){
            model.addAttribute("notice",notice);
            modelAndView.setViewName("notice/showAddNotice");
            notice.setUser(user);
            return modelAndView;
        }
        //这里是添加页面的修改
        else if(flag == 2){
            notice.setUser(user);
            otherServiceInterface.addNotice(notice);
        }

        modelAndView.setViewName("redirect:/notice/selectNotice");
        return modelAndView;

    }


    @RequestMapping("/notice/previewNotice")
    public ModelAndView previewNotice(Model model,Integer id){

        ModelAndView modelAndView = new ModelAndView();

        Notice noticeById = otherServiceInterface.findNoticeById(id);
        model.addAttribute("notice",noticeById);
        modelAndView.setViewName("/notice/previewNotice");

        return modelAndView;
    }


	
}
