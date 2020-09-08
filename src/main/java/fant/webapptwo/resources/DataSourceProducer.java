/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.resources;

import static fant.webapptwo.resources.DataSourceProducer.JNDI_NAME;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

/**
 *
 * @author marti
 */
@Singleton
@DataSourceDefinition(
    name = JNDI_NAME,
    className = "org.h2.jdbcx.JdbcDataSource",
    url = "jdbc:h2:~/auth.db")
public class DataSourceProducer {
    public static final String JNDI_NAME =  "java:app/jdbc/default";

    @Resource(lookup=JNDI_NAME)
    DataSource ds;

    @Produces
    public DataSource getDatasource() {
        return ds;
    }
}
