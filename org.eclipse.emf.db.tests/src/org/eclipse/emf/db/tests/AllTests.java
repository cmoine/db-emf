package org.eclipse.emf.db.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ org.eclipse.emf.db.tests.core.AllTests.class, org.eclipse.emf.db.tests.query.AllTests.class })
public class AllTests {

}
