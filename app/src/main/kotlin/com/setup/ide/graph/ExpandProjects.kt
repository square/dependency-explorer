package com.setup.ide.graph

import com.setup.ide.graph.model.GradleProject
import java.nio.file.Path

/**
 * From a given input gradle project starting point, we collect all possible [GradleProject]
 * that can be found from that node.
 *
 * Example: If an input directory has a tree structure
 *    a
 *    b
 *      c
 *        d
 *
 * And input provides is `b`, then we find all possible paths that are valid modules from
 * node b.
 *
 * Resulting in a set [:b:c, :b:d] if c and d are the directories contain build files.
 */
interface ExpandProjects {
  /**
   * Collect modules from input and return a valid set of explorable [GradleProject]
   *
   * @param   input from where you start exploring which valid [GradleProject] should be
   *                considered for further exploration for finding dependencies
   *                NOTE: input is valid if it is : or / delimited, other formats of
   *                gradle projects are not valid.
   *
   * @return  Set<GradleProject> a valid set of projects that can be explored
   */
  fun expand(input: Set<GradleProject>): Set<GradleProject>
}