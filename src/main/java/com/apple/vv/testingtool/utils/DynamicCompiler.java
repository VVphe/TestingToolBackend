package com.apple.vv.testingtool.utils;

import javax.tools.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DynamicCompiler {
    public static Class myCompiler(File file) {
        //String fileToCompile = file.getAbsolutePath();
        String fileToCompile = "/Users/apple/myFiles/Test.java";

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnosticCollector, Locale.ENGLISH, Charset.forName("utf-8"));
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects(fileToCompile);

        //compile
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, diagnosticCollector, null, null, javaFileObjects);
        task.call();

        //load class
        URL[] urls = new URL[0];
        try {
            //File myFile = new File(file.getParentFile().getAbsolutePath());
            File myFile = new File("/Users/apple/myFiles");
            urls = new URL[]{myFile.toURI().toURL()};

            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            //Class myClass = urlClassLoader.loadClass(file.getName().substring(0, file.getName().lastIndexOf(".")));
            Class myClass = urlClassLoader.loadClass("Test");

            return myClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> getMethods(Class myClass) {

        List<String> methodNames = new ArrayList<>();
        for (Method method : myClass.getDeclaredMethods()) {
            String methodName = method.getName();
            methodNames.add(methodName);
        }
        return methodNames;

    }

    public static List<List<String>> getArgs(Class myClass, String methodName) {
        List<Method> methods = new ArrayList<>();
        List<List<String>> methodsArgs = new ArrayList<>();
        for (Method method : myClass.getDeclaredMethods()) {
            if(method.getName().equals(methodName)) {
                methods.add(method);
                Class[] argsTypes = method.getParameterTypes();
                List<String> argsTypesName = new ArrayList<>();
                for(Class argsType : argsTypes) {
                    argsTypesName.add(argsType.getName());
                }

                methodsArgs.add(argsTypesName);
            }
        }

        return methodsArgs;
    }

    public static Object doMethod(Class myClass, String methodName, Object[] args, List<String> argsTypes) {
        Class[] argsClass = new Class[args.length];
        Object[] newArgs = new Object[args.length];
        for(int i = 0; i < args.length; i++) {
            switch (argsTypes.get(i)) {
                case "int":
                    newArgs[i] = Integer.parseInt(args[i].toString());
                    argsClass[i] = int.class;
                    break;

                case "double":
                    newArgs[i] = Double.parseDouble(args[i].toString());
                    argsClass[i] = double.class;
                    break;

                case "String":
                    newArgs[i] = args[i].toString();
                    argsClass[i] = newArgs[i].getClass();
                    break;
            }
        }
        try {
            Method method = myClass.getMethod(methodName, argsClass);
           // System.out.println(method.invoke(myClass, newArgs));
            return method.invoke(myClass.newInstance(), newArgs);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
