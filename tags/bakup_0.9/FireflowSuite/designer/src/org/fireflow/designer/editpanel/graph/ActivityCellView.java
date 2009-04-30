package org.fireflow.designer.editpanel.graph;

import cn.bestsolution.tools.resourcesmanager.util.ImageLoader;
import java.beans.PropertyVetoException;
import javax.swing.event.ListSelectionEvent;
import org.fireflow.model.net.Activity;
import org.jgraph.graph.VertexView;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import java.awt.Component;
import javax.swing.*;
import java.awt.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.HashMap;
import javax.swing.*;

import javax.swing.border.LineBorder;

import javax.swing.event.ListSelectionListener;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.datamodel.element.TaskElement;
import org.fireflow.designer.simulation.SimulatorPanel;
import org.fireflow.model.Task;
import org.jgraph.*; //import org.jgraph.graph
import org.jgraph.graph.*;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

public class ActivityCellView extends VertexView {

    private static ActivityRenderer renderer = new ActivityRenderer();
    private static RealCellEditor editor = new RealCellEditor();

    public ActivityCellView(Object v) {
        super(v);

    }

    public GraphCellEditor getEditor() {
        return editor;
    }

    public static class RealCellEditor extends AbstractCellEditor implements
            GraphCellEditor {

        public Object getCellEditorValue() {

            return null;
        }

        public Component getGraphCellEditorComponent(JGraph arg0, Object arg1, boolean arg2) {

            DefaultGraphCell cell = (DefaultGraphCell) arg1;
            CellView cellView = arg0.getGraphLayoutCache().getMapping(cell, false);
            Rectangle2D rect = cellView.getBounds();
            AbstractNode activityElement = (AbstractNode) cell.getUserObject();

            Color backgroudColor = arg0.getBackground();
            if (backgroudColor == null) {
                backgroudColor = Color.WHITE;
            }
            Color foregroundColor = GraphConstants.getForeground(cellView.getAllAttributes());
            if (foregroundColor == null) {
                foregroundColor = Color.BLACK;
            }
            Color borderColor = GraphConstants.getBorderColor(cellView.getAllAttributes());
            if (borderColor == null) {
                borderColor = Color.BLACK;
            }

            HashMap taskColorProps = new HashMap();
            if (arg0 instanceof SimulatorPanel) {
                taskColorProps = ((SimulatorPanel) arg0).getTaskColorProps();
            }

            ActivityComponent comp = new ActivityComponent(((ProcessGraphModel) arg0.getModel()).getExplorerManager(),
                    activityElement, backgroudColor, foregroundColor, borderColor, taskColorProps);
            comp.setPreferredSize(new Dimension((int) rect.getWidth(), (int) rect.getHeight()));
            return comp;

        }
    }

    @Override
    public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
        if (getRenderer() instanceof ActivityRenderer) {
            return ((ActivityRenderer) getRenderer()).getPerimeterPoint(this,
                    source, p);
        }
        return super.getPerimeterPoint(edge, source, p);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class ActivityRenderer implements CellViewRenderer {

        transient protected boolean hasFocus,  selected,  preview,  childrenSelected;
        transient protected Color gradientColor = null,  gridColor = Color.black,  highlightColor = Color.black,  lockedHandleColor = Color.black,  backgroudColor = Color.white,  foregroundColor = Color.black,  borderColor = Color.black;

        public ActivityRenderer() {
        // this.setLayout(new BorderLayout());
        }

        public Point2D getPerimeterPoint(VertexView view, Point2D source,
                Point2D p) {
            Rectangle2D bounds = view.getBounds();
            double x = bounds.getX();
            double y = bounds.getY();
            double width = bounds.getWidth();
            double height = bounds.getHeight();
            double xCenter = x + width / 2;
            double yCenter = y + height / 2;
            double dx = p.getX() - xCenter; // Compute Angle
            double dy = p.getY() - yCenter;
            double alpha = Math.atan2(dy, dx);
            double xout = 0, yout = 0;
            double pi = Math.PI;
            double pi2 = Math.PI / 2.0;
            double beta = pi2 - alpha;
            double t = Math.atan2(height, width);
            if (alpha < -pi + t || alpha > pi - t) { // Left edge
                xout = x;
                yout = yCenter - width * Math.tan(alpha) / 2;
            } else if (alpha < -t) { // Top Edge
                yout = y;
                xout = xCenter - height * Math.tan(beta) / 2;
            } else if (alpha < t) { // Right Edge
                xout = x + width;
                yout = yCenter + width * Math.tan(alpha) / 2;
            } else { // Bottom Edge
                yout = y + height;
                xout = xCenter + height * Math.tan(beta) / 2;
            }
            return new Point2D.Double(xout, yout);
        }

        public Component getRendererComponent(JGraph graph, CellView view,
                boolean sel, boolean focus, boolean preview) {
            Object cell = view.getCell();
            if (!(cell instanceof DefaultGraphCell)) {
                return null;
            }

            DefaultGraphCell graphCell = (DefaultGraphCell) cell;
            if (!(graphCell.getUserObject() instanceof AbstractNode)) {
                return null;
            }
            AbstractNode activityElem = (AbstractNode) graphCell.getUserObject();

            selected = sel;
            hasFocus = focus;
            backgroudColor = graph.getBackground();
            if (backgroudColor == null) {
                backgroudColor = Color.WHITE;
            }
            foregroundColor = GraphConstants.getForeground(view.getAllAttributes());
            if (foregroundColor == null) {
                foregroundColor = Color.BLACK;
            }
            borderColor = GraphConstants.getBorderColor(view.getAllAttributes());
            if (borderColor == null) {
                borderColor = Color.BLACK;
            }

            HashMap taskColorProps = new HashMap();
            if (graph instanceof SimulatorPanel) {
                taskColorProps = ((SimulatorPanel) graph).getTaskColorProps();
            }
            ActivityComponent activityComponent = new ActivityComponent(((ProcessGraphModel) graph.getModel()).getExplorerManager(),
                    activityElem, backgroudColor, foregroundColor, borderColor, taskColorProps);

            return activityComponent;
        }
        }
    }

