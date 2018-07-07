package hrm.controller;

import hrm.domain.User;
import hrm.util.common.HrmConstants;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


/**
 * @Description:  
 * @date 2015年8月13日 下午8:30:37 
 * @version V1.0   
 */

/**
 * 动态页面跳转控制器
 * */
@Controller
public class FormController{

	@RequestMapping(value="/{formName}")
	 public String loginForm(@PathVariable String formName, HttpSession session){
		// 动态跳转页面
		System.out.println("formName = "+formName);

		if ("main".equals(formName)){
			//通过这个debug的时候查看一下session里面到底装的是什么
			String id = session.getId();

			SimplePrincipalCollection collection= (SimplePrincipalCollection)
					session.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");

			User primaryPrincipal = (User) collection.getPrimaryPrincipal();
			System.out.println("primaryPrincipal===========" + primaryPrincipal);
			session.setAttribute(HrmConstants.USER_SESSION,primaryPrincipal);

		}
		return formName;
	}

}

