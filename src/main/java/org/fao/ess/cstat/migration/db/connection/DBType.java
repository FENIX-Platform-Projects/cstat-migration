package org.fao.ess.cstat.migration.db.connection;


public enum DBType {

    year(4),
    time(4),
    month(4),
    day(4),
    code(12),
    customCode(12),
    text(12),
    number(2);

    private int dbType;

    DBType(int dbType) {
        this.dbType = dbType;
    }

    public int getDBType() {
        return dbType;
    }



}
