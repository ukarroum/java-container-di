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
    @DisplayName("Constructor injection : US2/4")
    void testConsInjec() {
        injector.bind(AuditService.class, SimpleAuditService.class);
        injector.bind(MovieFinder.class, WebMovieFinder.class);

        MovieLister finder = injector.newInstance(MovieLister.class);
        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Setter injection : US1/4")
    void testSetInjec() {
        injector.bind(AuditService.class, SimpleAuditService.class);

        MovieLister2 finder = injector.newInstance(MovieLister2.class);
        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Fields injection : US3/4")
    void testFieldInjec() {
        injector.bind(AuditService.class, SimpleAuditService.class);

        MovieLister3 finder = injector.newInstance(MovieLister3.class);
        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Singleton Support : True US5")
    void testSingSup() {
        injector.bind(AuditService.class, SimpleAuditService.class, true);

        MovieLister2 finder = injector.newInstance(MovieLister2.class);
        MovieLister2 finder2 = injector.newInstance(MovieLister2.class);

        assertTrue(finder.getAuditService() == finder2.getAuditService());
    }

    @Test
    @DisplayName("Singleton Support : False US5")
    void testNonSing() {
        injector.bind(AuditService.class, SimpleAuditService.class, false);

        MovieLister2 finder = injector.newInstance(MovieLister2.class);
        MovieLister2 finder2 = injector.newInstance(MovieLister2.class);

        assertTrue(finder.getAuditService() != finder2.getAuditService());
    }

    @Test
    @DisplayName("Multiple Implementations US6")
    void testMultImp() {
        injector.bind(AuditService.class, SimpleAuditService.class);
        injector.bind(AuditService.class, String.class);

        MovieLister4 finder = injector.newInstance(MovieLister4.class);

        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Autowiring US7")
    void testAutoWiring() {
        injector.bind(AuditService.class, SimpleAuditService.class);

        injector.setAutoWiring(AuditService.class);

        MovieLister5 finder = injector.newInstance(MovieLister5.class);

        assertTrue(finder.getAuditService() instanceof SimpleAuditService);
    }

    @Test
    @DisplayName("Graph Resolution US10")
    void testGraphResolution() {
        injector.bind(LogerService.class, SimpleLogerService.class);
        injector.bind(FileSystem.class, NTFSFileSystem.class);

        MovieLister6 finder = injector.newInstance(MovieLister6.class);

        assertTrue(((SimpleLogerService)finder.getLoger()).getFileSystem() instanceof NTFSFileSystem);
    }
}