package relcontext.actions;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.dialogs.relation.DownloadRelationMemberTask;
import org.openstreetmap.josm.gui.dialogs.relation.DownloadRelationTask;
import org.openstreetmap.josm.tools.ImageProvider;
import relcontext.ChosenRelation;
import relcontext.ChosenRelationListener;

/**
 * Downloads or updates chosen relation members, depending on completeness.
 * 
 * @author Zverik
 */
public class DownloadChosenRelationAction extends AbstractAction implements ChosenRelationListener {
    private ChosenRelation rel;

    public DownloadChosenRelationAction( ChosenRelation rel ) {
        super();
//        putValue(NAME, "D");
        putValue(SMALL_ICON, ImageProvider.get("relcontext", "download"));
        putValue(SHORT_DESCRIPTION, tr("Download incomplete members for the chosen relation"));
        this.rel = rel;
        rel.addChosenRelationListener(this);
        setEnabled(false);
    }

    public void actionPerformed( ActionEvent e ) {
        Relation relation = rel.get();
        if( relation == null || relation.isNew() ) return;
        int total = relation.getMembersCount();
        int incomplete = relation.getIncompleteMembers().size();
//        if( incomplete <= 5 || (incomplete <= 10 && incomplete * 3 < total) )
        if( incomplete <= 10 && incomplete * 3 < total )
            downloadIncomplete(relation);
        else
            downloadMembers(relation);
    }

    public void chosenRelationChanged( Relation oldRelation, Relation newRelation ) {
        boolean incomplete = false;
        if( newRelation != null ) {
            for( RelationMember m : newRelation.getMembers()) {
                if( m.getMember().isIncomplete() ) {
                    incomplete = true;
                    break;
                }
            }
        }
        setEnabled(newRelation != null && incomplete);
    }

    protected void downloadMembers( Relation rel ) {
        if( !rel.isNew() ) {
            Main.worker.submit(new DownloadRelationTask(Collections.singletonList(rel), Main.main.getEditLayer()));
        }
    }

    protected void downloadIncomplete( Relation rel ) {
        if( rel.isNew() ) return;
        Set<OsmPrimitive> ret = new HashSet<>();
        ret.addAll(rel.getIncompleteMembers());
        if( ret.isEmpty() ) return;
        Main.worker.submit(new DownloadRelationMemberTask(Collections.singletonList(rel), ret, Main.main.getEditLayer()));
    }
}
