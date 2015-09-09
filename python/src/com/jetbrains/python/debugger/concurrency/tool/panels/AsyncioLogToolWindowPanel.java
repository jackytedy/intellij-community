
/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.debugger.concurrency.tool.panels;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyService;
import com.jetbrains.python.debugger.concurrency.model.ConcurrencyGraphModel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyStatisticsTable;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.table.AsyncioTable;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.table.AsyncioTableModel;

import javax.swing.*;
import java.awt.*;

public class AsyncioLogToolWindowPanel extends ConcurrencyPanel {
  private final Project myProject;
  private final ConcurrencyGraphModel myGraphModel;
  private JTable myTable;

  public AsyncioLogToolWindowPanel(Project project) {
    super(false, project);
    myProject = project;
    graphModel = PyConcurrencyService.getInstance(myProject).getAsyncioInstance();
    myGraphModel = new ConcurrencyGraphModel(myProject);

    myGraphModel.registerListener(new ConcurrencyGraphModel.GraphListener() {
      @Override
      public void graphChanged() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
          @Override
          public void run() {
            buildLog();
          }
        });
      }
    });

    initMessage();
    buildLog();
  }

  @Override
  public void initMessage() {
    removeAll();
    myLabel = new JLabel();
    myLabel.setHorizontalAlignment(JLabel.CENTER);
    myLabel.setVerticalAlignment(JLabel.CENTER);
    myLabel.setText("<html>The Asyncio log is empty. <br>" +
                    "Check the box \"Build diagram for concurrent programs\" " +
                    "in Settings | Build, Execution, Deployment | Python debugger</html>");
    add(myLabel);
  }

  @Override
  protected JPanel createToolbarPanel() {
    final DefaultActionGroup group = new DefaultActionGroup();
    group.add(new StatisticsAction());

    final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("Toolbar", group, false);
    final JPanel buttonsPanel = new JPanel(new BorderLayout());
    buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
    return buttonsPanel;
  }

  private class StatisticsAction extends AnAction implements DumbAware {
    public StatisticsAction() {
      super("Statistical info", "Show asyncio statistics", AllIcons.ToolbarDecorator.Analyze);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      final ConcurrencyGraphModel graphModel = PyConcurrencyService.getInstance(myProject).getAsyncioInstance();
      UIUtil.invokeLaterIfNeeded(new Runnable() {
        @Override
        public void run() {
          ConcurrencyStatisticsTable frame = new ConcurrencyStatisticsTable(graphModel);
          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        }
      });
    }

  }

  public void buildLog() {
    if (myGraphModel.getSize() == 0) {
      myTable = null;
      initMessage();
      return;
    }

    if (myTable == null) {
      myLabel.setVisible(false);
      myTable = new AsyncioTable(myGraphModel, myProject, this);
      myTable.setModel(new AsyncioTableModel(myGraphModel));
      myGraphPane = ScrollPaneFactory.createScrollPane(myTable);
      add(myGraphPane);
      setToolbar(createToolbarPanel());
    }
    myTable.setModel(new AsyncioTableModel(myGraphModel));
  }

  @Override
  public void dispose() {

  }
}
