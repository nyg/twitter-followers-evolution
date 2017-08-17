package edu.self.twitter.vp.admin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.self.twitter.TwitterUI;

@UIScope
@SpringView(name = AdminView.NAME, ui = TwitterUI.class)
public class AdminViewImpl extends VerticalLayout implements AdminView {

    private static final long serialVersionUID = 1L;

    @Autowired
    private AdminPresenter presenter;

    @Override
    public void enter(ViewChangeEvent event) {
        buildUI();
    }

    private void buildUI() {

        // add new Twitter users
        TextField addField = new TextField();
        addField.setDescription("A comma-separated list of Twitter screen names.");
        addField.setWidth(100, Unit.PERCENTAGE);

        Button addButton = new Button("Add Users", e -> presenter.addUsers(addField.getValue()));
        HorizontalLayout addLayout = new HorizontalLayout(addField, addButton);
        addLayout.setWidth(100, Unit.PERCENTAGE);
        addLayout.setExpandRatio(addField, 1);

        // list of existing Twitter users in the database
        Grid<String> usersGrid = new Grid<>("Users", presenter.getAllUsers());
        usersGrid.addColumn(source -> source);
        usersGrid.removeHeaderRow(0);
        usersGrid.setHeightMode(HeightMode.CSS);
        usersGrid.setSizeFull();

        // remove an existing Twitter user from the database
        Button deleteButton = new Button("Delete Selected Users", e -> presenter.deleteUsers(usersGrid.getSelectedItems()));
        deleteButton.setWidth(100, Unit.PERCENTAGE);

        addComponents(addLayout, usersGrid, deleteButton);
        setExpandRatio(usersGrid, 1);
        setMargin(false);
        setWidth(40, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);
    }
}
