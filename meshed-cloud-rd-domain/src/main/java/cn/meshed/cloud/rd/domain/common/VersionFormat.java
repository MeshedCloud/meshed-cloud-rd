package cn.meshed.cloud.rd.domain.common;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public class VersionFormat {

    public static String version(Long versionNum) {
        if (versionNum == null || versionNum == 0) {
            return "未知";
        }
        //获取第一位 major and minor
        long major = versionNum / 10000;
        long minor = 0L;
        if (versionNum > 100) {
            minor = (versionNum % 1000) / 100;
        }
        return major + "." + minor + "." + (versionNum % 100);

    }
}
