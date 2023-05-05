package org.lodder.subtools.sublibrary.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.lodder.subtools.multisubdownloader.Messages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import lombok.Setter;
import lombok.experimental.Accessors;

public class InputPane {

    private InputPane() {
        // hide constructor
    }

    public static InputPaneBuilderTitleIntf create() {
        return create(null);
    }

    public static InputPaneBuilderTitleIntf create(JFrame parent) {
        return new InputPaneBuilder(parent);
    }

    public interface InputPaneBuilderTitleIntf {
        InputPaneBuilderMessageIntf title(String title);
    }

    public interface InputPaneBuilderMessageIntf {
        InputPaneBuilderErrorMessageIntf message(String message);
    }

    public interface InputPaneBuilderErrorMessageIntf {
        InputPaneBuilderValidatorIntf errorMessage(String errorMessage);
    }

    public interface InputPaneBuilderValidatorIntf extends InputPaneBuilderPromptIntf {
        InputPaneBuilderPromptIntf validator(Predicate<String> validator);
    }

    public interface InputPaneBuilderPromptIntf {
        InputPaneBuilderPromptIntf okText(String okText);

        InputPaneBuilderPromptIntf cancelText(String cancelText);

        Optional<String> prompt();
    }

    @Setter
    @Accessors(fluent = true)
    public static class InputPaneBuilder extends JDialog implements ActionListener, PropertyChangeListener,
            InputPaneBuilderPromptIntf, InputPaneBuilderErrorMessageIntf, InputPaneBuilderValidatorIntf, InputPaneBuilderMessageIntf,
            InputPaneBuilderTitleIntf {
        @Serial
        private static final long serialVersionUID = 1L;
        private final static String OK = Messages.getString("App.OK");
        private final static String CANCEL = Messages.getString("App.Cancel");
        private String title;
        private String message;
        private String errorMessage;
        private Predicate<String> validator;
        private String okText = OK;
        private String cancelText = CANCEL;

        //
        private Optional<String> typedText = Optional.empty();
        private JTextField textField;
        private JOptionPane optionPane;

        InputPaneBuilder(JFrame parent) {
            super(parent, true);
        }

        @Override
        public Optional<String> prompt() {
            textField = new JTextField(10);

            // Create an array of the text and components to be displayed.
            Object[] array = { message, textField };
            Object[] options = { okText, cancelText };
            // Create the JOptionPane.
            optionPane = new JOptionPane(array,
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION,
                    null, options, options[0]);

            optionPane.selectInitialValue();

            // Make this dialog display it.
            setContentPane(optionPane);

            // Handle window closing correctly.
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    // Instead of directly closing the window, we're going to change the JOptionPane's value property.
                    optionPane.setValue(JOptionPane.CLOSED_OPTION);
                }
            });

            // Ensure the text field always gets the first focus.
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent ce) {
                    textField.requestFocusInWindow();
                }
            });

            // Register an event handler that puts the text into the option pane.
            textField.addActionListener(this);

            // Register an event handler that reacts to option pane state changes.
            optionPane.addPropertyChangeListener(this);

            pack();
            setVisible(true);

            optionPane.getValue();
            return typedText;
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();

            if (isVisible()
                    && e.getSource() == optionPane
                    && (JOptionPane.VALUE_PROPERTY.equals(prop)
                            || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
                Object value = optionPane.getValue();

                if (value == JOptionPane.UNINITIALIZED_VALUE) {
                    // ignore reset
                    return;
                }

                // Reset the JOptionPane's value.
                // If you don't do this, then if the user presses the same button next time,
                // no property change event will be fired.
                optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                if (String.valueOf(okText).equals(String.valueOf(value))) {
                    typedText = Optional.ofNullable(textField.getText());
                    if (validator == null || typedText.map(validator::test).orElse(false)) {
                        exit();
                    } else {
                        // text was invalid
                        textField.selectAll();
                        Object[] array = { errorMessage, message, textField };
                        optionPane.setMessage(array);
                        typedText = Optional.empty();
                        textField.requestFocusInWindow();
                        pack();
                    }
                } else { // user closed dialog or clicked cancel
                    typedText = Optional.empty();
                    exit();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e);
        }

        /**
         * This method clears the dialog and hides it.
         */
        public void exit() {
            dispose();
        }
    }
}
