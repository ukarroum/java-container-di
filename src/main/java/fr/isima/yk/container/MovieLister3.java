package fr.isima.yk.container;

import javax.inject.Inject;

public class MovieLister3 {

    @MyInject
    AuditService audit;

    public MovieLister3() {

    }

    public AuditService getAuditService() {
        return audit;
    }
}
