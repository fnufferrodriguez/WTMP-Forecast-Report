/*
 *
 *  * Copyright 2023 United States Bureau of Reclamation (USBR).
 *  * United States Department of the Interior
 *  * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 *  * Source may not be released without written approval
 *  * from USBR
 *
 */

package usbr.wat.plugins.forecastreport.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import usbr.wat.plugins.actionpanel.ActionsWindow;
import usbr.wat.plugins.actionpanel.ui.UsbrPanel;
import usbr.wat.plugins.forecastreport.editors.DisplayForecastReportsSelector;

public class DisplayEnsembleSelectorAction extends AbstractAction
{
	private final ActionsWindow _parent;
	private final UsbrPanel _parentPanel;
	private DisplayForecastReportsSelector _selector;

	public DisplayEnsembleSelectorAction(ActionsWindow parent, UsbrPanel parentPanel)
	{
		super("Create Report...");
		setEnabled(false);
		_parent = parent;
		_parentPanel = parentPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		displayReportSelector();
	}
	private void displayReportSelector()
	{
		if ( _selector == null )
		{
			_selector = new DisplayForecastReportsSelector(_parent,_parentPanel);
		}
		_selector.setVisible(true);
	}

}
