/*
 * Copyright 2009-2011 Jean-Rémy Falleri
 * 
 * This file is part of Popsycle.
 * Popsycle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Popsycle is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Popsycle.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.labri.popsycle.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.zest.core.widgets.*;
import org.eclipse.zest.layouts.*;
import org.eclipse.zest.layouts.algorithms.*;

import fr.labri.popsycle.model.JClass;
import fr.labri.popsycle.model.JClassDep;
import fr.labri.popsycle.model.JClassGroup;
import fr.labri.popsycle.model.JDepKind;
import fr.labri.popsycle.model.utils.JavaUtils;

public class JClassGroupUI extends Composite implements SelectionListener {

	@SuppressWarnings("unused")
	private JClassGroup clsGrp;

	private static Color defaultColor = ColorConstants.lightGreen;

	private static Color selectedColor = ColorConstants.red;

	private Graph graph;

	private boolean compact;

	public JClassGroupUI(Composite parent,int style,boolean flat) {
		super(parent,style);
		this.setLayout(new FillLayout());
		this.compact = flat;
	}

	public void setJClassGroup(JClassGroup clsGrp) {
		this.clsGrp = clsGrp;
		graph = new Graph(this,SWT.NONE);
		Map<JClass,GraphNode> nodeMap = new HashMap<JClass, GraphNode>();
		graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		for( JClass cls: clsGrp.getJClasses() ) {
			GraphNode n = new GraphNode(graph,SWT.NONE,JavaUtils.getSimpleClassName(cls.getName()));
			n.setTooltip(new Label(cls.getName()));
			nodeMap.put(cls,n);
			n.setHighlightColor(selectedColor);
			n.unhighlight();
			unhighlight(n);
		}

		for( JClass cls: clsGrp.getJClasses() ) {
			for ( JClassDep dep: cls.getJClassDeps() ) {
				JClass adjCls = dep.getTarget();
				GraphConnection gc = new GraphConnection(graph,ZestStyles.CONNECTIONS_SOLID,nodeMap.get(cls),nodeMap.get(adjCls));
				gc.setTooltip(new Label(dep.toString()));
				setStyle(gc, dep.getWorseDepKind());
			}
		}

		if ( compact ) {
			graph.setLayoutAlgorithm((new
					CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new
							LayoutAlgorithm[] { new
							DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), new
							HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS) })),true);
		}
		else {
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS),true);
		}
		//graph.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		//graph.setLayoutAlgorithm(new (LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		//graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),true);

		//graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),true);

		//graph.setLayoutAlgorithm(new VerticalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),true);

		graph.addSelectionListener(this);
	}

	private void setStyle(GraphConnection gc,JDepKind depKind) {
		if ( depKind == JDepKind.REF ) {
			gc.setLineWidth(1);
			gc.setLineColor(ColorConstants.gray);
		}
		else if ( depKind == JDepKind.ITF ) {
			gc.setLineWidth(2);
			gc.setLineColor(ColorConstants.black);
		}
		else {
			gc.setLineWidth(3);
			gc.setLineColor(ColorConstants.red);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@SuppressWarnings("unchecked")
	@Override
	public void widgetSelected(SelectionEvent e) {
		Set<GraphNode> sNodes = new HashSet<GraphNode>();
		Set<GraphNode> cNodes = new HashSet<GraphNode>();
		for(Object o: graph.getSelection() ) {
			if ( o instanceof GraphNode )
				sNodes.add((GraphNode) o);
		}

		for(GraphNode s: sNodes) {
			highlight(s);
			for(GraphConnection c: (List<GraphConnection>) s.getSourceConnections()) {
				cNodes.add(c.getDestination());
				highlight(c.getDestination());
			}
			for(GraphConnection c: (List<GraphConnection>) s.getTargetConnections()) {
				cNodes.add(c.getSource());
				highlight(c.getSource());
			}
		}

		for(GraphNode n: (List<GraphNode>) graph.getNodes())
			if ( !sNodes.contains(n) && !cNodes.contains(n))
				unhighlight(n);

	}

	private static void highlight(GraphNode n) {
		n.setBackgroundColor(selectedColor);
	}

	private static void unhighlight(GraphNode n) {
		n.setBackgroundColor(defaultColor);
	}

}
