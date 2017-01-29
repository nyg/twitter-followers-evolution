package edu.self.twitter.vp.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.highcharts.StockChart;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

import edu.self.twitter.TwitterUI;

@SpringView(name = ChartView.NAME, ui = TwitterUI.class)
public class ChartViewImpl extends VerticalLayout implements ChartView {

    private static final long serialVersionUID = 1L;

    @Autowired
    ChartPresenter presenter;

    private String screenName;

    @Override
    public void enter(ViewChangeEvent event) {

        screenName = event.getParameters();

        setSizeFull();
        setSpacing(true);

        StockChart chart = new StockChart();
        chart.setScreenName(screenName);
        chart.setSizeFull();

        NativeSelect screenNameSelect = new NativeSelect(null, presenter.getAllUsers());
        screenNameSelect.setImmediate(true);
        screenNameSelect.setNullSelectionAllowed(true);
        screenNameSelect.select(screenName);
        screenNameSelect.addValueChangeListener(e -> ((TwitterUI) getUI()).navigateToChart(e.getProperty().getValue().toString()));

        String twitterUrl = "https://twitter.com/" + screenName;
        Link twitterLink = new Link(twitterUrl, new ExternalResource(twitterUrl));

        HorizontalLayout layout = new HorizontalLayout(screenNameSelect, twitterLink);
        layout.setSpacing(true);

        addComponent(layout);
        addComponent(chart);
        setExpandRatio(chart, 1);
    }
}
