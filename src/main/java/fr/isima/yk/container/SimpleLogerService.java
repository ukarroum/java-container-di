package fr.isima.yk.container;

import javax.inject.Inject;
import java.io.File;

public class SimpleLogerService extends LogerService {

    @MyInject
    FileSystem f;

    public FileSystem getFileSystem() { return f; }
}
