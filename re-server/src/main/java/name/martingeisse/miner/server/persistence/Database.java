package name.martingeisse.miner.server.persistence;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Database {

    Connection newConnection() throws SQLException;

    default void runVoid(Consumer<DSLContext> dslConsumer) {
        runReturn(dsl -> {
            dslConsumer.accept(dsl);
            return null;
        });
    }

    default <T> T runReturn(Function<DSLContext, T> dslConsumer) {
        try (Connection connection = newConnection()) {
            DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
            return dslConsumer.apply(dsl);
        } catch (SQLException e) {
            throw new DataAccessException("could not connect to the database", e);
        }
    }

    default void runVoidInSingleTransaction(Consumer<DSLContext> dslConsumer) {
        runVoid(outerDsl -> {
            outerDsl.transaction(innerConfiguration -> {
                var innerDsl = DSL.using(innerConfiguration);
                dslConsumer.accept(innerDsl);
            });
        });
    }

    default <T> T runReturnInSingleTransaction(Function<DSLContext, T> dslConsumer) {
        return runReturn(outerDsl -> {
            return outerDsl.transactionResult(innerConfiguration -> {
                var innerDsl = DSL.using(innerConfiguration);
                return dslConsumer.apply(innerDsl);
            });
        });
    }

}
