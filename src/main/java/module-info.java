module space.arim.injector {
	exports space.arim.injector;
	exports space.arim.injector.error;

	/*
	 * As of the time of writing this, the original JSR-330 (javax.inject) has not
	 * yet modularised. It is an active issue. The logical module name would be
	 * 'java.inject', but it has not been added to JSR-330 yet.
	 * 
	 * However, the Eclipse Foundation maintains Jakarta EE. They forked
	 * javax.inject in two ways.
	 * 
	 * First, the Eclipse foundation maintains jakarta.inject under Jakarta EE 9.
	 * The jakarta.inject namespace is modularised as 'jakarta.inject', and its
	 * maven coordinates are jakarta.inject:jakarta.inject-api, version 2.x.
	 * 
	 * Second, the Eclipse Foundation maintains an unofficial continuation of
	 * javax.inject as part of Jakarta EE 8. The javax.inject namespace is here
	 * modularised under 'java.inject'. Its maven coordinates are
	 * jakarta.inject:jakarta.inject-api, version 1.x.
	 * 
	 * Accordingly, 'java.inject' is the module name used here. This is despite the
	 * filename-derived module name being 'javax.inject'
	 */

	requires static jakarta.inject;
	requires static java.inject;
}