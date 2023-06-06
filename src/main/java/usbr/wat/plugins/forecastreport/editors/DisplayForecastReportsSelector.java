/*
 *
 *  * Copyright 2023 United States Bureau of Reclamation (USBR).
 *  * United States Department of the Interior
 *  * All Rights Reserved. USBR PROPRIETARY/CONFIDENTIAL.
 *  * Source may not be released without written approval
 *  * from USBR
 *
 */

package usbr.wat.plugins.forecastreport.editors;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.rma.io.FileManagerImpl;
import hec2.wat.model.WatSimulation;
import rma.swing.ButtonCmdPanel;
import rma.swing.ButtonCmdPanelListener;
import rma.swing.RmaInsets;
import rma.swing.RmaJDialog;
import rma.swing.RmaJTable;
import rma.swing.tree.LabelIconObject;
import usbr.wat.plugins.actionpanel.ActionsWindow;
import usbr.wat.plugins.actionpanel.io.ReportOptions;
import usbr.wat.plugins.actionpanel.model.ForecastReportingPlugin;
import usbr.wat.plugins.actionpanel.model.ReportPlugin;
import usbr.wat.plugins.actionpanel.model.ReportsManager;
import usbr.wat.plugins.actionpanel.model.SimulationReportInfo;
import usbr.wat.plugins.actionpanel.model.forecast.EnsembleSet;
import usbr.wat.plugins.actionpanel.model.forecast.ForecastSimGroup;
import usbr.wat.plugins.actionpanel.ui.UsbrPanel;


import static rma.swing.ButtonCmdPanel.CANCEL_BUTTON;
import static rma.swing.ButtonCmdPanel.OK_BUTTON;

public class DisplayForecastReportsSelector extends RmaJDialog
{
	private final ActionsWindow _parent;
	private final UsbrPanel _parentPanel;
	private RmaJTable _ensembleTable;
	private ButtonCmdPanel _cmdPanel;
	private JButton _selectAllBtn;
	private JButton _unselectAllBtn;

	public DisplayForecastReportsSelector(ActionsWindow parent, UsbrPanel parentPanel)
	{
		super(parent, false);
		_parent =parent;
		_parentPanel = parentPanel;
		buildControls();
		addListeners();
		pack();
		setSize(500,500);
		setLocationRelativeTo(getParent());
	}

	private void buildControls()
	{
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new GridBagLayout());
		setTitle("Select Ensemble Sets to Report on");

		String[] headers = new String[]{"Select", "Simulation","Ensemble Set", "Computed Members", "Members for Report"};
		_ensembleTable = new RmaJTable(this, headers)
		{
			public boolean isCellEditable(int row, int column)
			{
				return column == 3;
			}

		};

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = RmaInsets.INSETS5505;
		add(_ensembleTable.getScrollPane(), gbc);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = RmaInsets.INSETS5505;
		add(buttonPanel, gbc);

		_selectAllBtn = new JButton("Select All");
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = RmaInsets.INSETS5505;
		buttonPanel.add(_selectAllBtn, gbc);

		_unselectAllBtn = new JButton("Unselect All");
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = RmaInsets.INSETS5505;
		buttonPanel.add(_unselectAllBtn, gbc);

		_cmdPanel = new ButtonCmdPanel(ButtonCmdPanel.OK_CANCEL_BUTTONS);
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = RmaInsets.INSETS5505;
		add(_cmdPanel, gbc);
	}

	private void addListeners()
	{
		_cmdPanel.addCmdPanelListener(new ButtonCmdPanelListener()
		{
			public void buttonCmdActionPerformed(ActionEvent e)
			{
				switch (e.getID())
				{
					case OK_BUTTON:
						if ( createReport())
						{
							setVisible(false);
						}
						break;
					case CANCEL_BUTTON:
						setVisible(false);
						break;
				}
			}
		});
	}
	public void setVisible(boolean visible)
	{
		if ( visible )
		{
			if ( !checkSims())
			{
				return;
			}
		}
		super.setVisible(visible);
		if ( !visible)
		{
			//saveSelectedReports();
		}
	}
	private boolean checkSims()
	{
		if ( _parent.getSimulationGroup() == null )
		{
			JOptionPane.showMessageDialog(_parent,"Please create or select a Simulation Group first",
					"No Simulation Group Selected", JOptionPane.INFORMATION_MESSAGE);
			return false;

		}

		List<SimulationReportInfo> sims = _parentPanel.getSimulationReportInfos();

		SimulationReportInfo sri;
		String folder;

		Iterator<SimulationReportInfo> iter = sims.iterator();
		while ( iter.hasNext())
		{
			sri = iter.next();
			folder = sri.getSimFolder();
			if ( !FileManagerImpl.getFileManager().fileExists(folder))
			{
				final SimulationReportInfo fSri = sri;
				EventQueue.invokeLater(()->JOptionPane.showMessageDialog(_parent, "Simulation "+fSri.getSimulation().getName()
						+" has no results so no report can be created for it.","No Results", JOptionPane.INFORMATION_MESSAGE));
				iter.remove();
			}
		}

		if ( sims.isEmpty())
		{
			EventQueue.invokeLater(()->JOptionPane.showMessageDialog(_parent,"There are no Simulations with results selected to create reports for",
					"No Simulations Selected", JOptionPane.INFORMATION_MESSAGE));
			return false;
		}
		fillForm(sims);
		return true;
	}
	private void fillForm(List<SimulationReportInfo> sims)
	{
		_ensembleTable.deleteCells();
		List<ReportPlugin> plugins = ReportsManager.getPlugins();
		boolean canBeComparisionReport = sims.size()>1;
		ReportPlugin plugin;
		ForecastSimGroup simGroup = (ForecastSimGroup) _parent.getSimulationGroup();
		Vector<Object> row;
		for(int s = 0;s < sims.size();s++ )
		{
			SimulationReportInfo sim = sims.get(s);
			WatSimulation simulation = sim.getSimulation();
			List<EnsembleSet> esets = simGroup.getEnsembleSets(simulation);
			for (int e = 0;e < esets.size(); e++ )
			{
				row = new Vector<>();
				row.add(Boolean.FALSE);
				EnsembleSet eset = esets.get(e);
				row.add(simulation);
				row.add(eset);
				row.add("");
				row.add(eset.getComputedMembers());

				_ensembleTable.appendRow(row);

			}
		}


		//updateCreateReportButtonState();
	}
	private boolean createReport()
	{
		List<ReportPlugin> plugins = ReportsManager.getPlugins();
		for(int i = 0;i < plugins.size(); i++ )
		{
			if ( plugins.get(i) instanceof ForecastReportingPlugin)
			{
				//((ForecastReportingPlugin)plugins.get(i)).createReport(List< SimulationReportInfo > sris, ReportOptions options);
			}
		}
		return true;
	}

}
