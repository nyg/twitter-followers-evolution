package edu.self.twitter.vp.chart;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.highcharts.StockChart;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

import edu.self.twitter.TwitterUI;
import edu.self.twitter.model.Tuple;
import twitter4j.User;

@UIScope
@SpringView(name = ChartView.NAME, ui = TwitterUI.class)
public class ChartViewImpl extends HorizontalLayout implements ChartView {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ChartPresenter presenter;

    private boolean uiBuilt = false;
    private boolean userIsValid;
    private Optional<User> twitterUser;
    private List<User> allUsers;

    // UI
    //private Button deleteButton;
    private Link twitterLink;
    private StockChart chart;
    private NativeSelect<User> screenNameSelect;
    private HorizontalLayout twitterInfoLayout;

    private Grid<Tuple<String, Integer>> statsGrid;

    @Override
    public void enter(ViewChangeEvent event) {

        twitterUser = presenter.doesUserExists(event.getParameters());
        userIsValid = twitterUser.isPresent();

        if (!uiBuilt) {
            buildUI();
        }

        initUI();
    }

    private void buildUI() {

        if (allUsers == null) {
            allUsers = presenter.getAllUsers();
        }

        // Chart
        chart = new StockChart();
        chart.setSizeFull();

        // Native select
        screenNameSelect = new NativeSelect<>("Twitter Users", allUsers);
        screenNameSelect.setSizeFull();
        screenNameSelect.setEmptySelectionAllowed(false);
        screenNameSelect.setItemCaptionGenerator(User::getName);
        screenNameSelect.addValueChangeListener(e -> {
            User newScreenName = e.getValue();
            if (!newScreenName.equals(twitterUser.orElse(null))) {
                ((TwitterUI) getUI()).navigateToChart(newScreenName.getScreenName());
            }
        });

        // Twitter link
        twitterLink = new Link("Twitter", null);

        twitterInfoLayout = new HorizontalLayout(screenNameSelect, twitterLink);
        twitterInfoLayout.setExpandRatio(screenNameSelect, 1);
        //twitterInfoLayout.setSpacing(true);
        twitterInfoLayout.setWidth(100, Unit.PERCENTAGE);
        //twitterInfoLayout.setComponentAlignment(screenNameSelect, Alignment.BOTTOM_LEFT);
        twitterInfoLayout.setComponentAlignment(twitterLink, Alignment.BOTTOM_RIGHT);

        statsGrid = new Grid<>("Statistics");
        statsGrid.setWidth(100, Unit.PERCENTAGE);

        VerticalLayout statsLayout = new VerticalLayout(twitterInfoLayout, statsGrid);
        //statsLayout.setSpacing(true);
        statsLayout.setWidth(400, Unit.PIXELS);

        // Main layout
        addComponents(chart, statsLayout);
        setExpandRatio(chart, 1);
        setSizeFull();
        //setSpacing(true);

        uiBuilt = true;
    }

    private void initUI() {

        if (userIsValid) {
            screenNameSelect.setSelectedItem(twitterUser.get());
            chart.setScreenName(twitterUser.get().getScreenName());
            twitterLink.setResource(new ExternalResource("https://twitter.com/" + twitterUser.get().getScreenName()));
            statsGrid.setDataProvider(DataProvider.ofCollection(presenter.getUserFollowersStatistics(twitterUser.get().getScreenName())));
        }

        //twitterInfoLayout.setExpandRatio(twitterLink, validUser ? 1 : 0);
        //twitterInfoLayout.setExpandRatio(screenNameSelect, validUser ? 0 : 1);

        twitterLink.setVisible(userIsValid);
        chart.setVisible(userIsValid);
    }
}
