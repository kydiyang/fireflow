/**
 * Copyright 2007-2008 ��ؿ�ƣ���Ҳ,Chen Nieyun��
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation��
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.designer.editpanel.graph;

import javax.swing.JToolBar;
import java.awt.Insets;
import java.util.*;
import cn.bestsolution.tools.resourcesmanager.actions.*;
import javax.swing.*;

public class ProcessDesignToolBar extends JToolBar {

    private static Insets MARGIN = new Insets(4, 4, 4, 4);
    private ProcessDesignMediator mediator = null;
    private ButtonGroup buttonGrp = new ButtonGroup();

    public ProcessDesignToolBar(ProcessDesignMediator argMd) {
        mediator = argMd;
        init();
    }

    public void refreshButtonGroup() {
        JToggleButton button = (JToggleButton) buttonGrp.getElements().nextElement();
        button.doClick();
    }

    public void mountActions(List actionGroups) {
        this.removeAll();
        this.addSeparator();
        for (int i = 0; i < actionGroups.size(); i++) {
            Object obj = actionGroups.get(i);

            if (obj instanceof ActionGroup) {
                if (i > 0) {
                    this.addSeparator();
                }

                ActionGroup actGroup = (ActionGroup) obj;
                mountActions(actGroup.getChildActions());

            } else {
                JToggleButton button = new JToggleButton((Action) obj);
                button.setText("");
                this.add(button);
                buttonGrp.add(button);
//             button.setToolTipText(((UpdateAction)obj).getToolTip());
                button.setMargin(MARGIN);
                button.setFocusable(false);
            }
        }
        this.updateUI();
    }

    private void init() {
        setRollover(true);
        this.setFocusable(false);
        this.setBorderPainted(true);
        this.setOrientation(JToolBar.HORIZONTAL);

    }
}
