/**
 * This package provides a manual check. It's function is to provide a check
 * which can be (dis)approved manually. Adding this check to gate means that the
 * quality line will be interrupted at this point until some user approves the
 * check. The quality line gets further evaluated afterwards.
 * Disapproving leads to the failure of the given instance of the quality line.
 */
package de.binarytree.plugins.qualitygates.steps.manualcheck;

