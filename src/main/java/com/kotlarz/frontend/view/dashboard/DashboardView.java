package com.kotlarz.frontend.view.dashboard;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;

import javax.annotation.PostConstruct;

@SpringView( name = DashboardView.NAME )
@UIScope
public class DashboardView
                extends DashboardViewDesign
                implements View
{
    public static final String NAME = "dashboard";

    @PostConstruct
    public void init() {
    }
}