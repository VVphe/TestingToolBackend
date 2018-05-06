package com.apple.vv.testingtool.controller;

import com.apple.vv.testingtool.utils.DynamicCompiler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class JavaTestController {
    private File myFile ;
    private Class myClass;
    private String filePath ;

    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping("/uploadFile")
    public boolean uploadJava(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        String path = "/Users/apple/myFiles/testJava";
        File test = new File(path + "/" +filename);
        if(!test.getParentFile().exists()) {
            test.getParentFile().mkdir();
        }
        try {
            file.transferTo(test);
            this.filePath = path + "/" + filename;
            this.myFile = test;
            return true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping("/compiler")
    public boolean compiler() {
        myClass = DynamicCompiler.myCompiler(this.myFile);
        return true;
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping("/methods")
    public List<String> methods() {
        myClass = DynamicCompiler.myCompiler(this.myFile);
        return DynamicCompiler.getMethods(myClass);
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping("/arguments")
    public List<List<String>> getArgs(@RequestParam("methodName") String methodName) {
        return DynamicCompiler.getArgs(myClass, methodName);
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping("/testing")
    public Object testing(@RequestParam("methodName") String methodName, @RequestParam("args") Object[] args, @RequestParam("argsTypes") List<String> argsTypes) {
        return DynamicCompiler.doMethod(myClass, methodName, args, argsTypes);
    }
}
