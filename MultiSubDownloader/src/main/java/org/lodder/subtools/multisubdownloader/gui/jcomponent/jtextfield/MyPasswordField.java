package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextfield;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.Serial;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import manifold.ext.props.rt.api.var;
import org.apache.commons.lang3.StringUtils;
import org.lodder.subtools.sublibrary.util.function.BooleanConsumer;

public class MyPasswordField extends JPasswordField implements MyPasswordFieldOthersIntf {

    @Serial
    private static final long serialVersionUID = -3002009544577141751L;
    private static final String DEFAULT_BORDER_PROPERTY = "DefaultBorder";
    private static final Border ERROR_BORDER = new LineBorder(Color.RED, 1);

    public Predicate<String> valueVerifier = StringUtils::isNotEmpty;

    private boolean requireValue;
    private Consumer<String> valueChangedCallbackListener;
    private BooleanConsumer[] validityChangedCallbackListeners;

    private final ObjectWrapper<String> valueWrapper = new ObjectWrapper<>();
    private final ObjectWrapper<Boolean> validWrapper = new ObjectWrapper<>();
    private Predicate<String> completeValueVerifier;

    private MyPasswordField() {
        super();
        putClientProperty(DEFAULT_BORDER_PROPERTY, getBorder());
    }

    public static MyPasswordField builder() {
        return new MyPasswordField();
    }

    @Override
    public void setBorder(Border border) {
        setSuperBorder(border);
        putClientProperty(DEFAULT_BORDER_PROPERTY, border);
    }

    public void setErrorBorder() {
        setBorder(ERROR_BORDER);
    }

    private void setSuperBorder(Border border) {
        super.setBorder(border);
    }

    @Override
    public MyPasswordField withValueVerifier(Predicate<String> verifier) {
        this.valueVerifier = verifier;
        return this;
    }

    @Override
    public MyPasswordField requireValue(boolean requireValue) {
        this.requireValue = requireValue;
        return this;
    }

    @Override
    public MyPasswordField withValueChangedCallback(Consumer<String> valueChangedCallbackListener) {
        this.valueChangedCallbackListener = valueChangedCallbackListener;
        return this;
    }

    @Override
    public MyPasswordField withValidityChangedCallback(BooleanConsumer... validityChangedCallbackListeners) {
        this.validityChangedCallbackListeners = validityChangedCallbackListeners;
        return this;
    }

    private static class ObjectWrapper<S> {
        @var S value;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        refreshState();
    }

    public void refreshState() {
        if (!isEnabled()) {
            setSuperBorder(getDefaultBorder(this));
        } else if (!completeValueVerifier.test(getRawText())) {
            setSuperBorder(ERROR_BORDER);
        }
    }

    private static Border getDefaultBorder(JComponent thisTextField) {
        return (Border) thisTextField.getClientProperty(DEFAULT_BORDER_PROPERTY);
    }

    @Override
    public MyPasswordField build() {
        if (valueVerifier != null && requireValue) {
            completeValueVerifier = text -> (StringUtils.isNotEmpty(text) && valueVerifier.test(text));
        } else if (valueVerifier != null) {
            completeValueVerifier = valueVerifier;
        } else if (requireValue) {
            completeValueVerifier = StringUtils::isNotEmpty;
        } else {
            completeValueVerifier = _ -> true;
        }

        if (valueVerifier != null || requireValue || valueChangedCallbackListener != null ||
            validityChangedCallbackListeners != null) {
            checkValidity(getRawText());
            getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkValidity(getRawText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkValidity(getRawText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkValidity(getRawText());
                }

            });
        }
        return this;
    }

    private void checkValidity(String text) {
        boolean valid = completeValueVerifier.test(text);
        setSuperBorder(valid ? MyPasswordField.getDefaultBorder(this) : ERROR_BORDER);

        boolean changedValidity = Objects.equals(validWrapper.value, valid);
        validWrapper.value = valid;
        if (changedValidity && validityChangedCallbackListeners != null) {
            validityChangedCallbackListeners.forEach(listener -> listener.accept(valid));
        }

        if (valueChangedCallbackListener != null) {
            boolean valueChanged = !StringUtils.equals(valueWrapper.value, text);
            valueWrapper.value = text;
            if (valueChanged) {
                valueChangedCallbackListener.accept(text);
            }
        }
    }

    private String getRawText() {
        return new String(getPassword());
    }

    @Override
    public String getText() {
        String text = new String(getPassword());
        return completeValueVerifier.test(text) ? text : null;
    }

    @Override
    public void setText(String password) {
        super.setText(password);
        valueWrapper.value = password;
        validWrapper.value = completeValueVerifier.test(password);
    }

    public boolean hasValidValue() {
        return !isEnabled() || completeValueVerifier.test(getRawText());
    }
}
