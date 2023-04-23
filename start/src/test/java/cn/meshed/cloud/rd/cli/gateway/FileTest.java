package cn.meshed.cloud.rd.cli.gateway;

import cn.meshed.cloud.exception.security.AuthenticationException;

import java.util.Optional;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public class FileTest {

    public static void main(String[] args) {
//        String projectPath = "D:\\Temp\\meshed-cloud-test";
//        List<File> list = FileUtil.loopFiles(projectPath);
//        List<RepositoryFile> repositoryFiles = new ArrayList<>();
//        for (File file : list) {
//            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
//            String path = file.getPath().substring(projectPath.length() + 1).replaceAll("\\\\", "/");
//            repositoryFiles.add(new RepositoryFile(path, content));
//        }
        Optional.ofNullable(null)
                .orElseThrow(() -> new AuthenticationException("未登入,无用户信息"));
    }
}
