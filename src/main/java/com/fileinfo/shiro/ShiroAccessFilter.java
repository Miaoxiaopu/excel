package com.fileinfo.shiro;

import com.fileinfo.type.MessageEnumType;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.boot.autoconfigure.gson.GsonProperties;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.PrintWriter;

// 实现了serverlet的Filter接口
@Slf4j
public class ShiroAccessFilter extends AccessControlFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        servletResponse.getWriter().write(MessageEnumType.ACCESS_DENIED.getMessage());
        return false;
    }

    // 是否允许通过
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
       log.info(o.toString());//请求路径
//        return getSubject(servletRequest,servletResponse) != null ;
        return true;
    }
}
