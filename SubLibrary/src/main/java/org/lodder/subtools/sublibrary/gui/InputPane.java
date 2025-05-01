package org.lodder.subtools.sublibrary.gui;

import static org.lodder.subtools.multisubdownloader.Messages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lodder.subtools.sublibrary.util.Validator;

public class InputPane<T> extends JDialog implements ActionListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private final String message;
    private final List<Validator<String>> inputValidators;
    private final Function<String, T> toObjectMapper;
    private final List<Validator<T>> objectValidators;
    private final String okText;
    private final String cancelText;

    //
    private JTextField textField;
    private JOptionPane optionPane;
    private T input;

    public InputPane(@Nullable Frame owner=null,
        String title,
        String message,
        List<Validator<String>> inputValidators=new ArrayList<Validator<String>>(),
        Function<String, T> toObjectMapper,
        List<Validator<T>> objectValidators=new ArrayList<Validator<T>>(),
        String okText=getText("App.OK"),
        String cancelText=getText("App.Cancel")) {

        super(owner, true);
        setTitle(title);
        this.message = message;
        this.inputValidators = inputValidators;
        this.toObjectMapper = toObjectMapper;
        this.objectValidators = objectValidators;
        this.okText = okText;
        this.cancelText = cancelText;
    }

    public Optional<T> prompt() {
        textField = new JTextField(10);

        // Create an array of the text and components to be displayed.
        Object[] array = {message, textField};
        Object[] options = {okText, cancelText};
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

        return Optional.ofNullable(input);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible() && e.getSource() == optionPane
            && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {

            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                // ignore reset
                return;
            }

            // Reset the JOptionPane's value.
            // If this isn't done, no property change event will be fired when the button is pressed again.
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

            if (StringUtils.equals(okText, String.valueOf(value))) {
                String text = textField.getText();
                for (Validator<String> validator : inputValidators) {
                    if (validator.isInvalid(text)) {
                        invalidInputEncountered(validator.errorMessage);
                        return;
                    }
                }
                input = toObjectMapper.apply(text);
                for (Validator<T> validator : objectValidators) {
                    if (validator.isInvalid(input)) {
                        invalidInputEncountered(validator.errorMessage);
                        return;
                    }
                }
                exit();
            } else { // user closed dialog or clicked cancel
                input = null;
                exit();
            }
        }
    }

    private void invalidInputEncountered(String errorMessage) {
        textField.selectAll();
        Object[] array = {errorMessage, message, textField};
        optionPane.setMessage(array);
        input = null;
        textField.requestFocusInWindow();
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
    }

    /**
     * Clears the dialog and hides it.
     */
    public void exit() {
        dispose();
    }
}
