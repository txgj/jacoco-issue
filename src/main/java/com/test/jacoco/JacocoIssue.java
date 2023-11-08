package com.test.jacoco;

// please add this to idea settings->Build->Compiler->Java Compiler>Override compiler parameters per-module
// -add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import org.apache.commons.io.FileUtils;
import org.jacoco.agent.rt.internal.CoverageTransformer;
import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.InjectedClassRuntime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author txgj
 * @date 2023/11/8
 */
public class JacocoIssue {
    public static void main(String[] args) {
        String realFile = "{your project path}/test/target/classes/com/test/jacoco/SimpleClassForJacoco";
        String jacocoFile = "{your project path}/test/target/classes/com/test/jacoco/SimpleClassForJacoco" + "AfterTransformer";
        Class clazz = SimpleClassForJacoco.class;
        byte[] bytes;

        // first read and parse
        try {
            System.out.println("readAndParseClass before jacoco transform");
            bytes = readAndParseClass(realFile + ".class");
            System.out.println("before length: " + bytes.length);
        } catch (Throwable e) {
            System.out.println("readAndParseClass before jacoco transform error: " + e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // jacoco transform
        IRuntime runtime = new InjectedClassRuntime(clazz, clazz.getSimpleName());
        AgentOptions agentOptions = new AgentOptions();
        agentOptions.setInclNoLocationClasses(true);
        agentOptions.setIncludes("*");
        CoverageTransformer transformer = new CoverageTransformer(runtime, agentOptions, null);
        try {
            byte[] bytesAfter = transformer.transform(clazz.getClassLoader(), clazz.getSimpleName(), null, null, bytes);
            FileUtils.writeByteArrayToFile(new File(jacocoFile + ".class"), bytesAfter);
            System.out.println("after length:" + bytesAfter.length);

            // second read and parse
            try {
                System.out.println("readAndParseClass after jacoco transform");
                bytes = readAndParseClass(jacocoFile + ".class");
                System.out.println("reRead length:" + bytes.length);
            } catch (Throwable e) {
                System.out.println("readAndParseClass after jacoco transform error: " + e);
                e.printStackTrace();
            }
        } catch (Throwable e) {
            System.out.println("jacoco transformer error: " + e);
            e.printStackTrace();
        }
    }

    private static byte[] readAndParseClass(String path) throws IOException {
        ClassReader cr = new ClassReader(Files.newInputStream(Paths.get(path), StandardOpenOption.READ));
        String className = cr.getClassName();
        System.out.println("readAndParseClass className: " + className);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        byte[] reWrite = cw.toByteArray();
        System.out.println("readAndParseClass reWrite length: " + reWrite.length);
        return cr.b;
    }

}
