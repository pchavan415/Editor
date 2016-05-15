// SimpleEditor.java
// An example showing several DefaultEditorKit features. This class is designed
// to be easily extended for additional functionality.
//
import javax.swing.*;
import java.util.regex.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.util.Hashtable;

public class Editor extends JFrame implements KeyListener {

	private Action openAction = new OpenAction();
	private Action saveAction = new SaveAction();
	public static JTextArea ta;
	private JTextComponent textComp;
	private Hashtable actionHash = new Hashtable();

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {

		String str = ta.getText();
		ta.setText(str);
		ta.setCaretPosition(str.length());
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			if(ta.getText().charAt(ta.getText().length() -2) == KeyEvent.VK_BRACELEFT){
			ta.append("    ");
			str = ta.getText();
			ta.setCaretPosition(str.length());
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public static void main(String[] args) throws InterruptedException {
		Editor editor = new Editor();
		editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		editor.setVisible(true);
		Font f = new Font("Serif", Font.BOLD, 24);  
		ta.setFont(f);
		ta.addKeyListener(editor);
		String[] regex = new String[9];
		regex[0] = "[m][a][i][n][(][)]";
		regex[1] = "[i][n][t]";
		regex[2] = "[d][o][u][b][l][e]";
		regex[3] = "[c][h][a][r]";
		regex[4] = "[f][o][r]";
		regex[5] = "[i][f]";
		regex[6] = "[e][l][s][e]";
		regex[7] = "[w][h][i][l][e]";
		regex[8] = "[p][r][i][n][t][f]";
		
		while (true) {
			Thread.sleep(20);

			String input = ta.getText();
			for (int i = 0; i < regex.length; i++) {
				Pattern p = Pattern.compile(regex[i]);
				Matcher m = p.matcher(input); // get a matcher object

				while (m.find()) {

					int p0 = m.start();
					int p1 = m.end();

					if (p0 != -1) {
						highlightText(p0, p1, Color.yellow);
					}
					p0 = -1;
				}
			}

		}
	}

	static void highlightText(int p0, int p1, Color c) {
		Highlighter highlighter = ta.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
				c);
		try {
			highlighter.addHighlight(p0, p1, painter);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Create an editor.
	public Editor() {
		super("Swing Editor");
		textComp = createTextComponent();
		makeActionsPretty();

		Container content = getContentPane();
		content.add(textComp, BorderLayout.CENTER);
		content.add(createToolBar(), BorderLayout.NORTH);
		setJMenuBar(createMenuBar());
		setSize(320, 240);

	}

	// Create the JTextComponent subclass.
	protected JTextComponent createTextComponent() {
		ta = new JTextArea();
		ta.setLineWrap(true);
		return ta;
	}

	// Add icons and friendly names to actions we care about.
	protected void makeActionsPretty() {
		Action a;
		a = textComp.getActionMap().get(DefaultEditorKit.cutAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("icons/cut.gif"));
		a.putValue(Action.NAME, "Cut");

		a = textComp.getActionMap().get(DefaultEditorKit.copyAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("icons/copy.gif"));
		a.putValue(Action.NAME, "Copy");

		a = textComp.getActionMap().get(DefaultEditorKit.pasteAction);
		a.putValue(Action.SMALL_ICON, new ImageIcon("icons/paste.gif"));
		a.putValue(Action.NAME, "Paste");

		a = textComp.getActionMap().get(DefaultEditorKit.selectAllAction);
		a.putValue(Action.NAME, "Select All");
	}

	// Create a simple JToolBar with some buttons.
	protected JToolBar createToolBar() {
		JToolBar bar = new JToolBar();

		// Add simple actions for opening & saving.
		bar.add(getOpenAction()).setText("");
		bar.add(getSaveAction()).setText("");
		bar.addSeparator();

		// Add cut/copy/paste buttons.
		bar.add(textComp.getActionMap().get(DefaultEditorKit.cutAction))
				.setText("");
		bar.add(textComp.getActionMap().get(DefaultEditorKit.copyAction))
				.setText("");
		bar.add(textComp.getActionMap().get(DefaultEditorKit.pasteAction))
				.setText("");
		return bar;
	}

	// Create a JMenuBar with file & edit menus.
	protected JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		menubar.add(file);
		menubar.add(edit);

		file.add(getOpenAction());
		file.add(getSaveAction());
		file.add(new ExitAction());
		edit.add(textComp.getActionMap().get(DefaultEditorKit.cutAction));
		edit.add(textComp.getActionMap().get(DefaultEditorKit.copyAction));
		edit.add(textComp.getActionMap().get(DefaultEditorKit.pasteAction));
		edit.add(textComp.getActionMap().get(DefaultEditorKit.selectAllAction));
		return menubar;
	}

	// Subclass can override to use a different open action.
	protected Action getOpenAction() {
		return openAction;
	}

	// Subclass can override to use a different save action.
	protected Action getSaveAction() {
		return saveAction;
	}

	protected JTextComponent getTextComponent() {
		return textComp;
	}

	// ********** ACTION INNER CLASSES ********** //

	// A very simple exit action
	public class ExitAction extends AbstractAction {
		public ExitAction() {
			super("Exit");
		}

		public void actionPerformed(ActionEvent ev) {
			System.exit(0);
		}
	}

	// An action that opens an existing file
	class OpenAction extends AbstractAction {
		public OpenAction() {
			super("Open", new ImageIcon("icons/open.gif"));
		}

		// Query user for a filename and attempt to open and read the file into
		// the
		// text component.
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(Editor.this) != JFileChooser.APPROVE_OPTION)
				return;
			File file = chooser.getSelectedFile();
			if (file == null)
				return;

			FileReader reader = null;
			try {
				reader = new FileReader(file);
				textComp.read(reader, null);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(Editor.this, "File Not Found",
						"ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException x) {
					}
				}
			}
		}
	}

	// An action that saves the document to a file
	class SaveAction extends AbstractAction {
		public SaveAction() {
			super("Save", new ImageIcon("icons/save.gif"));
		}

		// Query user for a filename and attempt to open and write the text
		// component’s content to the file.
		public void actionPerformed(ActionEvent ev) {
			JFileChooser chooser = new JFileChooser();
			if (chooser.showSaveDialog(Editor.this) != JFileChooser.APPROVE_OPTION)
				return;
			File file = chooser.getSelectedFile();
			if (file == null)
				return;

			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				textComp.write(writer);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(Editor.this, "File Not Saved",
						"ERROR", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException x) {
					}
				}
			}
		}
	}
}