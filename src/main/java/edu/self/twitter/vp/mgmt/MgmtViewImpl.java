package edu.self.twitter.vp.mgmt;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;

import ch.nyg.java.util.LogUtil;
import edu.self.twitter.TwitterUI;

@SpringView(name = MgmtView.NAME, ui = TwitterUI.class)
public class MgmtViewImpl extends VerticalLayout implements MgmtView {

    private static final long serialVersionUID = 1L;

    @Autowired
    MgmtPresenter presenter;

    @Override
    public void enter(ViewChangeEvent event) {

        setSizeFull();
        setSpacing(true);

        String[] args = event.getParameters().split("/");

        switch (args[0]) {

            case "insert":
                String[] screenNames = Arrays.copyOfRange(args, 1, args.length);
                List<String> notInserted = presenter.insertUsers(screenNames);
                if (!notInserted.isEmpty()) {
                    LogUtil.info("Not inserted: %s", String.join(", ", notInserted));
                }
                break;

            case "delete":
                break;

            default:
                break;
        }
    }
}
