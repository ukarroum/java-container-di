package fr.isima.yk.container;

import fr.isima.yk.container.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyContainerTest {

    MyContainer injector;

    @BeforeEach
    void createInjector() {
        injector = new MyContainer();
    }

    @Test
    @DisplayName("Constructor injection : one level of dependencies")
    void testConsInjec() {
        injector.bind(AuditService.class, SimpleAuditService.class);
        injector.bind(MovieFinder.class, WebMovieFinder.class);

        MovieLister finder = injector.newInstance(MovieLister.class);
        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Setter injection : one level of dependencies")
    void testSetInjec() {
        injector.bind(AuditService.class, SimpleAuditService.class);

        MovieLister2 finder = injector.newInstance(MovieLister2.class);
        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Fields injection : one level of dependencies")
    void testFieldInjec() {
        injector.bind(AuditService.class, SimpleAuditService.class);

        MovieLister3 finder = injector.newInstance(MovieLister3.class);
        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Singleton Support : True")
    void testSingSup() {
        injector.bind(AuditService.class, SimpleAuditService.class, true);

        MovieLister2 finder = injector.newInstance(MovieLister2.class);
        MovieLister2 finder2 = injector.newInstance(MovieLister2.class);

        assertTrue(finder.getAuditService() == finder2.getAuditService());
    }

    @Test
    @DisplayName("Singleton Support : False")
    void testNonSing() {
        injector.bind(AuditService.class, SimpleAuditService.class, false);

        MovieLister2 finder = injector.newInstance(MovieLister2.class);
        MovieLister2 finder2 = injector.newInstance(MovieLister2.class);

        assertTrue(finder.getAuditService() != finder2.getAuditService());
    }

    @Test
    @DisplayName("Multiple Implementations")
    void testMultImp() {
        injector.bind(AuditService.class, SimpleAuditService.class);
        injector.bind(AuditService.class, String.class);

        MovieLister4 finder = injector.newInstance(MovieLister4.class);

        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Autowiring")
    void testAutoWiring() {
        injector.bind(AuditService.class, SimpleAuditService.class);

        injector.setAutoWiring(AuditService.class);

        MovieLister5 finder = injector.newInstance(MovieLister5.class);

        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Graph Resolution")
    void testGraphResolution() {
        injector.bind(LogerService.class, SimpleLogerService.class);
        injector.bind(FileSystem.class, NTFSFileSystem.class);

        MovieLister6 finder = injector.newInstance(MovieLister6.class);

        assertTrue(((SimpleLogerService)finder.getLoger()).getFileSystem() instanceof NTFSFileSystem);
    }
}