class ActivityComponent extends JPanel implements ListSelectionListener {

    AbstractNode activityElement = null;
    Activity activity = null;
    JList tasklistComponent = null;
    ExplorerManager explorerManager = null;

    public void valueChanged(ListSelectionEvent e) {
        try {
            TaskElement taskElement = (TaskElement) tasklistComponent.getSelectedValue();
//            int idx = e.getFirstIndex();
//            TaskElement taskElement = (TaskElement) tasklistComponent.getModel().getElementAt(idx);
            explorerManager.setSelectedNodes(new Node[]{taskElement});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public ActivityComponent(ExplorerManager explorerMgr, AbstractNode argActElm, Color backgroundColor, Color foregroundColor, Color borderColor, final HashMap taskColorProps) {
        explorerManager = explorerMgr;
        activityElement = argActElm;
        activity = activityElement.getLookup().lookup(Activity.class);

        this.setLayout(new BorderLayout());
        this.setBackground(backgroundColor);
        this.setForeground(foregroundColor);

        JLabel activityNameLabel = new JLabel(activityElement.getDisplayName(),
                SwingConstants.CENTER);
        activityNameLabel.setBackground(backgroundColor);
        activityNameLabel.setForeground(foregroundColor);

        OptionalLineBorder border = new OptionalLineBorder(borderColor,
                false, false, true, false);
        activityNameLabel.setBorder(border);

        LineBorder lineBorder = new LineBorder(borderColor);
        this.setBorder(lineBorder);
        this.add(activityNameLabel, BorderLayout.NORTH);


        if (activityElement.getChildren() == null || activityElement.getChildren().getNodesCount() == 0) {
            JLabel noTasksLabel = new JLabel("", SwingConstants.CENTER);//<空环节>
            noTasksLabel.setBackground(backgroundColor);
            noTasksLabel.setForeground(foregroundColor);
            this.add(noTasksLabel, BorderLayout.CENTER);
        } else {

            tasklistComponent = new JList(activityElement.getChildren().getNodes());

            tasklistComponent.setModel(new AbstractListModel() {

                public int getSize() {
                    return activityElement.getChildren().getNodes().length;
                }

                public Object getElementAt(int index) {
                    return activityElement.getChildren().getNodes()[index];
                }
            });
            tasklistComponent.setCellRenderer(new DefaultListCellRenderer() {

                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component retValue = super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus);
                    IFPDLElement taskElement = (IFPDLElement) value;

                    Task task = (Task) taskElement.getContent();
                    if (taskColorProps.get(task.getId()) != null) {
                        retValue.setForeground((Color) taskColorProps.get(task.getId()));
                    } else {
                        retValue.setForeground(Color.black);
                    }

                    if (taskElement.getElementType().equals(IFPDLElement.FORM_TASK)) {
                        setIcon(ImageLoader.getImageIcon("manual_task_16.gif"));
                    } else if (taskElement.getElementType().equals(IFPDLElement.SUBFLOW_TASK)) {
                        setIcon(ImageLoader.getImageIcon("subflow_task_16.gif"));
                    } else if (taskElement.getElementType().equals(IFPDLElement.TOOL_TASK)) {
                        setIcon(ImageLoader.getImageIcon("tool_task_16.gif"));
                    }
//                    EmptyBorder border = new EmptyBorder(2, 4, 1, 4);
//                    label.setBorder(border);
                    return retValue;
                }
            });

            tasklistComponent.addListSelectionListener(this);

            this.add(tasklistComponent, BorderLayout.CENTER);
        }
    }
}


