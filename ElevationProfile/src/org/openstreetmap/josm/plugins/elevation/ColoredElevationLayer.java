package org.openstreetmap.josm.plugins.elevation;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.BlockingDeque;

import javax.swing.Action;
import javax.swing.Icon;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.elevation.gui.ElevationColors;
import org.openstreetmap.josm.plugins.elevation.gui.Triangle;
import org.openstreetmap.josm.tools.ImageProvider;

public class ColoredElevationLayer extends Layer implements IEleRenderingListener {
    private ElevationRenderer renderer;
    

    public ColoredElevationLayer(String name) {
	super(name);

	// 
	setOpacity(0.5);
    }

    @Override
    public void paint(Graphics2D g, MapView mv, Bounds box) {
	if (renderer == null) {
	    renderer = new ElevationRenderer(getName(), box, this);
	    System.out.println("Start renderer...");
	    Main.worker.submit(renderer);
	}
	
	if (renderer.getVertices().size() > 0) {
	    BlockingDeque<EleVertex> list = renderer.getVertices();
	    for (EleVertex eleVertex : list) {
		Point p0 = mv.getPoint(eleVertex.get(0));
		Point p1 = mv.getPoint(eleVertex.get(1));
		Point p2 = mv.getPoint(eleVertex.get(2));
		Triangle t = new Triangle(p0, p1, p2);
		
		
		//g.setColor(Color.magenta);
		//g.draw(t);
		g.setColor(ElevationColors.getElevationColor(eleVertex.getEle()));
		g.fill(t);
	    }
	}
    }

    @Override
    public Icon getIcon() {	
	return ImageProvider.get("layer", "elevation_small");
    }

    @Override
    public String getToolTipText() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void mergeFrom(Layer from) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isMergable(Layer other) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void visitBoundingBox(BoundingXYVisitor v) {
	// TODO Auto-generated method stub

    }

    @Override
    public Object getInfoComponent() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Action[] getMenuEntries() {
	return new Action[] { new LayerListPopup.InfoAction(this) };
    }

    @Override
    public void finished(EleVertex vertex) {
	
    }

    @Override
    public void finishedAll() {
	Main.map.mapView.repaint();	
    }

}