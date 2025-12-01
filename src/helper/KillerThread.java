package helper;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Window;
import java.awt.EventQueue;

import gui.MainWindow;

/**
 * This class terminates its parent after certain time has passed since
 * the last user action. User actions include clicking buttons, being in a
 * dialogue, among other things.
 */
public class KillerThread extends Thread {
    
    MainWindow parent;
    int killTime;
    int dialogueTime;
    int timer = 0;
    boolean paused = false;
    private ReminderDialog reminder;
    
    /**
     * This class is a Dialog that pops up to remind the user to do something
     * or the program will terminate, contains a "Continue" button that will
     * dispose the dialog and reset the kill timer
     */
    class ReminderDialog extends Dialog {
        private Button ok = new Button("Continue");
        private Window parent;
        private Label errorLabel = new Label("The program will shut down in " + (killTime - timer) + " seconds, unless you do something :(");
        
        public ReminderDialog(Window owner, String title, ModalityType modalityType) {
            super(owner, title, modalityType);
            this.parent = owner;
            initialize();
        }
        
        public void initialize() {
            setLayout(new FlowLayout());
            add(errorLabel);
            add(ok);
            
            addWindowListener(new WindowAdapter() {
    			@Override
    			public void windowClosing(WindowEvent e) {
    				userAction();
    				dispose();
    			}
    			});
    
            ok.addActionListener((ae) -> {
                dispose();
                EventQueue.invokeLater(() -> {
                    KillerThread.this.userAction();
                });
            });
            
            pack();
            setLocationRelativeTo(parent);
        }
        
        private void updateLabel() {
            errorLabel.setText("The program will shut down in " + (killTime - timer) + " seconds, unless you do something :(");
        }
        
    }
    
    /**
     * @param parent Parent that will be terminated
     * @param killTime Time after which to terminate
     * @param dialogueTime Time when the user gets a pop-up Dialog to do something
     */
    public KillerThread(MainWindow parent, int killTime, int dialogueTime) {
        this.parent = parent;
        this.killTime = killTime;
        this.dialogueTime = dialogueTime;
        this.start();
    }
    
    public void pause() {
        paused = true;
    }
    
    public void unpause() {
        paused = false;
    }
    
    public void userAction() {
        timer = 0;
    }
    
    /**
     * Check if enough time has passed between user actions to terminate 
     * the parent, or pop up a reminder to the user to do something
     */
    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                if (!paused) {
                    if (timer == killTime) {
                        EventQueue.invokeLater(() -> parent.quit());
                        break; 
                    }
                    if (timer == dialogueTime) {
                        EventQueue.invokeLater(() -> {
                            reminder = new ReminderDialog(parent, "Are you still here?", ModalityType.APPLICATION_MODAL);
                            reminder.setVisible(true);
                        });
                    }
                    if (timer > dialogueTime) {
                        if (reminder != null) {
                                reminder.updateLabel();
                        }                      
                    }
                    sleep(1000);
                    timer++;
                }
            }
        } catch (InterruptedException e) {
            if (reminder != null && reminder.isDisplayable()) {
                EventQueue.invokeLater(() -> reminder.dispose());
            }
        }
    }
}