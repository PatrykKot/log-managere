package com.kotlarz.frontend.view.configuration.customers.single;

import com.kotlarz.frontend.presenter.configuration.customers.single.SingleCustomerConfigPresenter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringComponent
@UIScope
public class EditCustomerView
                extends SingleCustomerConfigView
{
    @Autowired
    private SingleCustomerConfigPresenter presenter;

    @Override
    @PostConstruct
    void init()
    {
        super.init();
        formButton.setCaption( "Save" );

        presenter.init(this);
    }
}
