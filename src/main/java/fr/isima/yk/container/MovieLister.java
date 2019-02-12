package fr.isima.yk.container;

import javax.inject.Inject;

public class MovieLister {
    MovieFinder finder;
    AuditService audit;

    @MyInject
    public MovieLister(MovieFinder m_finder, AuditService m_audit) {
        finder = m_finder;
        audit = m_audit;
    }

    public AuditService getAuditService() {
        return audit;
    }
}
