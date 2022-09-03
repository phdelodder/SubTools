package org.lodder.subtools.multisubdownloader.gui.panels;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class SearchPanel extends JPanel {

    private static final long serialVersionUID = -7602822323779710089L;
    private ResultPanel resultPanel;
    private InputPanel inputPanel;

    public SearchPanel() {
        initialize();
    }

    private void initialize() {
        setLayout(new MigLayout("", "[grow,fill]", "[][][]"));
    }

    public InputPanel getInputPanel() {
        return this.inputPanel;
    }

    public ResultPanel getResultPanel() {
        return this.resultPanel;
    }

    public void setInputPanel(InputPanel inputPanel) {
        this.inputPanel = inputPanel;
        add(inputPanel, "cell 0 0");
    }

    public void setResultPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
        add(resultPanel, "cell 0 1");
    }
}
