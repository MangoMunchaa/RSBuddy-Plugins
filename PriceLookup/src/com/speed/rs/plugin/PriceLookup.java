package com.speed.rs.plugin;

import com.rsbuddy.api.gui.Location;
import com.rsbuddy.api.net.GeObject;
import com.rsbuddy.api.net.GrandExchange;
import com.rsbuddy.plugin.WidgetPluginBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: Shivam
 * Date: 03/04/13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class PriceLookup extends WidgetPluginBase {
    private Label label;
    private static final Label DEFAULT_LABEL = new Label("Enter an ID or name to lookup the price."),
            NOT_FOUND_LABEL = new Label("Item not found.");
    private GrandExchange grandExchange;

    public PriceLookup() {
        super("Price Lookup", "up.png", "hover.png", "down.png");
    }

    @Override
    protected void init() {
        grandExchange = context().lookup(GrandExchange.class);
    }

    @Override
    public Location defaultLocation() {
        return Location.BOTTOM;
    }

    @Override
    public EnumSet<Location> supportedLocations() {
        return EnumSet.of(Location.LEFT, Location.BOTTOM, Location.RIGHT);
    }

    public Node content(Location location) {
        final VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(5);
        final TextField text = new TextField();
        final Button lookup = new Button("Lookup");
        label = DEFAULT_LABEL;
        label.autosize();
        lookup.autosize();
        text.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                lookup.fireEvent(actionEvent);
            }
        });
        lookup.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                GeObject object = null;
                try {
                    if (text.getText().matches("\\d+")) {
                        object = grandExchange.lookup(Integer.parseInt(text.getText()));
                    } else {
                        object = grandExchange.lookup(text.getText());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                vbox.getChildren().remove(label);
                if (object == null) {
                    label = NOT_FOUND_LABEL;
                } else {
                    label = new Label(object.name() + ": " + formatPrice(object.price()));
                    label.setTextFill(Color.WHITESMOKE);
                }
                label.autosize();
                vbox.getChildren().add(label);
            }
        });
        vbox.getChildren().add(text);
        vbox.getChildren().add(lookup);
        vbox.getChildren().add(label);
        return vbox;
    }


    private static String formatPrice(int price) {
        final String[] suffixes = new String[]{"", "", "", "K", "K", "K", "M", "M", "M", "B"};
        final int[] factors = new int[]{1, 1, 1, 1000, 1000, 1000, 1000000, 1000000, 1000000, 1000000000};
        int index = (int) Math.floor(Math.log10(price));
        String suffix = suffixes[index];
        double newPrice = price / (double) factors[index];
        return newPrice + suffix + " gp";
    }
}
