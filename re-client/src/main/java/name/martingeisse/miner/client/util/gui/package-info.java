/**
 * <h1>Threading model</h1>
 * GUI threading model: GUI classes are intended to be used by the game thread only. All classes and methods are
 * unsynchronized, including draw() methods and similar. All fields are nonvolatile.
 * <p>
 * Internally, the GUI code has to draw by submitting work units to the OpenGL thread. These work units need to access
 * the state of GUI elements, but they might do so concurrently with running GUI logic, so even synchronizing that
 * access won't help because it draws the GUI while in an inconsistent state. We could create new work units for every
 * frame and have them contain a copy of the state, but that would put too much load on the garbage collector. To
 * solve the problem, we create work units that copy the state, but cache them and invalidate that cache when the state
 * changes.
 * <p>
 * <h1>Coordinate system</h1>
 * The GUI uses an integer coordinate system where the total height of the GUI is a hundred thousand units, and the
 * total width a determined such that a unit has the same length along either axis.
 */
package name.martingeisse.miner.client.util.gui;
