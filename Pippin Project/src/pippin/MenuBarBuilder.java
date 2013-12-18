package pippin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBarBuilder implements Observer {
    private JMenuItem assemble = new JMenuItem("Assemble Source...");
    private JMenuItem load = new JMenuItem("Load Program...");
    private JMenuItem exit = new JMenuItem("Exit");
    private JMenuItem go = new JMenuItem("Go");
    private GUIMachine machine;

    public MenuBarBuilder(GUIMachine machine) {
        this.machine = machine;
        machine.addObserver(this);
    }

    public JMenu createMenu1() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        assemble.setMnemonic(KeyEvent.VK_S);
        assemble.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.CTRL_MASK));
        assemble.addActionListener(new AssembleListener());
        menu.add(assemble);
        load.setMnemonic(KeyEvent.VK_L);
        load.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, InputEvent.CTRL_MASK));
        load.addActionListener(new LoadListener());
        menu.add(load);
        
// add the JMenuItem load, with mnemonic L, accelerator CTRL+L, and listener LoadListener()

        menu.addSeparator(); // puts a line across the menu
        exit.setMnemonic(KeyEvent.VK_Q);
        exit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        exit.addActionListener(new ExitListener());
        menu.add(exit);
// add the JMenuItem exit, with mnemonic E, accelerator CTRL+E, and listener ExitListener()

        return menu;
    }

    public JMenu createMenu2() {
// follow the structure of createMenu1: create a JMenu labeled "Execute" with the mnemonic X
// set up the go menu item with the accelerator CTRL-G, mnemonic G, listener ExecuteListener and add it to the menu
    	JMenu menu = new JMenu("Execute");
    	menu.setMnemonic(KeyEvent.VK_X);
    	go.setMnemonic(KeyEvent.VK_G);
    	go.setAccelerator(KeyStroke.getKeyStroke(
    			KeyEvent.VK_G, InputEvent.CTRL_MASK));
    	go.addActionListener(new ExecuteListener());
    	menu.add(go);
    	
        return menu;
    }

    public void checkEnabledMenus() {
        assemble.setEnabled(machine.getState().getAssembleFileActive());
        load.setEnabled(machine.getState().getLoadFileActive());
        go.setEnabled(machine.getState().getStepActive());
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        checkEnabledMenus();
    }

    class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            machine.exit();
        }
    }

    class LoadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            machine.loadFile();
        }
    }

    class AssembleListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			machine.assembleFile();
			
		}
    // calls assembleFile in machine
    }

    class ExecuteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			machine.execute();
		}
    // calls execute in machine
    }
}