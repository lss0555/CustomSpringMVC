package com.sxkj.servlet;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import com.sxkj.annotation.*;
import com.sxkj.controller.UserController;
import com.sxkj.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description servert
 * @Author lss0555
 * @Date 2019/1/22/022 16:19
 **/
public class DispatcherServlet extends HttpServlet {
    //存放扫描包下的所有文件集合
    private List<String> mClassNames=new ArrayList<String>();
    //存放实例化对象的集合容器 Customcontroller,Customservice注解的对象
    private Map<String,Object> mBeans=new HashMap<String, Object>();
    //存放url映射的路径信息
    private HashMap<String,Object> mHandleUrlMapping=new HashMap<String, Object>();

    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("初始化");
        initScanFile("com.sxkj");//扫描所有的包下目录的所有文件
        initInstance();//初始化实例化对象
        initAutowired();//实例化autowired注解对象到该类的controller字段对象
        initHandleUrlMapping();//处理url映射关系
    }

    /**
     * @Description  处理url映射关系
     **/
    private void initHandleUrlMapping() {
        for (Map.Entry<String,Object> entry:mBeans.entrySet()) {
            //遍历集合容器中的对象
            Object instance = entry.getValue();
            //根据实例化对象拿到类的对象
            Class<?> clazz = instance.getClass();
            //判断是否为控制类
            if (clazz.isAnnotationPresent(CustomController.class)) {
                //拿到customRequstMapping注解对象
                CustomRequstMapping customRequstMapping=clazz.getAnnotation(CustomRequstMapping.class);
                //拿到controller上改注解的值  如:/test
                String classPath = customRequstMapping.value();
                //拿到该类的所有方法
                Method[] methods = clazz.getMethods();
                for (Method method:methods){
                    //判断该方法是否有customRequstMapping注解
                    if(method.isAnnotationPresent(CustomRequstMapping.class)){
                        //拿到customRequstMapping注解对象
                        CustomRequstMapping requstMapping=method.getAnnotation(CustomRequstMapping.class);
                        //拿到该值  如:/getName
                        String methodPath = requstMapping.value();
                        //将路径 /test/getName  放入map容器中
                        mHandleUrlMapping.put(classPath+methodPath,method);
                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }
    }

    /**
     * @Description  初始实例化autowired注解的对象
     **/
    private void initAutowired() {
        for (Map.Entry<String,Object> entry:mBeans.entrySet()){
            //遍历集合容器中的对象
            Object instance = entry.getValue();
            //根据实例化对象拿到类的对象
            Class<?> clazz=instance.getClass();
            if(clazz.isAnnotationPresent(CustomController.class)){
                //获取该类的成员变量
                Field[] fields = clazz.getDeclaredFields();
                //遍历成员变量
                for (Field field:fields){
                    //判断该变量是否有customAutored注解
                    if(field.isAnnotationPresent(CustomAutowired.class)){
                        CustomAutowired customAutowired=field.getAnnotation(CustomAutowired.class);
                        //获取该注解的value值,该值为mBean的key值
                        String key = customAutowired.value();
                        //通过该key值获取对象
                        Object value=mBeans.get(key);
                        //让该字段获取权限,让其可访问私有变量
                        field.setAccessible(true);
                        try {
                            //将该service类对象设置到controller类中的autowwired字段值去
                            field.set(instance,value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }
    }

    /**
     * @Description  实例化对象(扫描过的文件且有注解的)
     **/
    private void initInstance() {
        for (String className:mClassNames){
            //如:  com.sxkj.annotation.CustomAutowire.class 将.class后缀去掉
            String name = className.replace(".class", "");
            try {
                //通过文件名拿到类
                Class<?> clazz = Class.forName(name);
                //判断该类是否有CustomController注解类
                if(clazz.isAnnotationPresent(CustomController.class)){
                    //实例化对象
                    Object instance = clazz.newInstance();
                    //获取该类上的注解的requstmapping中的value值
                    CustomRequstMapping customRequstMapping=clazz.getAnnotation(CustomRequstMapping.class);
                    mBeans.put(customRequstMapping.value(),instance);
                } else if (clazz.isAnnotationPresent(CustomService.class)) {
                    //实例化对象
                    Object instance = clazz.newInstance();
                    //获取该类上的注解的requstmapping中的value值
                    CustomService customService=clazz.getAnnotation(CustomService.class);
                    mBeans.put(customService.value(),instance);
                }else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @Description  扫描文件
     **/
    private void initScanFile(String basepackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + basepackage.replaceAll("\\.", "/"));
        String files = url.getFile();
        File file = new File(files);
        String[] fileList = file.list();
        for (String filePath:fileList){
            File filePaths = new File(files + filePath);
            if(filePaths.isDirectory()){
                initScanFile(basepackage+"."+filePath);
            }else {
                // com.sxkj.annotation.CustomAutowire.class
                mClassNames.add(basepackage+"."+filePaths.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest requset, HttpServletResponse response) throws ServletException, IOException {
        //获取请求的路径
        String requestURI = requset.getRequestURI();//获取到工程名+请求路径
        //将工程名去掉,最后为请求路径  /test/getName
        String contextPath = requset.getContextPath();
        //切割后最后为请求的路径  /test/getName
        String urlPath=requestURI.replace(contextPath,"");
        //通过路径获取到mHandleUrlMapping容器中的值 method
        Method method= (Method) mHandleUrlMapping.get(urlPath);
        //通过路径 /test  获取到CustomController的实例化对象
        Object instance =  mBeans.get("/" + urlPath.split("/")[1]);
        Object[] args = handleArgs(requset, response, method);
        //执行方法
        try {
            method.invoke(instance,args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static Object[] handleArgs(HttpServletRequest request,HttpServletResponse response,Method method){
        //获取当前执行的方法有哪些参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        //根据参数的个数,new出数组参数的数量,将方法的所有参数复制到arg[]里面来
        Object[] args = new Object[parameterTypes.length];
        int arg_i=0;int index=0;
        for (Class<?> paramClass:parameterTypes){
            if(ServletRequest.class.isAssignableFrom(paramClass)){
                args[arg_i++]=request;
            }if(ServletResponse.class.isAssignableFrom(paramClass)){
                args[arg_i++]=response;
            }
            Annotation[] annotations = method.getParameterAnnotations()[index];
            if(annotations.length>0){
                for (Annotation annotation:annotations){
                    if(CustomRequstParam.class.isAssignableFrom(annotation.getClass())){
                        CustomRequstParam customRequstParam= (CustomRequstParam) annotation;
                        //获取注解里面的参数
                        args[arg_i++]=request.getParameter(customRequstParam.value());
                    }
                }
            }
            index++;
        }
        return args;
    }
}
