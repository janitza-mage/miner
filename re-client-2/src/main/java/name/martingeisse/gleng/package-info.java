/**
 * Naming:
 * - methods called glSomething always correspond to a single call to the same-named OpenGL function
 * - methods called gl__Something do not directly correspond to a single OpenGL function, but they call into
 *   OpenGL. They could e.g. call a differently named function, or call multiple functions. These methods, too,
 *   must only be called from the OpenGL thread.
 */
package name.martingeisse.gleng;
