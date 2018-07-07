//package hrm.interceptor;
//
//
//import hrm.domain.User;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 判断用户权限的Spring MVC的拦截器
// */
//public class AuthorizedInterceptor implements HandlerInterceptor {
//
//	/** 定义不需要拦截的请求 */
//	private static final String[] IGNORE_URI = {"/login", "/login","/404.html"};
//
//	/**
//	 * preHandle方法是进行处理器拦截用的，该方法将在Controller处理之前进行调用，
//	 * 当preHandle的返回值为false的时候整个请求就结束了。
//	 * 如果preHandle的返回值为true，则会继续执行postHandle和afterCompletion。
//	 */
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        String requestURI = request.getRequestURI();
//        System.out.println("requestURI===" + requestURI);
//
//		User user = (User)request.getSession().getAttribute("user_session");
//
//        if(requestURI.contains(IGNORE_URI[0]) || requestURI.contains(IGNORE_URI[1]) || requestURI.contains(IGNORE_URI[2])){
//            //不需要拦截的请求
//
//            return true;
//
//        }else if(user != null){
//
//        	return true;
//        }else {
//
//            response.sendRedirect( "loginForm");
//            return false;
//        }
//
//	}
//
//
//
//	/**
//	 * 这个方法在preHandle方法返回值为true的时候才会执行。
//	 * 执行时间是在处理器进行处理之 后，也就是在Controller的方法调用之后执行。
//	 */
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o,
//						   ModelAndView modelAndView) throws Exception {
//
//
//	}
//
//
//
//
//	/**
//	 * 该方法需要preHandle方法的返回值为true时才会执行。
//	 * 该方法将在整个请求完成之后执行，主要作用是用于清理资源。
//	 */
//	@Override
//	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {
//
//
//
//	}
//
//
//
//
//
//
//
//
//
//}
