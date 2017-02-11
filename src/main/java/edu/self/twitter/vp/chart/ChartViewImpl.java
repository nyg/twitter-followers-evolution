package edu.self.twitter.vp.chart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.highcharts.StockChart;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.self.twitter.TwitterUI;
import twitter4j.User;

@UIScope
@SpringView(name = ChartView.NAME, ui = TwitterUI.class)
public class ChartViewImpl extends VerticalLayout implements ChartView {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ChartPresenter presenter;

    private boolean uiBuilt = false;
    private boolean validScreenName;
    private String screenName;
    private BeanContainer<String, User> allUsers;

    // UI
    private Button deleteButton;
    private Link twitterLink;
    private StockChart chart;
    private NativeSelect screenNameSelect;
    private HorizontalLayout topLayout;

    @Override
    public void enter(ViewChangeEvent event) {

        validScreenName = true;
        screenName = event.getParameters();
        if (!presenter.doesUserExists(screenName)) {
            validScreenName = false;
            screenName = null;
        }

        if (!uiBuilt) {
            buildUI();
        }

        initUI();
    }

    private void buildUI() {

        if (allUsers == null) {
            allUsers = presenter.getAllUsers();
        }

        // Native select
        screenNameSelect = new NativeSelect(null, allUsers);
        screenNameSelect.setImmediate(true);
        screenNameSelect.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        screenNameSelect.setItemCaptionPropertyId("name");
        screenNameSelect.setNullSelectionAllowed(false);
        screenNameSelect.addValueChangeListener(e -> {
            String newScreenName = (String) e.getProperty().getValue();
            if (!newScreenName.equals(screenName)) {
                ((TwitterUI) getUI()).navigateToChart(newScreenName);
            }
        });

        // Delete button
        deleteButton = new Button("Delete", e -> {
            if (presenter.deleteUser(screenName)) {
                allUsers.removeItem(screenName);
                Notification.show("User deleted!");
            }
            else {
                Notification.show("User deletion failed!", Type.ERROR_MESSAGE);
            }
        });

        // Twitter link
        twitterLink = new Link("Twitter", null);

        // Text field & add button
        TextField newUserField = new TextField();
        Button addButton = new Button("Add new user", e -> {
            List<String> errors = presenter.addUsers(newUserField.getValue().split(","));
            if (errors.isEmpty()) {
                Notification.show("All users added!");
            }
            else {
                Notification.show("Some users could not be added: " + String.join(", ", errors), Type.ERROR_MESSAGE);
            }
        });

        topLayout = new HorizontalLayout(screenNameSelect, deleteButton, twitterLink, newUserField, addButton);
        topLayout.setSpacing(true);
        topLayout.setWidth(100, Unit.PERCENTAGE);
        topLayout.setComponentAlignment(screenNameSelect, Alignment.MIDDLE_LEFT);
        topLayout.setComponentAlignment(twitterLink, Alignment.MIDDLE_LEFT);

        chart = new StockChart();
        chart.setSizeFull();

        // Main layout
        addComponent(topLayout);
        addComponent(chart);
        setExpandRatio(chart, 1);
        setSizeFull();
        setSpacing(true);

        uiBuilt = true;
    }

    private void initUI() {

        if (validScreenName) {
            screenNameSelect.select(screenName);
            chart.setScreenName(screenName);
            twitterLink.setResource(new ExternalResource("https://twitter.com/" + screenName));
        }

        topLayout.setExpandRatio(twitterLink, validScreenName ? 1 : 0);
        topLayout.setExpandRatio(screenNameSelect, validScreenName ? 0 : 1);

        deleteButton.setVisible(validScreenName);
        twitterLink.setVisible(validScreenName);
        chart.setVisible(validScreenName);
    }
}
