package ahrd.test;

import static ahrd.controller.Settings.getSettings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ahrd.controller.Settings;

public class SettingsTest {

	@Before
	public void setUp() throws IOException {
		TestUtils.initTestSettings();
	}

	@Test
	public void testAhrdLoadsYamlInput() throws IOException {
		assertTrue(getSettings() instanceof Settings);
		assertNotNull("Input-Map should have proteins_fasta", getSettings()
				.getProteinsFasta());
		assertEquals(3.0, getSettings().getBlastDatabases().size(), 0.0);
		assertNotNull(getSettings().getBlastDatabases().contains("swissprot"));
		assertNotNull(getSettings().getBlastDatabases().contains("tair"));
		assertNotNull(getSettings().getBlastDatabases().contains("trembl"));
		assertNotNull(getSettings().getTokenBlackList("swissprot"));
		assertNotNull(getSettings().getTokenBlackList("tair"));
		assertNotNull(getSettings().getTokenBlackList("trembl"));
		assertEquals(
				"Test-Temperature in input.yml is set to 10 and should have been initialized correctly.",
				new Integer(10), getSettings().getTemperature());
	}

	@Test
	public void testClone() {
		Settings s = getSettings();
		s.setAvgEvaluationScore(0.8);
		Settings c = s.clone();
		// test
		assertTrue("A clone should not be it's 'parent'.", s != c);
		// All primitive types get automatically cloned by Object.clone(),
		// so just test the Maps:
		for (String blastDb : s.getBlastDatabases()) {
			// For some very strange reason, calls to the getter
			// 'getBlastDbWeight(String blastDatabaseName)' return
			// Integer-Object, as in this case Integer.parseInt returns the same
			// object for the equal int-values. Maybe this is some optimization
			// of Memory-Usage in the JVM? - Anyway, this is the reason, why
			// here we compare the original Strings, held in the Map
			// blastDbSettings:
			assertTrue(
					"The BlastDatabaseWeights should not be the same objects.",
					(s.getBlastDbSettings().get(blastDb)
							.get(Settings.BLAST_DB_WEIGHT_KEY) != c
							.getBlastDbSettings().get(blastDb)
							.get(Settings.BLAST_DB_WEIGHT_KEY)));
			assertTrue(
					"The BlastDatabase's DescriptionScoreBitScoreWeight should not be the same objects.",
					System.identityHashCode(s
							.getDescriptionScoreBitScoreWeight(blastDb)) != System.identityHashCode(c
							.getDescriptionScoreBitScoreWeight(blastDb)));
		}
		// Test passing on the average evaluation score:
		assertEquals(s.getAvgEvaluationScore(), c.getAvgEvaluationScore(), 0.0);
		// Assure they are different Objects. As the operator != does not reveal
		// this, we set the clone to a different value than its parent:
		c.setAvgEvaluationScore(0.7);
		assertTrue(
				"Cloning should result in the average evaluation scores being different Objects.",
				s.getAvgEvaluationScore() != c.getAvgEvaluationScore());
	}

	@Test
	public void testHasInterproAnnotations() {
		// Should have Interpro-Annotations with default test-Settings:
		assertTrue(getSettings().hasInterproAnnotations());
		String iprDbBackup = getSettings().getPathToInterproDatabase();
		getSettings().setPathToInterproDatabase(null);
		assertTrue(!getSettings().hasInterproAnnotations());
		getSettings().setPathToInterproDatabase(iprDbBackup);
		getSettings().setPathToInterproResults(null);
		assertTrue(!getSettings().hasInterproAnnotations());
		getSettings().setPathToInterproResults("/not/existing/path.raw");
		assertTrue(!getSettings().hasInterproAnnotations());
	}

	@Test
	public void testHasGeneOntologyAnnotations() {
		// Should have GO-Annotations with default test-Settings:
		assertTrue(getSettings().hasGeneOntologyAnnotations());
		getSettings().setPathToGeneOntologyResults(null);
		assertTrue(!getSettings().hasGeneOntologyAnnotations());
		getSettings().setPathToGeneOntologyResults("/not/existing/path.raw");
		assertTrue(!getSettings().hasGeneOntologyAnnotations());
	}

	@Test
	public void testRememberSimulatedAnnealingPath() {
		// Flag should be set in default Test-Settings:
		assertTrue(
				"Path through Parameter-Space should be remembered, but flag is set to FALSE.",
				getSettings().rememberSimulatedAnnealingPath());
	}
}
