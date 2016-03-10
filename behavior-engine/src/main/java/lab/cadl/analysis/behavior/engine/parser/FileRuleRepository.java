package lab.cadl.analysis.behavior.engine.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 */
public class FileRuleRepository implements RuleRepository {
    private File repositoryDir;

    public FileRuleRepository() {
        this.repositoryDir = new File(".");
    }

    public FileRuleRepository(File repositoryDir) {
        this.repositoryDir = repositoryDir;
    }

    @Override
    public InputStream open(String path) {
        File behaviorFile = repositoryDir.toPath().resolve(path.replace(".", File.separator) + ".b").toFile();
        if (!behaviorFile.isFile()) {
            throw new IllegalArgumentException("未找到规则文件：" + behaviorFile.toString());
        }

        try {
            return new FileInputStream(behaviorFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
