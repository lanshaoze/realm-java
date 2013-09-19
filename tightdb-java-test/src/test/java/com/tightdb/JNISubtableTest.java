package com.tightdb;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.Test;

public class JNISubtableTest {

    @Test()
    public void shouldSynchronizeNestedTables() {
        Group group = new Group();
        Table table = group.getTable("emp");

        TableSpec tableSpec = new TableSpec();
        tableSpec.addColumn(ColumnType.ColumnTypeString, "name");

        TableSpec subspec = tableSpec.addSubtableColumn("sub");
        subspec.addColumn(ColumnType.ColumnTypeInt, "num");

        table.updateFromSpec(tableSpec);

        table.add("Foo", null);
        assertEquals(1, table.size());

        Table subtable1 = table.getSubTable(1, 0);
        subtable1.add(123);
        assertEquals(1, subtable1.size());
        subtable1.private_debug_close();

        Table subtable2 = table.getSubTable(1, 0);
        assertEquals(1, subtable2.size());
        assertEquals(123, subtable2.getLong(0, 0));

        table.clear();
    }

    @Test()
    public void shouldInsertNestedTablesNested() {
        Group group = new Group();
        Table table = group.getTable("emp");

        // Define table
        TableSpec tableSpec = new TableSpec();
        tableSpec.addColumn(ColumnType.ColumnTypeString, "name");

        TableSpec subspec = tableSpec.addSubtableColumn("sub");
        subspec.addColumn(ColumnType.ColumnTypeInt, "num");

        tableSpec.addColumn(ColumnType.ColumnTypeInt, "Int");
        table.updateFromSpec(tableSpec);

        // Insert values
        table.add("Foo", null, 123456);
        table.getSubTable(1, 0).add(123);
        assertEquals(1, table.getSubTable(1, 0).size());
        assertEquals(123, table.getSubTable(1, 0).getLong(0,0));

        assertEquals(1, table.size());
    }

    @Test
    public void addColumnsToSubtables() {

        // Table definition
        Table persons = new Table();

        persons.addColumn(ColumnType.STRING, "name");
        persons.addColumn(ColumnType.STRING, "email");
        persons.addColumn(ColumnType.TABLE, "addresses");


        TableDefinition addresses = persons.getSubTableDefinition(2);
        addresses.addColumn(ColumnType.STRING, "street");
        addresses.addColumn(ColumnType.INTEGER, "zipcode");
        addresses.addColumn(ColumnType.TABLE, "phone_numbers");


        TableDefinition phone_numbers = addresses.getSubTableDefinition(2);
        phone_numbers.addColumn(ColumnType.INTEGER, "number");

        // Inserting data

        persons.add(new Object[] {"Mr X", "xx@xxxx.com", new Object[][] {{ "X Street", 1234, new Object[][] {{ 12345678 }} }} });


        // Assertions

        assertEquals(persons.getColumnName(2), "addresses");
        assertEquals(persons.getSubTable(2,0).getColumnName(2), "phone_numbers");
        assertEquals(persons.getSubTable(2,0).getSubTable(2,0).getColumnName(0), "number");

        assertEquals(persons.getString(1,0), "xx@xxxx.com");
        assertEquals(persons.getSubTable(2,0).getString(0,0), "X Street");
        assertEquals(persons.getSubTable(2,0).getSubTable(2,0).getLong(0,0), 12345678);

    }

    @Test
    public void removeColumnFromSubtable() {

        // Table definition
        Table persons = new Table();

        persons.addColumn(ColumnType.STRING, "name");
        persons.addColumn(ColumnType.STRING, "email");
        persons.addColumn(ColumnType.TABLE, "addresses");


        TableDefinition addresses = persons.getSubTableDefinition(2);
        addresses.addColumn(ColumnType.STRING, "street");
        addresses.addColumn(ColumnType.INTEGER, "zipcode");
        addresses.addColumn(ColumnType.TABLE, "phone_numbers");


        TableDefinition phone_numbers = addresses.getSubTableDefinition(2);
        phone_numbers.addColumn(ColumnType.INTEGER, "number");

        // Inserting data

        persons.add(new Object[] {"Mr X", "xx@xxxx.com", new Object[][] {{ "X Street", 1234, new Object[][] {{ 12345678 }} }} });


        // Assertions

        assertEquals(persons.getSubTable(2,0).getColumnCount(), 3);

        addresses.removeColumn(1);

        assertEquals(persons.getSubTable(2,0).getColumnCount(), 2);

    }

    @Test
    public void renameColumnInSubtable() {

        // Table definition
        Table persons = new Table();

        persons.addColumn(ColumnType.STRING, "name");
        persons.addColumn(ColumnType.STRING, "email");
        persons.addColumn(ColumnType.TABLE, "addresses");


        TableDefinition addresses = persons.getSubTableDefinition(2);
        addresses.addColumn(ColumnType.STRING, "street");
        addresses.addColumn(ColumnType.INTEGER, "zipcode");
        addresses.addColumn(ColumnType.TABLE, "phone_numbers");


        TableDefinition phone_numbers = addresses.getSubTableDefinition(2);
        phone_numbers.addColumn(ColumnType.INTEGER, "number");

        // Inserting data

        persons.add(new Object[] {"Mr X", "xx@xxxx.com", new Object[][] {{ "X Street", 1234, new Object[][] {{ 12345678 }} }} });


        // Assertions

        assertEquals("zipcode", persons.getSubTable(2,0).getColumnName(1));

        addresses.renameColumn(1, "zip");

        assertEquals("zip", persons.getSubTable(2,0).getColumnName(1));

    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void shouldThrowOnGetSubtableDefinitionFromSubtable() {

        // Table definition
        Table persons = new Table();

        persons.addColumn(ColumnType.STRING, "name");
        persons.addColumn(ColumnType.STRING, "email");
        persons.addColumn(ColumnType.TABLE, "addresses");


        TableDefinition addresses = persons.getSubTableDefinition(2);
        addresses.addColumn(ColumnType.STRING, "street");
        addresses.addColumn(ColumnType.INTEGER, "zipcode");
        addresses.addColumn(ColumnType.TABLE, "phone_numbers");


        TableDefinition phone_numbers = addresses.getSubTableDefinition(2);
        phone_numbers.addColumn(ColumnType.INTEGER, "number");

        // Inserting data

        persons.add(new Object[] {"Mr X", "xx@xxxx.com", new Object[][] {{ "X Street", 1234, new Object[][] {{ 12345678 }} }} });


        // Should throw

        persons.getSubTable(2,0).getSubTableDefinition(2);

    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void shouldThrowOnAddColumnFromSubtable() {

        // Table definition
        Table persons = new Table();

        persons.addColumn(ColumnType.STRING, "name");
        persons.addColumn(ColumnType.STRING, "email");
        persons.addColumn(ColumnType.TABLE, "addresses");


        TableDefinition addresses = persons.getSubTableDefinition(2);
        addresses.addColumn(ColumnType.STRING, "street");
        addresses.addColumn(ColumnType.INTEGER, "zipcode");
        addresses.addColumn(ColumnType.TABLE, "phone_numbers");


        TableDefinition phone_numbers = addresses.getSubTableDefinition(2);
        phone_numbers.addColumn(ColumnType.INTEGER, "number");

        // Inserting data

        persons.add(new Object[] {"Mr X", "xx@xxxx.com", new Object[][] {{ "X Street", 1234, new Object[][] {{ 12345678 }} }} });


        // Should throw

        persons.getSubTable(2,0).addColumn(ColumnType.INTEGER, "i");

    }

}
