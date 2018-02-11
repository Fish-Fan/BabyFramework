package babyframework;

import babyframework.bean.Data;
import babyframework.bean.Handler;
import babyframework.bean.Param;
import babyframework.bean.View;
import babyframework.factory.BeanFactory;
import babyframework.helper.BeanHelper;
import babyframework.helper.ConfigHelper;
import babyframework.helper.ControllerHelper;
import babyframework.helper.LoaderHelper;
import babyframework.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DispatchServlet extends HttpServlet{
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        //初始化容器
        LoaderHelper.init();
        //获取应用上下文
        ServletContext context = servletConfig.getServletContext();
        //处理JSP的servlet
        ServletRegistration jspServlet = context.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getJSPPath() + "*");
        //处理静态资源的默认servlet
        ServletRegistration defaultServlet = context.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getStaticPath() + "*");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        super.service(req,res);
    }

    public void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException {
        processRequest(req,resp);
    }

    public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException {
        processRequest(req,resp);
    }

    public void processRequest(HttpServletRequest req,HttpServletResponse res) throws IOException, ServletException {
        String reqMethod = req.getMethod();
        String reqPath = req.getRequestURI();
        //获取Action处理器
        Handler handler = ControllerHelper.getHandler(reqMethod,reqPath);
        if(handler != null) {
            BeanFactory factory = new BeanFactory();
            //获取controller的class和实例
            Class<?> controllerCls = handler.getControllerClass();
            Object controllerBean = factory.getBeanByClass(controllerCls);

            //创建请求参数和对象
            Map<String,Object> paramMap = new HashMap<String, Object>();
            Enumeration<String> paramNames = req.getParameterNames();

            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = req.getParameter(paramName);
                paramMap.put(paramName,paramValue);
            }

            //解析路径参数
            String body = CodeUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if(StringUtil.isNotEmpty(body)) {
                String[] params = StringUtil.splitString(body,"&");
                if(ArrayUtil.isNotEmpty(params)) {
                    for(String param : params) {
                        String[] array = StringUtil.splitString(param,"=");
                        if(ArrayUtil.isNotEmpty(array) && array.length == 2) {
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName,paramValue);
                        }
                    }
                }
            }

            Param param = new Param(paramMap);

            //调用Action方法
            Object result;
            Method actionMethod = handler.getActionMethod();
            if(param.isEmpty()) {
                result = ReflectionUtil.invokeMethod(controllerBean,actionMethod);
            } else {
                result = ReflectionUtil.invokeMethod(controllerBean,actionMethod,param);
            }


            //处理action返回值
            if(result instanceof View) {
                //返回jsp页面
                View view = (View) result;
                String path = view.getPath();
                if(StringUtil.isNotEmpty(path)) {
                    if(path.startsWith("/")) {
                        res.sendRedirect(req.getContextPath() + path);
                    } else {
                        Map<String,Object> model = view.getModel();
                        for(Map.Entry<String,Object> entry : model.entrySet()) {
                            req.setAttribute(entry.getKey(),entry.getValue());
                        }

                        req.getRequestDispatcher(ConfigHelper.getJSPPath() + path + ConfigHelper.getJSPENDWITH()).forward(req,res);
                    }
                }
            } else if(result instanceof Data) {
                //返回json数据
                Data data = (Data) result;
                Object model = data.getModel();

                if(model != null) {
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    PrintWriter out = res.getWriter();
                    String json = JsonUtil.toJson(model);
                    out.write(json);
                    out.flush();
                    out.close();
                }
            }
        }
    }
}
