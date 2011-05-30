// License: GPL. See LICENSE file for details.
package org.openstreetmap.josm.plugins.licensechange;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.visitor.AbstractVisitor;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.tools.GBC;

/**
 */
public class Check extends AbstractVisitor
{
    /** Name of the test */
    protected final String name;

    /** Description of the test */
    protected final String description;

    /** Whether this test is enabled. Enabled by default */
    protected boolean enabled = true;

    /** The preferences check for validation */
    protected JCheckBox checkEnabled;

    /** The preferences check for validation on upload */
    protected JCheckBox checkBeforeUpload;

    /** Whether this test must check before upload. Enabled by default */
    protected boolean testBeforeUpload = true;

    /** Whether this test is performing just before an upload */
    protected boolean isBeforeUpload;

    /** The list of errors */
    protected List<LicenseProblem> errors = new ArrayList<LicenseProblem>(30);

    /** the progress monitor to use */
    protected ProgressMonitor progressMonitor;
    /**
     * Constructor
     * @param name Name of the test
     * @param description Description of the test
     */
    public Check(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor
     * @param name Name of the test
     */
    public Check(String name)
    {
        this(name, null);
    }

    /**
     * Initializes any global data used this tester.
     * @param plugin The plugin
     * @throws Exception When cannot initialize the test
     */
    public void initialize(LicenseChangePlugin plugin) throws Exception {}

    /**
     * Start the test using a given progress monitor
     *
     * @param progressMonitor  the progress monitor
     */
    public void startCheck(ProgressMonitor progressMonitor) {
        if (progressMonitor == null) {
                this.progressMonitor = NullProgressMonitor.INSTANCE;
        } else {
                this.progressMonitor = progressMonitor;
        }
        this.progressMonitor.beginTask(tr("Running test {0}", name));
        errors = new ArrayList<LicenseProblem>(30);
    }

    /**
     * Gets the validation errors accumulated until this moment.
     * @return The list of errors
     */
    public List<LicenseProblem> getProblems()
    {
        return errors;
    }

    /**
     * Notification of the end of the test. The tester may perform additional
     * actions and destroy the used structures
     */
    public void endCheck() {
        progressMonitor.finishTask();
        progressMonitor = null;
    }

    /**
     * Visits all primitives to be tested. These primitives are always visited
     * in the same order: nodes first, then ways.
     *
     * @param selection The primitives to be tested
     */
    public void visit(Collection<OsmPrimitive> selection)
    {
        progressMonitor.setTicksCount(selection.size());
        for (OsmPrimitive p : selection) {
            if( p.isUsable() )
                p.visit(this);
            progressMonitor.worked(1);
        }
    }

    public void visit(Node n) {}

    public void visit(Way w) {}

    public void visit(Relation r) {}

    /**
     * Allow the tester to manage its own preferences
     * @param testPanel The panel to add any preferences component
     */
    public void addGui(JPanel testPanel)
    {
        checkEnabled = new JCheckBox(name, enabled);
        checkEnabled.setToolTipText(description);
        testPanel.add(checkEnabled, GBC.std());

        GBC a = GBC.eol();
        a.anchor = GridBagConstraints.EAST;
        checkBeforeUpload = new JCheckBox();
        checkBeforeUpload.setSelected(testBeforeUpload);
        testPanel.add(checkBeforeUpload, a);
    }

    /**
     * Called when the used submits the preferences
     */
    public boolean ok()
    {
        enabled = checkEnabled.isSelected();
        testBeforeUpload = checkBeforeUpload.isSelected();
        return false;
    }

    /**
     * Returns true if this plugin must check the uploaded data before uploading
     * @return true if this plugin must check the uploaded data before uploading
     */
    public boolean testBeforeUpload()
    {
        return testBeforeUpload;
    }

    /**
     * Sets the flag that marks an upload check
     * @param isUpload if true, the test is before upload
     */
    public void setBeforeUpload(boolean isUpload)
    {
        this.isBeforeUpload = isUpload;
    }
}