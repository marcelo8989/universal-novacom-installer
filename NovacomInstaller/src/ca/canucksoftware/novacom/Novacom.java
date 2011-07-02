
package ca.canucksoftware.novacom;

import java.io.File;

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
                    programPath64 = programPath64.replace("\\", "/");
                }

                if(programPath64!=null && new File(programPath64 + "/HP webOS/SDK/bin/novacom.exe").exists()) {
                    novacom = programPath64 + "/HP webOS/SDK/bin/novacom.exe";
                } else if(new File(programPath32 + "/HP webOS/SDK/bin/novacom.exe").exists()) {
                    novacom = programPath32 + "/HP webOS/SDK/bin/novacom.exe";
                } else if(programPath64!=null && new File(programPath64 + "/Palm, Inc/novacom.exe").exists()) {
                    novacom = programPath64 + "/Palm, Inc/novacom.exe";
                } else if(new File(programPath32 + "/Palm, Inc/novacom.exe").exists()) {
                    novacom = programPath32 + "/Palm, Inc/novacom.exe";
                } else if(programPath64!=null && new File(programPath64 + "/Palm, Inc/novacom/novacom.exe")
                        .exists()) {
                    novacom = programPath64 + "/Palm, Inc/novacom/novacom.exe";
                } else if(new File(programPath32 + "/Palm, Inc/novacom/novacom.exe").exists()) {
                    novacom = programPath32 + "/Palm, Inc/novacom/novacom.exe";
                } else if(programPath64!=null && new File(programPath64 + "/Palm/SDK/bin/novacom.exe").exists()) {
                    novacom = programPath64 + "/Palm/SDK/bin/novacom.exe";
                } else if(new File(programPath32 + "/Palm/SDK/bin/novacom.exe").exists()) {
                    novacom = programPath32 + "/Palm/SDK/bin/novacom.exe";
                } else if(programPath64!=null && new File(programPath64 + "/PDK/bin/novacom.exe").exists()) {
                    novacom = programPath64 + "/PDK/bin/novacom.exe";
                } else if(new File(programPath32 + "/PDK/bin/novacom.exe").exists()) {
                    novacom = programPath32 + "/PDK/bin/novacom.exe";
                }
            } else {
                if(new File("C:/Program Files/HP webOS/SDK/bin/novacom.exe").exists()) {
                    novacom = "C:/Program Files/HP webOS/SDK/bin/novacom.exe";
                } else if(new File("C:/Program Files/Palm, Inc/novacom.exe").exists()) {
                    novacom = "C:/Program Files/Palm, Inc/novacom.exe";
                } else if(new File("C:/Program Files/Palm, Inc/novacom/novacom.exe").exists()) {
                    novacom = "C:/Program Files/Palm, Inc/novacom/novacom.exe";
                } else if(new File("C:/Program Files/Palm/SDK/bin/novacom.exe").exists()) {
                    novacom = "C:/Program Files/Palm/SDK/bin/novacom.exe";
                } else if(new File("C:/Program Files/PDK/bin/novacom.exe").exists()) {
                    novacom = "C:/Program Files/PDK/bin/novacom.exe";
                }
            }
        }
        return novacom;
    }

    public static boolean isInstalled() {
        boolean result = true;
        String novacom = execPath();
        return new File(novacom).exists();
    }

    public static boolean serviceInstalled() {
        boolean installed = false;
        if(new File("/opt/Palm/novacom/novacomd").exists()) { //linux
            installed = true;
        } else if(new File("/opt/nova/bin/novacomd").exists() ||
                new File("/Library/LaunchDaemons/com.palm.novacomd").exists()) { //mac
            installed = true;
        }else { //windows
            String programPath32 = System.getenv("ProgramFiles");
            String programPath64 = null;
            if(programPath32!=null) {
                programPath32 = programPath32.replace("\\", "/").trim();
                if(programPath32.endsWith("(x86)")) {
                    programPath64 = programPath32.substring(0,
                            programPath32.lastIndexOf("(x86)")).trim();
                    programPath64 = programPath64.replace("\\", "/");
                }
                if(new File(programPath32 + "/HP webOS/SDK/bin/novacomd/amd64/novacomd.exe").exists() ||
                        new File(programPath32 + "/HP webOS/SDK/bin/novacomd/x86/novacomd.exe").exists() ||
                        new File(programPath32 + "/Palm, Inc/novacom/amd64/novacomd.exe").exists() ||
                        new File(programPath32 + "/Palm, Inc/novacom/x86/novacomd.exe").exists() ||
                        new File(programPath32 + "/Palm/SDK/novacom/amd64/novacomd.exe").exists() ||
                        new File(programPath32 + "/Palm/SDK/novacom/x86/novacomd.exe").exists() ||
                        new File(programPath32 + "/Palm/SDK/bin/novacom/amd64/novacomd.exe").exists() ||
                        new File(programPath32 + "/Palm/SDK/bin/novacom/x86/novacomd.exe").exists()) {
                    installed = true;
                }
                if(programPath64 != null) {
                    if(new File(programPath64 + "/HP webOS/SDK/bin/novacomd/amd64/novacomd.exe").exists() ||
                            new File(programPath64 + "/HP webOS/SDK/bin/novacomd/x86/novacomd.exe").exists() ||
                            new File(programPath64 + "/Palm, Inc/novacom/amd64/novacomd.exe").exists() ||
                            new File(programPath64 + "/Palm, Inc/novacom/x86/novacomd.exe").exists() ||
                            new File(programPath64 + "/Palm/SDK/novacom/amd64/novacomd.exe").exists() ||
                            new File(programPath64 + "/Palm/SDK/novacom/x86/novacomd.exe").exists() ||
                            new File(programPath64 + "/Palm/SDK/bin/novacom/amd64/novacomd.exe").exists() ||
                            new File(programPath64 + "/Palm/SDK/bin/novacom/x86/novacomd.exe").exists()) {
                        installed = true;
                    }
                }
            } else {
                if(new File("C:/Program Files/HP webOS/SDK/bin/novacomd/amd64/novacomd.exe").exists() ||
                        new File("C:/Program Files/HP webOS/SDK/bin/novacomd/x86/novacomd.exe").exists() ||
                        new File("C:/Program Files/Palm, Inc/novacom/amd64/novacomd.exe").exists() ||
                        new File("C:/Program Files/Palm, Inc/novacom/x86/novacomd.exe").exists() ||
                        new File("C:/Program Files/Palm/SDK/novacom/amd64/novacomd.exe").exists() ||
                        new File("C:/Program Files/Palm/SDK/novacom/x86/novacomd.exe").exists() ||
                        new File("C:/Program Files/Palm/SDK/bin/novacom/amd64/novacomd.exe").exists() ||
                        new File("C:/Program Files/Palm/SDK/bin/novacom/x86/novacomd.exe").exists()) {
                    installed = true;
                }
            }
        }
        return installed;
    }
}
