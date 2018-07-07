package hrm.controller;

import hrm.domain.User;
import hrm.service.HrmService;
import hrm.util.common.HrmConstants;
import hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理用户请求控制器
 */
@Controller
public class UserController {

    /**
     * 自动注入UserService
     */
    @Autowired
    @Qualifier("hrmService")
    private HrmService hrmService;

    /**
     * 处理登录请求
     *
     * @param loginname loginname  登录名
     * @param password  password 密码
     * @return 跳转的视图
     */
    @RequestMapping(value = "/login")
    public ModelAndView login(@RequestParam("loginname") String loginname,
                              @RequestParam("password") String password,
                              HttpSession session,
                              ModelAndView mv) {
        // 调用业务逻辑组件判断用户是否可以登录
        User user = hrmService.login(loginname, password);
        if (user != null) {
            // 将用户保存到HttpSession当中
            session.setAttribute(HrmConstants.USER_SESSION, user);
            // 客户端跳转到main页面
            mv.setViewName("redirect:/main");
        } else {
            // 设置登录失败提示信息
            mv.addObject("message", "登录名或密码错误!请重新输入");
            // 服务器内部跳转到登录页面
            mv.setViewName("forward:/loginForm");
        }
        return mv;

    }

    /**
     * 处理查询请求
     *
     * @param pageIndex 请求的是第几页
     * @param employee  模糊查询参数
     * @param model     model
     */
    @RequestMapping("/user/selectUser")
    public ModelAndView selectUser(Integer pageIndex, User user, Model model) {
        //如果没有得到username或status就表明是从左边点击来的，那么就分页显示所有的
        String username = user.getUsername();
        Integer status = user.getStatus();
        ModelAndView modelAndView = new ModelAndView();
        PageModel pageModel = new PageModel();

        //表示点击查询过来的
        if(pageIndex == null){
            pageIndex = 1;
        }
        //在service层判断是否username和status为null，这里返回的userlike最好不要分页，全部返回就行了
        List<User> userlike = hrmService.findUser(user, pageModel);

        if(userlike == null || userlike.size() == 0){
            modelAndView.setViewName("user/user");
            return modelAndView;
        }

        //要在这层告诉页面 总页数
        pageModel.setPageSize(pageModel.getPageSize());
        pageModel.setRecordCount(userlike.size());
        pageModel.setPageIndex(pageIndex);
        List<User> retUser =  new ArrayList<>();
        //然后根据当前页数来选择List中返回那几条
        if(pageModel.getTotalSize()-1 == 0){
            //只有一页
            for (int i = 0; i <userlike.size() ; i++) {
                retUser.add(userlike.get(i));
            }
            //index从1开始
        }else if(pageModel.getTotalSize() == pageIndex){
            //最后一页
            for (int i = pageModel.getPageSize()*(pageModel.getTotalSize()-1); i <userlike.size() ; i++) {
                retUser.add(userlike.get(i));
            }
        }else {
            //中间页
            for (int i = (pageIndex-1)*pageModel.getPageSize();
                 i <((pageIndex-1)*pageModel.getPageSize()+pageModel.getPageSize()) ;
                 i++) {
                retUser.add(userlike.get(i));
            }
        }
        System.out.println("pageModel.getPageIndex()======" + pageModel.getPageIndex());
        System.out.println("pageModel.getTotalSize()======" + pageModel.getTotalSize());
        //将分页信息塞入
        model.addAttribute("pageModel", pageModel);

        model.addAttribute("users", retUser);

        modelAndView.setViewName("user/user");

        return modelAndView;
    }

    /**
     * 处理删除用户请求
     *
     * @param String       ids 需要删除的id字符串
     * @param ModelAndView mv
     */

    @RequestMapping("/user/removeUser")
    public ModelAndView removeUser(Integer[] ids) {

        //批量删除,删除user，那么应该跟user相关的所有数据都要删除，这里，只有notice
        for (int i = 0; i <ids.length ; i++) {
            hrmService.removeUserById(ids[i]);
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/user/selectUser");
        return modelAndView;
    }


    @RequestMapping("/user/updateUser")
    public ModelAndView updateUser(User user,Model model,Integer flag){
        ModelAndView modelAndView = new ModelAndView();

        //这里是查询界面的跳转操作
        if(flag == 1){
            model.addAttribute("user",user);
            modelAndView.setViewName("user/showUpdateUser");
            return modelAndView;
        }
        //这里是修改页面的修改
        else if(flag == 2){
             hrmService.modifyUser(user);
        }

        modelAndView.setViewName("redirect:/user/selectUser");
        return modelAndView;
    }


    @RequestMapping("/user/addUser")
    public ModelAndView addUser(User user,Integer flag,Model model){

        ModelAndView modelAndView = new ModelAndView();

        //这里是添加界面的跳转操作
        if(flag == 1){
            model.addAttribute("user",user);
            modelAndView.setViewName("user/showAddUser");
            return modelAndView;
        }
        //这里是修改页面的修改
        else if(flag == 2){
            hrmService.addUser(user);
        }

        modelAndView.setViewName("redirect:/user/selectUser");
        return modelAndView;

    }


    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){

        try {
            //注销session，然后退出到登录界面
            request.getSession().invalidate();

            response.sendRedirect("loginForm");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
