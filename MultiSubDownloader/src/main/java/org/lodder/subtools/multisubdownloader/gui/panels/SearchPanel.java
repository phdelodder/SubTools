package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.*;
import java.io.Serial;

import manifold.ext.props.rt.api.val;
import net.miginfocom.swing.MigLayout;

public class SearchPanel<I extends InputPanel> extends JPanel {

    @Serial
    private static final long serialVersionUID = -7602822323779710089L;

    @val ResultPanel resultPanel;
    @val I inputPanel;

    public SearchPanel(I inputPanel, ResultPanel resultPanel) {
        this.inputPanel = inputPanel;
        this.resultPanel = resultPanel;
        initialize();
        add(inputPanel, "cell 0 0");
        add(resultPanel, "cell 0 1");
    }

    private void initialize() {
        setLayout(new MigLayout("", "[grow,fill]", "[][][]"));
    }
}
