package visual;

import java.net.MalformedURLException;
import java.net.URL;

import logica.AgenteBusqueda;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AgentsWindows {

	protected Shell shell;
	private Text txtUrl;
	private Text txtKeywords;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AgentsWindows window = new AgentsWindows();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		Label lurl = new Label(shell, SWT.NONE);
		lurl.setText("URL");
		lurl.setLocation(20, 20);
		lurl.pack();

		Label lkeywords = new Label(shell, SWT.NONE);
		lkeywords.setText("KeyWords");
		lkeywords.setLocation(20, 50);
		lkeywords.pack();

		txtUrl = new Text(shell, SWT.BORDER);
		txtUrl.setBounds(100, 17, 288, 19);

		txtKeywords = new Text(shell, SWT.BORDER);
		txtKeywords.setBounds(100, 44, 147, 19);

		final List list = new List(shell, SWT.BORDER);
		list.setBounds(100, 69, 147, 68);

		Button btnBorrar = new Button(shell, SWT.NONE);
		btnBorrar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				list.remove(list.getSelectionIndex());
			}
		});
		btnBorrar.setBounds(253, 69, 68, 23);
		btnBorrar.setText("Borrar");

		Button btnAnadir = new Button(shell, SWT.NONE);
		btnAnadir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!txtKeywords.getText().equals("")) {
					list.add(txtKeywords.getText());
					txtKeywords.setText("");
				}

			}
		});
		btnAnadir.setBounds(253, 42, 68, 23);
		btnAnadir.setText("A\u00F1adir");

		Button btnBuscar = new Button(shell, SWT.NONE);
		btnBuscar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AgenteBusqueda agent;
				try {
					String url = txtUrl.getText();
					if (url.startsWith("www")) {
						url = "http://" + url;
					}
					agent = new AgenteBusqueda("1", new URL(
							url), list.getItems());
					agent.start();
				} catch (MalformedURLException e1) {
					MessageDialog.openError(shell, "Error", "La URL no está bien formada");
					e1.printStackTrace();
				}

				
			}
		});
		btnBuscar.setBounds(179, 164, 68, 23);
		btnBuscar.setText("Buscar");

	}
}
