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
package com.jetbrains.python.debugger.concurrency.tool.asyncio;

import com.intellij.openapi.project.Project;
import com.intellij.ui.AppUIUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.XSourcePosition;
import com.jetbrains.python.debugger.concurrency.model.ConcurrencyGraphModel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyGraphSettings;
import com.jetbrains.python.debugger.concurrency.tool.panels.ConcurrencyPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableModel;

public class ConcurrencyTable extends JBTable {
  protected ConcurrencyGraphModel myGraphModel;
  protected Project myProject;
  protected ConcurrencyPanel myPanel;
  protected boolean myColumnsInitialized = false;

  public ConcurrencyTable(ConcurrencyGraphModel graphModel, Project project, ConcurrencyPanel panel) {
    super();
    myGraphModel = graphModel;
    myProject = project;
    myPanel = panel;
    setRowHeight(ConcurrencyGraphSettings.CELL_HEIGHT);
    setShowHorizontalLines(false);
  }

  protected void navigateToSource(final XSourcePosition sourcePosition) {
    if (sourcePosition != null) {
      AppUIUtil.invokeOnEdt(new Runnable() {
        @Override
        public void run() {
          sourcePosition.createNavigatable(myProject).navigate(true);
        }
      }, myProject.getDisposed());
    }
  }

  @Override
  public void setModel(@NotNull TableModel model) {
    super.setModel(model);
    if (!myColumnsInitialized) {
      myColumnsInitialized = true;
    }
  }
}
