package com.github.fabriziocucci.jersey.issue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationTest extends JerseyTest {

	private static final CountDownLatch COUNT_DOWN_LATCH_FOR_FEATURE = new CountDownLatch(2);
	
	private static Container container;
	
	@Override
	protected Application configure() {
		return new ResourceConfig()
				.register(feature(COUNT_DOWN_LATCH_FOR_FEATURE))
				.register(containerLifeCycleListenerForSavingContainer());
	}
	
	@Test
	public void testThatReloadDoesNotReloadFeatures() throws InterruptedException {
		container.reload();
		boolean isFeatureReloaded = COUNT_DOWN_LATCH_FOR_FEATURE.await(5, TimeUnit.SECONDS);
		Assert.assertTrue("Feature was not reloaded", isFeatureReloaded);
	}
	
	private static ContainerLifecycleListener containerLifeCycleListenerForSavingContainer() {
		return new ContainerLifecycleListener() {
			
			public void onStartup(Container container) {
				ApplicationTest.container = container;
			}
			
			public void onShutdown(Container container) {
				// nothing interesting to do!
			}			
			
			public void onReload(Container container) {
				// nothing interesting to do!
			}
			
		};
	}
	
	private static Feature feature(CountDownLatch countDownLatch) {
		return new Feature() {
			@Override
			public boolean configure(FeatureContext context) {
				countDownLatch.countDown();
				return true;
			}
		};
	}
	
}
