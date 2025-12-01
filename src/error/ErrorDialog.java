package error;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * This class is a simple Dialog that pops up on Errors, that lists the error and has an OK button
 */
public class ErrorDialog extends Dialog {
    private Label errorLabel; 
    private Button ok = new Button("OK");
    private String message;
    private Window parent;

    public ErrorDialog(Frame f, String errorMessage) {
        super(f, "Error", true);
        this.message = errorMessage;
        this.parent = f;
        initialize();
    }

    public ErrorDialog(Dialog d, String errorMessage) {
        super(d, "Error", true);
        this.message = errorMessage;
        this.parent = d;
        initialize();
    }

    private void initialize() {
        errorLabel = new Label(message);
        setLayout(new FlowLayout());
        add(errorLabel);
        add(ok);

        ok.addActionListener((ae) -> {
            dispose();
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose(); 
            }
        });

        pack();
        setLocationRelativeTo(parent); 
        setVisible(true);
    }
}
