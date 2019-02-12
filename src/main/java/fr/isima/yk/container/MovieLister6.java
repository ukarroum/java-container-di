package fr.isima.yk.container;

import javax.inject.Inject;

public class MovieLister6 {

    @MyInject
    LogerService loger;

    public MovieLister6() {

    }

    public LogerService getLoger() {
        return loger;
    }
}
