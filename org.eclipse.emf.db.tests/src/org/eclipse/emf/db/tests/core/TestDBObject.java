package org.eclipse.emf.db.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.db.tests.model.Child;
import org.eclipse.emf.db.tests.model.ModelFactory;
import org.eclipse.emf.db.tests.model.Root;
import org.eclipse.emf.db.tests.model.SubRoot;
import org.junit.Test;

public class TestDBObject {
    @Test
    public void add_01_0n() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        Child child=ModelFactory.eINSTANCE.createChild();
        root.getChildren().add(child);
        assertNotNull("Inverse Add not made", child.getRoot());
    }

    @Test
    public void add_01_0n_bis() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        Child child=ModelFactory.eINSTANCE.createChild();
        child.setRoot(root);
        assertEquals("Inverse Add not made", 1, root.getChildren().size());
    }

    @Test
    public void add_01_01() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        SubRoot subRoot=ModelFactory.eINSTANCE.createSubRoot();
        root.setSubRoot(subRoot);
        assertNotNull("Inverse Add not made", subRoot.getRoot());
    }

    @Test
    public void add_01_01_bis() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        SubRoot subRoot=ModelFactory.eINSTANCE.createSubRoot();
        subRoot.setRoot(root);
        assertNotNull("Inverse Add not made", root.getSubRoot());
    }

    @Test
    public void remove_01_0n() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        Child child=ModelFactory.eINSTANCE.createChild();
        root.getChildren().add(child);
        child.setRoot(null);
        assertEquals("Inverse Remove not made", 0, root.getChildren().size());
    }

    @Test
    public void remove_01_0n_bis() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        Child child=ModelFactory.eINSTANCE.createChild();
        child.setRoot(root);
        root.getChildren().remove(child);
        assertNull("Inverse Remove not made", child.getRoot());
    }

    @Test
    public void remove_01_01() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        SubRoot subRoot=ModelFactory.eINSTANCE.createSubRoot();
        root.setSubRoot(subRoot);
        subRoot.setRoot(null);
        assertNull("Inverse Remove not made", root.getSubRoot());
    }

    @Test
    public void remove_01_01_bis() {
        Root root=ModelFactory.eINSTANCE.createRoot();
        SubRoot subRoot=ModelFactory.eINSTANCE.createSubRoot();
        subRoot.setRoot(root);
        root.setSubRoot(null);
        assertNull("Inverse Remove not made", subRoot.getRoot());
    }

    @Test
    public void reparent() {
        Root root1=ModelFactory.eINSTANCE.createRoot();
        Root root2=ModelFactory.eINSTANCE.createRoot();
        Child child=ModelFactory.eINSTANCE.createChild();
        child.setRoot(root1);
        child.setRoot(root2); // Change parent
        assertTrue("Reparent not made", root1.getChildren().isEmpty());
    }

    @Test
    public void reparentExt() {
        Root root1=ModelFactory.eINSTANCE.createRoot();
        Root root2=ModelFactory.eINSTANCE.createRoot();
        Child child=ModelFactory.eINSTANCE.createChild();
        Child child2=ModelFactory.eINSTANCE.createChild();
        // First data
        root1.getChildren().add(child);
        root2.getChildren().add(child2);

        // Merge
        root1.getChildren().clear();
        root1.getChildren().add(child);
        root1.getChildren().add(child2);
        assertTrue("Reparent not made", root2.getChildren().isEmpty());
    }

}
