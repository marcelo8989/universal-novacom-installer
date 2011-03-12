
package ca.canucksoftware.novacom;

import ca.canucksoftware.utils.TextStreamConsumer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jason Robitaille
 */
public class Novacom {
    public static String execPath() {
        String novacom = "novacom";
        if(new File("/usr/local/bin/novacom").exists()) { //mac or linux
            novacom = "/usr/local/bin/novacom";
        } else { //windows
            String programPath32 = System.getenv("ProgramFiles");
            String programPath64 = null;
            if(programPath32!=null) {
                programPath32 = programPath32.replace("\\", "/").trim();
                if(programPath32.endsWith("(x86)")) {
                    programPath64 = programPath32.substring(0,
                            programPath32.lastIndexOf("(x86)")).trim();
                }
                if(new File(programPath32 + "/Palm, Inc/novacom.exe").exists()) {
                    novacom = programPath32 + "/Palm, Inc/novacom.exe";
                }else if(new File(programPath32 + "/Palm, Inc/novacom/novacom.exe").exists()) {
                    novacom = programPath32 + "/Palm, Inc/novacom/novacom.exe";
                }
                if(programPath64!=null) {
                    programPath64 = programPath64.replace("\\", "/");
                    if(new File(programPath64 + "/Palm, Inc/novacom.exe").exists()) {
                        novacom = programPath64 + "/Palm, Inc/novacom.exe";
                    }else if(new File(programPath64 + "/Palm, Inc/novacom/novacom.exe").exists()) {
                        novacom = programPath64 + "/Palm, Inc/novacom/novacom.exe";
                    }
                }
            } else {
                if(new File("C:/Program Files/Palm, Inc/novacom.exe").exists()) {
                    novacom = "C:/Program Files/Palm, Inc/novacom.exe";
                }else if(new File("C:/Program Files/Palm, Inc/novacom/novacom.exe").exists()) {
                    novacom = "C:/Program Files/Palm, Inc/novacom/novacom.exe";
                }
            }
        }
        return novacom;
    }

    public static boolean isInstalled() {
        boolean result = true;
        String novacom = execPath();
        if(!new File(novacom).exists()) {
            Process p;
            try {
                p = Runtime.getRuntime().exec(novacom + " -V");
                OutputStream os = p.getOutputStream();
                os.flush();
                os.close();
                TextStreamConsumer stdout = new TextStreamConsumer(p.getInputStream());
                stdout.start();
                TextStreamConsumer stderr = new TextStreamConsumer(p.getErrorStream());
                stderr.start();
            } catch(IOException e) {
                result = false;
            }
        }
        return result;
    }
}
