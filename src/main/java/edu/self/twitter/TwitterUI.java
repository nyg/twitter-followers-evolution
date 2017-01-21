package edu.self.twitter;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.self.twitter.vp.chart.ChartView;

@SpringUI
public class TwitterUI extends UI {

    private static final long serialVersionUID = 1L;

    @Autowired
    SpringNavigator navigator;

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);

        navigator.init(this, content);

        if (Page.getCurrent().getUriFragment() == null) {
            navigator.navigateTo(ChartView.NAME);
        }
    }

    public void navigateToChart(String screenName) {
        navigator.navigateTo(ChartView.NAME + "/" + screenName);
    }
}
