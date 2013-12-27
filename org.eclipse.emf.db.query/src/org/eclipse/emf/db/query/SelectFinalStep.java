package org.eclipse.emf.db.query;


public class SelectFinalStep {
    private final SelectQuery query;

    /* package */SelectFinalStep(SelectQuery query) {
        this.query=query;
    }

    /* package */SelectQuery getQuery() {
        return query;
    }

    // public <T> List<T> getAll() throws SQLException {
    // Statement stmt=query.connection.createStatement();
    // stmt.closeOnCompletion();
    // ResultSet rSet=stmt.executeQuery(getSQL());
    // List<T> result;
    // if (rSet.last()) {
    // result=new ArrayList<T>(rSet.getRow());
    // rSet.beforeFirst();
    // while (rSet.next()) {
    // // TODO
    // }
    // } else
    // result=Collections.emptyList();
    //
    // return Collections.unmodifiableList(result);
    // }

    public String getSQL() {
        return query.toString();
    }
}
