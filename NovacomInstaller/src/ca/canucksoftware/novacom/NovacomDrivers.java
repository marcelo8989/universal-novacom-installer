
package ca.canucksoftware.novacom;

import ca.canucksoftware.utils.OnlineFile;
import ca.canucksoftware.utils.TextStreamConsumer;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * @author Jason Robitaille
 */
public class NovacomDrivers {
    private String doctor;
    private Driver driver;
    private JProgressBar pBar;
    private JLabel label;
    private boolean cancel;

    public NovacomDrivers(String webOSDoctor) {
        pBar = null;
        label = null;
        cancel = false;
        doctor = webOSDoctor;
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("windows")) {
            if(System.getenv("ProgramFiles(x86)")==null) {
                driver = Driver.Windows_x86;
            } else {
                driver = Driver.Windows_x64;
            }
        } else if(os.contains("mac")) {
            driver = Driver.Mac;
        } else if(os.contains("linux")) {
            if(!is64bitLinux()) {
                driver = Driver.Linux_x86;
            } else {
                driver = Driver.Linux_x64;
            }
        }
    }

    public void setGUI(JLabel lbl, JProgressBar bar) {
        label = lbl;
        pBar = bar;
    }

    private boolean is64bitLinux() {
        boolean result = false;
        try {
            Process p = Runtime.getRuntime().exec("dpkg --print-architecture");
            OutputStream os = p.getOutputStream();
            os.flush();
            os.close();
            TextStreamConsumer stdout = new TextStreamConsumer(p.getInputStream());
            stdout.start();
            TextStreamConsumer stderr = new TextStreamConsumer(p.getErrorStream());
            stderr.start();
            stdout.waitFor();
            result = !stdout.toString().toLowerCase().contains("i386");
        } catch (IOException e) {
            System.err.println("Unable to check Linux system architecture");
        }
        return result;
    }

    public boolean install() {
        boolean result = false;
        if(driver==Driver.Windows_x86 || driver==Driver.Windows_x64) {
            result = installForWindows();
        } else if(driver==Driver.Mac) {
            result = installForMac();
        } else if(driver==Driver.Linux_x86 || driver==Driver.Linux_x64) {
            result = installForLinux();
        }
        return result;
    }

    public boolean installForWindows() {
        boolean result = false;
        File installer = extractInstaller();
        if(label!=null) {
            label.setText("<html>" + "Installing driver...");
        }
        String command = "msiexec /i " + installer.getAbsolutePath() + " /passive";
        if(Novacom.isInstalled()) {
            command = "msiexec /i " + installer.getAbsolutePath()+ " REINSTALL=ALL REINSTALLMODE=vomus /norestart /passive";
        }
        try {
            Process p = Runtime.getRuntime().exec(command);
            OutputStream os = p.getOutputStream();
            os.flush();
            os.close();
            TextStreamConsumer stdout = new TextStreamConsumer(p.getInputStream());
            stdout.start();
            TextStreamConsumer stderr = new TextStreamConsumer(p.getErrorStream());
            stderr.start();
            result = (p.waitFor()==0);
            stdout.waitFor();
            stderr.waitFor();
            installer.delete();
            result = true;
        } catch (Exception e) {
            System.err.println("Unable to install " + driver.toString());
            e.printStackTrace();
        }
        return result;
    }

    public boolean installForMac() {
        boolean result = false;
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        File baseDir = new File(tmpFilePath, "novacom-drivers");
        if(baseDir.exists()) {
            deleteDirectory(baseDir);
        }
        GZIPInputStream gzip;
        try {
            gzip = new GZIPInputStream(new FileInputStream(extractInstaller()));
            if(label!=null) {
                label.setText("<html>" + "Installing driver...");
            }
            TarInputStream tar = new TarInputStream(gzip);
            TarEntry entry = tar.getNextEntry();
            while(entry != null) {
                File curr = new File(baseDir, entry.getName());
                if(entry.isDirectory()) {
                    curr.mkdirs();
                } else {
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(curr));
                    byte[] buffer = new byte[2048];
                    for (;;)  {
                        int nBytes = tar.read(buffer);
                        if (nBytes <= 0)
                            break;
                        out.write(buffer, 0, nBytes);
                    }
                    out.flush();
                    out.close();
                    try {
                        Process p = Runtime.getRuntime().exec("chmod ugoa+x " + curr.getAbsolutePath());
                        OutputStream os = p.getOutputStream();
                        os.flush();
                        os.close();
                        TextStreamConsumer stdout = new TextStreamConsumer(p.getInputStream());
                        stdout.start();
                        TextStreamConsumer stderr = new TextStreamConsumer(p.getErrorStream());
                        stderr.start();
                        p.waitFor();
                        stdout.waitFor();
                        stderr.waitFor();
                    } catch (Exception e) {
                        System.err.println("Unable to chmod " + curr.getAbsolutePath());
                    }
                }
                entry = tar.getNextEntry();
            }
            tar.close();
            String pkg = baseDir.getAbsolutePath();
            if(!pkg.endsWith("/")) {
                pkg += "/";
            }
            pkg += "NovacomInstaller.pkg";
            try {
                Process p = Runtime.getRuntime().exec("open -W " + pkg);
                OutputStream os = p.getOutputStream();
                os.flush();
                os.close();
                TextStreamConsumer stdout = new TextStreamConsumer(p.getInputStream());
                stdout.start();
                TextStreamConsumer stderr = new TextStreamConsumer(p.getErrorStream());
                stderr.start();
                result = (p.waitFor()==0);
                stdout.waitFor();
                stderr.waitFor();
                deleteDirectory(baseDir);
                result = true;
            } catch (Exception e) {
                System.err.println("Unable to install " + pkg);
            }
        } catch (Exception ex) {
            System.err.println("Unable to install " + driver.toString());
        }

        return result;
    }

    private boolean deleteDirectory(File path) {
        if(path.isDirectory()) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                        files[i].delete();
                }
            }
        }
        return(path.delete());
    }

    public boolean installForLinux() {
        boolean result = false;
        OnlineFile url = new OnlineFile(driver.toString());
        File installer = url.download();
        if(label!=null) {
            label.setText("<html>" + "Installing driver...");
        }
        if(installer!=null) {
            try {
                Process p = Runtime.getRuntime().exec("xterm +hold -e sudo dpkg -i "
                        + installer.getAbsolutePath());
                OutputStream os = p.getOutputStream();
                os.flush();
                os.close();
                TextStreamConsumer stdout = new TextStreamConsumer(p.getInputStream());
                stdout.start();
                TextStreamConsumer stderr = new TextStreamConsumer(p.getErrorStream());
                stderr.start();
                result = (p.waitFor()==0);
                stdout.waitFor();
                stderr.waitFor();
                installer.delete();
                result = true;
            } catch (Exception e) {
                System.err.println("Unable to install " + driver.toString());
                e.printStackTrace();
            }
        }
        return result;
    }

    private File extractInstaller() {
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        File result = new File(tmpFilePath, driver.toString().substring(
                driver.toString().lastIndexOf("/")+1));
        if(doctor.startsWith("http://")) {
            int maxSize = 202910;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setGroupingUsed(false);
            nf.setMaximumFractionDigits(2);
            JarInputStream jis = null;
            try {
                byte data[] = new byte[1024];
                HttpURLConnection urlCon = (HttpURLConnection) new URL(doctor).openConnection();
                urlCon.setInstanceFollowRedirects(true);
                urlCon.setRequestProperty("REFERER", doctor);
                urlCon.connect();
                jis = new JarInputStream(urlCon.getInputStream());
                JarEntry curr = jis.getNextJarEntry();
                while(curr!=null) {
                    if(curr.getName().equalsIgnoreCase(driver.toString())) {
                        curr.getSize();
                        double percent = 0;
                        int count = 0;
                        int kbCount = 0;
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(result));
                        while((count = jis.read(data)) != -1) {
                            if(label!=null) {
                                if(kbCount<1000) {
                                    pBar.setString(kbCount + " KB");
                                } else {
                                    pBar.setString(nf.format(((double) kbCount)/1024.0) + " MB");
                                }
                            }
                            kbCount++;
                            out.write(data, 0, count);
                        }
                        out.flush();
                        out.close();
                        break;
                    }
                    curr = jis.getNextJarEntry();
                }
                jis.close();
            } catch(Exception e) {
                e.printStackTrace();
                System.err.println("Unable to extract " + driver.toString());
                result = null;
            }
        } else {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(new File(doctor));
                InputStream in = jarFile.getInputStream(jarFile.getEntry(driver.toString()));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(result));
                byte[] buffer = new byte[2048];
                for (;;)  {
                    int nBytes = in.read(buffer);
                    if (nBytes <= 0)
                        break;
                    out.write(buffer, 0, nBytes);
                }
                out.flush();
                out.close();
                in.close();
            } catch (IOException e) {
                System.err.println("Unable to extract " + driver.toString());
                result = null;
            }
        }
        return result;
    }

    private enum Driver {
        Windows_x86("resources/NovacomInstaller_x86.msi"),
        Windows_x64("resources/NovacomInstaller_x64.msi"),
        Mac("resources/NovacomInstaller.pkg.tar.gz"),
        Linux_x86("http://cdn.downloads.palm.com/sdkdownloads/1.4.5.465/sdkBinaries/palm-novacom_1.0.56_i386.deb"),
        Linux_x64("http://cdn.downloads.palm.com/sdkdownloads/1.4.5.465/sdkBinaries/palm-novacom_1.0.56_amd64.deb");

        private String model;
        Driver(String val) {
            model = val;
        }
        @Override
        public String toString() {
            return model;
        }
    }
}
