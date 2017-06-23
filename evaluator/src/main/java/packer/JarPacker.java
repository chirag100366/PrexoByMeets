package packer;

import com.badlogicgames.packr.Packr;
import com.badlogicgames.packr.PackrConfig;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by rohitpatiyal on 23/6/17.
 */
public class JarPacker {
    public static void main(String[] args) throws IOException {
        PackrConfig config = new PackrConfig();
        config.platform = PackrConfig.Platform.Linux64;
        config.jdk = "/usr/lib/jvm/openjdk-1.7.0-u80-unofficial-linux-amd64-image.zip";
        config.executable = "getSysInfo";
        config.classpath = Arrays.asList("target/getSysInfo.jar");
        config.mainClass = "specs.Login";
        config.vmArgs = Arrays.asList("Xmx1G");
        config.minimizeJre = "soft";
        config.outDir = new java.io.File("out-linux");

        new Packr().pack(config);
    }
}
