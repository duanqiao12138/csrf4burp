package cn.csrf;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CsrfMenuItemsProvider implements ContextMenuItemsProvider {

    private MontoyaApi Api;

    public CsrfMenuItemsProvider(MontoyaApi montoyaApi) {
        this.Api = montoyaApi;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        ArrayList<Component> menuItems = new ArrayList<>();
        JMenuItem menuItem = new JMenuItem("Send to CSRF4Burp");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        menuItems.add(menuItem);
        return menuItems;
    }
}
