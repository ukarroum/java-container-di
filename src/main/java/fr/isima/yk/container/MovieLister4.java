package fr.isima.yk.container;

import javax.inject.Inject;

public class MovieLister4 {

    @MyInject(name=SimpleAuditService.class)
    AuditService audit;

    public MovieLister4() {

    }

    public AuditService getAuditService() {
        return audit;
    }
}
