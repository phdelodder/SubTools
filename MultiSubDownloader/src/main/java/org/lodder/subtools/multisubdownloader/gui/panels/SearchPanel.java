package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.JPanel;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

@Getter
public class SearchPanel<I extends InputPanel> extends JPanel {

    private static final long serialVersionUID = -7602822323779710089L;
    private final ResultPanel resultPanel;
    private final I inputPanel;

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
