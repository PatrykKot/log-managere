package com.kotlarz.frontend.view.main;

import com.kotlarz.frontend.presenter.MainPresenter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringViewDisplay
@UIScope
public class MainView extends MainViewDesign implements ViewDisplay {

    @Autowired
    private MainPresenter presenter;

    @PostConstruct
    private void init() {
        presenter.initView(this);
    }

    @Override
    public void showView(View view) {
        contentPanel.setContent(view.getViewComponent());
    }
}
