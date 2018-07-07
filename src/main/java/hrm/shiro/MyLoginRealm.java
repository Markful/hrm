package hrm.shiro;

import hrm.domain.User;
import hrm.service.HrmService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;


public class MyLoginRealm extends AuthorizingRealm {

    @Autowired
    private HrmService hrmService;
    //认证
    @Override
    protected org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
                            throws AuthenticationException {

        //从token中取出身份信息
        String username = (String)token.getPrincipal();

        //根据用户输入的结果搜索数据库
        User user = hrmService.findUserByName(username);
        if(user == null){
            return null;
        }else {
            //查询得到返回认证信息
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                    user,user.getPassword(),this.getName());

            return simpleAuthenticationInfo;
        }

    }


    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        return null;
    }


}
