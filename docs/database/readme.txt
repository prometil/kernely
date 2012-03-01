Database schema are UML model with Topcased. We chose an UML Class Diagram to represents database.

- KernelyDB.uml contains an xml description of all classes (database tables), their attributes (table columns), and associations (foreign keys).

- KernelyDB.umldi contains only visual descriptions, to build the schema shonw in kernelydbschema.png.

- kernelydbschema.png is the visualization of Kernely database structure.
	> Each package (core, holidays...) represents a plugin, and database tables contains by the plugin.
	> Classes with blue background are model tables.
	> Classes with green background are association tables (to represents a ManyToMany association).
