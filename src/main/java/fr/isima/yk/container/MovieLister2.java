package fr.isima.yk.container;

import javax.inject.Inject;

public class MovieLister2 {

    AuditService audit;

    public MovieLister2() {

    }

    @MyInject
    public void setAudit(AuditService audit) {
        this.audit = audit;
    }

    public AuditService getAuditService() {
        return audit;
    }
}
