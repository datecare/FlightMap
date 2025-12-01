package helper;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InfoDialog extends Dialog {
    private Label errorLabel; 
    private Button ok = new Button("OK");
    private String message;
    private Window parent;

    public InfoDialog(Frame f, String errorMessage) {
        super(f, "Error", true);
        this.message = errorMessage;
        this.parent = f;
        initialize();
    }

    public InfoDialog(Window w, String infoMessage) {
        super(w, "Info");
        this.message = infoMessage;
        this.parent = w;
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
